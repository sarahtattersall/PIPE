/**
 * SimpleNode
 * 
 * This class is a simplified representation of Performance Tree
 * nodes, omitting unnecessary detail and only containing
 * information that is relevant for the evaluation of queries.
 * 
 * @author Tamas Suto
 * @date 11/01/08
 */

package pipe.server.performancequery;

import java.io.Serializable;

import pipe.common.PetriNetNode;

public abstract class SimpleNode implements Serializable
{

	private static final long	serialVersionUID	= 1L;
	private final String			id;						// the node's unique id
	private final PetriNetNode type;						// the type of the
	// node (e.g.
	// PassageTimeDensityNode)
    private final String			parent;					// reference to the ID

	// of the parent node

	protected SimpleNode(String id, PetriNetNode type, String parent) {
		this.id = id;
		this.type = type;
		this.parent = parent;
	}

	public String getID()
	{
		return this.id;
	}

	public String getParent()
	{
		return this.parent;
	}

	public PetriNetNode getType()
	{
		return this.type;
	}

}
