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
class BoolNode extends ValueNodeAnalyser implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -2020391809902951868L;
	private final boolean		value;

	public BoolNode(final boolean value) {
		super(PetriNetNode.BOOL);
		this.value = value;
	}

	public boolean getValue()
	{
		return this.value;
	}
}
