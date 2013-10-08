/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import java.io.Serializable;

import pipe.common.PetriNetNode;

/**
 * @author dazz
 * 
 */
public class RangeNode extends ValueNodeAnalyser implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 6588015336211739426L;
	private final NodeAnalyser	lhs, rhs;

	public RangeNode(final NodeAnalyser lhs, final NodeAnalyser rhs) throws InvalidNodeAnalyserException {
		super(PetriNetNode.RANGE);

		this.lhs = this.checkChildValid(lhs);
		this.rhs = this.checkChildValid(rhs);

		if (((NumNode) lhs).getValue() > ((NumNode) rhs).getValue())
			throw new InvalidNodeAnalyserException("LHS of range is >= RHS");
	}

	@Override
	public ValueNodeAnalyser calculate() throws InvalidNodeAnalyserException
	{
		if (this.canEvaluate())
			return super.calculate();
		else throw new InvalidNodeAnalyserException("Can't Evaluate children not evaluated yet");
	}

	@Override
	public boolean canEvaluate()
	{
		return this.lhs.canEvaluate() && this.rhs.canEvaluate();
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
			case ACTIONS :
			case ARGUMENT :
			case MACRO :
			case NUM :
				return child;
			default :
				throw new InvalidNodeAnalyserException(child.getType() +
														" doesn't return type num node, Can't create range node");
		}
	}

	/**
	 * @return the value
     * @throws InvalidNodeAnalyserException
	 */
	public double getFinish() throws InvalidNodeAnalyserException
	{
		return ((NumNode) this.rhs.calculate()).getValue();
	}

	/**
	 * @return the lhs
	 */
	public NodeAnalyser getLhs()
	{
		return this.lhs;
	}

	/**
	 * @return the rhs
	 */
	public NodeAnalyser getRhs()
	{
		return this.rhs;
	}

	/**
	 * @return the value
     * @throws InvalidNodeAnalyserException
	 */
	public double getStart() throws InvalidNodeAnalyserException
	{
		return ((NumNode) this.lhs.calculate()).getValue();
	}
}
