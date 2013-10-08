/**
 * NumNode
 * 
 * Represents a numerical value
 * 
 * @author Tamas Suto
 * @date 21/05/07
 */

package pipe.modules.queryeditor.gui.performancetrees.valuenodes;

import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;



public class BoolNode extends ValueNode {
	
	private Boolean booleanValue;
	
	public BoolNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public BoolNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("BoolNode");
		setNodeType(PetriNetNode.BOOL);
		
		// set return type
		setReturnType(BOOL_TYPE);
		
		booleanValue = null;
	}
	
	Boolean getBooleanObject() {
		return booleanValue;
	}
	
	boolean getBooleanValue() {
		return booleanValue;
	}
	
	public void setBooleanValue(boolean newValue) {
		booleanValue = Boolean.valueOf(newValue);
	}
	
	public static String getTooltip() {
		return  "Bool  (a boolean value)";
	}
	
	public static String getNodeInfo() {
		return  QueryManager.addColouring("The Boolean node represents a boolean value.");
	}
	
	public String printTextualRepresentation() {
		String description = "";			
		if (getBooleanObject() != null)
			description += QueryManager.addColouring(Boolean.toString(getBooleanValue()));
		else 
			description += QueryManager.addColouring("the boolean value that has not been specified yet");
		return description;	
	}
	
}
