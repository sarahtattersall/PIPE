/**
 * InIntervalNode
 * 
 * Represents the boolean value indicating whether a numerical value is in a specified
 * numerical range.
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



public class InIntervalNode extends OperationNode {

	public InIntervalNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public InIntervalNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("InIntervalNode");
		setNodeType(PetriNetNode.ININTERVAL);
			
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
		setRequiredChildNode(inIntervalChildNum, NUM_TYPE);
		setRequiredChildNode(inIntervalChildRange, RANGE_TYPE);
	}
	
	public static String getTooltip() {
		return "In Interval  (a boolean operator that determines whether "+
			"a numerical value is within an interval or possibly within multiple intervals)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The In Interval node checks whether a given numerical "+
			"value lies in a particular range.<br><br>"+
			"The required arguments are an expression that represents a numerical value and a numerical range.<br>"+
			"The operator returns a boolean value.");
	}
	
	public String printTextualRepresentation() {
		String description = "";	
		String op = " lies within ";
					
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
						QueryManager.colourDown();
						description += QueryManager.addColouring(op);
						QueryManager.colourUp();
						description += QueryManager.addColouring("a certain range that has not been specified yet ");
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
			description += QueryManager.addColouring(" a numerical range that has not been specified yet ");
			QueryManager.colourDown();
		}
		return description;	
	}
	
}
