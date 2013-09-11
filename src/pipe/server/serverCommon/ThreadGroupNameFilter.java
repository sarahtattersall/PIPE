/**
 * 
 */
package pipe.server.serverCommon;

import java.util.logging.Filter;
import java.util.logging.LogRecord;

/**
 * @author dazz
 * 
 */
public class ThreadGroupNameFilter implements Filter
{
	private final String	mainThreadName;

	public ThreadGroupNameFilter() {
		this.mainThreadName = Thread.currentThread().getThreadGroup().getName();
	}

	public ThreadGroupNameFilter(final String name) {
		this.mainThreadName = name;
	}

	public boolean isLoggable(final LogRecord record)
	{
		return Thread.currentThread().getThreadGroup().getName().equals(this.mainThreadName);
	}
}
