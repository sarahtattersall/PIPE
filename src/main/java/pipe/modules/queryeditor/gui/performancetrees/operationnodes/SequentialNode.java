/**
 * SequentialNode
 * 
 * This is the Performance Tree node that allows to connect multiple independent
 * performance queries into one.
 * 
 * @author Tamas Suto
 * @date 30/04/07
 */

package pipe.modules.queryeditor.gui.performancetrees.operationnodes;

import java.util.ArrayList;
import java.util.Iterator;

import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;


public class SequentialNode extends OperationNode {
	
	private int numberOfQueriesConnected = 0;
	
	public SequentialNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public SequentialNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("SequentialNode");
		setNodeType(PetriNetNode.SEQUENTIAL);
		
		// only one subnode
		setRequiredArguments(2);
		
		// and nothing else
		setMaxArguments(10);
		
		// set up required arguments of node
		initialiseRequiredChildNodes();
		
		// set return type
		setReturnType(MULTI_TYPE);
		
		// indicate that we don't want labels on the arcs
		showArcLabels = true;
		
		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}
	
	private void initialiseRequiredChildNodes() {
		ArrayList requiredNodeTypes = new ArrayList();
		requiredNodeTypes.add(DENS_TYPE);
		requiredNodeTypes.add(DIST_TYPE);
		requiredNodeTypes.add(STATES_TYPE);
		requiredNodeTypes.add(ACTIONS_TYPE);
		requiredNodeTypes.add(NUM_TYPE);
		requiredNodeTypes.add(RANGE_TYPE);
		requiredNodeTypes.add(BOOL_TYPE);
		requiredNodeTypes.add(FUNC_TYPE);
		
		incrementNumberOfQueriesConnected();
		setRequiredChildNode("query "+numberOfQueriesConnected, requiredNodeTypes);
		incrementNumberOfQueriesConnected();
		setRequiredChildNode("query "+numberOfQueriesConnected, requiredNodeTypes);
	}
	
	public static String getTooltip() {
		return "Sequential Composition  (connects multiple independent queries for single job submission)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Sequential node joins together multiple independent queries. This is "+
			"useful when you want to submit only a single query for analysis.<br><br>"+
			"The required arguments can be any operation node, except a Sequential node. "+
			"Once they have been assigned, new arcs will appear to allow for further "+
			"assignments.");
	}
	
	public String printTextualRepresentation() {
		String description = QueryManager.addColouring("the result of the independent evaluation of: <br>");	
		String op = ", and <br>";
		int counter = 0;
					
		ArrayList children = getChildNodes();
		if (children != null) {
			Iterator<PerformanceTreeNode> i = children.iterator();
			while (i.hasNext()) {
				PerformanceTreeNode child = i.next();
				counter++;
				description += QueryManager.addColouring(Integer.toString(counter)+". ");
				QueryManager.colourUp();
				description += child.printTextualRepresentation();	
				if (i.hasNext()) {
					QueryManager.resetTextColour();
					description += QueryManager.addColouring(op);
				}
				else {
					if (children.size() == 1) {
						QueryManager.resetTextColour();
						description += QueryManager.addColouring(op);
						description += QueryManager.addColouring("2. ");
						QueryManager.colourUp();
						description += QueryManager.addColouring("another query that has not been specified yet");
						QueryManager.colourDown();
					}
					else
						QueryManager.colourDown();
				}	
			}
		}
		else {
			description += QueryManager.addColouring("1. ");
			QueryManager.colourUp();
			description += QueryManager.addColouring("a query that has not been specified yet ");
			QueryManager.resetTextColour();
			description += QueryManager.addColouring(op);
			description += QueryManager.addColouring("2. ");
			QueryManager.colourUp();
			description += QueryManager.addColouring("another query that has also not been specified yet");
			QueryManager.colourDown();
		}
		return description;	
	}
			
	public int getNumberOfQueriesConnected() {
		return numberOfQueriesConnected;
	}
	
	void incrementNumberOfQueriesConnected() {
		numberOfQueriesConnected++;
	}
	
	/**
	 * This checks whether both required child nodes have been assigned. A SequentialNode makes
	 * only sense when it connects at least 2 sub-queries, but of course it can connect more than
	 * that. To cater for that case, when both required nodes have been assigned, a new optional
	 * arc needs to appear connected to the SequentialNode.
	 *  
	 * @return
	 */
	public boolean allArcsAssigned() {
		boolean allAssigned = true;
		Iterator i = outgoingArcIDs.iterator();
		while (i.hasNext()) {
			String arcID = (String)i.next();
			PerformanceTreeArc arc = QueryManager.getData().getArc(arcID);
			if (arc.getTarget() == null) {
				allAssigned = false;
			}
		}	
		return allAssigned;
	}
	
	/**
	 * This method tells us whether the node has an optional outgoing arc
	 * that has not been assigned to a node yet. This is needed when connecting
	 * up nodes to the SequentialNode, since we don't want to create new 
	 * optional arcs unnecessarily if there's already one that's not been assigned 
	 * yet. 
	 * @return
	 */
	public boolean haveOptionalArcAvailable() {
		boolean optionalArcAvailable = false;
		Iterator i = outgoingArcIDs.iterator();
		while (i.hasNext()) {
			String arcID = (String)i.next();
			PerformanceTreeArc arc = QueryManager.getData().getArc(arcID);
			if (!arc.isRequired() && arc.getTarget() == null) {
				optionalArcAvailable = true;
			}
		}	
		return optionalArcAvailable;
	}
	
	/**
	 * This method makes another optional arc appear emanating from the node.
	 * It's used for the case when both initially required child nodes have
	 * been assigned.
	 */
	public void drawAdditionalOptionalArc() {
		
		// set up the valid types of nodes that can be assigned to the optional arc
		ArrayList optionalNodeTypes = new ArrayList();		
		optionalNodeTypes.add(DENS_TYPE);
		optionalNodeTypes.add(DIST_TYPE);
		optionalNodeTypes.add(STATES_TYPE);
		optionalNodeTypes.add(ACTIONS_TYPE);
		optionalNodeTypes.add(NUM_TYPE);
		optionalNodeTypes.add(RANGE_TYPE);
		optionalNodeTypes.add(BOOL_TYPE);
		optionalNodeTypes.add(FUNC_TYPE);
		
		incrementNumberOfQueriesConnected();
		setOptionalChildNode("query "+numberOfQueriesConnected, optionalNodeTypes);
		
		// create a new PerformanceTreeArc
		double arcStartPointX = positionX + (componentWidth / 2);
		double arcStartPointY = positionY + componentHeight;
		double arcEndPointX = positionX + (componentWidth * 2);
		double arcEndPointY = positionY;
		String role = "query "+numberOfQueriesConnected;
    	PerformanceTreeArc arc = new PerformanceTreeArc(arcStartPointX, arcStartPointY, arcEndPointX, arcEndPointY, this, role, showArcLabels);
    	arc.setRequired(false);
    	
    	// add it to QueryData
    	arc = (PerformanceTreeArc)QueryManager.getData().addPerformanceTreeObject(arc);
    	
    	// update node to indicate that it has an additional arc
    	String arcID = arc.getId();
    	outgoingArcIDs.add(arcID);
    	QueryManager.getData().updateNode(this);
    	
    	// get it to draw on the canvas
    	QueryManager.getView().addNewPerformanceTreeObject(arc);
	}

}
