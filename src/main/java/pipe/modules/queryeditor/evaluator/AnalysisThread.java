/**
 * AnalysisThread
 * 
 * This thread is responsible for the submission of relevant model and query data to the
 * analysis server, as well as the processing of returned results.
 * 
 * @author Tamas Suto
 * @date 11/01/08
 */

package pipe.modules.queryeditor.evaluator;

import pipe.common.SimplePlaces;
import pipe.common.*;
import pipe.common.dataLayer.StateGroup;
import pipe.gui.ApplicationSettings;
import pipe.modules.interfaces.Cleanable;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.evaluator.gui.ProgressWindow;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.server.performancequery.SimpleNode;
import pipe.views.PetriNetView;

import java.io.IOException;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Handler;
import java.util.logging.Level;

public class AnalysisThread implements Runnable, CommunicatorStarter, EvaluatorLoggingHandler, Cleanable
{
	private String					serverAddress			= "";
	private int						serverPort				= 0;
    private int analysisListenerPort = 0;
	private InterruptableSocketIO	serverConnection;

	private AnalysisSettings		settings;

	private LoggingListener			loggingListener;
	private AnalysisResultsReceiver	resultsReceiver;
	private AnalysisStatusUpdater	serverListener;
	private Thread					serverListenerThread	= null;
	


	public AnalysisThread() {
		EvaluatorLoggingHandler.logger.setLevel(Level.WARNING);
	}

	public void cleanUp()
	{
		if (this.serverListener != null)
		{
			if (this.serverListener.isOpen())
			{
				EvaluatorLoggingHandler.logger.info("Stopping Server thread");
				this.serverListener.sendLine(AnalysisInstruction.STOP.toString());
			}
			this.serverListener.finish();
			this.serverListener.cleanUp();
		}
		if (this.loggingListener != null)
		{
			this.loggingListener.finish();
			this.loggingListener.cleanUp();
		}
		if (this.resultsReceiver != null)
		{
			this.resultsReceiver.finish();
			this.resultsReceiver.cleanUp();
		}
		if (this.serverConnection != null)
		{
			this.serverConnection.cleanUp();
		}
		EvaluatorLoggingHandler.logger.info("Closing Client side log handlers..");
		for (final Handler h : EvaluatorLoggingHandler.logger.getHandlers())
		{
			h.close();
		}
	}

	public void finish()
	{
	}

	public boolean hasStarted()
	{
		return this.serverListener != null;
	}

	private void printStatusMessage(final String message)
	{
		QueryManager.printStatusMessage(message);
	}

	public synchronized void run()
	{
		try
		{
			this.printStatusMessage("Retrieving analysis server settings");

			this.settings = SettingsManager.getAnalysisSettings();

			EvaluatorLoggingHandler.logger.setLevel(this.settings.clientLoggingLevel);

			EvaluatorLoggingHandler.logger.info("\nStarting new Analysis Session... \n");

			this.serverAddress = this.settings.serverAddress;
			this.serverPort = this.settings.serverPort;

			// See PerformanceQueryServerAction currently logging port is
			// serverPort + 2
			// if you change this change that too!
			this.analysisListenerPort = this.serverPort + 1;
            int loggingPort = this.serverPort + 2;

			// send data to server
			final String msg = "Opening main connection to server on " + this.serverAddress + ":" +
								this.serverPort;
			this.printStatusMessage(msg);
			EvaluatorLoggingHandler.logger.info(msg);
			this.serverConnection = new InterruptableSocketIO(this.serverAddress, this.serverPort);

			// listen to status messages from the server
			if (this.settings.serverLoggingLevel.intValue() < Level.OFF.intValue())
			{
				this.loggingListener = new LoggingListener(loggingPort);
				this.loggingListener.start();
			}

			this.sendDataToServer();

			this.resultsReceiver = new AnalysisResultsReceiver(this.serverConnection, this, this);
			final Thread t = new Thread(this.resultsReceiver);
			t.start();

			this.printStatusMessage("Awaiting server's response...");
			QueryEvaluator.updateStatusAll(EvaluationStatus.EVALNOTSTARTED);

			if (this.serverListenerThread == null)
			{
				this.wait();
			}
			this.serverListenerThread.join();
		}
		catch (final InterruptedException e)
		{
			EvaluatorLoggingHandler.logger.log(Level.WARNING, "Join interrupted", e);
		}
		catch (final StreamCorruptedException e)
		{
			final String msg = "Stream Corrupted Exception";
			EvaluatorLoggingHandler.logger.log(Level.WARNING, msg, e);
		}
		catch (final UnknownHostException e)
		{
			final String msg = "Unknown host exception";
			EvaluatorLoggingHandler.logger.log(Level.WARNING, msg, e);
		}
		catch (final OptionalDataException e)
		{
			final String msg = "Data Exception";
			EvaluatorLoggingHandler.logger.log(Level.WARNING, msg, e);
		}
		catch (final IOException e)
		{
			EvaluatorLoggingHandler.logger.log(Level.WARNING, "Problem in Analysis session initialisation", e);
		}
		finally
		{
			// switch Cancel button to OK on ProgressWindow
			this.updateProgressWindow();
			EvaluatorLoggingHandler.logger.info("Ending Analysis Session... \n");
		}
	}

	private void sendDataToServer()
	{
		EvaluatorLoggingHandler.logger.info("Sending query data to server");
		// model data
        final PetriNetView modelData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
		final SimplePlaces modelPlaces = new SimplePlaces(modelData);
		final SimpleTransitions modelTransitions = new SimpleTransitions(modelData);
		final ArrayList<StateGroup> stateGroups = modelData.getStateGroupsArray();

		// query data
		final HashMap<String, ArrayList<String>> stateLabels = QueryManager.getData().getStateLabels();

		final ArrayList<SimpleNode> simpleNodes = new ArrayList<SimpleNode>();
		final ArrayList<PerformanceTreeNode> queryNodes = QueryManager.getData().getTreeNodes();
		final Iterator<PerformanceTreeNode> i = queryNodes.iterator();
		PerformanceTreeNodeWrapper converter;
		while (i.hasNext())
		{
			final PerformanceTreeNode ptNode = i.next();
			converter = new PerformanceTreeNodeWrapper(ptNode, null);
			final SimpleNode sNode = converter.convertToSimpleNode();
			simpleNodes.add(sNode);
		}

		// the ordering of the sends has to coincide with the reading in
		// ServerAction
		this.printStatusMessage("Sending data to server");
		this.serverConnection.sendObject(AnalysisType.PERFORMANCEQUERY);
		this.serverConnection.sendObject(this.settings);
		this.serverConnection.sendObject(modelPlaces);
		this.serverConnection.sendObject(modelTransitions);
		this.serverConnection.sendObject(stateGroups);
		this.serverConnection.sendObject(stateLabels);
		this.serverConnection.sendObject(simpleNodes);
	}

	public void setServer(final String serverAddr, final int serverPrt)
	{
		this.serverAddress = serverAddr;
		this.serverPort = serverPrt;
	}

	public synchronized void startCommunicator() throws IOException
	{
		this.serverListener = new AnalysisStatusUpdater(this.serverConnection.getServer(),
														this.analysisListenerPort);
		this.serverListenerThread = new Thread(this.serverListener);
		this.serverListenerThread.start();
		this.notify();
	}

	private void updateProgressWindow()
	{
		final ProgressWindow w = QueryManager.getProgressWindow();
		if (w != null)
		{
			w.swapButton();
		}
	}

}
