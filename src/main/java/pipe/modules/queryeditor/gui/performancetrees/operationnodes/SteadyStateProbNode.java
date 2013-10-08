/**
 * SteadyStateProbNode
 * 
 * Represents the steady-state probability distribution of an arbitrary state function
 * over a set of states.
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



public class SteadyStateProbNode extends OperationNode {

	public SteadyStateProbNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public SteadyStateProbNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("SteadyStateProbNode");
		setNodeType(PetriNetNode.STEADYSTATEPROB);
			
		// only one subnode
		setRequiredArguments(2);
		
		// and nothing else
		setMaxArguments(2);
		
		// set up required arguments of node
		initialiseRequiredChildNodes();
		
		// set return type
		setReturnType(DIST_TYPE);
		
		// indicate that we want labels on the arcs
		showArcLabels = true;
		
		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}
	
	private void initialiseRequiredChildNodes() {	
		setRequiredChildNode(sSPChildStateFunc, FUNC_TYPE);
		setOptionalChildNode(sSPChildStates, STATES_TYPE);
	}	
	
	public static String getTooltip() {
		return "Steady-State Probability  (the steady-state probability distribution for a given set of states)";
	}
	
	public static String getNodeInfo() {
		return  QueryManager.addColouring("The Steady-State Probability node represents the "+
			"steady-state probability distribution of an arbitrary state function over a set "+
			"of states.<br><br>"+
			"The required arguments are a set of states and a function on that set of states.\n"+
			"The operator returns a probability distribution.");
	}
	
	public String printTextualRepresentation() {
		String description = QueryManager.addColouring("the steady-state probability distribution of ");	
		String op = " applied over ";
					
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
						description += QueryManager.addColouring("the set of all states in the model ");
						QueryManager.colourDown();
					}
				}		
			}
		}
		else {
			QueryManager.colourUp();
			description += QueryManager.addColouring("a state function that has not been specified yet ");
			QueryManager.colourDown();
			description += QueryManager.addColouring(op);
			QueryManager.colourUp();
			description += QueryManager.addColouring("the set of all states in the model ");
			QueryManager.colourDown();
		}
		return description;	
	}
	
}
