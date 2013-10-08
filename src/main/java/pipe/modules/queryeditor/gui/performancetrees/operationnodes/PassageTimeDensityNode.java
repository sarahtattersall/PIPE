/**
 * PassageTimeDensityNode
 * 
 * Represents a passage time density
 * 
 * @author Tamas Suto
 * @date 22/05/07
 */

package pipe.modules.queryeditor.gui.performancetrees.operationnodes;

import java.util.ArrayList;
import java.util.Iterator;

import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StatesNode;

public class PassageTimeDensityNode extends OperationNode {

	public PassageTimeDensityNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public PassageTimeDensityNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("PassageTimeDensityNode");
		setNodeType(PetriNetNode.PASSAGETIMEDENSITY);
			
		// only one subnode
		setRequiredArguments(2);
		
		// and nothing else
		setMaxArguments(5);
		
		// set up required arguments of node
		initialiseRequiredChildNodes();
		
		// set up possible arguments of node
		initialiseOptionalChildNodes();
		
		// set return type
		setReturnType(DENS_TYPE);
		
		// indicate that we want labels on the arcs
		showArcLabels = true;
		
		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}
	
	private void initialiseRequiredChildNodes() {
		setRequiredChildNode(pTDChildStartStates, STATES_TYPE);
		setRequiredChildNode(pTDChildTargetStates, STATES_TYPE);
	}	
	
	private void initialiseOptionalChildNodes() {
		setOptionalChildNode(pTDChildExcludedStates, STATES_TYPE);
		//setOptionalChildNode("included actions", ACTIONS_TYPE);
		//setOptionalChildNode("excluded actions", ACTIONS_TYPE);
	}
	
	public static String getTooltip() {
		return "Passage Time Density  (calculates the passage time density of "+
				"a passage defined by a set of start and target states)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Passage Time Density node represents a passage "+
		  "time density, calculated from a given "+
		  "set of start and target states, as well as optional excluded states.<br><br>"+
		  "The required arguments are the set of start and the set of target states.<br>"+
		  "The optional argument is the set of excluded states.<br>"+
		  "The operator returns a passage time density function.");
	}
	
	public String printTextualRepresentation() {
		String description = "";
		ArrayList children = getChildNodes();
		if (children != null) {
			PerformanceTreeNode child;	
			Iterator<PerformanceTreeNode> i = children.iterator();
			if (childrenAreTheSame(children)){
				description = QueryManager.addColouring("the cycle time density of the passage starting and ending in ");
				child = i.next();
				QueryManager.colourUp();
				description += child.printTextualRepresentation();
				QueryManager.colourDown();
			}
			else {
				description = QueryManager.addColouring("the passage time density ");
				String op1 = " and ";
				String op2 = " which avoids the ";
				description += QueryManager.addColouring("defined by ");
				i = children.iterator();
				int childCount = 0;
				while (i.hasNext()) {
					child = i.next();	
					// print out child's textual representation
					childCount++;
					QueryManager.colourUp();
					description += child.printTextualRepresentation();
					QueryManager.colourDown();
					if (i.hasNext()) {
						if (childCount < 2) 
							description += QueryManager.addColouring(op1);
						else 
							description += QueryManager.addColouring(op2);
					}
					else {
						if (children.size() == 1) {
							description += QueryManager.addColouring(op1);
							QueryManager.colourUp();
							description += QueryManager.addColouring("the set of target states that has not been specified yet ");
							QueryManager.colourDown();
						}
					}	
				}
			}
		}
		else {
			description += QueryManager.addColouring("the passage time density of an unspecified passage ");
		}
		return description;	
	}
	
	private boolean childrenAreTheSame(ArrayList children) {
		if (children.size() == 2) {
			Iterator i = children.iterator();
			String childsLabelBuffer = "";
			while (i.hasNext()) {
				PerformanceTreeNode childNode = (PerformanceTreeNode)i.next();
				if (childNode instanceof StatesNode) {
					if (((StatesNode)childNode).getNodeLabelObject() != null) {
						String childNodeLabel = ((StatesNode)childNode).getNodeLabel();
						if (childsLabelBuffer.equals(childNodeLabel)) 
							return true;
						else 
							childsLabelBuffer = childNodeLabel;
					}
				}
			}
			return false;
		}
		else 
			return false;
	}
	
}
