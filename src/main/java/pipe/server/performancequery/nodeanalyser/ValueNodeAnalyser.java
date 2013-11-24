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
public abstract class ValueNodeAnalyser extends NodeAnalyser implements Serializable
{

	ValueNodeAnalyser(final PetriNetNode type) {
		super(type);
	}

	@Override
	public ValueNodeAnalyser calculate() throws InvalidNodeAnalyserException
	{
		return this;
	}

	@Override
	public boolean canEvaluate()
	{
		return true;
	}

	@Override
	protected NodeAnalyser checkChildValid(final NodeAnalyser child) throws InvalidNodeAnalyserException
	{
		throw new UnsupportedOperationException("This method is invalid for value nodes");
	}
}
