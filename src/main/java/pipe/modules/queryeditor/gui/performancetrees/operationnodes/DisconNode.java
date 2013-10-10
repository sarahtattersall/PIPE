/**
 * DisconNode
 * 
 * Represents a boolean disjunction or conjunction.
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



public class DisconNode extends OperationNode {

	public DisconNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public DisconNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("DisconNode");
		setNodeType(PetriNetNode.DISCON);
			
		// only one subnode
		setRequiredArguments(2);
		
		// and nothing else
		setMaxArguments(2);
		
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
		setRequiredChildNode(disConChildBool1, BOOL_TYPE);
		setRequiredChildNode(disConChildBool2, BOOL_TYPE);
	}	
	
	public static String getTooltip() {
		return "Disjunction / Conjunction  (a boolean disjunction or conjunction "+
			"of two logical expressions)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Disjunction / Conjunction node represents a boolean disjunction "+
			"or conjunction between two logical expressions.<br><br>"+
			"Please make sure to select the operation you require, once having created the node, by right-clicking "+
			"on the node with the Select tool and choosing the appropriate operation type.<br><br>"+
			"The required arguments are two logical expressions.<br>"+
			"The operator returns a boolean value.");
	}
	
	public String printTextualRepresentation() {
		String description = "";	
		String op = " and / or ";
		
		if (getOperation().equals("and"))
			op = " and ";
		else if (getOperation().equals("or"))
			op = " or ";
					
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
						description += QueryManager.addColouring("another boolean value that has not been specified yet ");
						QueryManager.colourDown();
					}
				}	
			}
		}
		else {
			QueryManager.colourUp();
			description += QueryManager.addColouring("a boolean value that has not been specified yet ");
			QueryManager.colourDown();
			description += QueryManager.addColouring(op);
			QueryManager.colourUp();
			description += QueryManager.addColouring("another boolean value that has also not been specified yet ");
			QueryManager.colourDown();
		}
		return description + QueryManager.addColouring("holds ");	
	}
	
}
