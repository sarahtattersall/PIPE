/**
 * DistributionNode
 * 
 * Represents a passage time distribution. It is mainly used to convert a 
 * passage time density into a distribution.
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


public class DistributionNode extends OperationNode {

	public DistributionNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public DistributionNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("DistributionNode");
		setNodeType(PetriNetNode.DISTRIBUTION);
			
		// only one subnode
		setRequiredArguments(1);
		
		// and nothing else
		setMaxArguments(1);
		
		// set up required arguments of node
		initialiseRequiredChildNodes();
		
		// set return type
		setReturnType(DIST_TYPE);
		
		// indicate that we want labels on the arcs
		showArcLabels = true;
		
		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}
	
	private void initialiseRequiredChildNodes() {
		setRequiredChildNode(distChildDensity, DENS_TYPE);
	}	
	
	public static String getTooltip() {
		return "Passage Time Distribution  (converts a passage time density into a distribution)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Passage Time Distribution node represents a passage time distribution, calculated "+
			"from a passage time density.<br><br>"+
			"The required argument is a passage time density.<br>"+
			"The operator returns a passage time distribution function.");
	}
	
	public String printTextualRepresentation() {
		String description = QueryManager.addColouring("the cumulative distribution function ");	
		ArrayList children = getChildNodes();
		if (children != null) {
			description += QueryManager.addColouring("calculated from ");
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
			description += QueryManager.addColouring("calculated from a passage that has not been specified yet");
		}
		return description;	
	}
	
}
