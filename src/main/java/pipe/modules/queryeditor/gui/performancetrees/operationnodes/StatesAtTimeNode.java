/**
 * StatesAtTimeNode
 * 
 * Represents the set of states that the system can occupy at a given time instant
 * 
 * @author Tamas Suto
 * @date 23/05/07
 */

package pipe.modules.queryeditor.gui.performancetrees.operationnodes;

import java.util.ArrayList;
import java.util.Iterator;

import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;


public class StatesAtTimeNode extends OperationNode {
	

	public StatesAtTimeNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public StatesAtTimeNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("StatesAtTimeNode");
		setNodeType(PetriNetNode.STATESATTIME);
		
		// only one subnode
		setRequiredArguments(2);
		
		// and nothing else
		setMaxArguments(2);
		
		// set up required arguments of node
		initialiseRequiredChildNodes();
		
		// set return type
		setReturnType(STATES_TYPE);
		
		// indicate that we want to see labels on the arcs
		showArcLabels = true;
		
		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}
	
	private void initialiseRequiredChildNodes() {		
		setRequiredChildNode(statesAtTChildNum, NUM_TYPE);
		setRequiredChildNode(statesAtTChildRange, RANGE_TYPE);
	}
	
	public static String getTooltip() {
		return "States At Time  (the set of states that the system can occupy at a given time instant)";
	}
	
	public static String getNodeInfo() {
		return  QueryManager.addColouring("The States At Time node represents the set of states "+
			"that the system can occupy at a given time instant with a given probability.<br><br>"+
			"The required arguments are the time instant and a probability range.<br>"+
			"The operator returns a set of states.");
	}
	
	public String printTextualRepresentation() {
		String description = QueryManager.addColouring("the set of states that the system can be in at time instant ");	
		String op = " within a probability bound given by ";
					
		ArrayList children = getChildNodes();
		if (children != null) {
			Iterator<PerformanceTreeNode> i = children.iterator();
			while (i.hasNext()) {
				PerformanceTreeNode child = i.next();
				QueryManager.colourUp();
				description += child.printTextualRepresentation();	
				QueryManager.colourDown();
				if (i.hasNext()) 			
					description += QueryManager.addColouring(op);
				else {
					if (children.size() == 1) {
						description += QueryManager.addColouring(op);
						QueryManager.colourUp();
						description += QueryManager.addColouring(" a range that has not been specified yet");
						QueryManager.colourDown();
					}
				}	
			}
		}
		else {
			QueryManager.colourUp();
			description += QueryManager.addColouring("an unspecified numerical value ");
			QueryManager.colourDown();
			description += QueryManager.addColouring(op);
			QueryManager.colourUp();
			description += QueryManager.addColouring(" a range that has not been specified yet either");
			QueryManager.colourDown();
		}
		return description;	
	}

}
