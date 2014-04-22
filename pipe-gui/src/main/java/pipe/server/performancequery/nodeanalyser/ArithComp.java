/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import pipe.common.PTArithComp;
import pipe.common.PetriNetNode;

/**
 * @author dazz
 * 
 */
public class ArithComp extends BinaryNodeAnalyser
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -8327961131807675249L;
	private final PTArithComp	operation;

	public ArithComp(final NodeAnalyser lhs, final NodeAnalyser rhs, final PTArithComp operation) throws InvalidNodeAnalyserException {
		super(PetriNetNode.ARITHCOMP, lhs, rhs);
		this.operation = operation;
	}

	// TODO implement calc
	@Override
	public ValueNodeAnalyser calculate() throws InvalidNodeAnalyserException
	{
		if (this.canEvaluate())
		{
			BoolNode result;
			NumNode lhs, rhs;
			lhs = (NumNode) this.getLhs();
			rhs = (NumNode) this.getRhs();
			double lvalue, rvalue;
			lvalue = ((NumNode) lhs.calculate()).getValue();
			rvalue = ((NumNode) rhs.calculate()).getValue();
			switch (this.operation)
			{
				case LESS :
					result = new BoolNode(lvalue < rvalue);
					break;
				case LEQ :
					result = new BoolNode(lvalue <= rvalue);
					break;
				case EQ :
					result = new BoolNode(lvalue == rvalue);
					break;
				case GEQ :
					result = new BoolNode(lvalue >= rvalue);
					break;
				case GREATER :
					result = new BoolNode(lvalue > rvalue);
					break;
				default :
					throw new InvalidNodeAnalyserException("Arith Comp operation not of correct type " +
															this.operation);
			}
			return result;
		}
		else
		{
			throw new InvalidNodeAnalyserException("ArithComp " + this.operation +
													" Children not evaluated yet");
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
				return child;
			default :
				throw new InvalidNodeAnalyserException(child.getType() +
														" doesn't return type num node, Can't create range node");
		}
	}

}
