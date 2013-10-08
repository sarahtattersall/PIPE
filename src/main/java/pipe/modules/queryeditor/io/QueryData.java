/**
 * QueryData
 * 
 * - in essence encapsulates a Performance Tree query
 * - implements methods for the loading of a query from an XML file
 * 
 * @author Tamas Suto
 * @date 17/04/07
 */

package pipe.modules.queryeditor.io;

import pipe.common.dataLayer.StateGroup;
import pipe.common.PetriNetNode;
import pipe.gui.ApplicationSettings;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.QueryException;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObject;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroDefinition;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.OperationNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ResultNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SequentialNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StatesNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ValueNode;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;
import pipe.views.TransitionView;

import javax.swing.*;
import java.io.File;
import java.util.*;

public class QueryData extends Observable implements QueryConstants, Cloneable
{

	public static String						queryName		= null;

	// list of tree elements
	private ArrayList<PerformanceTreeNode>		treeNodes		= null;
	private ArrayList<PerformanceTreeArc>		treeArcs		= null;

	private ArrayList<String>					placeNames		= null;

	// state labels are HashMaps indexed by the label name and return an
	// ArrayList
	// of names of the StateGroups that are assigned to the label
	private HashMap<String, ArrayList<String>>	stateLabels		= null;

	// action labels are stored in an ArrayList and are just transition names
	private ArrayList<String>					actionLabels	= null;

	// macros are referenced by their name and all information about them is
	// contained in
	// MacroDefinition objects
	private HashMap<String, MacroDefinition>	macros			= null;

	public int									nodeCounter		= 0;	// used
	// for
	// naming
	// nodes
	public int									arcCounter		= 0;	// used

	// for
	// naming
	// arcs

	private QueryData(final String ptmlFileName) {
		initialiseArrays();
		QueryTransformer transform = new QueryTransformer();
		File temp = new File(ptmlFileName);
		QueryData.queryName = temp.getName();
		QueryLoader.loadQueryFromXML(transform.transformPTML(ptmlFileName), this);
	}

	public QueryData(final File ptmlFile) {
		this(ptmlFile.getAbsolutePath());
	}

	public QueryData() {
		initialiseArrays();
	}

	public String getURI()
	{
		return QueryData.queryName;
	}

	public void emptyData()
	{
		QueryData.queryName = null;
		initialiseArrays();
	}

	private void initialiseArrays()
	{
		this.treeNodes = new ArrayList<PerformanceTreeNode>();
		this.treeArcs = new ArrayList<PerformanceTreeArc>();
		this.placeNames = new ArrayList<String>();
		this.stateLabels = new HashMap<String, ArrayList<String>>();
		this.actionLabels = new ArrayList<String>();
		this.macros = new HashMap<String, MacroDefinition>();
		this.nodeCounter = 0;
		this.arcCounter = 0;
	}

	/**
	 * Update the information in QueryData. Used when loading in a query.
	 * 
	 * @param updatedQueryData
	 */
	public void updateData(final QueryData updatedQueryData)
	{
		QueryData.queryName = QueryData.queryName;
		this.treeNodes = updatedQueryData.treeNodes;
		this.treeArcs = updatedQueryData.treeArcs;
		this.placeNames = updatedQueryData.placeNames;
		this.stateLabels = updatedQueryData.stateLabels;
		this.actionLabels = updatedQueryData.actionLabels;
		this.macros = updatedQueryData.macros;
		this.nodeCounter = updatedQueryData.nodeCounter;
		this.arcCounter = updatedQueryData.arcCounter;
	}

	/**
	 * Add the specified PerformanceTreeObject
	 * 
	 * @param ptObject -
	 *            the PerformanceTreeObject to be added.
     * @return
	 */
	public PerformanceTreeObject addPerformanceTreeObject(final PerformanceTreeObject ptObject)
	{
		if (ptObject instanceof PerformanceTreeNode)
			return addNode((PerformanceTreeNode) ptObject);
		else if (ptObject instanceof PerformanceTreeArc)
			return addArc((PerformanceTreeArc) ptObject);
		else return null;
	}

	/**
	 * Removes the specified PerformanceTreeObject
	 * 
	 * @param ptObject
	 *            The PerformanceTreeObject to be removed.
	 */
	public void removePerformanceTreeObject(final PerformanceTreeObject ptObject)
	{
		if (ptObject instanceof PerformanceTreeNode)
		{
			deleteNode((PerformanceTreeNode) ptObject);
		}
		else if (ptObject instanceof PerformanceTreeArc)
		{
			deleteArc((PerformanceTreeArc) ptObject);
		}
	}

