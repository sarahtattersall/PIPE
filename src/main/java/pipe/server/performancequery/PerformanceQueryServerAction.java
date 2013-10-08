package pipe.server.performancequery;

import pipe.common.SimplePlaces;
import pipe.common.SimpleTransitions;
import pipe.common.dataLayer.StateGroup;
import pipe.common.*;
import pipe.modules.interfaces.Cleanable;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryresult.ResultWrapper;
import pipe.server.CommunicationsManager;
import pipe.server.performancequery.nodeanalyser.InvalidNodeAnalyserException;
import pipe.server.performancequery.structure.*;
import pipe.server.serverCommon.PathsWrapper;
import pipe.server.serverCommon.ServerAction;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetAddress;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.concurrent.*;
import java.util.logging.Handler;
import java.util.logging.Level;

public class PerformanceQueryServerAction extends ServerAction
implements
	Runnable,
	ServerLoggingHandler,
        Cleanable,
	PoolWaiter,
	ThreadFactory
{

	private SocketChannel					clientConnection;

	// data from client
	private PriorityQueue<ParentSubtree>	executionSchedule;

	private ResultSubtree					root;

	private ClientCommunicator				updateSender;

	private ResultSender					resultSender;

    private final int						loggingPort;

	private final String					hostName;

	private ArrayList<SimpleNode>			queryNodes;

	private final String					slash	= System.getProperty("file.separator");

	private TranslateQueryTree				modFileGenerator;

	private final String					resultsDirPath;

	private final ExecutorService			subtreePool;
	private final ScheduledExecutorService	workerPool;

	public PerformanceQueryServerAction(final ObjectInputStream receiver,
										final SocketChannel connection,
										final int id,
										final PathsWrapper paths,
										final HashMap<InetAddress, Integer> clients) {
		super(receiver, connection.socket(), id, paths, clients);

		this.clientConnection = connection;
		this.resultsDirPath = this.workPath + this.slash + "results";
		this.resultsDir = new File(this.resultsDirPath);
		this.resultsDir.mkdir();

		Thread.setDefaultUncaughtExceptionHandler(new ServerExceptionHandler(this));

		this.subtreePool = Executors.newCachedThreadPool(this);
		this.workerPool = Executors.newScheduledThreadPool(3, this);
		
		// old-style dynamic port assignments, depending on what the user chooses
		//this.statusPort = connection.socket().getLocalPort() + 1;
		//this.loggingPort = this.statusPort + 1;
		
		// ports are now fixed and defined in CommunicationsManager
        int statusPort = CommunicationsManager.statusPort;
		this.loggingPort = CommunicationsManager.loggingPort;
		this.hostName = this.clientConnection.socket().getInetAddress().getHostName();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void run()
	{
		try
		{
			this.setupLogger();
			ServerLoggingHandler.logger.log(Level.INFO,
											"Server-side thread dealing with client request started");

			this.getObjectsInOrder();

			if (this.settings.serverLoggingLevel.intValue() < Level.OFF.intValue())
			{
				ServerLoggingHandler.logger.info("Setting up socket logging");
				// schedule a thread to connect the SocketHander this will
				// connect it but not wait for the connect
				final ScheduledFuture<?> sched = this.workerPool.schedule(	LoggingHelper.startSocketLogging(	this.hostName,
																												this.loggingPort,
																												ServerLoggingHandler.logger,
																												this.settings.serverLoggingLevel),
																			0,
																			TimeUnit.SECONDS);
				// stop the connect if it hasn't completed in 10 seconds
				this.workerPool.schedule(new Runnable()
				{
					public void run()
					{
						sched.cancel(true);
					}
				}, 10, TimeUnit.SECONDS);
			}

			this.resultSender.sendObject(AnalysisInstruction.START);

			// Create a thread to send updates to the client, wait for
			// connection
			// response from client after AnalysisInstruction.START sent above
			//this.updateSender = new ClientCommunicator(this.statusPort, this);
			//this.updateSender.waitForConnection();
			// this.workerPool.execute(this.updateSender.getClientListener());

			this.updateSender = new ClientCommunicator(this);
			this.updateSender.waitForConnection();
			
			this.analyseQuery();

			this.waitForPoolComplete();

			this.cleanUp();
		}
		catch (final InvalidNodeAnalyserException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Couldn't complete analysis", e);
			this.cleanUp();
		}
		catch (final QueryServerException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Couldn't complete analysis", e);
			this.cleanUp();
		}
		catch (final IOException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Couldn't complete analysis", e);
			this.cleanUp();
		}
		catch (final ClassNotFoundException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING,
											"Ensure grail-service has read permissions and class is present in grail-service/server",
											e);
			this.cleanUp();
		}
	}

	private void setupLogger()
	{
		ServerLoggingHandler.logger.setLevel(Level.ALL);
		try
		{
			final String logFile = this.workPath + "/analysis%u.log";
			LoggingHelper.setupFileLogging(logFile, ServerLoggingHandler.logger, Level.ALL);
		}
		catch (final IOException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Couldn't set up Logger", e);
		}
	}

	public void waitForPoolComplete()
	{
		ServerLoggingHandler.logger.info("Waiting for Subtree Thread pool Execution to complete");
		try
		{
			// 2 hours timeout window - should be enough for most jobs
			boolean finishedBeforeTermination = this.subtreePool.awaitTermination(7200, TimeUnit.SECONDS);
			if (!finishedBeforeTermination)
			{
				ServerLoggingHandler.logger.warning("Pool was terminated by timeout");
			}
		}
		catch (final InterruptedException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Wait for subtreePool completion interrupted", e);
		}
		finally
		{
			ServerLoggingHandler.logger.info("Subtree Thread Pool complete");

		}
	}

	private void analyseQuery() throws InvalidNodeAnalyserException, QueryServerException

	{
		ServerLoggingHandler.logger.log(Level.INFO, "Performing query analysis");

		this.root = SubtreeHelper.constructSubtrees(this.queryNodes, this.updateSender);
		this.executionSchedule = new PriorityQueue<ParentSubtree>(	this.root.getDecendantSubtrees().size(),
																	new SubtreeComparator());
		this.executionSchedule.addAll(SubtreeHelper.createExecutionSchedule(this.root));

		ServerLoggingHandler.logger.log(Level.INFO, "Performing query evaluation");
		this.updateSender.sendLine("Performing query evaluation");

		while (!this.executionSchedule.isEmpty())
		{
			this.performEvaluation(this.executionSchedule.remove());
		}

		final String msg = "All nodes sent for evaluation";
		ServerLoggingHandler.logger.info(msg);
		this.updateSender.sendLine(msg);

		this.subtreePool.shutdown();
	}

	public void cleanUp()
	{
		ServerLoggingHandler.logger.info("Analysis over, Closing down resources");

		this.subtreePool.shutdownNow();
		this.workerPool.shutdownNow();

		try
		{
			this.decrementConnections();

			if (this.in != null)
			{
				this.in.close();
			}
			if (this.clientConnection != null)
			{
				this.clientConnection.close();
			}
			if (this.resultSender != null)
			{
				this.resultSender.sendObject(AnalysisInstruction.FINISHED);
				this.resultSender.cleanUp();
			}
			this.notifyClientFinalState();
			if (this.updateSender != null)
			{
				this.updateSender.cleanUp();
			}
			ServerLoggingHandler.logger.info("Closing logging handers..");
			for (final Handler h : ServerLoggingHandler.logger.getHandlers())
			{
				h.close();
			}
			if (!this.subtreePool.isTerminated())
			{
				this.subtreePool.shutdownNow();
			}
		}
		catch (final IOException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Couldn't close Socket", e);
		}
		finally
		{
			ServerLoggingHandler.logger.info("Finished resource cleanup");
		}
	}

	private void evaluateRange(final RangeSubtree range)
	{
		final Future<ResultWrapper> result = this.subtreePool.submit(new NodeAnalyserExecutor(	range,
																								this.resultSender));
		range.setFutureResult(result);
	}

	private void evaluateRoot()
	{
		this.root.setFutureResult(this.subtreePool.submit(new SimpleAnalysisExecutor(	this.root,
																						this.resultSender)));
	}

	private void evaluateSequential(final SequentialSubtree seq) throws QueryServerException
	{
		if (seq.getType() == PetriNetNode.SEQUENTIAL)
		{
			seq.setFutureResult(this.subtreePool.submit(new SimpleAnalysisExecutor(seq, this.resultSender)));
		}
		else throw new QueryServerException("Subtree is " + seq.getType().toString() + " expected Sequential");
	}

	private void evalutateOperation(final OperationSubtree toBeExecuted)
	{
		try
		{
			AnalysisExecutor ex;
			switch (toBeExecuted.getType())
			{
				case PROBININTERVAL :
					ex = new DrmaaExecutor(	toBeExecuted,
											this.paths.getProbInIntervalPath(),
											this.workPath,
											this.resultsDirPath,
											this.settings,
											this.maxProcessors,
											this.resultSender);
					break;
				case PERCENTILE :
					ex = new DrmaaExecutor(	toBeExecuted,
											this.paths.getPercentilePath(),
											this.workPath,
											this.resultsDirPath,
											this.settings,
											this.maxProcessors,
											this.resultSender);
					break;
				case CONVOLUTION :
					ex = new DrmaaExecutor(	toBeExecuted,
											this.paths.getConvoPath(),
											this.workPath,
											this.resultsDirPath,
											this.settings,
											this.maxProcessors,
											this.resultSender);
					break;
				case PROBINSTATES :
					if (!toBeExecuted.hasModFile())
					{
						this.genModFile(toBeExecuted);
					}
					ex = new DrmaaExecutor(	toBeExecuted,
											this.paths.getHydraPath(),
											this.workPath,
											this.resultsDirPath,
											this.settings,
											this.maxProcessors,
											this.resultSender);
					break;
				case DISTRIBUTION :
					ex = new SimpleAnalysisExecutor(toBeExecuted, this.resultSender);
					break;
				case MOMENT :
					if (toBeExecuted.getChildByRole(QueryConstants.momentChildDensDist).getType() == PetriNetNode.STEADYSTATEPROB)
					{
						ex = new MomentSSPExecutor(toBeExecuted, this.resultSender);
					}
					else
					{
						if (!toBeExecuted.hasModFile())
						{
							this.genModFile(toBeExecuted);
						}
						ex = new DrmaaExecutor(	toBeExecuted,
												this.paths.getMomaPath(),
												this.workPath,
												this.resultsDirPath,
												this.settings,
												this.maxProcessors,
												this.resultSender);
					}
					break;
				case PASSAGETIMEDENSITY :
					if (!toBeExecuted.hasModFile())
					{
						this.genModFile(toBeExecuted);
					}
					ex = new DrmaaExecutor(	toBeExecuted,
											this.paths.getSmartaPath(),
											this.workPath,
											this.resultsDirPath,
											this.settings,
											this.maxProcessors,
											this.resultSender);
					break;
				case FIRINGRATE :
				case STEADYSTATEPROB :
					if (!toBeExecuted.hasModFile())
					{
						this.genModFile(toBeExecuted);
					}
					ex = new DrmaaExecutor(	toBeExecuted,
											this.paths.getDnamacaPath(),
											this.workPath,
											this.resultsDirPath,
											this.settings,
											this.maxProcessors,
											this.resultSender);
					break;
				case ININTERVAL :
				case DISCON :
				case ARITHCOMP :
				case ARITHOP :
				case NEGATION :
					ex = new NodeAnalyserExecutor(toBeExecuted, this.resultSender);
					break;
				case STEADYSTATESTATES :
				case STATESATTIME :
				case SUBSET :
				default :
					throw new QueryServerException("Unexpected/Unsupported OperationSubtree in Execution Schedule");
			}
			toBeExecuted.setFutureResult(this.subtreePool.submit(ex));
		}
		catch (final IOException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Couldn't create mod file for subtree " +
															toBeExecuted.getID(), e);
			toBeExecuted.failed();
		}
		catch (final QueryServerException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Couldn't create mod file for subtree " +
															toBeExecuted.getID(), e);
			toBeExecuted.failed();
		}
	}

	// do nothing!
	public void finish()
	{
	}

	public boolean finished()
	{
		for (final Subtree s : this.executionSchedule)
		{
			if (s.hasEvalCompleted())
				return false;
		}
		return true;
	}

	private void genModFile(final ParentSubtree toBeExecuted) throws QueryServerException, IOException
	{
		if (toBeExecuted instanceof OperationSubtree)
		{
			if (this.modFileGenerator == null)
			{
				// create mod file generator
				this.modFileGenerator = this.getTranslateQueryTree();
			}
			this.modFileGenerator.genModForSubtree((OperationSubtree) toBeExecuted);
		}
		else throw new QueryServerException("Can't create mod file for type " + toBeExecuted.getType());
	}

	private void getObjectsInOrder() throws ClassNotFoundException, IOException, QueryServerException
	{
		ServerLoggingHandler.logger.log(Level.INFO, "Receiving data from incoming object stream");
		try
		{
			this.settings = (AnalysisSettings) this.in.readObject();
			this.places = (SimplePlaces) this.in.readObject();
			this.transitions = (SimpleTransitions) this.in.readObject();
			this.stateGroups = (ArrayList<StateGroup>) this.in.readObject();
			this.stateLabels = (HashMap<String, ArrayList<String>>) this.in.readObject();
			this.queryNodes = (ArrayList<SimpleNode>) this.in.readObject();
			ServerLoggingHandler.logger.info("Got all Objects from client");
		}
		catch (final ClassCastException e)
		{
			throw new QueryServerException("Objects sent from client in unexpected order", e);
		}
		finally
		{
			this.clientConnection.socket().shutdownInput();
			this.resultSender = new ResultSender(this.clientConnection);
			this.clientConnection = null;
			this.in = null;
		}
	}

	private TranslateQueryTree getTranslateQueryTree()
	{
		TranslateQueryTree translateQuery;

		// need to change path and id variables
		final String path = this.workPath + this.slash;
		ServerLoggingHandler.logger.log(Level.INFO, "path is :" + path);
		translateQuery = new TranslateQueryTree(this.places,
												this.transitions,
												this.stateGroups,
												this.stateLabels,
												this.queryNodes,
												this.settings,
												path);
		return translateQuery;
	}

	public Thread newThread(final Runnable r)
	{
		return new Thread(Thread.currentThread().getThreadGroup(), r);
	}

	private void notifyClientFinalState()
	{
		AnalysisInstruction endState = AnalysisInstruction.FINISHED;
		if (this.root != null)
		{
			for (final Subtree s : this.root.getDecendantSubtrees())
			{
				if (s.hasFailed())
				{
					endState = AnalysisInstruction.FAILED;
				}
				else if (endState != AnalysisInstruction.FAILED && s.isInProgress())
				{
					endState = AnalysisInstruction.TIMEOUT;
				}
			}
		}
		this.updateSender.sendLine(endState.toString());
	}

	private void performEvaluation(final ParentSubtree toBeExecuted)
	{
		try
		{

			ServerLoggingHandler.logger.info("Executing :" + toBeExecuted.getType() + " " +
												toBeExecuted.getID());
			if (toBeExecuted instanceof OperationSubtree)
			{
				this.evalutateOperation((OperationSubtree) toBeExecuted);
			}
			else if (toBeExecuted instanceof ResultSubtree)
			{
				this.evaluateRoot();
			}
			else if (toBeExecuted instanceof RangeSubtree)
			{
				this.evaluateRange((RangeSubtree) toBeExecuted);
			}
			else if (toBeExecuted instanceof SequentialSubtree)
			{
				this.evaluateSequential((SequentialSubtree) toBeExecuted);
			}
			else throw new QueryServerException("Unexpected Subtree Type in Execution Schedule");
		}
		catch (final QueryServerException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Evaluation of subtree " + toBeExecuted.getID() +
															" failed", e);
			toBeExecuted.failed();
		}
	}

}
