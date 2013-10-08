/**
 * 
 */
package pipe.modules.queryeditor.evaluator;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.util.logging.Level;

import pipe.modules.interfaces.Cleanable;

/**
 * @author dazz
 * 
 */
public class InterruptableSocketIO implements EvaluatorLoggingHandler, Cleanable
{

	private final String				server;
	private final int					portNo;
	private final SocketChannel			socketConnection;

	private ObjectInputStream			in;
	private final ObjectOutputStream	out;

	public InterruptableSocketIO(final String host, final int port) throws IOException {
		this.server = host;
		this.portNo = port;
		this.socketConnection = SocketChannel.open(new InetSocketAddress(this.server, this.portNo));
		this.out = new ObjectOutputStream(Channels.newOutputStream(this.socketConnection));
		this.in = null;
	}

	public void cleanUp()
	{
		try
		{
			if (this.socketConnection.isOpen())
			{
				this.socketConnection.close();
			}
		}
		catch (final IOException e)
		{
			EvaluatorLoggingHandler.logger.log(Level.WARNING, "Couldn't cleanup all resources", e);
		}
	}

	public void finish()
	{
	}

	/**
	 * @return the portNo
	 */
	public int getPortNo()
	{
		return this.portNo;
	}

	/**
	 * @return the server
	 */
	public String getServer()
	{
		return this.server;
	}

	public boolean isOpen()
	{
		return this.socketConnection.isOpen();
	}

	public Object readObject() throws IOException, ClassNotFoundException
	{
		try
		{
			if (this.in == null)
			{
				this.in = new ObjectInputStream(Channels.newInputStream(this.socketConnection));
			}
			return this.in.readObject();
		}
		catch (final IOException e)
		{
			this.cleanUp();
			throw e;
		}
		catch (final ClassNotFoundException e)
		{
			this.cleanUp();
			throw e;
		}
	}

	public void sendObject(final Object sendObj)
	{
		try
		{
			this.out.writeObject(sendObj);
			this.out.flush();
		}
		catch (final IOException e)
		{
			EvaluatorLoggingHandler.logger.log(Level.WARNING, "Couldn't send object " + sendObj.toString(), e);
		}

	}

	public void setSoTimeout(final int millis) throws IOException
	{
		this.socketConnection.socket().setSoTimeout(millis);
	}
}
