/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import pipe.common.PetriNetNode;

import java.io.Serializable;

/**
 * @author dazz
 * 
 */
public abstract class NodeAnalyser implements NodeAnalyserLoggingHandler, Serializable
{
	private final PetriNetNode type;

	NodeAnalyser(final PetriNetNode type) {
		this.type = type;
	}

	public abstract ValueNodeAnalyser calculate() throws InvalidNodeAnalyserException;

	protected abstract boolean canEvaluate();

	protected abstract NodeAnalyser checkChildValid(final NodeAnalyser child) throws InvalidNodeAnalyserException;

	/**
	 * @return the type
	 */
    PetriNetNode getType()
	{
		return this.type;
	}
}
