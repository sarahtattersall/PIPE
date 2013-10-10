package pipe.modules.passageTimeForTaggedNet;

import pipe.views.ConditionPlaceView;
import pipe.views.PetriNetView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * This class provides the mouse operations that can be performed on conditional places.
 * This includes the left click dialog for entering a condition on the place and the right
 * click pop up menu for removing the condition from a place.
 * 
 * @author Barry Kearns
 * @date August 2007
 */

class ConditionPlaceHandler extends MouseAdapter
{
	private JDialog parent = null;
	private ConditionPlaceView _placeView = null;
	
	private JDialog conditionEdit;
	private JComboBox operaterCombo;
	private JButton okButton;
    private JButton cancelButton;
	private JTextField conditionValue;
	
	private final PetriNetView _currentPNML;
	
	public ConditionPlaceHandler(JDialog parentDialog, ConditionPlaceView placeView, PetriNetView data)
	{
		parent = parentDialog;
		this._placeView = placeView;
		this._currentPNML = data;
	}
	
		
	private void showDialog()
	{		
		// Create dialog
		conditionEdit = new JDialog(parent, "Add condition to " + _placeView.getName(), true);
		
		   
		// Set layout
		Container contentPane= conditionEdit.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));
		
			
		
		// Panel for entry of place condition for the state
		JPanel mainPanel = new JPanel();
		
		// combo box for operator
		operaterCombo = new JComboBox();
		
		// Add standard operators to combo-box
		
		conditionValue  = new JTextField(5);
		
		operaterCombo.addItem("=");
		operaterCombo.addItem("<");
		operaterCombo.addItem("\u2264"); // <= character
		operaterCombo.addItem(">");
		operaterCombo.addItem("\u2265");		// >= character
		//if(!((StateEditor)parent).activeStateGroup.getTaggedPlaceExist())
		conditionValue.setEnabled(true);
		//if(!((StateEditor)parent).getTaggedPlaceExist()){
			operaterCombo.addItem("T");
			
		//}
		
		operaterCombo.addActionListener(combo);
		
		
				
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
	
	private final ActionListener combo = new ActionListener()
	 {
		 public void actionPerformed(ActionEvent event)
		 {
			 String operatorStr;		 
			 operatorStr = (String) operaterCombo.getSelectedItem();
			 if(operatorStr.equals("T"))conditionValue.setEnabled(false);
			 else conditionValue.setEnabled(true);
		 }
	 };
	
	/**
	  * This action listener responds to the buttons used in
	  * the "Add Condition" pop up dialog
	  * i.e the Cancel / OK buttons 
	  */

    private final ActionListener BtnClick = new ActionListener()
	 {
		 public void actionPerformed(ActionEvent event)
		 {
			 if (event.getSource() == cancelButton)
			 {
				 closeWindow();
			 }
			 
			 else if (event.getSource() == okButton)
			 {
				 String placeStr, operatorStr, targetStr;
				 
				 placeStr= _placeView.getId();
				 
				 operatorStr = (String) operaterCombo.getSelectedItem();

				 if(operatorStr.equals("T")){
					 //targetStr = "T";
//					 placeStr="tagged_location";
//					 operatorStr= "==";
					 
					 Integer targetStrInt=new Integer(_currentPNML.getPlaceIndex(placeStr));
					 
					 targetStr=targetStrInt.toString();
				 }
				 else 
					 targetStr = conditionValue.getText();

				 // Check that the input is valid, then check the new condition
				 if (inputValid(targetStr))
				 {
					 try
					 {						 
						 // Update the change buffer					 
						 ((StateEditor)parent).addStateElement(placeStr, operatorStr, targetStr);
						
						 // Update the UI
						 _placeView.setCondition(operatorStr, targetStr);
						
						 closeWindow();				 
					 }
					 catch (Exception exp) {
						 System.out.println("Error creating state: " + exp);
					 }	
				 }
				 else {
					 JOptionPane.showMessageDialog(null, "Please specify the number of tokens for the condition (under 1,000).",
								"Warning", JOptionPane.ERROR_MESSAGE);
					 }
				 
			 }
		 }
	 };
	
	private void closeWindow()
	{
		conditionEdit.setVisible(false);
		conditionEdit.dispose();		 
	}
	
	 public void mousePressed(MouseEvent e)
	 {
		 // left click will display the add condition dialog
		 if (e.getButton() == MouseEvent.BUTTON1)
			 showDialog();
		 
		 else
		 {
			 JPopupMenu popup = getPopup(e);	
			 popup.show(e.getComponent(), e.getX(), e.getY());
		 }
	 }
	 
	 // Check that the number is a valid integer less than 10000
	 private boolean inputValid(String inputString)
	 {
		 try
		 {
			 if(inputString.equals("T"))return true;
			 else
			 {
				 int checkInput = Integer.parseInt(inputString);
                 return checkInput < 1000;
			 }
			
		 }
		 catch(Exception exp)
		 {
			 return false;
		 }
		 
		
	 }
	 
	  /** Creates the popup menu that the user will see when they right click on a component
       * @param e
       * @return*/
      private JPopupMenu getPopup(MouseEvent e) {

	    JPopupMenu popup = new JPopupMenu();

	    JMenuItem menuItem = new JMenuItem("Remove");
	    
	    // Add action lister for the remove pop up item
	    menuItem.addActionListener ( new ActionListener () {
			public void actionPerformed(ActionEvent e)
			{
				remove(); 
			}

		});


	    popup.add(menuItem);

	    return popup;
	  }
	  
	  // Removes condition from currently selected place
	  private void remove()
	  {
		// Update the change buffer					 
		((StateEditor)parent).addStateElement(_placeView.getId(), "", "");
			
		// Update the UI
		 _placeView.removeCondition();
	  }
}

