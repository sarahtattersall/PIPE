/**
 * 
 */
package pipe.server.performancequery;

import java.lang.Thread.UncaughtExceptionHandler;

import pipe.modules.interfaces.Cleanable;
import pipe.common.LoggingHelper;

/**
 * @author dazz
 * 
 */
class ServerExceptionHandler implements UncaughtExceptionHandler, ServerLoggingHandler
{

	private final Cleanable cleaner;

	public ServerExceptionHandler(final Cleanable cleaner) {
		this.cleaner = cleaner;
	}

	void handleException(final Thread arg0, final Throwable arg1, final int callCount)
	{
		if (callCount == 0)
		{
			ServerLoggingHandler.logger.severe("Uncaught Exception in thread:" + arg0.getName() + " id:" +
												arg0.getId() + " handled by ServerExceptionHandler");
			ServerLoggingHandler.logger.severe(LoggingHelper.getStackTrace(arg1));
		}
		this.cleaner.cleanUp();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread,
	 *      java.lang.Throwable)
	 */
	public void uncaughtException(final Thread arg0, final Throwable arg1)
	{
		this.handleException(arg0, arg1, 0);
	}
}
