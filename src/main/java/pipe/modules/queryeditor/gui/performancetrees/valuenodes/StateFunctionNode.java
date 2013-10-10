/**
 * StateFunctionNode
 * 
 * Represents a function of a state
 * 
 * @author Tamas Suto
 * @date 21/05/07
 */

package pipe.modules.queryeditor.gui.performancetrees.valuenodes;

import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;


public class StateFunctionNode extends ValueNode {
	
	private String function;
	
	public StateFunctionNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public StateFunctionNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("StateFunctionNode");
		setNodeType(PetriNetNode.STATEFUNCTION);
		
		// set return type
		setReturnType(FUNC_TYPE);
		
		function = null;
	}
	
	String getFunction() {
		return function;
	}
	
	public void setFunction(String newFunction) {
		function = newFunction;
	}
	
	public static String getTooltip() {
		return "State Function  (a function of a set of states)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The State Function node represents a function on a set of states of the model.");
	}
	
	public String printTextualRepresentation() {
		String description = QueryManager.addColouring("the state function ");			
		if (getFunction() != null)
			description += QueryManager.addColouring("'"+getFunction()+"'");
		else 
			description += QueryManager.addColouring("that has not been specified yet");
		return description;	
	}
		
}