	/**
	 * - add PerformanceTreeNode nodeInput to the back of the nodes ArrayList -
	 * this is where the ID is being assigned to the object
	 * 
	 * @param nodeInput -
	 *            PerformanceTreeNode Object to add
     * @return
	 */
	public PerformanceTreeNode addNode(final PerformanceTreeNode nodeInput)
	{
		boolean unique = true;
		if (nodeInput != null)
		{
			if (nodeInput.getId() != null && nodeInput.getId().length() > 0)
			{
				// node already has an ID
                for(PerformanceTreeNode treeNode : this.treeNodes)
                {
                    if(nodeInput.getId().equals(treeNode.getId()))
                    {
                        // check if a node with the same ID doesn't already
                        // exist
                        unique = false;
                    }
                }
			}
			else
			{
				// ID hasn't been assigned yet, since the node has just been
				// created, so do it now
				this.nodeCounter += 1;
				nodeInput.setId("Node" + this.nodeCounter);
			}

			if (QueryManager.getMode() == QueryConstants.LOADING)
			{
				// we're loading in a node, so need to increment the node
				// counter
				this.nodeCounter++;
			}

			if (unique)
			{
				// if it's an OperationNode, need to add all of its initial
				// arcs, too
				if (nodeInput instanceof OperationNode && QueryManager.getMode() != QueryConstants.LOADING)
				{
					PerformanceTreeArc arc;

					// take care of required nodes
					Iterator<PerformanceTreeArc> it = ((OperationNode) nodeInput)	.getRequiredArcs()
																					.iterator();
					while (it.hasNext())
					{
						arc = it.next();
						arc.setSource(nodeInput);
						arc = addArc(arc); // add arc to data structure

						// add arc to node's outgoingArcIDs list
						String arcID = arc.getId();
						((OperationNode) nodeInput).addOutgoingArcID(arcID);
					}

					// take care of optional nodes
					it = ((OperationNode) nodeInput).getOptionalArcs().iterator();
					while (it.hasNext())
					{
						arc = it.next();
						arc.setSource(nodeInput);
						arc = addArc(arc); // add arc to data structure

						// add arc to node's outgoingArcIDs list
						String arcID = arc.getId();
						((OperationNode) nodeInput).addOutgoingArcID(arcID);
					}
				}

				this.treeNodes.add(nodeInput);
				setChanged();
				notifyObservers(nodeInput);
				return nodeInput;
			}
		}
		return null;
	}

	/**
	 * Updates a node having the same ID in the nodesArray as the one passed as
	 * an argument
	 * 
	 * @param updatedNode
	 */
	public void updateNode(final PerformanceTreeNode updatedNode)
	{
		String updatedNodeID = updatedNode.getId();
		if (getNode(updatedNodeID) != null)
		{
			// a node with our updated node's ID already exists in the
			// nodesArray
			PerformanceTreeNode existingNode = getNode(updatedNodeID);

			// update node by removing old instance and adding new instance
			// that has all the updated information
			this.treeNodes.remove(existingNode);
			this.treeNodes.add(updatedNode);
			setChanged();
			// perhaps try notifyObservers(existingNode);
			notifyObservers(updatedNode);
		}
	}

	/**
	 * Deletes a PerformanceTreeNode, both from the data structures and from the
	 * canvas
	 * 
	 * @param nodeToDelete
     * @param node
	 */
    private void deleteNode(final PerformanceTreeNode node)
	{
		String nodeID = node.getId();

		// retrieve node from data structure - to have the most current version
		PerformanceTreeNode nodeToDelete = getNode(nodeID);
		if (nodeToDelete != null)
		{
			// Decouple from parent node
			if (nodeToDelete.getIncomingArcID() != null)
			{
				// update incoming arc's data
				String incomingArcID = nodeToDelete.getIncomingArcID();
				PerformanceTreeArc incomingArc = getArc(incomingArcID);
				incomingArc.setTarget(null);
				updateArc(incomingArc);
				// update current node's data
				nodeToDelete.removeArcCompareObject(incomingArc);
				nodeToDelete.setIncomingArcID(null);
			}

			// Decouple from children nodes
			if (nodeToDelete instanceof OperationNode)
			{
				Iterator<String> it = ((OperationNode) nodeToDelete).getOutgoingArcIDs().iterator();
				while (it.hasNext())
				{
					String outgoingArcID = it.next();
					PerformanceTreeArc outgoingArc = getArc(outgoingArcID);
					if (outgoingArc.getTarget() != null)
					{
						// arc has a target, meaning that a child node is
						// assigned to the node we're
						// deleting through the current arc
						PerformanceTreeNode targetNode = outgoingArc.getTarget();
						targetNode.removeArcCompareObject(outgoingArc);
						targetNode.setIncomingArcID(null);
						updateNode(targetNode);
					}

					// delete outgoing arc
					outgoingArc.setSource(null);
					outgoingArc.setTarget(null);
					updateArc(outgoingArc);
					outgoingArc.delete();
				}
				if (nodeToDelete instanceof MacroNode)
				{
					// delete label as well
					if (((MacroNode) nodeToDelete).getNodeLabelObject() != null)
						((MacroNode) nodeToDelete).getNodeLabelObject().delete();
				}
			}
			else if (nodeToDelete instanceof ValueNode)
			{
				// remove node label
				if (((ValueNode) nodeToDelete).getNodeLabelObject() != null)
					((ValueNode) nodeToDelete).getNodeLabelObject().delete();
			}

			// delete node from nodesArray
			this.treeNodes.remove(nodeToDelete);
			setChanged();
			notifyObservers(nodeToDelete);
		}
	}

