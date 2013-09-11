/**
 * NegationNode
 * 
 * Represents a boolean negation.
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


public class NegationNode extends OperationNode {

	public NegationNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public NegationNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("NegationNode");
		setNodeType(PetriNetNode.NEGATION);
			
		// only one subnode
		setRequiredArguments(1);
		
		// and nothing else
		setMaxArguments(1);
		
		// set up required arguments of node
		initialiseRequiredChildNodes();
		
		// set return type
		setReturnType(BOOL_TYPE);
		
		// indicate that we want labels on the arcs
		showArcLabels = true;
		
		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}
	
	private void initialiseRequiredChildNodes() {
		setRequiredChildNode(negChildBool, BOOL_TYPE);
	}	
	
	public static String getTooltip() {
		return "Negation  (the boolean negation of a logical expression)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Negation node represents a boolean negation of a logical expression.<br><br>"+
			"The required argument is a logical expression.<br>"+
			"The operator returns a boolean value.");
	}
	
	public String printTextualRepresentation() {
		String description = QueryManager.addColouring("the negation of ");	
		ArrayList children = getChildNodes();
		if (children != null) {
			Iterator<PerformanceTreeNode> i = children.iterator();
			while (i.hasNext()) {
				PerformanceTreeNode child = i.next();	
				// print out child's textual representation
				QueryManager.colourUp();
				description += child.printTextualRepresentation();
				QueryManager.colourDown();
			}
		}
		else {
			description += QueryManager.addColouring("a boolean expression that has not been specified yet");
		}
		return description + QueryManager.addColouring(" holds");	
	}
	
}
