/**
 * ProbInIntervalNode
 * 
 * Represents the probability with which a passage takes place in a given
 * amount of time.
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


public class ProbInIntervalNode extends OperationNode {

	public ProbInIntervalNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public ProbInIntervalNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("ProbInIntervalNode");
		setNodeType(PetriNetNode.PROBININTERVAL);
			
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
		setRequiredChildNode(probInIntervalChildDens, DENS_TYPE);
		setRequiredChildNode(probInIntervalChildRange, RANGE_TYPE);
	}	
	
	public static String getTooltip() {
		return "Probability In Interval  (the probability with which a passage "+
			"takes place in a certain amount of time)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Probability In Interval node represents the probability with which a passage takes "+
			"place in a given amount of time.<br><br>"+
			"The required arguments are a passage time density that defines the passage and a time range.<br>"+
			"The operator returns a probability value.");
	}
	
	public String printTextualRepresentation() {
		String description = QueryManager.addColouring("the probability with which a value sampled from ");	
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
						description += QueryManager.addColouring(op);
						QueryManager.colourUp();
						description += QueryManager.addColouring(" a certain time given by a range that has not been specified yet");
						QueryManager.colourDown();
					}
				}	
			}
		}
		else {
			QueryManager.colourUp();
			description += QueryManager.addColouring("a passage time density that has not been specified yet ");
			QueryManager.colourDown();
			description += QueryManager.addColouring(op);
			QueryManager.colourUp();
			description += QueryManager.addColouring(" a certain range that has not been specified yet");
			QueryManager.colourDown();
		}
		return description;	
	}
	
}
