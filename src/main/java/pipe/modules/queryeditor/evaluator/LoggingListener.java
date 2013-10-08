/**
 * 
 */
package pipe.modules.queryeditor.evaluator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

import pipe.modules.interfaces.Cleanable;
import pipe.server.CommunicationsManager;

/**
 * @author dazz
 * 
 */
public class LoggingListener extends Thread implements Cleanable, EvaluatorLoggingHandler
{
	private ServerSocketChannel	server;

	private boolean				notFinished	= true;
	private final int			port;

    private SocketChannel		clientConnection;

	public LoggingListener(final int port) {
		this.port = port;
		try
		{
			this.server = ServerSocketChannel.open();
			
			CommunicationsManager.safeBind(this.server, port, "Client LoggingListener"); 
		}
		catch (IOException e)
		{
			EvaluatorLoggingHandler.logger.log(	Level.WARNING,
												"Couldn't set up Server Socket on client, port no:" + port,
												e);
		}
	}

	public void cleanUp()
	{
		try
		{
			if (this.clientConnection != null)
			{
				EvaluatorLoggingHandler.logger.info("Logging Listener: Closing Client Connection");
				this.clientConnection.close();
			}
			if (this.server != null)
			{
				EvaluatorLoggingHandler.logger.info("Logging Listener: Closing Socket Server");
				this.server.close();
			}

		}
		catch (final IOException e)
		{
			EvaluatorLoggingHandler.logger.log(Level.WARNING, "Problem on Socket, port no:" + this.port, e);
		}
	}

	public void finish()
	{
		this.notFinished = false;
	}

	public boolean isRunning()
	{
		return this.notFinished;
	}

	@Override
	public void run()
	{
		try
		{
			EvaluatorLoggingHandler.logger.info("Waiting for log info from server on port " + this.port);

			this.clientConnection = this.server.accept();

			EvaluatorLoggingHandler.logger.info("Incoming Logging request from server accepted");

            BufferedReader in = new BufferedReader(new InputStreamReader(Channels.newInputStream(this.clientConnection)));
			String line;
			while ((line = in.readLine()) != null && this.notFinished)
			{
				System.out.println(line);
			}
			this.cleanUp();
		}
		catch (final SocketTimeoutException e)
		{
			EvaluatorLoggingHandler.logger.log(	Level.WARNING,
												"Wait for server logging connection timed out",
												e);
		}
		catch (final IOException e)
		{
			EvaluatorLoggingHandler.logger.log(Level.WARNING, "Problem on Socket, port no:" + this.port, e);
		}
		finally
		{
			this.cleanUp();
		}
		EvaluatorLoggingHandler.logger.info("Logging Listener: Ending...");

	}
}
