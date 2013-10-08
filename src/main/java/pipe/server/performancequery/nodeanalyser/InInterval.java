/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import pipe.common.PetriNetNode;

/**
 * @author dazz
 * 
 */
public class InInterval extends BinaryNodeAnalyser
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5908148808857718537L;

	public InInterval(final NodeAnalyser lhs, final NodeAnalyser rhs) throws InvalidNodeAnalyserException {
		super(PetriNetNode.ININTERVAL, lhs, rhs);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pipe.server.performancequery.nodeanalyser.CalculationNodeAnalyser#calculate()
	 */
	@Override
	public ValueNodeAnalyser calculate() throws InvalidNodeAnalyserException
	{
		if (this.canEvaluate())
		{
			final NumNode lhs = (NumNode) this.getLhs().calculate();
			final RangeNode rhs = (RangeNode) this.getRhs().calculate();
			final double rhsStart = rhs.getStart();
			final double rhsFinish = rhs.getFinish();

			NodeAnalyserLoggingHandler.logger.info("lhs.getValue() >= rhsStart" +
													String.valueOf(lhs.getValue() >= rhsStart) +
													" lhs.getValue() <= rhsFinish" +
													String.valueOf(lhs.getValue() <= rhsFinish));
			return new BoolNode(lhs.getValue() >= rhsStart && lhs.getValue() <= rhsFinish);
		}
		else
		{
			throw new InvalidNodeAnalyserException("InInterval Children not evaluated yet");
		}
	}

	@Override
	protected NodeAnalyser checkChildValid(final NodeAnalyser child) throws InvalidNodeAnalyserException
	{
		switch (child.getType())
		{
			case PROBININTERVAL :
			case PROBINSTATES :
			case MOMENT :
			case FIRINGRATE :
			case STEADYSTATEPROB :
			case PERCENTILE :
			case ARITHOP :
			case MACRO :
			case NUM :
			case RANGE :
				return child;
			default :
				throw new InvalidNodeAnalyserException(child.getType() +
														" doesn't return type num node, Can't create range node");
		}
	}

}
