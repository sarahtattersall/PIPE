/**
 * ClientUpdater
 * 
 * This class is responsible for updating the client by sending the 
 * Strings through the socket connection that it has received from
 * ResultSender, which are in essence the job status messages.
 * 
 * @author Barry Kearns
 * @author Tamas Suto
 * @date 16/01/08
 */

package pipe.server.performancequery;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintWriter;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

import pipe.common.AnalysisInstruction;
import pipe.modules.interfaces.Cleanable;
import pipe.common.LoggingHelper;
import pipe.server.CommunicationsManager;

public class ClientUpdater implements ServerLoggingHandler, Runnable
{
	private PrintWriter					out;
	private BufferedReader				in;
	private final ServerSocketChannel			server = ServerSocketChannel.open();
    private boolean						finished;
	private Cleanable cleaner;
	private final ReentrantLock			sendLock;
	private final int 						port;

	public ClientUpdater() throws IOException {
		// bind ClientUpdater to status port
		port = CommunicationsManager.statusPort;
		CommunicationsManager.safeBind(this.server, port, "Server ClientUpdater"); 
		this.sendLock = new ReentrantLock();
		this.finished = false;
		ServerLoggingHandler.logger.info("ClientUpdater set up successfully");
	}
	
	public void setCleaner(Cleanable clean) {
		cleaner = clean;
	}
	
	public int getPort() {
		return port;
	}

	public void cleanUp()
	{
		try
		{
			if (this.out != null)
				this.out.close();
		}
		catch (final Exception e)
		{
			ServerLoggingHandler.logger.warning(LoggingHelper.getStackTrace(e));
		}
	}

	void finish()
	{
		this.finished = true;
	}

	public void run()
	{
		try
		{
			ServerLoggingHandler.logger.info("Listening for stop requests");
			this.sendLine(AnalysisInstruction.START.toString());
			String s;
			while (!this.finished)
			{
				try
				{
					if ((s = this.in.readLine()) != null &&
						AnalysisInstruction.getFromName(s) == AnalysisInstruction.STOP)
					{
						ServerLoggingHandler.logger.info("Recieved stop request from client");
						this.finish();
						this.cleaner.cleanUp();
					}
				}
				catch (final InterruptedIOException e)
				{
					ServerLoggingHandler.logger.fine("timeout, retrying");
				}
			}
		}
		catch (final IOException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Client updater Thread Exiting...", e);
		}
		finally
		{
			//this.cleanUp();
		}
	}

	public void sendLine(final String status)
	{
		this.sendLock.lock();
		try
		{
			this.sendLine(status, true);
		}
		finally
		{
			this.sendLock.unlock();
		}
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

	public void sendStatusUpdate(final String instruction, final String id, final String status)
	{
		this.sendLock.lock();
		try
		{
			ServerLoggingHandler.logger.info(String.format("sending update %s %s %s", instruction, id, status));
			this.out.flush();
			this.sendLine(instruction, false);
			this.sendLine(id, false);
			this.sendLine(status, true);
		}
		finally
		{
			this.sendLock.unlock();
		}
	}

	public synchronized void waitForConnection() throws IOException
	{
		ServerLoggingHandler.logger.info("Waiting for client connection");
        SocketChannel socketConnection = this.server.accept();
		//this.socketConnection.socket().setSoTimeout(1000);
		this.out = new PrintWriter(Channels.newOutputStream(socketConnection));
		this.notifyAll();
		this.in = new BufferedReader(new InputStreamReader(Channels.newInputStream(socketConnection)));
	}
}
