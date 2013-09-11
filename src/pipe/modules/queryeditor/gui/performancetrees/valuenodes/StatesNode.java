/**
 * StatesNode
 * 
 * This node represents a set of states.
 * 
 * @author Tamas Suto
 * @date 20/05/07
 */

package pipe.modules.queryeditor.gui.performancetrees.valuenodes;

import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ResultNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SequentialNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SubsetNode;



public class StatesNode extends ValueNode {
	
	private String stateLabel;
	
	public StatesNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
		initialiseNode();
	}
	
	public StatesNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
		initialiseNode();
	}
	
	
	private void initialiseNode() {
		// set name and node type
		setName("StatesNode");
		setNodeType(PetriNetNode.STATES);
		
		// set return type
		setReturnType(STATES_TYPE);
		
		stateLabel = null;
	}
	
	public String getStateLabel() {
		return stateLabel;
	}
	
	public void setStateLabel(String newLabel) {
		stateLabel = newLabel;
	}
	
	public static String getTooltip() {
		return "States  (a set of states of the underlying model)";
	}
	
	public static String getNodeInfo() {
		return QueryManager.addColouring("The States node represents a set of states of the model (identified by state labels).");
	}
	
	public void select() {
		super.select();
		// if node has a state label assigned to it, display its definition in the
		// info box
		if (stateLabel != null) {
			String stateLabelDefinition = QueryManager.getData().getStateLabelDefinitionAsText(stateLabel);
			QueryManager.writeToInfoBox(QueryManager.addColouring("The States node represents the set of "+
				"states identified by the state label: <br><br>"+stateLabel+" := "+stateLabelDefinition));
		}
	}
	
	public String printTextualRepresentation() {
		String description = "";	
		String roleForParentNode = "";
		if (getParentNode() != null) {
			if (getParentNode() instanceof ResultNode || getParentNode() instanceof SequentialNode) {
				if (getStateLabel() != null)
					description += QueryManager.addColouring("the set of states identified by label '"+getStateLabel() +"'");
				else 
					description += QueryManager.addColouring("the set of states that has not been specified yet");
			}
			else if (getParentNode() instanceof SubsetNode) {
				if (MacroManager.getEditor() == null) 
					roleForParentNode = QueryManager.getData().getArc(getIncomingArcID()).getArcLabel();
				else
					roleForParentNode = MacroManager.getEditor().getArc(getIncomingArcID()).getArcLabel();
				
				if (getStateLabel() != null)
					description += QueryManager.addColouring("the set of states identified by label '"+getStateLabel()+"'");
				else 
					description += QueryManager.addColouring("the set of states that has not been specified yet");
			}
			else {		
				if (MacroManager.getEditor() == null) 
					roleForParentNode = QueryManager.getData().getArc(getIncomingArcID()).getArcLabel();
				else
					roleForParentNode = MacroManager.getEditor().getArc(getIncomingArcID()).getArcLabel();
				
				if (getStateLabel() != null)
					description += QueryManager.addColouring("the set of "+roleForParentNode+" identified by label '"+getStateLabel()+"'");
				else 
					description += QueryManager.addColouring("the set of "+roleForParentNode+" that has not been specified yet");
			}
		}
		return description;	
	}
}
