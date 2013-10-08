/**
 * SimpleOperationNode
 * 
 * This class is a simplified representation of Performance Tree
 * operation nodes, omitting unnecessary detail and only containing
 * information that is relevant for the evaluation of queries.
 * 
 * @author Tamas Suto
 * @date 11/01/08
 */

package pipe.common;

import java.util.HashMap;

import pipe.common.PetriNetNode;
import pipe.server.performancequery.SimpleNode;

public class SimpleOperationNode extends SimpleNode
{

	private static final long				serialVersionUID	= 1L;
	private String							operation;
	// an optional property that applies to certain nodes only
	private final HashMap<String, String>	children;

	// references to the children nodes. The key of the HashMap is the role that
	// the child
	// node has for the OperationNode and the value is the id reference of the
	// child node

	private SimpleOperationNode(String id, PetriNetNode type, String parent, HashMap<String, String> children) {
		super(id, type, parent);
		this.children = children;
	}

	public SimpleOperationNode(	String id,
								PetriNetNode type,
								String parent,
								HashMap<String, String> children,
								String operation) {
		this(id, type, parent, children);
		this.operation = operation;
	}

	public HashMap<String, String> getChildren()
	{
		return this.children;
	}

	public String getOperation()
	{
		return this.operation;
	}

}
