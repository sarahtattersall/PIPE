/**
 * SimpleValueNode
 * 
 * This class is a simplified representation of Performance Tree
 * value nodes, omitting unnecessary detail and only containing
 * information that is relevant for the evaluation of queries.
 * 
 * @author Tamas Suto
 * @date 11/01/08
 */

package pipe.server.performancequery;

import pipe.common.PetriNetNode;

public class SimpleValueNode extends SimpleNode
{

	private static final long	serialVersionUID	= 1L;
	private final String		value;						// the value

	// that

	// the node
	// represents

	public SimpleValueNode(String id, PetriNetNode type, String parent, String value) {
		super(id, type, parent);
		this.value = value;
	}

	public String getValue()
	{
		return this.value;
	}

}
