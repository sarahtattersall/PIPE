/**
 * SteadyStateStatesNode
 * 
 * Represents the set of states that have a steady-state probability of a certain 
 * value, represented by a probability range
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


public class SteadyStateStatesNode extends OperationNode {

	public SteadyStateStatesNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public SteadyStateStatesNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("SteadyStateStatesNode");
		setNodeType(PetriNetNode.STEADYSTATESTATES);
			
		// only one subnode
		setRequiredArguments(1);
		
		// and nothing else
		setMaxArguments(1);
		
		// set up required arguments of node
		initialiseRequiredChildNodes();
		
		// set return type
		setReturnType(STATES_TYPE);
		
		// indicate that we want labels on the arcs
		showArcLabels = true;
		
		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}
	
	private void initialiseRequiredChildNodes() {
		//setRequiredChildNode(sSSChildStartStates, STATES_TYPE);
		setRequiredChildNode(sSSChildRange, RANGE_TYPE);
	}
	
	public static String getTooltip() {
		return "Steady-State States  (the set of states that have a certain steady-state probability)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Steady-State States node represents the set of states "+
			"that have a steady-state probability of a certain value.<br><br>"+
			"The required arguments are the set of start states and a probability range.<br>"+
			"The operator returns a set of states.");
	}
	
	public String printTextualRepresentation() {
		String description = QueryManager.addColouring("the set of states that, provided that the system has started in ");	
		String op = ", has a certain steady-state probability lying in ";
					
		ArrayList children = getChildNodes();
		if (children != null) {
			Iterator<PerformanceTreeNode> i = children.iterator();
			while (i.hasNext()) {
				PerformanceTreeNode child = i.next();
				QueryManager.colourUp();
				description += child.printTextualRepresentation();	
				QueryManager.colourDown();
				if (i.hasNext()) 
					description += QueryManager.addColouring(op)+" ";
				else {
					if (children.size() == 1) {
						description += QueryManager.addColouring(op);
						QueryManager.colourUp();
						description += QueryManager.addColouring("a range that has not been specified yet");
						QueryManager.colourDown();
					}
				}	
			}
		}
		else {
			QueryManager.colourUp();
			description += QueryManager.addColouring("a set of states that has not been specified yet ");
			QueryManager.colourDown();
			description += QueryManager.addColouring(op);
			QueryManager.colourUp();
			description += QueryManager.addColouring("a range that has also not been specified yet");
			QueryManager.colourDown();
		}
		return description;	
	}
	
}
