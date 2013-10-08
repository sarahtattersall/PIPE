/**
 * MacroDefinition
 * 
 * This class contains all relevant information for the definition of a macro.
 * 
 * @author Tamas Suto
 * @date 06/10/07
 */

package pipe.modules.queryeditor.gui.performancetrees.macros;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Observable;

import pipe.common.PetriNetNode;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.OperationNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ValueNode;

public class MacroDefinition extends Observable implements QueryConstants
{

	private String									macroName;
	private String									macroDescription;
	private String									macroReturnType;
	private final ArrayList<PerformanceTreeNode>	macroNodes;
	private final ArrayList<PerformanceTreeArc>		macroArcs;
	private int										nodeCounter;
	private int										arcCounter;

	public MacroDefinition(final String name) {
		this.macroName = name;
		this.macroDescription = null;
		this.macroReturnType = null;
		this.macroNodes = new ArrayList<PerformanceTreeNode>();
		this.macroArcs = new ArrayList<PerformanceTreeArc>();
		this.nodeCounter = 0;
		this.arcCounter = 0;
	}

	public String getName()
	{
		return this.macroName;
	}

	public void setName(final String name)
	{
		this.macroName = name;
	}

	public String getDescription()
	{
		return this.macroDescription;
	}

	public void setDescription(final String description)
	{
		this.macroDescription = description;
	}

	public String getReturnType()
	{
		return this.macroReturnType;
	}

	public void setReturnType(final String newType)
	{
		this.macroReturnType = newType;
	}

	/**
	 * This method checks the return type of the node connecting to the Macro
	 * node in the macro definition in order to determine the macro's return
	 * type
	 * 
	 * @return
	 */
	public String determineMacroReturnType()
	{
		if (this.macroNodes != null)
		{
			PerformanceTreeNode topNode = this.macroNodes.get(0);
			String topNodeID = topNode.getId();

			Collection topNodesOutgoingArcIDs = ((MacroNode) topNode).getOutgoingArcIDs();
			Iterator<String> i = topNodesOutgoingArcIDs.iterator();
			String topNodesArcID = i.next();
			PerformanceTreeArc topNodesArc = getMacroArc(topNodesArcID);

			if (topNodesArc.getTargetID() != null)
			{
				String topNodesArcsTargetID = topNodesArc.getTargetID();
				PerformanceTreeNode topNodesChildNode = getMacroNode(topNodesArcsTargetID);
                return topNodesChildNode.getReturnType();
			}
		}
		return null;
	}

	private int getNodeCounter()
	{
		return this.nodeCounter;
	}

	private void incrementNodeCounter()
	{
		this.nodeCounter++;
	}

	private int getArcCounter()
	{
		return this.arcCounter;
	}

	private void incrementArcCounter()
	{
		this.arcCounter++;
	}

