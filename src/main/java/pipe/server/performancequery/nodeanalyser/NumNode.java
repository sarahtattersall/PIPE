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
public class NumNode extends ValueNodeAnalyser implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3765447657491758628L;
	private final double		value;

	public NumNode(final double value) {
		super(PetriNetNode.NUM);
		this.value = value;
	}

	/**
	 * @return the value
	 */
	public double getValue()
	{
		return this.value;
	}
}
