/**
 * ConditionPlaceHandler
 * 
 * @author Barry Kearns
 * @author Tamas Suto
 * @date 18/08/07
 */


package pipe.modules.queryeditor.gui.performancetrees.valuenodes.labels;

import pipe.modules.queryeditor.QueryManager;
import pipe.views.ConditionPlaceView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;


class ConditionPlaceHandler extends MouseAdapter {

	private JDialog parentDialog = null;
	private ConditionPlaceView _placeView = null;

	private JDialog conditionEdit;
	private JComboBox operaterCombo;
	private JButton okButton;
    private JButton cancelButton;
	private JTextField conditionValue;

	public ConditionPlaceHandler(JDialog parentDialog, ConditionPlaceView placeView) {
		this.parentDialog = parentDialog;
		this._placeView = placeView;
	}


	private void showDialog() {
		// Create dialog
		conditionEdit = new JDialog(parentDialog, "Add condition to " + _placeView.getName(), true);

		// Set layout
		Container contentPane= conditionEdit.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));

		// Panel for entry of place condition for the state
		JPanel mainPanel = new JPanel();

		// combo box for operator
		operaterCombo = new JComboBox();

		// Add standard operators to combo-box
		operaterCombo.addItem("<");
		operaterCombo.addItem("\u2264"); // <= character
		operaterCombo.addItem("=");
		operaterCombo.addItem("\u2265"); // >= character
		operaterCombo.addItem(">");
		operaterCombo.setSelectedIndex(2);
		conditionValue  = new JTextField(5);	
		mainPanel.add(new JLabel(_placeView.getName()));
		mainPanel.add(operaterCombo);
		mainPanel.add(conditionValue);

		// Panel for buttons
		JPanel buttonPanel = new JPanel();	
		okButton = new JButton("OK");
		conditionEdit.getRootPane().setDefaultButton(okButton); // Pressing 'Enter' key will activate the button
		okButton.setMnemonic(KeyEvent.VK_O);
		okButton.addActionListener(BtnClick);
		cancelButton = new JButton("Cancel");
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.addActionListener(BtnClick);
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);

		// Build UI
		contentPane.add(new JLabel("Please enter the condition for this place:"));
		contentPane.add(mainPanel);
		contentPane.add(buttonPanel);

		// Pack, Centre, Display
		conditionEdit.pack();
		conditionEdit.setResizable(false);
		conditionEdit.setLocationRelativeTo(null);
		conditionEdit.setVisible(true);
	}

	/** 
	 * Creates the popup menu that the user will see when they right-click on 
	 * a component
     * @param e
     * @return
     */
    private JPopupMenu getPopup(MouseEvent e) {
		JPopupMenu popup = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("Remove Condition");

		// Add action lister for the remove pop up item
		menuItem.addActionListener ( new ActionListener () {
			public void actionPerformed(ActionEvent e) {
				remove(); 
			}
		});

		popup.add(menuItem);
		return popup;
	}

	/** 
	 * Removes condition from currently selected place
	 */
	private void remove() {
		// Update the change buffer					 
		((StateGroupEditor)parentDialog).addStateElement(_placeView.getId(), "", "");
		// Update the UI
		_placeView.removeCondition();
	}


	/**
	 * This action listener responds to the buttons used in
	 * the "Add Condition" pop up dialog
	 * i.e the Cancel / OK buttons 
	 */

    private final ActionListener BtnClick = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			if (event.getSource() == cancelButton) {
				closeWindow();
			}
			else if (event.getSource() == okButton) {
				String operatorStr, targetStr;
				operatorStr = (String) operaterCombo.getSelectedItem();
				targetStr = conditionValue.getText();
				if (inputValid(targetStr)) {
					try {
						// Update the change buffer					 
						((StateGroupEditor)parentDialog).addStateElement(_placeView.getId(), operatorStr, targetStr);
						// Update the UI
						_placeView.setCondition(operatorStr, targetStr);
						closeWindow();				 
					} catch (Exception exp) {
						System.out.println("Error creating state: " + exp);
					}	
				}
				else {
					JOptionPane.showMessageDialog(QueryManager.getEditor().getContentPane(),
							"Please specify the operator and the number of tokens when\n"+
							"setting up a condition for a place.",
							"Warning",
							JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	};

	private boolean inputValid(String inputString) {
		boolean inputOK = false;
		if (inputString != null && !inputString.equals("")) {
			for (int i = 0; i < inputString.length(); i++){
				char chr = inputString.charAt(i);
				if (Character.isDigit(chr)) 
					inputOK = true;
			}
		}
		return inputOK;
	}

	private void closeWindow() {
		conditionEdit.setVisible(false);
		conditionEdit.dispose();		 
	}

	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			// left click will display the add condition dialog
			showDialog();
		}
		else {
			// right-click will bring up a popup menu that allows the user to
			// clear the condition on the place
			JPopupMenu popup = getPopup(e);	
			popup.show(e.getComponent(), e.getX(), e.getY());
		}
	}	 

}
