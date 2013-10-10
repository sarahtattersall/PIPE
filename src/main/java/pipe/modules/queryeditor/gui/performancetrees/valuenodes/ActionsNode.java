/**
 * ActionsNode
 * 
 * Represents a set of actions in a model
 * 
 * @author Tamas Suto
 * @date 21/05/07
 */

package pipe.modules.queryeditor.gui.performancetrees.valuenodes;

import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;


public class ActionsNode extends ValueNode {
	
	private String actionLabel;
	
	public ActionsNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public ActionsNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("ActionsNode");
		setNodeType(PetriNetNode.ACTIONS);
		
		// set return type
		setReturnType(ACTIONS_TYPE);
		
		actionLabel = null;
	}
	
	
	public String getActionLabel() {
		return actionLabel;
	}
	
	public void setActionLabel(String newActionLabel) {
		actionLabel = newActionLabel;
	}
	
	public static String getTooltip() {
		return "Actions (an action of the underlying model)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The Actions node represents an action "+
			"of the model (identified by an action label).");
	}
	
	public String printTextualRepresentation() {
		String description = QueryManager.addColouring("the action ");			
		if (getActionLabel() != null)
			description += QueryManager.addColouring("identified by label '"+getActionLabel() +"'");
		else 
			description += QueryManager.addColouring("that has not been specified yet");
		return description;	
	}
	
}
