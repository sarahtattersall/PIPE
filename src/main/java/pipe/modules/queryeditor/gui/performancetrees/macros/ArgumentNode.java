/**
 * ArgumentNode
 * 
 * Represents an argument of a macro tree
 * 
 * @author Tamas Suto
 * @date 21/05/07
 */

package pipe.modules.queryeditor.gui.performancetrees.macros;

import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ValueNode;


public class ArgumentNode extends ValueNode {
	
	private String argumentName;
	
	public ArgumentNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public ArgumentNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("ArgumentNode");
		setNodeType(PetriNetNode.ARGUMENT);
		
		// set return type
		setReturnType(ARGUMENT_TYPE);
		
		argumentName = null;
	}
	
	
	public String getArgumentName() {
		return argumentName;
	}
	
	public void setArgumentName(String newArgumentName) {
		argumentName = newArgumentName;
	}
	
	public static String getTooltip() {
		return  "Argument (an argument of a macro)";
	}
	
	public static String getNodeInfo() {
		return   QueryManager.addColouring("The Argument node represents an argument of a macro tree.");
	}
	
	public String printTextualRepresentation() {
		String description = QueryManager.addColouring("the argument ");			
		if (getArgumentName() != null)
			description += QueryManager.addColouring("called '"+getArgumentName()+"'");
		else 
			description += QueryManager.addColouring("that has not been specified yet");
		return description;	
	}
	
}
