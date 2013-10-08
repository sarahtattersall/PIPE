/**
 * ArithOpNode
 * 
 * Represents an arithmetic operation between two numerical values.
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



public class ArithOpNode extends OperationNode {

	public ArithOpNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public ArithOpNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("ArithOpNode");
		setNodeType(PetriNetNode.ARITHOP);
			
		// only one subnode
		setRequiredArguments(2);
		
		// and nothing else
		setMaxArguments(2);
		
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
		setRequiredChildNode(arithOpChildNum1, NUM_TYPE);
		setRequiredChildNode(arithOpChildNum2, NUM_TYPE);
	}	
	
	public static String getTooltip() {
		return "Arithmetic Operation  (an arithmetic operation on two numerical values)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Arithmetic Operation node represents an arithmetic operation on two "+
			"numerical values.<br><br>"+
			"Please make sure to select the operation you require, once having created the node, by right-clicking "+
			"on the node with the Select tool and choosing the appropriate operation type.<br><br>"+
			"The required arguments are two expressions that evaluate to numerical values.<br>"+
			"The operator returns a numerical value.");
	}
	
	public String printTextualRepresentation() {
		String description = "";	
		String op = " operated on in some way by ";
		
		if (getOperation().equals("plus"))
			op = QueryManager.addColouring(" plus ");
		else if (getOperation().equals("minus"))
			op = QueryManager.addColouring(" minus ");
		else if (getOperation().equals("times"))
			op = QueryManager.addColouring(" multiplied by ");
		else if (getOperation().equals("div"))
			op = QueryManager.addColouring(" divided by ");
		else if (getOperation().equals("power"))
			op = QueryManager.addColouring(" raised to the power of ");
					
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
						description += QueryManager.addColouring("another numerical value that has not been specified yet ");
						QueryManager.colourDown();
					}
				}	
			}
		}
		else {
			QueryManager.colourUp();
			description += QueryManager.addColouring("a numerical value that has not been specified yet ");
			QueryManager.colourDown();
			description += QueryManager.addColouring(op);
			QueryManager.colourUp();
			description += QueryManager.addColouring("another numerical value that has also not been specified yet ");
			QueryManager.colourDown();
		}
		return description;	
	}
	
}
