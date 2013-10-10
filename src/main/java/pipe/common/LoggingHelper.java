/**
 * 
 */
package pipe.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.SocketTimeoutException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.SocketHandler;

import pipe.handlers.SoftExceptionHandler;
import pipe.server.serverCommon.ThreadGroupNameFilter;

/**
 * @author dazz
 * 
 */
public class LoggingHelper
{
	public class SocketHandlerConnector extends Thread
	{

		private final String	host;
		private final int		port;
		private final Logger	logger;
		private final Level		level;
		private final String	threadGroupName;

		public SocketHandlerConnector(	final String host,
										final int port,
										final Logger logger,
										final Level level,
										final String threadGroupName) {
			this.host = host;
			this.port = port;
			this.level = level;
			this.logger = logger;
			this.threadGroupName = threadGroupName;
			this.setUncaughtExceptionHandler(new SoftExceptionHandler(logger));
		}

		@Override
		public void run()
		{
			try
			{
				this.logger.info("Setting up socket logging...");

				final SocketHandler sh = new SocketHandler(this.host, this.port);
				sh.setFilter(new ThreadGroupNameFilter(this.threadGroupName));
				final LogManager l = LogManager.getLogManager();
				l.addLogger(this.logger);

				sh.setFormatter(new SimpleFormatter());
				sh.setLevel(this.level);

				this.logger.addHandler(sh);
				this.logger.info("Logging Socket is up");
			}
			catch (final SocketTimeoutException e)
			{
				this.logger.log(Level.WARNING, "Logging Socket Connection to Client Timed out", e);
			}
			catch (final IOException e)
			{
				this.logger.log(Level.WARNING, "Logging Socket IO exception", e);
			}
		}
	}

	public static String getStackTrace(final Throwable e)
	{
		final OutputStream buf = new ByteArrayOutputStream();
		final PrintStream p = new PrintStream(buf);
		e.printStackTrace(p);
		return buf.toString();
	}

	public static void setupConsoleLogging(final Logger logger, final Level level) throws SecurityException
	{
		// we only need 1 console handler!
		for (final Handler h : logger.getHandlers())
		{
			if (h instanceof ConsoleHandler)
			{
				return;
			}
		}
		final ConsoleHandler ch = new ConsoleHandler();
		ch.setFilter(new ThreadGroupNameFilter());

		final LogManager l = LogManager.getLogManager();
		l.addLogger(logger);

		ch.setFormatter(new SimpleFormatter());
		ch.setLevel(level);

		logger.addHandler(ch);
	}

	public static void setupFileLogging(final String filePath, final Logger logger, final Level level)	throws IOException,
																										SecurityException
	{
		LoggingHelper.setupFileLogging(filePath, logger, level, null);
	}

	private static void setupFileLogging(final String filePath,
                                         final Logger logger,
                                         final Level level,
                                         final Filter filter) throws IOException, SecurityException
	{
		final FileHandler fh = new FileHandler(filePath, true);
		fh.setFilter(filter == null ? new ThreadGroupNameFilter() : filter);
		final LogManager l = LogManager.getLogManager();
		l.addLogger(logger);

		fh.setFormatter(new SimpleFormatter());
		fh.setLevel(level);

		logger.addHandler(fh);

	}

	public static Runnable startSocketLogging(	final String host,
												final int port,
												final Logger logger,
												final Level level)
	{
		return new LoggingHelper().new SocketHandlerConnector(	host,
																port,
																logger,
																level,
																Thread	.currentThread()
																		.getThreadGroup()
																		.getName());
	}

}
