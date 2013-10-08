/**
 * ProbInStatesNode
 * 
 * Represents the probability of being in a given set of states at a given
 * point in time, having started from a given set of states.
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

public class ProbInStatesNode extends OperationNode {

	public ProbInStatesNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public ProbInStatesNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("ProbInStatesNode");
		setNodeType(PetriNetNode.PROBINSTATES);
			
		// only one subnode
		setRequiredArguments(3);
		
		// and nothing else
		setMaxArguments(3);
		
		// set up required arguments of node
		initialiseRequiredChildNodes();
		
		// set return type
		setReturnType(NUM_TYPE);
		
		// indicate that we want labels on the arcs
		showArcLabels = true;
		
		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}
	
	private void initialiseRequiredChildNodes() {
		//setRequiredChildNode(probInStatesChildStartStates, STATES_TYPE);
		setRequiredChildNode(probInStatesChildObservedStates, STATES_TYPE);
		setRequiredChildNode(probInStatesChildNum, NUM_TYPE);
	}

	public static String getTooltip() {
		return "Probability Of Being In States  (the transient probability of the system being in a given set of" +
			" states at a given instant in time)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Probability In States node represents the transient probability of the system being "+
			"in a given set of states at a given instant in time.<br><br>"+
			"The required arguments are the set of start states, the set of target states and the time "+
			"instant at which to consider the state of the model.<br>" +
			"The operator returns a probability value.");
	}
	
	public String printTextualRepresentation() {
		String description = QueryManager.addColouring("the transient probability of the system having started in ");	
		String op1 = " and being in ";
		String op2 = " at the time instant given by ";	
		
		ArrayList children = getChildNodes();
		if (children != null) {
			int childrenLeft = 3;
			Iterator<PerformanceTreeNode> i = children.iterator();
			while (i.hasNext()) {
				PerformanceTreeNode child = i.next();	
				QueryManager.colourUp();
				description += child.printTextualRepresentation();	
				QueryManager.colourDown();
				childrenLeft--;
				if (i.hasNext()) {
					if (childrenLeft == 2) 
						description += QueryManager.addColouring(op1);
					else if (childrenLeft == 1) 
						description += QueryManager.addColouring(op2);
				}
				else {
					if (childrenLeft == 2) {
						description += QueryManager.addColouring(op1);
						QueryManager.colourUp();
						description += QueryManager.addColouring("a set of states that has not been specified yet "+
								"at an unspecified time instant");
						QueryManager.colourDown();
					}
					else if (childrenLeft == 1) {
						QueryManager.colourUp();
						description += QueryManager.addColouring(" at an unspecified time instant");
						QueryManager.colourDown();
					}
				}	
			}
		}
		else {
			QueryManager.colourUp();
			description += QueryManager.addColouring("a set of states that has not been specified yet");
			QueryManager.colourDown();
			description += QueryManager.addColouring(op1);
			QueryManager.colourUp();
			description += QueryManager.addColouring("a set of states that has also not been specified yet");
			QueryManager.colourDown();
			description += QueryManager.addColouring(op2);
			QueryManager.colourUp();
			description += QueryManager.addColouring("that is currently unknown");
			QueryManager.colourDown();
		}
		return description;	
	}
	
}
