/**
 * AnalysisStatusUpdater
 * 
 * This class deals with the reception of status updates from the server.
 * The server reads the log file that contains the current state of affairs 
 * of the analysis process and sends the status updates to the client via
 * the socket connection. This class deals with the messages coming in. 
 * 
 * @author Barry Kearns
 * @author Tamas Suto
 * @date 13/01/08
 */

package pipe.modules.queryeditor.evaluator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

import pipe.common.AnalysisInstruction;
import pipe.modules.interfaces.Cleanable;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.evaluator.gui.ProgressWindow;
import pipe.server.performancequery.ServerLoggingHandler;

public class AnalysisStatusUpdater implements Cleanable, Runnable, EvaluatorLoggingHandler
{

	private final SocketChannel		socketConnection;
	private String					completedState;
	private final PrintWriter		out;

	private final BufferedReader	in;

	private boolean					finished;

	public AnalysisStatusUpdater(final String host, final int port) throws IOException {
		EvaluatorLoggingHandler.logger.info("Listening for server status on port " + port);
		this.socketConnection = SocketChannel.open(new InetSocketAddress(host, port));

		this.socketConnection.socket().setSoTimeout(250);

		this.out = new PrintWriter(Channels.newOutputStream(this.socketConnection));
		this.in = new BufferedReader(new InputStreamReader(Channels.newInputStream(this.socketConnection)));

		this.completedState = QueryConstants.failedComplete;
		this.finished = false;
	}

	public void cleanUp()
	{
		EvaluatorLoggingHandler.logger.fine("AnalysisStatusUpdater: Closing connection");
		try
		{
			if (this.in != null)
			{
				this.in.close();
			}
			this.socketConnection.close();
		}
		catch (final IOException e)
		{
			EvaluatorLoggingHandler.logger.log(Level.WARNING, "Couldn't cleanup all resources", e);
		}
	}

	public void finish()
	{
		this.finished = true;
	}

	/**
	 * @return the completedState
	 */
	public String getCompletedState()
	{
		return this.completedState;
	}

	public boolean isOpen()
	{
		return this.socketConnection.isOpen();
	}

	public void run()
	{
		final ProgressWindow progressWindow = QueryManager.getProgressWindow();
		String status = null;
		boolean firstTimeWaiting = true;
		EvaluatorLoggingHandler.logger.info("listening for status updates");
		try
		{
			while (!this.finished)
			{
				// keep checking messages from the server until FINISHED,
				// FAILED or TIMEOUT is encountered
				try
				{
					while ((status = this.in.readLine()) != null)
					{
						switch (AnalysisInstruction.getFromName(status))
						{
							case FAILED :
								// job failed
								this.completedState = QueryConstants.failedComplete;

								this.finish();
								break;
							case FINISHED :
								// job finished
								this.completedState = QueryConstants.successfulComplete;

								this.finish();
								break;
							case START :
								// jobs are queueing
								if (firstTimeWaiting)
								{
									firstTimeWaiting = false;
									status = "Queueing...";
									progressWindow.setProgressBarText(status);
								}
								break;
							case STOP :

								break;
							case TIMEOUT :
								// job timed-out
								this.completedState = QueryConstants.timeoutComplete;

								this.finish();
								break;
							case UPDATE :
								final String nodeID = this.in.ready() ? this.in.readLine() : null;
								final String statusUpdate = this.in.ready() ? this.in.readLine() : null;
								if (nodeID != null && statusUpdate != null)
								{
									QueryEvaluator.updateStatus(new NodeStatusUpdater(statusUpdate, nodeID));
									EvaluatorLoggingHandler.logger.info(nodeID + " " + statusUpdate);
								}
								else
								{
									EvaluatorLoggingHandler.logger.warning("Recieved instructions in unexpected order");
								}
								break;
							default :
								EvaluatorLoggingHandler.logger.info(status);
								progressWindow.setProgressBarText(status);
								break;
						}
					}
				}
				catch (final InterruptedIOException e)
				{
					EvaluatorLoggingHandler.logger.warning("read timed out, restarting..");
				}
				catch (final QueryAnalysisException e)
				{
					EvaluatorLoggingHandler.logger.log(Level.WARNING, "Couldn't update node status", e);
				}
			}
		}
		catch (final IOException e)
		{
			EvaluatorLoggingHandler.logger.log(Level.WARNING, "AnalysisListener interruped", e);
		}
		finally
		{
			EvaluatorLoggingHandler.logger.info("Finished listening, " + this.completedState + ", " + status);
			progressWindow.setProgressBarText(this.completedState);
			QueryEvaluator.fillProgressBar();
			this.cleanUp();
		}
	}

	public void sendLine(final String status)
	{
		this.sendLine(status, true);
	}

	private void sendLine(final String status, final boolean flush)
	{
		try
		{
			if (this.out == null)
			{
				this.wait();
			}
			this.out.println(status);
			if (flush)
			{
				this.out.flush();
			}
		}
		catch (final InterruptedException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Wait for client object out stream interrupted", e);
		}
	}
}
