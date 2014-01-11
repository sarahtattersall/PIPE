/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import pipe.common.PTArithOp;
import pipe.common.PetriNetNode;

/**
 * @author dazz
 * 
 */
public class ArithOp extends BinaryNodeAnalyser
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 7005807209519118999L;
	private final PTArithOp		operation;

	public ArithOp(final NodeAnalyser lhs, final NodeAnalyser rhs, final PTArithOp operation) throws InvalidNodeAnalyserException {
		super(PetriNetNode.ARITHOP, lhs, rhs);
		this.operation = operation;
	}

	// TODO implement calc
	@Override
	public ValueNodeAnalyser calculate() throws InvalidNodeAnalyserException
	{
		if (this.canEvaluate())
		{
			NumNode result;
			NumNode lhs, rhs;
			lhs = (NumNode) this.getLhs();
			rhs = (NumNode) this.getRhs();
			double lvalue, rvalue;
			lvalue = ((NumNode) lhs.calculate()).getValue();
			rvalue = ((NumNode) rhs.calculate()).getValue();
			switch (this.operation)
			{
				case PLUS :
					result = new NumNode(lvalue + rvalue);
					break;
				case MINUS :
					result = new NumNode(lvalue - rvalue);
					break;
				case MULTIPLY :
					result = new NumNode(lvalue * rvalue);
					break;
				case DIVIDE :
					result = new NumNode(lvalue / rvalue);
					break;
				case POWER :
					result = new NumNode(Math.pow(lvalue, rvalue));
					break;
				default :
					throw new InvalidNodeAnalyserException("ArithOP operation not of correct type " +
															this.operation);
			}
			return result;
		}
		else
		{
			throw new InvalidNodeAnalyserException("ArithOp " + this.operation +
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
