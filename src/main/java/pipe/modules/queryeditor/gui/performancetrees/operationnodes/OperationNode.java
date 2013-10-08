/**
 * OperationNode
 * 
 * Implements common methods for Performance Tree Operation Nodes
 * 
 * @author Tamas Suto
 * @date 24/04/07
 */

package pipe.modules.queryeditor.gui.performancetrees.operationnodes;

import pipe.common.PetriNetNode;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.QueryView;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroEditor;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroView;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public abstract class OperationNode extends PerformanceTreeNode
{

	protected ArrayList	outgoingArcIDs;
	// the set of IDs of outgoing arcs connecting the node to its children

	private int		requiredArguments;
	// how many arguments (child nodes) are required as the bare minimum

	int		maxArguments;
	// no. of total possible arguments (child nodes)

	private HashMap	optionalChildNodes;
	// Types of nodes that are permitted to be used as *optional* arguments to
	// the
	// current node. The HashMap is indexed by a String that represents the role
	// of
	// the node and corresponds to either a String, indicating a node type that
	// can be
	// assigned to that role, or an ArrayList, which lists the types of nodes
	// that can
	// be assigned to that role. This is so because sometimes, like in Result or
	// Sequential, there's only one role, but many possible types that can be
	// assigned
	// to it. Since a HashMap's keys need to be unique, it could only be made to
	// work
	// with an ArrayList element corresponding to the one role.
	protected ArrayList	optionalChildNodesOrdered;

	HashMap	requiredChildNodes;
	// This HashMap specifies which roles are required to be fulfilled by
	// subnodes.
	// The HashMap is indexed in the same way as above, i.e. by a String
	// representing
	// the role, which corresponds to a String, representing the type of node
	// that is
	// required to be assigned to the role, or an ArrayList, which lists the
	// types of
	// nodes that can be assigned to the required role.
	protected ArrayList	requiredChildNodesOrdered;

	protected ArrayList	requiredArcs;
	// These are the arcs for the required nodes that are drawn when the node is
	// created.

	protected ArrayList	optionalArcs;
	// These are the arcs for the optional nodes that are drawn when the node is
	// created.

	protected boolean	showArcLabels;
	// boolean indicating whether we want arc labels to be displayed for this
	// particular node

	private String		operation	= "";

	// used for nodes that represent a multitude of operations

	public OperationNode(	final double positionXInput,
							final double positionYInput,
							final String idInput,
							final PetriNetNode typeInput) {
		super(positionXInput, positionYInput, idInput, typeInput);
		setupNode();
	}

	protected OperationNode(final double positionXInput, final double positionYInput, final String idInput) {
		super(positionXInput, positionYInput, idInput);
		setupNode();
	}

	protected OperationNode(final double positionXInput, final double positionYInput) {
		super(positionXInput, positionYInput);
		setupNode();
	}

	private void setupNode()
	{
		this.outgoingArcIDs = new ArrayList();
        this.optionalChildNodes = new HashMap();
		this.optionalChildNodesOrdered = new ArrayList();
		this.requiredChildNodes = new HashMap();
		this.requiredChildNodesOrdered = new ArrayList();
		this.requiredArcs = new ArrayList();
		this.optionalArcs = new ArrayList();
	}

	/**
	 * Responsible for drawing outgoing arcs with labels for required and
	 * optional child nodes. Is called from the subclasses, since they need to
	 * first initialise the HashMaps containing required and optional arguments
	 * (children), which this method makes use of.
	 */
	protected void setupOutgoingArcs()
	{
		if (QueryManager.getMode() != QueryConstants.LOADING &&
			MacroManager.getMode() != QueryConstants.LOADING)
		{
			// don't do this when loading in nodes, otherwise unwanted arcs will
			// be created
			PerformanceTreeArc arc;
			int arcsToDraw = 0;

			// find out how many arcs need to be drawn in total (required and
			// optional arcs together
			String role;
			Iterator mapIterator = this.requiredChildNodes.keySet().iterator();
			while (mapIterator.hasNext())
			{
				role = (String) mapIterator.next();
				arcsToDraw++;
			}
			mapIterator = this.optionalChildNodes.keySet().iterator();
			while (mapIterator.hasNext())
			{
				role = (String) mapIterator.next();
				arcsToDraw++;
			}

			// the center of the lower side of the node. The point that we
			// use as a reference to calculate the X-coordinates of the arc
			// end points
			double arcConnectionPointX = this.positionX + this.componentWidth / 2;
			double arcConnectionPointY = this.positionY + this.componentHeight;

			double arcStartPointX, arcStartPointY, arcEndPointX, arcEndPointY;
			int noOfArcsOnTheSide;
			// how many arcs are on either side of the divider line coming down
			// from arcConnectionPoint
			double spacing = this.componentWidth * 2.5;
			// how much spacing there should be between the arcs' endpoints

			arcStartPointX = arcConnectionPointX;
			arcStartPointY = arcConnectionPointY;
			arcEndPointY = arcStartPointY + this.componentHeight * 1.5;

			// find out the coordinates of the leftmost arc. From then on
			// it's easy, since only a spacing value will need to be added
			// to get the next arc's endpoint
			if (arcsToDraw % 2 == 0)
			{
				// we have an even number of arcs, so there won't be an arc
				// coming down
				// straight from arcConnectionPoint in the middle of the node
				noOfArcsOnTheSide = arcsToDraw / 2;
				arcEndPointX = arcConnectionPointX - ((noOfArcsOnTheSide - 1) * spacing + spacing / 2);
			}
			else
			{
				// we have an odd number of arcs, so we will also have an arc
				// coming down straight from the middle of the component
				noOfArcsOnTheSide = (arcsToDraw + 1) / 2 - 1;
				arcEndPointX = arcConnectionPointX - noOfArcsOnTheSide * spacing;
			}

			// Set up arcs for required nodes
			mapIterator = this.requiredChildNodesOrdered.iterator();
			while (mapIterator.hasNext())
			{
				role = (String) mapIterator.next();
				arc = new PerformanceTreeArc(	arcStartPointX,
												arcStartPointY,
												arcEndPointX,
												arcEndPointY,
												this,
												role,
												this.showArcLabels);
				arc.setRequired(true);
				this.requiredArcs.add(arc);

				// Set coordinates for next arc
				arcEndPointX += spacing;
			}

			// Set up arcs for optional nodes
			mapIterator = this.optionalChildNodesOrdered.iterator();
			while (mapIterator.hasNext())
			{
				role = (String) mapIterator.next();
				arc = new PerformanceTreeArc(	arcStartPointX,
												arcStartPointY,
												arcEndPointX,
												arcEndPointY,
												this,
												role,
												this.showArcLabels);
				arc.setRequired(false);
				this.optionalArcs.add(arc);

				// Set coordinates for next arc
				arcEndPointX += spacing;
			}
		}
	}

	public ArrayList getOutgoingArcIDs()
	{
		return this.outgoingArcIDs;
	}

	public void setOutgoingArcIDs(final ArrayList input)
	{
		this.outgoingArcIDs = input;
	}

	public void addOutgoingArcID(final String arcID)
	{
		if (!this.outgoingArcIDs.contains(arcID))
			this.outgoingArcIDs.add(arcID);
	}

	public void removeOutgoingArcID(final String arcID)
	{
		if (this.outgoingArcIDs.contains(arcID))
			this.outgoingArcIDs.remove(arcID);
	}

	public int getRequiredArguments()
	{
		return this.requiredArguments;
	}

	protected void setRequiredArguments(final int newValue)
	{
		this.requiredArguments = newValue;
	}

	public int getMaxArguments()
	{
		return this.maxArguments;
	}

	public void setMaxArguments(final int maxArg)
	{
		this.maxArguments = maxArg;
	}

	public HashMap getOptionalChildNodes()
	{
		return this.optionalChildNodes;
	}

	void setOptionalChildNode(final String role, final String nodeType)
	{
		// keep the ordering
		this.optionalChildNodesOrdered.add(role);
		// store the actual node type associated with the role
		this.optionalChildNodes.put(role, nodeType);
	}

	/**
	 * For the case when a number of node types are valid for a possible role
	 * for a node
     * @param role
     * @param nodeTypes
     */
	public void setOptionalChildNode(final String role, final ArrayList nodeTypes)
	{
		// keep the ordering
		this.optionalChildNodesOrdered.add(role);
		// store the actual node types associated with the role
		this.optionalChildNodes.put(role, nodeTypes);
	}

	public HashMap getRequiredChildNodes()
	{
		return this.requiredChildNodes;
	}

	void setRequiredChildNode(final String role, final String nodeType)
	{
		// keep the ordering
		this.requiredChildNodesOrdered.add(role);
		// store the actual node type associated with the role
		this.requiredChildNodes.put(role, nodeType);
	}

	/**
	 * For the case when a number of node types are valid for a required role
	 * for a node
     * @param role
     * @param nodeTypes
     */
	public void setRequiredChildNode(final String role, final ArrayList nodeTypes)
	{
		// keep the ordering
		this.requiredChildNodesOrdered.add(role);
		// store the actual node types associated with the role
		this.requiredChildNodes.put(role, nodeTypes);
	}

	protected ArrayList getChildNodes()
	{
		ArrayList children = new ArrayList();
		if (this.outgoingArcIDs != null)
		{
			// node could have some children
			Iterator it = this.outgoingArcIDs.iterator();
			while (it.hasNext())
			{
				String outgoingArcID = (String) it.next();
				PerformanceTreeArc outgoingArc;
				if (MacroManager.getEditor() == null)
					outgoingArc = QueryManager.getData().getArc(outgoingArcID);
				else outgoingArc = MacroManager.getEditor().getArc(outgoingArcID);
				if (outgoingArc.getTarget() != null)
				{
					// node has a child node
					PerformanceTreeNode childNode = outgoingArc.getTarget();
					children.add(childNode);
				}
			}
		}
		if (children.isEmpty())
			return null;
		else return children;
	}

	public ArrayList getRequiredArcs()
	{
		return this.requiredArcs;
	}

	public ArrayList getOptionalArcs()
	{
		return this.optionalArcs;
	}

	public boolean getArcLabelsRequired()
	{
		return this.showArcLabels;
	}

	public String getOperation()
	{
		return this.operation;
	}

	public void setOperation(final String operationInput)
	{
		// update variable to indicate what it now represents
		this.operation = operationInput;

		// update image
		URL newImageURL = Thread.currentThread().getContextClassLoader().getResource(QueryManager.imgPath +
																						this.nodeType + "-" +
																						operationInput +
																						".png");
		setNodeImage(newImageURL);
	}

	/**
	 * This method adds the argument arcs to the drawing canvas
	 * 
	 * @param canvas
     * @param container
	 */
	public void addArcsToContainer(final JLayeredPane container)
	{
		PerformanceTreeArc outgoingArc;
		// add required arcs
		Iterator it = this.requiredArcs.iterator();
		while (it.hasNext())
		{
			outgoingArc = (PerformanceTreeArc) it.next();
			if (MacroManager.getEditor() == null)
				((QueryView) container).addNewPerformanceTreeObject(outgoingArc);
			else ((MacroView) container).addNewMacroObject(outgoingArc);
		}
		// add optional arcs
		it = this.optionalArcs.iterator();
		while (it.hasNext())
		{
			outgoingArc = (PerformanceTreeArc) it.next();
			if (MacroManager.getEditor() == null)
				((QueryView) container).addNewPerformanceTreeObject(outgoingArc);
			else ((MacroView) container).addNewMacroObject(outgoingArc);
		}
	}

	@Override
	public void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
	}

	/** Handles selection for Nodes */
	@Override
	public void select()
	{
		if (this.selectable && !this.selected)
		{
			super.select();
			if (this.outgoingArcIDs != null)
			{
				// select outgoing arcs as well
				Iterator it = this.outgoingArcIDs.iterator();
				while (it.hasNext())
				{
					String outgoingArcID = (String) it.next();
					PerformanceTreeArc outgoingArc;
					if (MacroManager.getEditor() == null)
					{
						// we're selecting an arc on the main drawing canvas
						outgoingArc = QueryManager.getData().getArc(outgoingArcID);
					}
					else
					{
						// we're selecting an arc in the macro editor
						outgoingArc = MacroManager.getEditor().getArc(outgoingArcID);
					}
					outgoingArc.select();
				}
			}
		}
	}

	/** Updates location of any attached arcs */
	@Override
	public void updateConnected()
	{
		// update incoming arc
		super.updateConnected();

		// update outgoing arcs
		Iterator it = this.outgoingArcIDs.iterator();
		while (it.hasNext())
		{
			String outgoingArcID = (String) it.next();
			PerformanceTreeArc outgoingArc;
			if (MacroManager.getEditor() == null)
				outgoingArc = QueryManager.getData().getArc(outgoingArcID);
			else outgoingArc = MacroManager.getEditor().getArc(outgoingArcID);
			updateEndPoint(outgoingArc);
			outgoingArc.updateArcPosition();
			if (MacroManager.getEditor() == null)
				QueryManager.getData().updateArc(outgoingArc);
			else MacroManager.getEditor().updateArc(outgoingArc);
		}
	}

	/**
	 * This method is called when an arc emanating from this node is about to be
	 * connected to another node. It verifies the validity of the connection. It
	 * doesn't check whether the target node already has a parent assigned, as
	 * that would happen in the method
	 * PerformanceTreeObjectHandler.mouseReleased. It only checks if the node
	 * we're trying to assign to this node as a child is among the list of
	 * required or possible child nodes.
	 * 
	 * @param arc -
	 *            the arc of this node that we're trying to connect to the
	 *            target node
	 * @param node -
	 *            the node that we're trying to connect to (i.e. assign as a
	 *            child)
	 * @return - true if the connection is valid, false otherwise
	 */
	@Override
	public boolean childAssignmentValid(final PerformanceTreeArc arc, final PerformanceTreeNode node)
	{
		PetriNetNode thisNodeType = getNodeType();
		String possibleChildReturnType = node.getReturnType();
		String role = arc.getArcLabel();
		String problem;

		// Can we assign the subnode? Have we not reached the limit of
		// assignable subnodes in total?
		if (numberOfChildren() < this.maxArguments)
		{
			// Does the node we're trying to assign as a subnode have a legal
			// return type?
			if (this.requiredChildNodes.containsKey(role))
			{
				// the role is amongst the required roles to be filled in
				if (this.requiredChildNodes.get(role) instanceof String)
				{
					// we only have a single node return type assigned for a
					// role
					if (this.requiredChildNodes.get(role).equals(possibleChildReturnType))
						// the roles and return types match, so the assignment
						// is valid
						return true;
				}
				else if (this.requiredChildNodes.get(role) instanceof ArrayList)
				{
					// we have a number of possible node return types assigned
					// for the same role
					ArrayList nodeReturnTypes = (ArrayList) this.requiredChildNodes.get(role);
					if (nodeReturnTypes.contains(possibleChildReturnType))
						// node return type is amongst the valid ones for the
						// role
						return true;
				}
			}
			else if (this.optionalChildNodes.containsKey(role))
			{
				// the role is amongst the possible roles to fill in
				if (this.optionalChildNodes.get(role) instanceof String)
				{
					// we only have a single node return type assigned to a role
					if (this.optionalChildNodes.get(role).equals(possibleChildReturnType))
						// the roles and return types match, so the assignment
						// is valid
						return true;
				}
				else if (this.optionalChildNodes.get(role) instanceof ArrayList)
				{
					// we have a number of possible node return types assigned
					// for the same role
					ArrayList nodeReturnTypes = (ArrayList) this.optionalChildNodes.get(role);
					if (nodeReturnTypes.contains(possibleChildReturnType))
						// node return type is amongst the valid ones for the
						// role
						return true;
				}
			}
			else
			{
				// We're trying to assign an invalid subnode
				problem = node.getNodeType() + " is not a legal subnode with role " + role + " of a " +
							thisNodeType + " node.";
				if (MacroManager.getEditor() == null)
					QueryManager.appendToInfoBox(problem);
				else MacroEditor.writeToInfoBox(problem);
			}
		}
		else
		{
			problem = "The " + thisNodeType +
						" node already has its maximum allowed number of subnodes assigned.";
			if (MacroManager.getEditor() == null)
				QueryManager.appendToInfoBox(problem);
			else MacroEditor.writeToInfoBox(problem);
		}

		return false;
	}

	/**
	 * This methods goes through the outgoing arcs and sees how many have
	 * targets, i.e. how many children the node has
	 * 
	 * @return
	 */
    int numberOfChildren()
	{
		int numOfChildren = 0;
		Iterator it = this.outgoingArcIDs.iterator();
		while (it.hasNext())
		{
			String outgoingArcID = (String) it.next();
			PerformanceTreeArc outgoingArc;
			if (MacroManager.getEditor() == null)
				outgoingArc = QueryManager.getData().getArc(outgoingArcID);
			else outgoingArc = MacroManager.getEditor().getArc(outgoingArcID);
			if (outgoingArc.getTarget() != null)
				numOfChildren++;
		}
		return numOfChildren;
	}

	/**
	 * Implemented in child nodes
	 */
	@Override
	public String printTextualRepresentation()
	{
		return "";
	}

	@Override
	public OperationNode clone()
	{
        return (OperationNode) super.clone();
	}

}
