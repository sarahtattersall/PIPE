/**
 * AnalysisResultsReceiver
 * 
 * This class receives the results file from the server and displays a graph of the data 
 * 
 * @author Barry Kearns
 * @date September 2007
 */

package pipe.modules.queryeditor.evaluator;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.channels.AsynchronousCloseException;
import java.util.logging.Level;

import pipe.common.AnalysisInstruction;
import pipe.modules.interfaces.Cleanable;
import pipe.modules.queryresult.ResultWrapper;
import pipe.exceptions.UnexpectedResultException;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.evaluator.gui.ProgressView;

public class AnalysisResultsReceiver implements Runnable, Cleanable, EvaluatorLoggingHandler
{

	private final InterruptableSocketIO	server;
	private boolean						finished;

	private final ProgressView			progressView;

	private final CommunicatorStarter					starter;

	private Cleanable							cleaner;

	public AnalysisResultsReceiver(	final InterruptableSocketIO server,
									final CommunicatorStarter r,
									final Cleanable cleaner) throws IOException {
		this.starter = r;
		this.finished = false;
		this.server = server;
		server.setSoTimeout(250);
		this.progressView = QueryManager.getProgressView();
		this.cleaner = cleaner;
	}

	public void cleanUp()
	{
		if (this.server != null)
		{
			EvaluatorLoggingHandler.logger.info("AnalysisResultsReceiver: Closing Server connection");
			this.server.cleanUp();
		}
	}

	public void finish()
	{
		this.finished = true;
	}

	private void receiveResult() throws ClassNotFoundException, IOException, UnexpectedResultException
	{
		try
		{
			Object o;
			while ((o = this.server.readObject()) != null)
			{
				if (o instanceof ResultWrapper)
				{
					final ResultWrapper w = (ResultWrapper) o;
					EvaluatorLoggingHandler.logger.info("Got result for " + w.getNodeID() + " " + w.getType());
					this.progressView.setNodeResult(w, this);
				}
				else if (o instanceof AnalysisInstruction)
				{
					switch ((AnalysisInstruction) o)
					{
						case FINISHED :
							EvaluatorLoggingHandler.logger.info("Recieved finished message from server");
							this.finish();
							break;
						case START :
							EvaluatorLoggingHandler.logger.info("Recieved start message from server");
							if (!this.starter.hasStarted())
							{
								this.starter.startCommunicator();
							}
							break;
						default :
							break;
					}
				}
				else throw new UnexpectedResultException("Unexpected type recieved");
			}
		}
		catch (final InterruptedIOException e)
		{
			EvaluatorLoggingHandler.logger.warning("read timed out, restarting..");
		}
	}

	public void run()
	{
		try
		{
			while (!this.finished)
			{
				this.receiveResult();
			}
		}
		catch (final ClassNotFoundException e)
		{
			EvaluatorLoggingHandler.logger.log(Level.WARNING, "Results reciever Thread Exiting...", e);
		}
		catch (final AsynchronousCloseException e)
		{
			EvaluatorLoggingHandler.logger.info("Results reciever Thread Exiting...");
		}
		catch (final IOException e)
		{
			EvaluatorLoggingHandler.logger.log(Level.WARNING, "Results reciever Thread Exiting...", e);
		}
		catch (final UnexpectedResultException e)
		{
			EvaluatorLoggingHandler.logger.log(Level.WARNING, "Results reciever Thread Exiting...", e);
		}
		finally
		{
			this.cleaner.cleanUp();
			this.cleanUp();
		}
	}
}