	/**
	 * This method returns information that is required for drawing outgoing
	 * arcs to assign arguments to once the macro node has been set up. The
	 * HashMap will contain as key the names of Argument nodes (which will be
	 * used as roles) and as entries a String or an ArrayList of possible types
	 * of subnodes.
	 * 
	 * @return
	 */
	public HashMap getOutgoingArcInformation()
	{
		HashMap outgoingArcsInfo = new HashMap();
		Iterator<PerformanceTreeNode> i = this.macroNodes.iterator();
		while (i.hasNext())
		{
			PerformanceTreeNode retrievedNode = i.next();
			PetriNetNode retrievedNodeType = retrievedNode.getNodeType();

			if (retrievedNodeType == PetriNetNode.ARGUMENT)
			{
				String argumentNodeName = ((ArgumentNode) retrievedNode).getArgumentName();

				if (argumentNodeName != null)
				{
					PerformanceTreeArc retrievedNodesIncomingArc = getMacroArc(retrievedNode.getIncomingArc()
																							.getId());
					OperationNode retrievedNodesParent = (OperationNode) getMacroNode(retrievedNode	.getIncomingArc()
																									.getSourceID());
					HashMap retrievedNodesParentsRequiredChildNodes = retrievedNodesParent.getRequiredChildNodes();
					HashMap retrievedNodesParentsOptionalChildNodes = retrievedNodesParent.getOptionalChildNodes();
					String retrievedNodesIncomingArcRole = retrievedNode.getIncomingArc().getArcLabel();
					boolean argumentRequired = retrievedNodesIncomingArc.isRequired();

					if (retrievedNodesParentsRequiredChildNodes.get(retrievedNodesIncomingArcRole) instanceof String)
					{
						String acceptableNodeType = (String) retrievedNodesParentsRequiredChildNodes.get(retrievedNodesIncomingArcRole);
						ArrayList acceptableChildNodeTypes = new ArrayList();
						acceptableChildNodeTypes.add(acceptableNodeType);
						OutgoingArcInfo arcInfo = new OutgoingArcInfo(	acceptableChildNodeTypes,
																		argumentRequired);
						outgoingArcsInfo.put(argumentNodeName, arcInfo);
					}
					else if (retrievedNodesParentsRequiredChildNodes.get(retrievedNodesIncomingArcRole) instanceof ArrayList)
					{
						ArrayList acceptableChildNodeTypes = (ArrayList) retrievedNodesParentsRequiredChildNodes.get(retrievedNodesIncomingArcRole);
						OutgoingArcInfo arcInfo = new OutgoingArcInfo(	acceptableChildNodeTypes,
																		argumentRequired);
						outgoingArcsInfo.put(argumentNodeName, arcInfo);
					}
					else if (retrievedNodesParentsOptionalChildNodes.get(retrievedNodesIncomingArcRole) instanceof String)
					{
						String acceptableNodeType = (String) retrievedNodesParentsOptionalChildNodes.get(retrievedNodesIncomingArcRole);
						ArrayList acceptableChildNodeTypes = new ArrayList();
						acceptableChildNodeTypes.add(acceptableNodeType);
						OutgoingArcInfo arcInfo = new OutgoingArcInfo(	acceptableChildNodeTypes,
																		argumentRequired);
						outgoingArcsInfo.put(argumentNodeName, arcInfo);
					}
					else if (retrievedNodesParentsOptionalChildNodes.get(retrievedNodesIncomingArcRole) instanceof ArrayList)
					{
						ArrayList acceptableChildNodeTypes = (ArrayList) retrievedNodesParentsOptionalChildNodes.get(retrievedNodesIncomingArcRole);
						OutgoingArcInfo arcInfo = new OutgoingArcInfo(	acceptableChildNodeTypes,
																		argumentRequired);
						outgoingArcsInfo.put(argumentNodeName, arcInfo);
					}
				}
			}
		}
		return outgoingArcsInfo;
	}

	public class OutgoingArcInfo
	{

		private ArrayList	acceptableChildNodeTypes	= new ArrayList();
		private boolean		argumentRequired			= false;

		public OutgoingArcInfo(final ArrayList childNodeTypes, final boolean required) {
			this.acceptableChildNodeTypes = childNodeTypes;
			this.argumentRequired = required;
		}

		public ArrayList getAcceptableChildNodeTypes()
		{
			return this.acceptableChildNodeTypes;
		}

		public void setAcceptableChildNodeTypes(final ArrayList nodeTypes)
		{
			this.acceptableChildNodeTypes = nodeTypes;
		}

		public boolean getArgumentRequired()
		{
			return this.argumentRequired;
		}

		public void setArgumentRequired(final boolean required)
		{
			this.argumentRequired = required;
		}
	}

	public ArrayList<PerformanceTreeNode> getMacroNodes()
	{
		return this.macroNodes;
	}

	public PerformanceTreeNode getMacroNode(final String nodeID)
	{
		Iterator<PerformanceTreeNode> i = this.macroNodes.iterator();
		while (i.hasNext())
		{
			PerformanceTreeNode retrievedNode = i.next();
			String retrievedNodeID = retrievedNode.getId();
			if (retrievedNodeID.equals(nodeID))
				return retrievedNode;
		}
		return null;
	}

