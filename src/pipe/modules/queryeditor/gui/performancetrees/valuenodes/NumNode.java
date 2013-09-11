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



public class NumNode extends ValueNode {
	
	private Double numValue;
	private String numType;
	
	public NumNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public NumNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("NumNode");
		setNodeType(PetriNetNode.NUM);
		
		// set return type
		setReturnType(NUM_TYPE);
		
		numValue = null;
	}
	
	public Double getNumObject() {
		return numValue;
	}
	
	public double getNumValue() {
		return numValue;
	}
	
	public void setNumValue(double newValue) {
		numValue = Double.valueOf(newValue);
	}
	
	public void setNumValue(int newValue) {
		numValue = Double.valueOf((double)newValue);
	}
	
	public String getNumType() {
		return numType;
	}
	
	public void setNumType(String newType) {
		numType = newType;
	}
	
	public static String getTooltip() {
		return "Num  (a numerical value)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Numerical Value node represents a numerical value.");
	}
	
	public String printTextualRepresentation() {
		String description = "";			
		if (getNumObject() != null) {
			String input = Double.toString(getNumValue());
			String output = "";
			if (isDotZero(input)) {
				int indexOfDot = input.indexOf(".");
				for (int i=0; i<indexOfDot; i++) {
					output += String.valueOf(input.charAt(i));
				}
			}
			else
				output = input;
			description += QueryManager.addColouring(output);
		}
		else 
			description += QueryManager.addColouring("a numerical value that has not been specified yet");
		return description;	
	}
	
	private boolean isDotZero(String input) {
		if (input.contains(".0")) {
			int dotPosition = input.indexOf(".");
			int stringLength = input.length();
			for (int i=dotPosition+1; i<=stringLength-1; i++) {
				if (!String.valueOf(input.charAt(i)).equals("0"))
					return false;
			}
			return true;
		}
		else
			return false;
	}
	
}
