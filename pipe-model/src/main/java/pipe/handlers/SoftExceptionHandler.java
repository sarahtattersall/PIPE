/**
 * 
 */
package pipe.handlers;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author dazz
 * 
 */
public class SoftExceptionHandler implements UncaughtExceptionHandler
{
	private final Logger	logger;

	public SoftExceptionHandler(final Logger logger) {
		this.logger = logger;
	}

	public void uncaughtException(final Thread arg0, final Throwable arg1)
	{
		this.logger.log(Level.WARNING, "Soft Exception handler thread caught unhandled exeception", arg1);
	}

}
