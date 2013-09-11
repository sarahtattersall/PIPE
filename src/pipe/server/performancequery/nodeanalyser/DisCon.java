/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import pipe.common.PTDisCon;
import pipe.common.PetriNetNode;

/**
 * @author dazz
 * 
 */
public class DisCon extends BinaryNodeAnalyser
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8007772695956162843L;
	private final PTDisCon		discon;

	public DisCon(final NodeAnalyser lhs, final NodeAnalyser rhs, final PTDisCon discon) throws InvalidNodeAnalyserException {
		super(PetriNetNode.DISCON, lhs, rhs);
		this.discon = discon;
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
			boolean value;
			BoolNode lhs, rhs;
			lhs = (BoolNode) this.getLhs().calculate();
			rhs = (BoolNode) this.getRhs().calculate();
			boolean lvalue, rvalue;
			lvalue = lhs.getValue();
			rvalue = rhs.getValue();
			switch (this.discon)
			{
				case CONJUNCTION :
					value = lvalue && rvalue;
					break;
				case DISJUNCTION :
					value = lvalue || rvalue;
					break;
				default :
					throw new InvalidNodeAnalyserException("discon not Conj or Disj??");
			}
			return new BoolNode(value);
		}
		else
		{
			throw new InvalidNodeAnalyserException("Children of Discon not Evaluated yet");
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
