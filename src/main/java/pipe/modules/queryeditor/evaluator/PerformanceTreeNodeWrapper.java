/**
 * PerformanceTreeNodeConverter
 * 
 * This class extracts the necessary info from PerformanceTreeNodes 
 * and creates simplified data structures from them to be used by the
 * server.
 * 
 * @author Tamas Suto
 * @date 11/01/08
 */

package pipe.modules.queryeditor.evaluator;

import java.util.HashMap;
import java.util.Iterator;

import pipe.common.PetriNetNode;
import pipe.common.SimpleOperationNode;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroDefinition;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.OperationNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ValueNode;
import pipe.server.performancequery.SimpleNode;
import pipe.server.performancequery.SimpleValueNode;

class PerformanceTreeNodeWrapper
{

	private final PerformanceTreeNode	node;		// the node being converted
	private final String				id;		// the node's unique id
	private final PetriNetNode type;		// the type of the node
	// (e.g.
	// PassageTimeDensityNode)
	private String						parent;	// reference to the ID of the
	// parent
	// node

	// for OperationNodes
	private HashMap<String, String>		children;	// children of an operation
	// node

// private HashMap<String, PerformanceTreeNode> kids;
// ArrayList<PerformanceTreeNode> ks ;

	private String						operation;	// operation of an operation
	// node
	private final String				macroName;	// the macro that the node
	// belongs to (if that is the
	// case)

	// for ValueNodes
	private String						value;		// the value of a ValueNode

	public PerformanceTreeNodeWrapper(final PerformanceTreeNode ptNode, final String macroName) {
		this.node = ptNode;
		this.id = this.node.getId();
		this.type = this.node.getNodeType();
		this.setParent(this.node);

		if (this.node instanceof OperationNode)
		{
			this.setChildren((OperationNode) this.node);

			// look if an operation is defined for the node
			if (((OperationNode) this.node).getOperation() != null &&
				!((OperationNode) this.node).getOperation().equals(""))
			{
				this.operation = ((OperationNode) this.node).getOperation();
// ks = new ArrayList<PerformanceTreeNode>(opNode.getChildNodes());
// for (int i = 0 ; i < ks.size(); i++){
// if (ks.get(i) == null){
// ks.remove(i);
// }
// }
			}
		}
		else if (this.node instanceof ValueNode)
		{
			if (((ValueNode) this.node).getNodeLabel() != null &&
				!((ValueNode) this.node).getNodeLabel().equals(""))
			{
				this.value = ((ValueNode) this.node).getNodeLabel();
			}
		}

		this.macroName = macroName;
	}

	public SimpleNode convertToSimpleNode()
	{
		SimpleNode simpleNode = null;
		if (this.node instanceof OperationNode)
		{
			simpleNode = new SimpleOperationNode(	this.id,
													this.type,
													this.parent,
													this.children,
													this.operation);
		}
		else if (this.node instanceof ValueNode)
		{
			simpleNode = new SimpleValueNode(this.id, this.type, this.parent, this.value);
		}
		return simpleNode;
	}

	private void setChild(final String role, final String childID)
	{
		if (role != null && childID != null)
		{
			this.children.put(role, childID);
// for(PerformanceTreeNode n : ks){
// if (n.getId().equals(childID)){
// kids.put(role, n);
// System.out.println("Added " + role + " " + childID);
// }
// }
		}
	}

	private void setChildren(final OperationNode opNode)
	{
		this.children = new HashMap<String, String>();
		String childRole, childID;
		final Iterator<String> i = opNode.getOutgoingArcIDs().iterator();
		while (i.hasNext())
		{
			final String arcID = i.next();
			if (this.macroName == null)
			{
				// it's an OperationNode from QueryData
				childRole = QueryManager.getData().getArc(arcID).getArcLabel();
				childID = QueryManager.getData().getArc(arcID).getTargetID();
				this.setChild(childRole, childID);
			}
			else
			{
				// it's a macro's OperationNode
				final MacroDefinition macro = QueryManager.getData().getMacro(this.macroName);
				childRole = macro.getMacroArc(arcID).getArcLabel();
				childID = macro.getMacroArc(arcID).getTargetID();
				this.setChild(childRole, childID);
			}
		}
	}

	void setParent(final PerformanceTreeNode ptNode)
	{
		if (ptNode.getIncomingArcID() != null)
		{
			if (this.macroName == null)
			{
				// it's a node from QueryData
				this.parent = QueryManager.getData().getArc(ptNode.getIncomingArcID()).getSourceID();
			}
			else
			{
				// it's a macro's node
				final MacroDefinition macro = QueryManager.getData().getMacro(this.macroName);
				this.parent = macro.getMacroArc(ptNode.getIncomingArcID()).getSourceID();
			}
		}
		else
		{
			// Result or Macro node
			this.parent = "";
		}
	}

}