	public int getNodeCounter()
	{
		return this.nodeCounter;
	}

	public void incrementNodeCounter()
	{
		this.nodeCounter++;
	}

	public int getArcCounter()
	{
		return this.arcCounter;
	}

	public void incrementArcCounter()
	{
		this.arcCounter++;
	}

	/**
	 * Add arcInput to the back of the PerformanceTreeArc ArrayList All
	 * observers are notified of this change (Model-View Architecture)
	 * 
	 * @param arcInput -
	 *            PerformanceTreeArc object to add
     * @return
	 */
	public PerformanceTreeArc addArc(final PerformanceTreeArc arcInput)
	{
		boolean unique = true;
		if (arcInput != null)
		{
			if (arcInput.getId() != null && arcInput.getId().length() > 0)
			{
				// arc already has an ID assigned to it
                for(PerformanceTreeArc treeArc : this.treeArcs)
                {
                    if(arcInput.getId().equals(treeArc.getId()))
                    {
                        // check if an arc with the same id already exists
                        unique = false;
                    }
                }
			}
			else
			{
				// ID hasn't been assigned yet, since the arc has just been
				// created, so do it now
				this.arcCounter += 1;
				arcInput.setId("Arc " + this.arcCounter);
			}

			if (QueryManager.getMode() == QueryConstants.LOADING)
			{
				// we're loading in an arc, so need to increment the counter
				this.arcCounter++;
			}

			if (unique)
			{
				this.treeArcs.add(arcInput);
				setChanged();
				notifyObservers(arcInput);
				return arcInput;
			}
		}
		return null;
	}

	/**
	 * Updates an arc having the same ID in the arcsArray as the one passed as
	 * an argument
	 * 
	 * @param updatedArc
	 */
	public void updateArc(final PerformanceTreeArc updatedArc)
	{
		String updatedArcID = updatedArc.getId();
		if (getArc(updatedArcID) != null)
		{
			// an arc with our updated arc's ID already exists in the arcsArray
			PerformanceTreeArc existingArc = getArc(updatedArcID);
			// update arc
			this.treeArcs.remove(existingArc);
			this.treeArcs.add(updatedArc);
			setChanged();
			notifyObservers(updatedArc);
		}
	}

	/**
	 * Deletes an arc
	 * 
	 * @param arc
	 */
    private void deleteArc(final PerformanceTreeArc arc)
	{
		String arcID = arc.getId();
		PerformanceTreeArc arcToDelete = getArc(arcID);

		if (arcToDelete != null)
		{
			PerformanceTreeNode arcSource = arcToDelete.getSource();
			// remove arc from list of outgoingArcs of its source
			if (arcToDelete.getSource() != null && arcSource instanceof OperationNode)
			{
				OperationNode source = (OperationNode) arcSource;
				source.removeArcCompareObject(arcToDelete);
				source.removeOutgoingArcID(arcID);
				updateNode(source);
			}

			// remove incomingArc from arc's target, if any
			if (arcToDelete.getTarget() != null)
			{
				PerformanceTreeNode arcTarget = arcToDelete.getTarget();
				arcTarget.removeArcCompareObject(arcToDelete);
				arcTarget.setIncomingArcID(null);
				updateNode(arcTarget);
			}

			// remove arc's label
			arcToDelete.getArcLabelObject().delete();

			// remove arc path points
			arcToDelete.getArcPath().delete();

			// remove arc from arcsArray
			this.treeArcs.remove(arcToDelete);
			setChanged();
			notifyObservers(arcToDelete);
		}
	}

