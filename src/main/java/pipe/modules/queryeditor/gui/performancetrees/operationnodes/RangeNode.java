/**
 * RangeNode
 * 
 * Represents a numerical interval. It is really a value node, since it represents a numerical
 * range and doesn't stand for any operation in particular, but since it is the only value
 * node that has children, for convenience's sake, it is classified as an operation node.
 * 
 * @author Tamas Suto
 * @date 21/05/07
 */

package pipe.modules.queryeditor.gui.performancetrees.operationnodes;

import java.util.ArrayList;
import java.util.Iterator;

import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;


public class RangeNode extends OperationNode {

	public RangeNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public RangeNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("RangeNode");
		setNodeType(PetriNetNode.RANGE);
			
		// only one subnode
		setRequiredArguments(2);
		
		// and nothing else
		setMaxArguments(2);
		
		// set up required arguments of node
		initialiseRequiredChildNodes();
		
		// set return type
		setReturnType(RANGE_TYPE);
		
		// indicate that we want labels on the arcs
		showArcLabels = true;
		
		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}
	
	private void initialiseRequiredChildNodes() {
		setRequiredChildNode(rangeChildFromNum, NUM_TYPE);
		setRequiredChildNode(rangeChildToNum, NUM_TYPE);
	}	
	
	public static String getTooltip() {
		return "Range  (represents a numerical range or interval)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Range node represents a numerical range.<br><br>"+
			"The required arguments are two expressions that evaluate to numerical values.");
	}
	
	public String printTextualRepresentation() {
		String description = QueryManager.addColouring("the range of ");	
		String op = " to ";
					
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
						description += QueryManager.addColouring("another numerical value that has not been specified yet");
						QueryManager.colourDown();
					}
				}	
			}
		}
		else {
			QueryManager.colourUp();
			description += QueryManager.addColouring("a numerical value that has not been specified yet");
			QueryManager.colourDown();
			description += QueryManager.addColouring(op);
			QueryManager.colourUp();
			description += QueryManager.addColouring(" another numerical value that has also not been specified yet");
			QueryManager.colourDown();
		}
		return description;	
	}
	
}