	/**
	 * This method performs the adding of a node forming part of a macro tree
	 * 
	 * @param node
     * @return
	 */
	public PerformanceTreeNode addMacroNode(final PerformanceTreeNode node)
	{
		boolean unique = true;
		if (node != null)
		{
			if (node.getId() != null && node.getId().length() > 0)
			{
				// node already has an ID.
				Iterator<PerformanceTreeNode> i = this.macroNodes.iterator();
				while (i.hasNext())
				{
					String existingNodeID = i.next().getId();
					String nodeID = node.getId();
					if (existingNodeID.equals(nodeID))
					{
						// a node with the same ID already exists in the
						// MacroDefinition
						unique = false;
					}
				}
			}
			else
			{
				// an ID hasn't been assigned yet, since the node has just been
				// created
				incrementNodeCounter();
				String nodeID = "Macro Node " + getNodeCounter();
				node.setId(nodeID);
			}

			if (QueryManager.getMode() == QueryConstants.LOADING ||
				MacroManager.getMode() == QueryConstants.LOADING)
			{
				// we're loading in a node, so need to increment the node
				// counter
				incrementNodeCounter();
			}

			if (unique)
			{
				// if it's an OperationNode, need to add all of its initial
				// arcs, too
				if (node instanceof OperationNode && MacroManager.getMode() != QueryConstants.LOADING)
				{
					PerformanceTreeArc arc;

					// take care of required nodes
					Iterator<PerformanceTreeArc> it = ((OperationNode) node).getRequiredArcs().iterator();
					while (it.hasNext())
					{
						arc = it.next();
						arc.setSource(node);
						addMacroArc(arc); // add arc to MacroDefinition

						// add arc to node's outgoingArcIDs list
						String arcID = arc.getId();
						((OperationNode) node).addOutgoingArcID(arcID);
					}

					// take care of optional nodes
					it = ((OperationNode) node).getOptionalArcs().iterator();
					while (it.hasNext())
					{
						arc = it.next();
						arc.setSource(node);
						addMacroArc(arc); // add arc to data structure

						// add arc to node's outgoingArcIDs list
						String arcID = arc.getId();
						((OperationNode) node).addOutgoingArcID(arcID);
					}
				}
				this.macroNodes.add(node);
				setChanged();
				notifyObservers(node);
				return node;
			}
		}
		return null;
	}

	/**
	 * This method deletes a PerformanceTreeNode from the MacroDefinition
	 * 
	 * @param node
	 */
	public void deleteMacroNode(final PerformanceTreeNode node)
	{
		if (this.macroNodes.contains(node))
		{
			String nodeID = node.getId();
			// retrieve node from data structure - to have the most current
			// version
			PerformanceTreeNode nodeToDelete = getMacroNode(nodeID);
			if (nodeToDelete != null)
			{
				// Decouple from parent node
				if (nodeToDelete.getIncomingArcID() != null)
				{
					// update incoming arc's data
					String incomingArcID = nodeToDelete.getIncomingArcID();
					PerformanceTreeArc incomingArc = getMacroArc(incomingArcID);
					incomingArc.setTarget(null);
					updateMacroArc(incomingArc);
					// update current node's data
					nodeToDelete.removeArcCompareObject(incomingArc);
					nodeToDelete.setIncomingArcID(null);
				}

				// Decouple from children nodes
				if (nodeToDelete instanceof OperationNode)
				{
					Iterator it = ((OperationNode) nodeToDelete).getOutgoingArcIDs().iterator();
					while (it.hasNext())
					{
						String outgoingArcID = (String) it.next();
						PerformanceTreeArc outgoingArc = getMacroArc(outgoingArcID);
						if (outgoingArc.getTarget() != null)
						{
							// arc has a target, meaning that a child node is
							// assigned to the node we're
							// deleting through the current arc
							PerformanceTreeNode targetNode = outgoingArc.getTarget();
							targetNode.removeArcCompareObject(outgoingArc);
							targetNode.setIncomingArcID(null);
							updateMacroNode(targetNode);
						}

						// delete outgoing arc
						outgoingArc.setSource(null);
						outgoingArc.setTarget(null);
						updateMacroArc(outgoingArc);
						outgoingArc.delete();
					}
				}
				else if (nodeToDelete instanceof ValueNode)
				{
					// remove node label
					if (((ValueNode) nodeToDelete).getNodeLabelObject() != null)
						((ValueNode) nodeToDelete).getNodeLabelObject().delete();
				}

				// delete node from nodesArray
				this.macroNodes.remove(node);
				setChanged();
				notifyObservers(node);
			}
		}
	}

