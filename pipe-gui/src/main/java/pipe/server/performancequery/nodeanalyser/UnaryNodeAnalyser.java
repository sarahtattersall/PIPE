/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import pipe.common.PetriNetNode;

/**
 * @author dazz
 * 
 */
public abstract class UnaryNodeAnalyser extends CalculationNodeAnalyser
{

	private final NodeAnalyser	child;

	UnaryNodeAnalyser(final PetriNetNode type, final NodeAnalyser child) throws InvalidNodeAnalyserException {
		super(type);
		this.child = this.checkChildValid(child);
	}

	@Override
    protected boolean canEvaluate()
	{
		return this.child.canEvaluate();
	}

	/**
	 * @return the child
	 */
    NodeAnalyser getChild()
	{
		return this.child;
	}
}