	private void printDataContents()
	{
		System.out.println("--- nodesArray ---");
		Iterator<PerformanceTreeNode> i = this.treeNodes.iterator();
		while (i.hasNext())
		{
			PerformanceTreeNode node = i.next();
			String nodeID = node.getId();
			PetriNetNode nodeType = node.getNodeType();
			System.out.println(nodeID + " of type " + nodeType.toString());
			if (node.getIncomingArcID() != null)
			{
				String incomingArcID = node.getIncomingArcID();
				System.out.println("  that has an incoming arc with ID " + incomingArcID);
			}
			if (node instanceof OperationNode)
			{
				if (((OperationNode) node).getOutgoingArcIDs() != null)
				{
					Iterator<String> j = ((OperationNode) node).getOutgoingArcIDs().iterator();
					while (j.hasNext())
					{
						String outgoingArcID = j.next();
						System.out.println("  and an outgoing arc with ID " + outgoingArcID);
					}
				}
			}
		}
		System.out.println("\n");
		System.out.println("--- arcsArray ---");
		Iterator<PerformanceTreeArc> k = this.treeArcs.iterator();
		while (k.hasNext())
		{
			PerformanceTreeArc arc = k.next();
			String arcID = arc.getId();
			System.out.print(arcID + " ");
			if (arc.getSource() != null)
			{
				PerformanceTreeNode sourceNode = arc.getSource();
				String sourceNodeID = sourceNode.getId();
				PetriNetNode sourceNodeType = sourceNode.getNodeType();
				System.out.print("has source " + sourceNodeID + " of type " + sourceNodeType.toString() + " ");
			}
			if (arc.getTarget() != null)
			{
				PerformanceTreeNode targeteNode = arc.getTarget();
				String targetNodeID = targeteNode.getId();
				PetriNetNode targetNodeType = targeteNode.getNodeType();
				System.out.print("and target " + targetNodeID + " of type " + targetNodeType.toString() + " ");
			}
			System.out.print("\n\n");
		}
	}

// public Iterator<PerformanceTreeObject> getPerformanceTreeObjects(){
// ArrayList<PerformanceTreeObject> all = new
// ArrayList<PerformanceTreeObject>();
// all.addAll(treeNodes);
// all.addAll(treeArcs);
// return all.iterator();
// }

	public ArrayList<PerformanceTreeObject> getPerformanceTreeObjects()
	{
		ArrayList<PerformanceTreeObject> all = new ArrayList<PerformanceTreeObject>();
		all.addAll(this.treeNodes);
		all.addAll(this.treeArcs);
		return all;
	}

	/**
	 * Return the PerformanceTreeNode called nodeType from the query
	 * 
	 * @param nodeType -
	 *            Name of PerformanceTreeNode object to return
	 * @return The first PerformanceTreeNode object found with an ID equal to
	 *         nodeID
     * @param nodeID
	 */
	public PerformanceTreeNode getNode(final String nodeID)
	{
		PerformanceTreeNode returnNode = null;
		Iterator<PerformanceTreeNode> it = this.treeNodes.iterator();
		while (it.hasNext())
		{
			PerformanceTreeNode arrayNode = it.next();
			String arrayNodeID = arrayNode.getId();
			if (nodeID.equalsIgnoreCase(arrayNodeID))
				returnNode = arrayNode;
		}
		return returnNode;
	}

	public ArrayList<PerformanceTreeNode> getTreeNodes()
	{
		return this.treeNodes;
	}

	/**
	 * Get a list of all the PerformanceTreeNode objects in the query
	 * 
	 * @return An List of all the PerformanceTreeNode objects
	 */
	public PerformanceTreeNode[] getNodes()
	{
		PerformanceTreeNode[] returnArray = new PerformanceTreeNode[this.treeNodes.size()];
		for (int i = 0; i < this.treeNodes.size(); i++)
			returnArray[i] = this.treeNodes.get(i);
		return returnArray;
	}

	/**
	 * Return the PerformanceTreeArc with the ID arcID from the query
	 * 
	 * @param arcID -
	 *            ID of PerformanceTreeArc object to return
	 * @return The first PerformanceTreeArc object found with a name equal to
	 *         arcName
	 */
	public PerformanceTreeArc getArc(final String arcID)
	{
		PerformanceTreeArc returnArc = null;
		Iterator<PerformanceTreeArc> it = this.treeArcs.iterator();
		while (it.hasNext())
		{
			PerformanceTreeArc arrayArc = it.next();
			String arrayArcID = arrayArc.getId();
			if (arcID.equalsIgnoreCase(arrayArcID))
				returnArc = arrayArc;
		}

		return returnArc;
	}

	/**
	 * Get an List of all the Arcs objects in the query
	 * 
	 * @return An array of all the PerformanceTreeArc objects
	 */
	public PerformanceTreeArc[] getArcs()
	{
		PerformanceTreeArc[] returnArray = new PerformanceTreeArc[this.treeArcs.size()];

		for (int i = 0; i < this.treeArcs.size(); i++)
			returnArray[i] = this.treeArcs.get(i);

		return returnArray;
	}

	/**
	 * This method reads in all the place names from the model's PetriNet and
	 * returns them as a sorted ArrayList
	 * 
	 * @return
	 */
	public ArrayList<String> getPlaceNames()
	{
        PlaceView[] placesArray = ApplicationSettings.getApplicationView().getCurrentPetriNetView().places();
        for(PlaceView aPlacesArray : placesArray)
        {
            String placeName = aPlacesArray.getNameLabel().getText();
            if(!this.placeNames.contains(placeName))
            {
                // only update if something new's available
                this.placeNames.add(placeName);
            }
        }
        this.placeNames = sortArrayList(this.placeNames);
		return this.placeNames;
	}