	public void updateMacroNode(final PerformanceTreeNode updatedNode)
	{
		String updatedNodeID = updatedNode.getId();
		if (getMacroNode(updatedNodeID) != null)
		{
			// a node with our updated node's ID already exists in the
			// nodesArray
			PerformanceTreeNode existingNode = getMacroNode(updatedNodeID);

			// update node by removing old instance and adding new instance
			// that has all the updated information
			this.macroNodes.remove(existingNode);
			this.macroNodes.add(updatedNode);
			setChanged();
			// perhaps try notifyObservers(existingNode);
			notifyObservers(updatedNode);
		}
	}

	public ArrayList<PerformanceTreeArc> getMacroArcs()
	{
		return this.macroArcs;
	}

	public PerformanceTreeArc getMacroArc(final String arcID)
	{
		Iterator<PerformanceTreeArc> i = this.macroArcs.iterator();
		while (i.hasNext())
		{
			PerformanceTreeArc retrievedArc = i.next();
			String retrievedArcID = retrievedArc.getId();
			if (retrievedArcID.equals(arcID))
				return retrievedArc;
		}
		return null;
	}

	/**
	 * This method performs and adding and an update of an arc forming part of a
	 * macro tree
	 * 
	 * @param node
     * @param arc
     * @return
	 */
	public PerformanceTreeArc addMacroArc(final PerformanceTreeArc arc)
	{
		boolean unique = true;
		if (arc != null)
		{
			if (arc.getId() != null && arc.getId().length() > 0)
			{
				// arc already has an ID.
				Iterator<PerformanceTreeArc> i = this.macroArcs.iterator();
				while (i.hasNext())
				{
					String existingArcID = i.next().getId();
					String arcID = arc.getId();
					if (existingArcID.equals(arcID))
					{
						// an arc with the same ID already exists in the
						// MacroDefinition
						unique = false;
					}
				}
			}
			else
			{
				// an ID hasn't been assigned yet, since the arc has just been
				// created
				incrementArcCounter();
				String arcID = "Macro Arc " + getArcCounter();
				arc.setId(arcID);
			}

			if (MacroManager.getMode() == QueryConstants.LOADING)
			{
				// we're loading in an arc, so need to increment the counter
				incrementArcCounter();
			}

			if (unique)
			{
				this.macroArcs.add(arc);
				setChanged();
				notifyObservers(arc);
				return arc;
			}
		}
		return null;
	}

	/**
	 * This method deletes a PerformanceTreeArc from the MacroDefinition
	 * 
	 * @param node
     * @param arc
	 */
	public void deleteMacroArc(final PerformanceTreeArc arc)
	{
		if (this.macroArcs.contains(arc))
		{
			String arcID = arc.getId();
			PerformanceTreeArc arcToDelete = getMacroArc(arcID);

			if (arcToDelete != null)
			{
				// remove arc from list of outgoingArcs of its source
				PerformanceTreeNode arcSource = arcToDelete.getSource();
				if (arcToDelete.getSource() != null && arcSource instanceof OperationNode)
				{
					OperationNode source = (OperationNode) arcSource;
					source.removeArcCompareObject(arcToDelete);
					source.removeOutgoingArcID(arcID);
					updateMacroNode(source);
				}

				// remove incomingArc from arc's target, if any
				if (arcToDelete.getTarget() != null)
				{
					PerformanceTreeNode arcTarget = arcToDelete.getTarget();
					arcTarget.removeArcCompareObject(arcToDelete);
					arcTarget.setIncomingArcID(null);
					updateMacroNode(arcTarget);
				}

				// remove arc's label
				arcToDelete.getArcLabelObject().delete();

				// remove arc path points
				arcToDelete.getArcPath().delete();

				// remove arc from arcsArray
				this.macroArcs.remove(arcToDelete);
				setChanged();
				notifyObservers(arcToDelete);
			}
		}
	}

