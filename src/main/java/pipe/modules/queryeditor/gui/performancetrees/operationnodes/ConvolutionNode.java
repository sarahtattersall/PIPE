/**
 * ConvolutionNode
 * 
 * Represents a convolution of two passage time density functions or two
 * passage time distribution functions. In essence, it is merging two
 * functions into one.
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



public class ConvolutionNode extends OperationNode {

	public ConvolutionNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public ConvolutionNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("ConvolutionNode");
		setNodeType(PetriNetNode.CONVOLUTION);
			
		// only one subnode
		setRequiredArguments(2);
		
		// and nothing else
		setMaxArguments(2);
		
		// set up required arguments of node
		initialiseRequiredChildNodes();
		
		// set return type
		setReturnType(DENS_TYPE);
		
		// indicate that we want labels on the arcs
		showArcLabels = true;
		
		// set up outgoing arcs (implemented in OperationNode)
		setupOutgoingArcs();
	}
	
	private void initialiseRequiredChildNodes() {
		setRequiredChildNode(convChildDensity1, DENS_TYPE);
		setRequiredChildNode(convChildDensity2, DENS_TYPE);
	}	
	
	public static String getTooltip() {
		return "Convolution  (calculates the convolution of two passage "+
				"time densities or two passage time distributions)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Convolution node represents a convolution of two "+
			"passage time densities.<br><br>"+
			"The required arguments are two passage time densities.<br>"+
			"The operator returns a convoluted passage time density function.");
	}
	
	public String printTextualRepresentation() {
		String description = QueryManager.addColouring("the convolution of ");	
		String op = " and ";
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
						description += QueryManager.addColouring("another passage time density that has not been specified yet ");
						QueryManager.colourDown();
					}
				}
			}
		}
		else {
			description += QueryManager.addColouring("two passage time densities that have not been specified yet");
		}
		return description;	
	}
	
}