	/**
	 * Returns the list of available action labels
	 * 
	 * @return
	 */
	public ArrayList<String> getActionLabels()
	{
        return sortArrayList(this.actionLabels);
	}

	/**
	 * Sorts an ArrayList
	 * 
	 * @param o
	 * @return
     * @param toBeSorted
	 */
	public ArrayList<String> sortArrayList(final ArrayList<String> toBeSorted)
	{
		if (toBeSorted.size() > 0)
		{
			List<String> list = Collections.synchronizedList(toBeSorted);
			Collections.sort(list);
            return new ArrayList<String>(list);
		}
		else return toBeSorted;
	}

	/**
	 * Add a new label to the list of action labels
	 * 
	 * @param aLabel
	 */
	public void addActionLabel(final String aLabel)
	{
		boolean labelAlreadyExists = false;
		Iterator<String> i = this.actionLabels.iterator();
		while (i.hasNext())
		{
			String labelEntry = i.next();
			if (labelEntry.equals(aLabel))
				labelAlreadyExists = true;
		}
		if (!labelAlreadyExists)
		{
			this.actionLabels.add(aLabel);
		}
	}

	/**
	 * Returns the list of available state labels
	 * 
	 * @return
	 */
	public HashMap<String, ArrayList<String>> getStateLabels()
	{
		return this.stateLabels;
	}

	/**
	 * Returns an ArrayList of all the available state labels
	 * 
	 * @return
	 */
	public ArrayList<String> getStateLabelNames()
	{
		ArrayList<String> stateLabelNames = new ArrayList<String>();
		Iterator<String> i = this.stateLabels.keySet().iterator();
		while (i.hasNext())
		{
			String stateLabel = i.next();
			stateLabelNames.add(stateLabel);
		}
		stateLabelNames = sortArrayList(stateLabelNames);
		return stateLabelNames;
	}

	/**
	 * Extracts the definition of a state label
	 * 
	 * @param stateLabel
	 * @return
	 */
	public ArrayList<String> getStatesAssignedToStateLabel(final String stateLabel)
	{
		if (stateLabelExistsAlready(stateLabel))
		{
			if (this.stateLabels.get(stateLabel) != null)
				return this.stateLabels.get(stateLabel);
			else
			{
                return new ArrayList<String>();
			}
		}
		else return null;
	}

	public boolean stateLabelExistsAlready(final String label)
	{
		return this.stateLabels.containsKey(label);
	}

	/**
	 * Returns a string definition of a state label, i.e. a disjunction of all
	 * the StateGroups that have been assigned to that state label
	 * 
	 * @param stateLabel
	 * @return
	 */
	public String getStateLabelDefinitionAsText(final String stateLabel)
	{
		if (stateLabelExistsAlready(stateLabel))
		{
			String stateLabelDefinition = "";
			ArrayList<String> assignedStateGroups = getStatesAssignedToStateLabel(stateLabel);
			Iterator<String> i = assignedStateGroups.iterator();
			while (i.hasNext())
			{
				String stateName = i.next();
				if (stateLabelDefinition.equals(""))
					stateLabelDefinition = stateName;
				else stateLabelDefinition = stateLabelDefinition + " || " + stateName;
			}
			return stateLabelDefinition;
		}
		else return null;
	}

	/**
	 * Checks if a stategroups exists for the given statelabel
	 * 
	 * @param stateLabel
	 * @param stateGroups
     * @return
	 */
    private boolean stateGroupExists(final String stateLabel, final ArrayList<StateGroup> stateGroups)
	{
		boolean contained = true;

		ArrayList<String> assignedStateGroups = getStatesAssignedToStateLabel(stateLabel);

		if (assignedStateGroups != null)
		{
			for (String s : assignedStateGroups)
			{
				boolean sContained = false;
				for (StateGroup stateGroup : stateGroups)
				{
					sContained |= stateGroup.getName().equalsIgnoreCase(s);
				}
				contained &= sContained;
			}
		}
		return contained;
	}

	public void addStateLabel(final String label, final String stateToAssignToLabel)
	{
		if (stateLabelExistsAlready(label))
		{
			ArrayList<String> statesAssignedToLabel = getStatesAssignedToStateLabel(label);
			if (!statesAssignedToLabel.contains(stateToAssignToLabel))
			{
				statesAssignedToLabel.add(stateToAssignToLabel);
				this.stateLabels.remove(label);
				this.stateLabels.put(label, statesAssignedToLabel);
			}
		}
		else
		{
			ArrayList<String> stateNames = new ArrayList<String>();
			if (stateToAssignToLabel != null)
				stateNames.add(stateToAssignToLabel);
			this.stateLabels.put(label, stateNames);
		}
	}