	public void updateMacroArc(final PerformanceTreeArc arc)
	{
		String updatedArcID = arc.getId();
		if (getMacroArc(updatedArcID) != null)
		{
			// an arc with our updated arc's ID already exists in the arcsArray
			PerformanceTreeArc existingArc = getMacroArc(updatedArcID);
			// update arc
			this.macroArcs.remove(existingArc);
			this.macroArcs.add(arc);
			setChanged();
			notifyObservers(arc);
		}
	}

	public void printMacroDefintionContents()
	{
		// print node info
		System.out.println("----- Begin MacroDefinition printout -----");
		System.out.println("Macro name: " + this.macroName);
		System.out.println("Return type: " + this.macroReturnType);
		System.out.println("----- Begin macroNodes data printout -----");
		if (!this.macroNodes.isEmpty())
		{
			Iterator<PerformanceTreeNode> it = this.macroNodes.iterator();
			while (it.hasNext())
			{
				PerformanceTreeNode macroNode = it.next();
				String nodeID = macroNode.getId();
				PetriNetNode nodeType = macroNode.getNodeType();
				System.out.println("The array has a node entry with ID " + nodeID + " and type " + nodeType);
				if (macroNode.getParentNode() != null)
				{

					PerformanceTreeNode parentNode = getMacroNode(getMacroArc(macroNode.getIncomingArcID())	.getSourceID());
					String parentNodeID = parentNode.getId();
					PetriNetNode parentNodeType = parentNode.getNodeType();
					System.out.println("  a parent node with ID " + parentNodeID + " and type " +
										parentNodeType);
				}
				if (macroNode instanceof OperationNode)
				{
					if (!((OperationNode) macroNode).getOutgoingArcIDs().isEmpty())
					{
						Iterator<String> i = ((OperationNode) macroNode).getOutgoingArcIDs().iterator();
						while (i.hasNext())
						{
							String outgoingArcID = i.next();
							System.out.println("  an outgoing arc with ID " + outgoingArcID + " ");
							if (getMacroArc(outgoingArcID).getTargetID() != null)
							{
								PerformanceTreeNode childNode = getMacroNode(getMacroArc(outgoingArcID)	.getTargetID());
								String childNodeID = childNode.getId();
								PetriNetNode childNodeType = childNode.getNodeType();
								System.out.println("    linking to a child node with ID " + childNodeID +
													" and type " + childNodeType);
							}
						}
					}
				}
			}
		}
		System.out.println("----- End macroNodes data printout -----");

		// print arc info
		System.out.println("----- Begin macroArcs data printout -----");
		if (!this.macroArcs.isEmpty())
		{
			Iterator<PerformanceTreeArc> it = this.macroArcs.iterator();
			while (it.hasNext())
			{
				PerformanceTreeArc arc = it.next();
				String arcID = arc.getId();
				System.out.println("The array has an arc entry with ID " + arcID);
				if (arc.getSourceID() != null)
				{
					String arcSourceID = arc.getSourceID();
					PerformanceTreeNode arcSource = getMacroNode(arcSourceID);
					PetriNetNode arcSourceType = arcSource.getNodeType();
					System.out.println("  a source node with ID " + arcSourceID + " and type " +
										arcSourceType);
				}
				if (arc.getTargetID() != null)
				{
					String arcTargetID = arc.getTargetID();
					PerformanceTreeNode arcTarget = getMacroNode(arcTargetID);
					PetriNetNode arcTargetType = arcTarget.getNodeType();
					System.out.println("  a target node with ID " + arcTargetID + " and type " +
										arcTargetType);
				}
			}
		}
		System.out.println("----- End macroArcs data printout -----");
	}

}
