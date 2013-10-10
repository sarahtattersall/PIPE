/**
 * 
 */
package pipe.modules.queryresult;

import java.io.Serializable;
import java.util.logging.Level;

import pipe.common.PetriNetNode;
import pipe.server.performancequery.nodeanalyser.ValueNodeAnalyser;

/**
 * @author dazz
 * 
 */
public abstract class ResultWrapper implements Serializable, Cloneable, QueryResultLoggingHandler
{
	private String			nodeID;
	private PetriNetNode type;
	private final PetriNetNode orginalType;
	private final int		hash;

	ResultWrapper(final String nodeID, final PetriNetNode type) {
		this.nodeID = nodeID;
		this.orginalType = this.type = type;
		this.hash = nodeID.hashCode() + type.hashCode();
	}

	/**
	 * Perform Shallow copy of this and change nodeID and type
	 * 
	 * @param nodeID
	 * @param type
	 * @return
	 */
	public ResultWrapper copyData(final String nodeID, final PetriNetNode type)
	{
		ResultWrapper r = null;
		try
		{
			r = (ResultWrapper) this.clone();
			r.nodeID = nodeID;
			r.type = type;
			return r;
		}
		catch (final CloneNotSupportedException e)
		{
			QueryResultLoggingHandler.logger.log(Level.WARNING, "Couldn't clone Result Wrapper object", e);
		}
		return r;
	}

	/**
	 * @return the nodeID
	 */
	public String getNodeID()
	{
		return this.nodeID;
	}

	/**
	 * @return the orginalType
	 */
	public PetriNetNode getOrginalType()
	{
		return this.orginalType;
	}

	public abstract ValueNodeAnalyser getResult();

	/**
	 * @return the type
	 */
	public PetriNetNode getType()
	{
		return this.type;
	}

	@Override
	public int hashCode()
	{
		return this.hash;
	}

}