	/**
	 * Remove a label from the set of existing state labels
	 * 
	 * @param labelToRemove
	 */
	public void removeStateLabel(final String labelToRemove)
	{
		if (stateLabelExistsAlready(labelToRemove))
		{
			// remove it from the data structure
			this.stateLabels.remove(labelToRemove);
			// search through all StatesNodes and see if any has the label
			// assigned to it
			Iterator<PerformanceTreeNode> i = this.treeNodes.iterator();
			while (i.hasNext())
			{
				PerformanceTreeNode node = i.next();
				if (node instanceof StatesNode)
				{
					if (((StatesNode) node).getStateLabel().equals(labelToRemove))
					{
						((StatesNode) node).setStateLabel(null);
						((StatesNode) node).setNodeLabel("");
					}
				}
			}
		}
	}

	public void removeStateFromStateLabel(final String stateLabel, final String stateName)
	{
		if (stateLabelExistsAlready(stateLabel))
		{
			ArrayList<String> stateNames = getStatesAssignedToStateLabel(stateLabel);
			if (stateNames.contains(stateName))
			{
				stateNames.remove(stateName);
				this.stateLabels.remove(stateLabel);
				this.stateLabels.put(stateLabel, stateNames);
			}
		}
	}

	/**
	 * Check all defined state labels and if the state is found in any label's
	 * definition, delete it from the definition
	 * 
	 * @param stateName
	 */
	public void removeStateFromAllLabels(final String stateName)
	{
		ArrayList<String> labelAssignmentsToModify = new ArrayList<String>();
		Iterator<String> i = this.stateLabels.keySet().iterator();
		while (i.hasNext())
		{
			String label = i.next();
			ArrayList<String> assignedStates = this.stateLabels.get(label);
			if (assignedStates.contains(stateName))
				labelAssignmentsToModify.add(label);
		}
		Iterator<String> j = labelAssignmentsToModify.iterator();
		while (j.hasNext())
		{
			String labelToModify = j.next();
			ArrayList<String> assignedStates = getStatesAssignedToStateLabel(labelToModify);
			assignedStates.remove(stateName);
			this.stateLabels.remove(labelToModify);
			this.stateLabels.put(labelToModify, assignedStates);
		}
	}

	/**
	 * This checks all labels and renames states with oldStateName to
	 * newStateName
	 * 
	 * @param oldStateName
	 * @param newStateName
	 */
	public void updateLabelsWithNewStateName(final String oldStateName, final String newStateName)
	{
		ArrayList<String> labelAssignmentsToModify = new ArrayList<String>();
		Iterator<String> i = this.stateLabels.keySet().iterator();
		while (i.hasNext())
		{
			String label = i.next();
			ArrayList<String> assignedStates = this.stateLabels.get(label);
			if (assignedStates.contains(oldStateName))
				labelAssignmentsToModify.add(label);
		}
		Iterator<String> j = labelAssignmentsToModify.iterator();
		while (j.hasNext())
		{
			String labelToModify = j.next();
			ArrayList<String> assignedStates = getStatesAssignedToStateLabel(labelToModify);
			assignedStates.remove(oldStateName);
			assignedStates.add(newStateName);
			this.stateLabels.remove(labelToModify);
			this.stateLabels.put(labelToModify, assignedStates);
		}
	}

	/**
	 * Renames a state label
	 * 
	 * @param oldLabelName
	 * @param newLabelName
	 */
	public void renameStateLabel(final String oldLabelName, final String newLabelName)
	{
		if (stateLabelExistsAlready(oldLabelName))
		{
			ArrayList<String> oldLabelsAssignedStates = this.stateLabels.get(oldLabelName);
			this.stateLabels.remove(oldLabelName);
			this.stateLabels.put(newLabelName, oldLabelsAssignedStates);
		}
	}

	/**
	 * Returns a boolean indicating whether a ResultNode has already been
	 * created. This is used in QueryView during the creation of
	 * PerformanceTreeObjects, since we want to ensure that only one instance of
	 * a ResultNode ever exists in a query.
	 * 
	 * @return
	 */
	public boolean resultNodeAlreadyCreated()
	{
		boolean nodeAlreadyCreated = false;
		Iterator<PerformanceTreeNode> i = this.treeNodes.iterator();
		while (i.hasNext())
		{
			PerformanceTreeNode node = i.next();
			if (node instanceof ResultNode)
				nodeAlreadyCreated = true;
		}
		return nodeAlreadyCreated;
	}

	/**
	 * Returns a boolean indicating whether a SequentialNode has already been
	 * created. This is used in QueryView during the creation of
	 * PerformanceTreeObjects, since we want to ensure that only one instance of
	 * a SequentialNode ever exists in a query.
	 * 
	 * @return
	 */
	public boolean sequentialNodeAlreadyCreated()
	{
		boolean nodeAlreadyCreated = false;
		Iterator<PerformanceTreeNode> i = this.treeNodes.iterator();
		while (i.hasNext())
		{
			PerformanceTreeNode node = i.next();
			if (node instanceof SequentialNode)
				nodeAlreadyCreated = true;
		}
		return nodeAlreadyCreated;
	}

