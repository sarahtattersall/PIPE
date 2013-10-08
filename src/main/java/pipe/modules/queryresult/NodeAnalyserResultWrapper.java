/**
 * 
 */
package pipe.modules.queryresult;

import java.io.Serializable;

import pipe.common.PetriNetNode;
import pipe.server.performancequery.nodeanalyser.ValueNodeAnalyser;

/**
 * @author dazz
 * 
 */
public class NodeAnalyserResultWrapper extends ResultWrapper implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8456161540976402177L;
	private final ValueNodeAnalyser			result;

	public NodeAnalyserResultWrapper(final ValueNodeAnalyser result, final String nodeID, final PetriNetNode type) {
		super(nodeID, type);
		this.result = result;
	}

	/**
	 * @return the result
	 */
	@Override
	public ValueNodeAnalyser getResult()
	{
		return this.result;
	}

}
