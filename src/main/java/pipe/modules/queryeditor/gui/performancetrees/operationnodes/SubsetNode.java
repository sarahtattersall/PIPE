/**
 * SubsetNode
 * 
 * Represents a boolean value, indicating whether a set is a subset of another
 * 
 * @author Tamas Suto
 * @date 23/05/07
 */

package pipe.modules.queryeditor.gui.performancetrees.operationnodes;

import java.util.ArrayList;
import java.util.Iterator;

import pipe.common.PetriNetNode;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroEditor;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;

public class SubsetNode extends OperationNode
{

	public SubsetNode(final double positionXInput, final double positionYInput, final String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}

	public SubsetNode(final double positionXInput, final double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}

	private void initialiseNode()
	{
		// set name and node type
		setName("SubsetNode");
		setNodeType(PetriNetNode.SUBSET);

		// only one subnode
		setRequiredArguments(2);

		// and nothing else
		setMaxArguments(2);

		// set up required arguments of node
		initialiseRequiredChildNodes();

		// set return type
		setReturnType(QueryConstants.BOOL_TYPE);

		// indicate that we want labels on the arcs
		this.showArcLabels = true;

		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}

	private void initialiseRequiredChildNodes()
	{
		ArrayList requiredNodeTypes = new ArrayList();
		requiredNodeTypes.add(QueryConstants.STATES_TYPE);
		requiredNodeTypes.add(QueryConstants.ACTIONS_TYPE);

		setRequiredChildNode(QueryConstants.subset1, requiredNodeTypes);
		setRequiredChildNode(QueryConstants.subset2, requiredNodeTypes);
	}

	public static String getTooltip()
	{
		return "Subset  (a boolean operator that determines whether a set is included in or corresponds to another set)";
	}

	public static String getNodeInfo()
	{
		return QueryManager.addColouring("The Subset node determines whether a set is included "
											+ "in or corresponds to another set.<br><br>"
											+ "The required arguments are two sets.<br>"
											+ "The operator returns a boolean value.");
	}

	@Override
	public String printTextualRepresentation()
	{
		String description = "";
		ArrayList children = getChildNodes();
		if (children != null)
		{
			Iterator<PerformanceTreeNode> i = children.iterator();
			while (i.hasNext())
			{
				PerformanceTreeNode child = i.next();
				QueryManager.colourUp();
				description += child.printTextualRepresentation();
				QueryManager.colourDown();
				if (i.hasNext())
					description += QueryManager.addColouring(" is a subset of ");
				else
				{
					if (children.size() == 1)
					{
						description += QueryManager.addColouring(" is a subset of ");
						QueryManager.colourUp();
						description += QueryManager.addColouring(" another set that has not been specified yet");
						QueryManager.colourDown();
					}
				}
			}
		}
		else
		{
			description += QueryManager.addColouring("the subset relationship between two sets that have not been "
														+ "specified yet is satisfied");
		}
		return description;
	}

	/**
	 * Need to override method of same name in OperationNode, since we need to
	 * ensure that the two children are of the same type in order for an
	 * assignment to be valid.
	 */
	@Override
	public boolean childAssignmentValid(final PerformanceTreeArc arc, final PerformanceTreeNode node)
	{
		String possibleChildReturnType = node.getReturnType();
		String role = arc.getArcLabel();
		String problem;

		// Can we assign the subnode? Have we not reached the limit of
		// assignable subnodes in total?
		if (numberOfChildren() < this.maxArguments)
		{
			if (numberOfChildren() == 0)
			{
				// no child has been assigned yet, so we can connect any valid
				// type
				if (this.requiredChildNodes.containsKey(role))
				{
					// check if the role is supported
					ArrayList allowedNodeReturnTypes = (ArrayList) this.requiredChildNodes.get(role);
					if (allowedNodeReturnTypes.contains(possibleChildReturnType))
						// role is supported as well as node return type
						return true;
					else
					{
						// node return type is not supported
						problem = "The node you are trying to connect is not a valid subnode of a Subset node";
						writeErrorMessage(problem);
						return false;
					}
				}
				else
				{
					// role is not supported
					problem = "No permissible child node of a Subset node has the role associated with it that you want to assign it to";
					writeErrorMessage(problem);
					return false;
				}
			}
			else
			{
				// a child has already been assigned
				PerformanceTreeNode existingChild = null;
				String existingChildReturnType = null;
				Iterator it = this.outgoingArcIDs.iterator();
				while (it.hasNext())
				{
					String outgoingArcID = (String) it.next();
					PerformanceTreeArc outgoingArc;
					if (MacroManager.getEditor() == null)
					{
						outgoingArc = QueryManager.getData().getArc(outgoingArcID);
					}
					else
					{
						outgoingArc = MacroManager.getEditor().getArc(outgoingArcID);
					}
					if (outgoingArc.getTarget() != null)
					{
						existingChild = outgoingArc.getTarget();
						existingChildReturnType = existingChild.getReturnType();
					}
				}
				if (this.requiredChildNodes.containsKey(role))
				{
					// role is supported
					ArrayList allowedNodeReturnTypes = (ArrayList) this.requiredChildNodes.get(role);
					if (allowedNodeReturnTypes.contains(possibleChildReturnType))
					{
						// node type is supported
						if (possibleChildReturnType.equals(existingChildReturnType))
							// child node return types agree
							return true;
						else
						{
							// child node return types don't agree
							problem = "The node you are trying to connect is not a valid subnode of a Subset node in the "
										+ "current setup, since it needs to have the same return type as the already existing subnode.";
							writeErrorMessage(problem);
							return false;
						}
					}
					else
					{
						// node return type is not supported
						problem = "The node you are trying to connect is not a valid subnode of a Subset node";
						writeErrorMessage(problem);
						return false;
					}
				}
				else
				{
					// role type is not supported
					problem = "No permissible child node of a Subset node has the role associated with it that you want to assign it to";
					writeErrorMessage(problem);
					return false;
				}
			}
		}
		return false;
	}

	private void writeErrorMessage(final String message)
	{
		String msg = QueryManager.addColouring(message);
		if (MacroManager.getEditor() == null)
			QueryManager.writeToInfoBox(msg);
		else MacroEditor.writeToInfoBox(msg);
	}

}
