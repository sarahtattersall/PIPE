package pipe.server.performancequery;

import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

class ResultSender implements ServerLoggingHandler
{
	private final SocketChannel	clientConnection;

	private ObjectOutputStream	out	= null;

	/**
	 * Constructor that checks the log file, as well as sends results over to
	 * the client
     * @param clientConnection
     * @throws java.io.IOException
     */
	public ResultSender(final SocketChannel clientConnection) throws IOException {
		this.clientConnection = clientConnection;
		this.out = new ObjectOutputStream(Channels.newOutputStream(this.clientConnection));
	}

	public void cleanUp()
	{
		try
		{
			if (this.clientConnection != null && this.clientConnection.isOpen())
			{
				this.clientConnection.close();
			}
		}
		catch (final IOException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Couldn't close socket", e);
		}
	}

	void deleteDirectory(final String dirPath)
	{
		final File clearDir = new File(dirPath);
		final String children[] = clearDir.list();
		for (final String element : children)
		{
			final File tmp = new File(clearDir, element);
			if (tmp.isDirectory())
			{
				this.deleteDirectory(tmp.getAbsolutePath());
			}
			else
			{
				tmp.delete();
			}
		}
		clearDir.delete();
	}

	public synchronized void sendObject(final Object obj) throws IOException
	{
		try
		{
			if (obj != null)
			{
				ServerLoggingHandler.logger.info("Sending Object");
				this.out.writeObject(obj);
				this.out.flush();
			}
		}
		catch (final IOException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Problem sending object " + obj.toString());
			this.cleanUp();
			throw e;

		}
	}
}
