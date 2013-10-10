/**
 * FiringRateNode
 * 
 * Represents the average firing rate of an action / transition
 * 
 * @author Tamas Suto
 * @date 23/05/07
 */

package pipe.modules.queryeditor.gui.performancetrees.operationnodes;

import java.util.ArrayList;

import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;


public class FiringRateNode extends OperationNode {
	

	public FiringRateNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public FiringRateNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("FiringRateNode");
		setNodeType(PetriNetNode.FIRINGRATE);
		
		// only one subnode
		setRequiredArguments(1);
		
		// and nothing else
		setMaxArguments(1);
		
		// set up required arguments of node
		initialiseRequiredChildNodes();
		
		// set return type
		setReturnType(NUM_TYPE);
		
		// indicate that we want to see labels on the arcs
		showArcLabels = true;
		
		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}
	
	private void initialiseRequiredChildNodes() {		
		setRequiredChildNode(firingRateChildAction, ACTIONS_TYPE);
	}

	public static String getTooltip() {
		return "Firing Rate  (the average occurrence rate of an action)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Firing Rate node represents the average rate of occurrence "+
			"of an action actions.<br><br>"+
			"The required argument is an action.<br>"+
			"The operator returns the firing rate (a real value).");
	}
	
	public String printTextualRepresentation() {
		// set this node's textual description
		String description = QueryManager.addColouring("the average rate of occurrence of ");	
		ArrayList children = getChildNodes();
		if (children != null) {
			PerformanceTreeNode child = (PerformanceTreeNode)children.get(0);
			// print out child's textual representation
			QueryManager.colourUp();
			description += child.printTextualRepresentation();
			QueryManager.colourDown();
		}
		else {
			description += QueryManager.addColouring("an action that has not been specified yet");
		}
		
		return description;	
	}
	
}
