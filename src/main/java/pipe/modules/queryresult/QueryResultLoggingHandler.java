
package pipe.modules.queryresult;

import java.util.logging.Logger;

public interface QueryResultLoggingHandler
{
	public static final String	pipeCommonQResult	= "pipe.modules.queryresult";
	public static Logger		logger				= Logger.getLogger(QueryResultLoggingHandler.pipeCommonQResult);
}