	// debug
	/**
	 * prints out the contents of nodesArray and arcsArray
	 */
	public void printQueryDataContents()
	{
		// print node info
		System.out.println("----- Begin QueryData printout for query '" + QueryData.queryName + "-----");
		System.out.println("----- Begin nodesArray data printout -----");
		if (!this.treeNodes.isEmpty())
		{
			Iterator<PerformanceTreeNode> it = this.treeNodes.iterator();
			while (it.hasNext())
			{
				PerformanceTreeNode node = it.next();
				String nodeID = node.getId();
				PetriNetNode nodeType = node.getNodeType();
				System.out.println("The array has a node entry with ID " + nodeID + " and type " +
									nodeType.toString());
				if (node.getParentNode() != null)
				{
					PerformanceTreeNode parentNode = node.getParentNode();
					String parentNodeID = parentNode.getId();
					PetriNetNode parentNodeType = parentNode.getNodeType();
					System.out.println("  a parent node with ID " + parentNodeID + " and type " +
										parentNodeType.toString());
				}
				if (node instanceof OperationNode)
				{
					if (!((OperationNode) node).getOutgoingArcIDs().isEmpty())
					{
						Iterator<String> i = ((OperationNode) node).getOutgoingArcIDs().iterator();
						while (i.hasNext())
						{
							String outgoingArcID = i.next();
							System.out.println("  an outgoing arc with ID " + outgoingArcID + " ");
							if (getArc(outgoingArcID).getTarget() != null)
							{
								PerformanceTreeNode childNode = getArc(outgoingArcID).getTarget();
								String childNodeID = childNode.getId();
								PetriNetNode childNodeType = childNode.getNodeType();
								System.out.println("    linking to a child node with ID " + childNodeID +
													" and type " + childNodeType.toString());
							}
						}
					}
				}
			}
		}
		System.out.println("----- End nodesArray data printout -----");

		// print arc info
		System.out.println("----- Begin arcsArray data printout -----");
		if (!this.treeArcs.isEmpty())
		{
			Iterator<PerformanceTreeArc> it = this.treeArcs.iterator();
			while (it.hasNext())
			{
				PerformanceTreeArc arc = it.next();
				String arcID = arc.getId();
				System.out.println("The array has an arc entry with ID " + arcID);
				if (arc.getSource() != null)
				{
					PerformanceTreeNode arcSource = arc.getSource();
					String arcSourceID = arcSource.getId();
					PetriNetNode arcSourceType = arcSource.getNodeType();
					System.out.println("  a source node with ID " + arcSourceID + " and type " +
										arcSourceType.toString());
				}
				if (arc.getTarget() != null)
				{
					PerformanceTreeNode arcTarget = arc.getTarget();
					String arcTargetID = arcTarget.getId();
					PetriNetNode arcTargetType = arcTarget.getNodeType();
					System.out.println("  a target node with ID " + arcTargetID + " and type " +
										arcTargetType.toString());
				}
			}
		}
		System.out.println("----- End arcsArray data printout -----");

		// print state labels
		System.out.println("----- Begin stateLabels data printout -----");
		if (!this.stateLabels.isEmpty())
		{
			Iterator<String> it = this.stateLabels.keySet().iterator();
			while (it.hasNext())
			{
				String stateLabel = it.next();
				System.out.println("State label '" + stateLabel + "' is defined");
			}
		}
		System.out.println("----- End stateLabels data printout -----");

		// print action labels
		System.out.println("----- Begin actionLabels data printout -----");
		if (!this.actionLabels.isEmpty())
		{
			Iterator<String> it = this.actionLabels.iterator();
			while (it.hasNext())
			{
				String actionLabel = it.next();
				System.out.println("Action label '" + actionLabel + "' is defined.");
			}
		}
		System.out.println("----- End actionLabels data printout -----");
		System.out.println("nodeCounter = " + this.nodeCounter);
		System.out.println("arcCounter = " + this.arcCounter);

		// print macro info
		System.out.println("----- Begin macros data printout -----");
		if (!this.macros.isEmpty())
		{
			Iterator<String> it = this.macros.keySet().iterator();
			while (it.hasNext())
			{
				String macroName = it.next();
				System.out.println("The HashMap has a macro entry with name " + macroName);
				MacroDefinition macro = this.macros.get(macroName);
				macro.printMacroDefintionContents();
			}
		}
		System.out.println("----- End macros data printout -----");
	}

	// debug

