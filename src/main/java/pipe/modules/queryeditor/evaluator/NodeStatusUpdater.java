/**
 * 
 */
package pipe.modules.queryeditor.evaluator;

import java.io.Serializable;

import pipe.common.EvaluationStatus;
import pipe.modules.interfaces.QueryConstants;

/**
 * @author dazz
 * 
 */
public class NodeStatusUpdater implements Serializable, EvaluatorLoggingHandler
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3619437248080262475L;

	private EvaluationStatus	status;

	private final String		nodeID;

	public NodeStatusUpdater(final EvaluationStatus status, final String nodeID) {
		this.nodeID = nodeID;
		this.status = status;
	}

	public NodeStatusUpdater(final String status, final String nodeID) throws QueryAnalysisException {
		this.nodeID = nodeID;
		if (status.equals(QueryConstants.EVALNOTSUPPORTED))
		{
			this.status = EvaluationStatus.EVALNOTSUPPORTED;
		}
		else if (status.equals(QueryConstants.EVALINPROGRESS))
		{
			this.status = EvaluationStatus.EVALINPROGRESS;
		}
		else if (status.equals(QueryConstants.EVALCOMPLETE))
		{
			this.status = EvaluationStatus.EVALCOMPLETE;
		}
		else if (status.equals(QueryConstants.EVALNOTSTARTED))
		{
			this.status = EvaluationStatus.EVALNOTSTARTED;
		}
		else if (status.equals(QueryConstants.EVALFAILED))
		{
			this.status = EvaluationStatus.EVALFAILED;
		}
		else
		{
			throw new QueryAnalysisException("Invalid argument:" + status +
												" passed to NodeStatusUpdater for node:" + nodeID);
		}
	}

	public EvaluationStatus getEvalStatus()
	{
		return this.status;
	}

	public String getId()
	{
		return this.nodeID;
	}

}
