/**
 * MomentNode
 * 
 * Represents a raw moment of a passage time density.
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
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.NumNode;


public class MomentNode extends OperationNode {

	public MomentNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public MomentNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("MomentNode");
		setNodeType(PetriNetNode.MOMENT);
			
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
		setRequiredChildNode(momentChildNum, NUM_TYPE);
		ArrayList<String> requiredChildTypes = new ArrayList<String>();
		requiredChildTypes.add(DENS_TYPE);
		requiredChildTypes.add(DIST_TYPE);
		setRequiredChildNode(momentChildDensDist, requiredChildTypes);
	}	
	
	public static String getTooltip() {
		return "Moment (a raw moment of a passage time density or distribution)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Moment node represents a raw moment of a passage time density.<br><br>"+
			"The required arguments are an integer, representing which moment is to be calculated, and "+
			"a passage time density function that the moment is to be calculated from.<br>"+
			"The operator returns a raw moment (a real value).");
	}
	
	public String printTextualRepresentation() {
		String description = "";	
		String op = " of";
					
		ArrayList children = getChildNodes();
		if (children != null) {
			Iterator<PerformanceTreeNode> i = children.iterator();
			while (i.hasNext()) {
				PerformanceTreeNode child = i.next();	
				String childsReturnType = child.getReturnType();
				if (childsReturnType.equals(NUM_TYPE)) {
					// we know we're dealing with the argument that specifies which moment we want
					String numVal = "";
					if (child instanceof NumNode) {
						if (((NumNode)child).getNumObject() != null) {
							numVal = ((NumNode)child).getNumObject().toString();
							int intNumVal = (int)Double.parseDouble(numVal); 
							if (intNumVal == 1) {
								// first moment - mean
								description += QueryManager.addColouring("the mean");
							}
							else if (intNumVal == 2) {
								// second moment - variance
								description += QueryManager.addColouring("the " + intNumVal + "nd raw moment");
							}
							else if (intNumVal == 3) {
								// third moment - skewness
								description += QueryManager.addColouring("the " + intNumVal + "rd raw moment");
							}
							else {	
								description += QueryManager.addColouring("the " + intNumVal + "th raw moment");
							}
						}
						else {
							description += QueryManager.addColouring("the raw moment identified by ");
							QueryManager.colourUp();
							description += child.printTextualRepresentation();
							QueryManager.colourDown();
						}
					}
				}
				else {
					QueryManager.colourUp();
					description += child.printTextualRepresentation();
					QueryManager.colourDown();
				}
							
				if (i.hasNext()) {
					description += QueryManager.addColouring(op)+" ";
				}
				else {
					if (children.size() == 1) {
						description += QueryManager.addColouring(op);
						description += QueryManager.addColouring(" a passage time density or distribution that has not been specified yet");
						QueryManager.colourDown();
					}
					else
						QueryManager.colourDown();
				}	
			}
		}
		else {
			description += QueryManager.addColouring("the unspecified raw moment ");
			description += QueryManager.addColouring(op);
			description += QueryManager.addColouring(" a passage time density or distribution that has not been specified yet");
			QueryManager.colourDown();
		}
		return description;	
	}
	
}