	/**
	 * This method checks the current Petri net's data in PetriNet and updates
	 * the Query designer's information about place and action labels in the
	 * net, so that the latest state is reflected. This method is invoked
	 * whenever state or action label assignments are made.
     * @param invokedBy
     * @return
     */
	public boolean checkCurrentData(final String invokedBy)
	{
		String errormsg;
        PetriNetView netViewData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
		try
		{
			if (netViewData.numberOfPlaces() == 0 && netViewData.numberOfTransitions() == 0)
			{
				// no net has been defined yet, so no need to do anything
				errormsg = "Please ensure in the model designer that you have defined\n"
							+ "a Petri net model, since the query designer works with\n"
							+ "information extracted from the underlying model.";
				throw new QueryException(errormsg);
			}
			else
			{
				// something has been drawn on the canvas, so need to extract
				// place
				// names and transition labels from there and update our record
				// of them
				this.placeNames = new ArrayList<String>();
				this.actionLabels = new ArrayList<String>();
				PlaceView[] placeViews = netViewData.places();
				
				if (placeViews.length == 0 && invokedBy.equals("States"))
				{
					errormsg = "Please ensure in the model designer that your PT net has\n"
								+ "places defined in it. Amend your model and try again.";
					throw new QueryException(errormsg);
				}
				int placeCount = 0;
				for (PlaceView p : placeViews)
				{
					placeCount += p.getCurrentMarkingView().getFirst().getCurrentMarking();

					String placeLabel = p.getNameLabel().getName();
					System.out.println(placeLabel);
					if (!this.placeNames.contains(placeLabel))
						this.placeNames.add(placeLabel);
					else
					{
						if (invokedBy.equals("States"))
						{
							errormsg = "Please ensure in the model designer that your PT net doesn't\n"
										+ "feature multiple places with the same name label. Amend your\n"
										+ "model and then try the label assignment again.";
							throw new QueryException(errormsg);
						}
					}
				}
				if (placeCount == 0)
				{
					errormsg = "Please assign at least 1 token to a place on the model";
					throw new QueryException(errormsg);
				}
				TransitionView[] transitionViews = netViewData.getTransitionViews();
				for (TransitionView t : transitionViews)
				{
					String actionName = t.getNameLabel().getText();
					if (!this.actionLabels.contains(actionName))
						this.actionLabels.add(actionName);
					else
					{
						if (invokedBy.equals("Actions"))
						{
							errormsg = "Please ensure in the model designer that your PT net doesn't\n"
										+ "feature multiple transitions with the same name label. Amend\n"
										+ "your model and then try the label assignment again.";
							throw new QueryException(errormsg);
						}
					}
				}
				for (String s : this.stateLabels.keySet())
				{
					if (!stateGroupExists(s, netViewData.getStateGroupsArray()))
					{
						errormsg = "The State label " + s +
									" doesn't correspond to a stategroup in this model\n" + "Please assign " +
									s + " to a stategroup and try again";
						throw new QueryException(errormsg);
					}
				}
				return true;
			}
		}
		catch (QueryException e)
		{
			String msg = e.getMessage();
			JOptionPane.showMessageDialog(	QueryManager.getEditor().getContentPane(),
											msg,
											"Warning",
											JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	public ArrayList<String> getMacroNames()
	{
		ArrayList<String> unsortedMacroNamesList = new ArrayList<String>();
		Iterator<String> i = this.macros.keySet().iterator();
		while (i.hasNext())
		{
			String macroName = i.next();
			unsortedMacroNamesList.add(macroName);
		}
        return sortArrayList(unsortedMacroNamesList);
	}

	public MacroDefinition getMacro(final String name)
	{
		if (this.macros.containsKey(name))
		{
            return this.macros.get(name);
		}
		else return null;
	}

	public void saveMacro(final MacroDefinition macroToSave)
	{
		String macroToSaveName = macroToSave.getName();
		if (this.macros.containsKey(macroToSaveName))
		{
			this.macros.remove(macroToSaveName);
			this.macros.put(macroToSaveName, macroToSave);
		}
		else this.macros.put(macroToSaveName, macroToSave);
	}

	public void deleteMacro(final String macroName)
	{
		if (this.macros.containsKey(macroName))
		{
			this.macros.remove(macroName);
		}
	}

	public boolean macroExistsAlready(final String macroName)
	{
        return this.macros.containsKey(macroName);
	}

	@Override
	public QueryData clone()
	{
		QueryData queryDataCopy = new QueryData();
		QueryData.queryName = QueryData.queryName;
		queryDataCopy.treeNodes = QueryData.deepCopy(this.treeNodes);
		queryDataCopy.treeArcs = QueryData.deepCopy(this.treeArcs);
		return queryDataCopy;
	}

	private static ArrayList deepCopy(final ArrayList original)
	{
		ArrayList copiedList = new ArrayList();
		Iterator i = original.iterator();
		while (i.hasNext())
		{
			PerformanceTreeObject ptObj = (PerformanceTreeObject) i.next();
			PerformanceTreeObject clonedObj = ptObj.clone();
			copiedList.add(clonedObj);
		}
		return copiedList;
	}

}
