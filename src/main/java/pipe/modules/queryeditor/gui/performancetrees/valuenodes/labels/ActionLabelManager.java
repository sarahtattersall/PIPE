/**
 * ActionLabelManager
 * 
 * This class contains methods for dealing with the management and
 * assignment of action labels.
 * 
 * @author Tamas Suto
 * @date 23/07/07
 */


package pipe.modules.queryeditor.gui.performancetrees.valuenodes.labels;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.ButtonBar;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ActionsNode;


public class ActionLabelManager {

	private static JDialog guiDialog;
	private static ActionsNode node;
	//
	private static String actionText = "";
	//
	
	/**
	 * This method launches a popup that enables the assignment of an action label
	 * to an Actions node
	 * @param node
     * @param nodeInput
	 */
	public static void actionLabelAssignmentDialog(ActionsNode nodeInput) {
		// update our local copy of the node object. Need this to be able to
		// refer to the node from the ActionListeners
		node = nodeInput;
		
		// make sure the query designer has the lates net info
		boolean okToProceed = QueryManager.getData().checkCurrentData("Actions");	
		
		if(okToProceed) {
			// create popup dialogue
			guiDialog = new JDialog(QueryManager.getEditor(),"Action Specification",true);
			Container contentPane = guiDialog.getContentPane();
			contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.Y_AXIS));     

			// add text
			JLabel instructionLabel = new JLabel("  To assign an action, select the relevant label below:  ");
			instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
			instructionLabel.setBorder(BorderFactory.createEmptyBorder(10,4,10,4));
			contentPane.add(instructionLabel);       

			// add dropdown menu
			ArrayList<String> actionLabels = new ArrayList<String>();
			String existingActionLabel = node.getActionLabel();
			if (existingActionLabel == null || (!actionLabels.contains(existingActionLabel)))
				actionLabels.add("-- Select --");
			actionLabels.addAll(QueryManager.getData().getActionLabels());		
			JComboBox comboBox = new JComboBox(actionLabels.toArray());	
			if (existingActionLabel == null)
				comboBox.setSelectedItem("-- Select --");				
			else
				comboBox.setSelectedItem(existingActionLabel);
			ActionListener comboBoxListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					JComboBox cb = (JComboBox)e.getSource();
					String actionLabel = (String)cb.getSelectedItem();				
					if (!actionLabel.equals("-- Select --")) {
						// set action label in node
						node.setActionLabel(actionLabel);				
						// display action label below node
						node.setNodeLabel(actionLabel);
						actionText = actionLabel;
						// update node 
						if (MacroManager.getEditor() == null)
							QueryManager.getData().updateNode(node);
						else
							MacroManager.getEditor().updateNode(node);
						// close popup
						killPopup();
					}
				} 
			};	
			comboBox.addActionListener(comboBoxListener);
			contentPane.add(comboBox);    

			// add buttons
			ActionListener cancelButtonListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					killPopup();
				} 
			};
			contentPane.add(new ButtonBar("Cancel", cancelButtonListener));     

			// visualise popup
			guiDialog.pack();
			guiDialog.setLocationRelativeTo(null);
			guiDialog.setVisible(true);	
		}
	}
	
	public String getActionText(){
		return actionText;
	}
	
	/**
	 * This method disposes of the popup
	 *
	 */
	private static void killPopup() {
		guiDialog.dispose();
	}

}
