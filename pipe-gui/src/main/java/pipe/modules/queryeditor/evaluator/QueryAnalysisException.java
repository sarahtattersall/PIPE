/**
 * 
 */
package pipe.modules.queryeditor.evaluator;

import pipe.common.LoggingHelper;

/**
 * @author dazz
 * 
 */
public class QueryAnalysisException extends Exception
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7278427210650858233L;

	public QueryAnalysisException(final String msg) {
		super(msg);
	}

	public String stackTraceToString()
	{
		return LoggingHelper.getStackTrace(this);
	}
}
