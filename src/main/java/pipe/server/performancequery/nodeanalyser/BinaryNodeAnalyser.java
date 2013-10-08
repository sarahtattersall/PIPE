/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import pipe.common.PetriNetNode;

/**
 * @author dazz
 * 
 */
public abstract class BinaryNodeAnalyser extends CalculationNodeAnalyser
{

	private NodeAnalyser	lhs, rhs;

	BinaryNodeAnalyser(final PetriNetNode type, final NodeAnalyser lhs, final NodeAnalyser rhs) throws InvalidNodeAnalyserException {
		super(type);
		this.lhs = this.checkChildValid(lhs);
		this.rhs = this.checkChildValid(rhs);
	}

	@Override
    protected boolean canEvaluate()
	{
		return this.lhs.canEvaluate() && this.rhs.canEvaluate();
	}

	/**
	 * @return the lhs
	 */
    NodeAnalyser getLhs()
	{
		return this.lhs;
	}

	/**
	 * @return the rhs
	 */
    NodeAnalyser getRhs()
	{
		return this.rhs;
	}

	/**
	 * @param lhs
	 *            the lhs to set
	 */
	protected void setLhs(final NodeAnalyser lhs)
	{
		this.lhs = lhs;
	}

	/**
	 * @param rhs
	 *            the rhs to set
	 */
	protected void setRhs(final NodeAnalyser rhs)
	{
		this.rhs = rhs;
	}

}
