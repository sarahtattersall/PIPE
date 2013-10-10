/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import pipe.common.PetriNetNode;

/**
 * @author dazz
 * 
 */
public class Negation extends UnaryNodeAnalyser
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -4654435017160927510L;

	public Negation(final NodeAnalyser child) throws InvalidNodeAnalyserException {
		super(PetriNetNode.NEGATION, child);
	}

	@Override
	public ValueNodeAnalyser calculate() throws InvalidNodeAnalyserException
	{
		if (this.canEvaluate())
		{
			final NodeAnalyser child = this.getChild();
			boolean value = ((BoolNode) child.calculate()).getValue();
			return new BoolNode(!value);
		}
		else
		{
			throw new InvalidNodeAnalyserException("Negation child not BoolNode");
		}
	}

	@Override
	protected NodeAnalyser checkChildValid(final NodeAnalyser child) throws InvalidNodeAnalyserException
	{
		switch (child.getType())
		{

			case ININTERVAL :
			case DISCON :
			case ARITHCOMP :
			case NEGATION :
			case BOOL :
			case MACRO :
				return child;

			default :
				throw new InvalidNodeAnalyserException(child.getType() +
														" doesn't return type bool node, Can't create range node");
		}
	}
}
