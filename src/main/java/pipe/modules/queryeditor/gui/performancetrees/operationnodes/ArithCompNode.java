/**
 * ArithCompNode
 * 
 * Represents an arithmetic comparison of two numerical values.
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


public class ArithCompNode extends OperationNode {

	public ArithCompNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public ArithCompNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("ArithCompNode");
		setNodeType(PetriNetNode.ARITHCOMP);
			
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
		setRequiredChildNode(arithCompChildNum1, NUM_TYPE);
		setRequiredChildNode(arithCompChildNum2, NUM_TYPE);
	}	
	
	public static String getTooltip() {
		return "Arithmetic Comparison  (an arithmetic comparison of two numerical values)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Arithmetic Comparison node represents an arithmetic "+
			"comparison between two numerical values.<br><br>"+
			"Please make sure to select the operation you require, once having created the node, by right-clicking "+
			"on the node with the Select tool and choosing the appropriate operation type.<br><br>"+
			"The required arguments are two expressions that evaluate to numerical values.<br>"+
			"The operator returns a boolean value.");
	}
	
	public String printTextualRepresentation() {
		String description = "";	
		String op = " is in some way comparable to ";
		
		if (getOperation().equals("lt"))
			op = " is less than ";
		else if (getOperation().equals("leq"))
			op = " is less than or equal to ";
		else if (getOperation().equals("eq"))
			op = " is equal to ";
		else if (getOperation().equals("geq"))
			op = " is greater than or equal to ";
		else if (getOperation().equals("gt"))
			op = " is greater than ";
				
		if (getParentNode().getNodeType().equals(PetriNetNode.SEQUENTIAL))
			description += QueryManager.addColouring("is it true that ");
		
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
