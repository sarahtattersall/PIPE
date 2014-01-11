/**
 * StateEditor
 * 
 * This is the popup that allows the user to edit a state of the underlying SPN model
 * 
 * @author Barry Kearns
 * @author Tamas Suto
 * @date 18/08/07
 */

package pipe.modules.queryeditor.gui.performancetrees.valuenodes.labels;

import pipe.common.dataLayer.StateElement;
import pipe.common.dataLayer.StateGroup;
import pipe.gui.ApplicationSettings;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.QueryException;
import pipe.views.PetriNetView;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

class StateGroupEditor extends JDialog
{

	private static final long	serialVersionUID	= 1L;

	private PetriNetView _pnModel;
	private StateView					viewer;
	private ArrayList<StateElement>		changeBuffer;
	private StateGroup activeStateGroup;
	private boolean						newStateGroup		= false;
	private String						initialStateName;

	private JDialog						stateDialog;
	private JTextField					stateNameTextField;
	private JButton						okButton;
    private JButton cancelButton;
	private JButton						initialStateButton;
    private JButton allZeroButton;
    private JButton clearStatesButton;

	public void addState(final PetriNetView pnmlData)
	{
		this._pnModel = pnmlData;
		this.activeStateGroup = new StateGroup(pnmlData);
		this.newStateGroup = true;
		init();
	}

	public void editState(final PetriNetView pnmlData, final StateGroup editStateGroup)
	{
		this._pnModel = pnmlData;
		this.activeStateGroup = editStateGroup;
		this.initialStateName = this.activeStateGroup.getName();
		init();
	}

	private void init()
	{
		this.changeBuffer = new ArrayList<StateElement>();

		this.stateDialog = new JDialog(StateLabelManager.popupDialog, "State Group Editor", true);

		JPanel stateViewPanel = new JPanel();
		stateViewPanel.setBorder(new EtchedBorder());
		stateViewPanel.setLayout(new BoxLayout(stateViewPanel, BoxLayout.Y_AXIS));

		// State name panel
		JPanel stateNamePanel = new JPanel();
		stateNamePanel.setBorder((new TitledBorder(new EtchedBorder(), "State Group Information")));
		stateNamePanel.setLayout(new SpringLayout());
		JLabel stateNameLabel = new JLabel("State Group Name: ");
		this.stateNameTextField = new JTextField(30);
		this.stateNameTextField.setText(this.activeStateGroup.getName());
		stateNamePanel.add(stateNameLabel);
		stateNamePanel.add(this.stateNameTextField);
		SpringLayoutUtilities.makeCompactGrid(stateNamePanel, 1, 2, 6, 6, 6, 12);

		// Main state editor panel
		this.viewer = new StateView();
		this.viewer.setParent(this);
		this.viewer.drawPetriNet(this._pnModel, this.activeStateGroup);
		JScrollPane viewPanel = new JScrollPane(this.viewer);
		viewPanel.setBorder((new TitledBorder(	new EtchedBorder(),
												"Click on individual places to set up conditions that uniquely identify the stat group")));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		int viewPanelPreferredWidth = screenSize.width * 60 / 100;
		int viewPanelPreferredHeight = screenSize.height * 60 / 100;
		Dimension viewPanelPreferredSize = new Dimension(viewPanelPreferredWidth, viewPanelPreferredHeight);
		viewPanel.setPreferredSize(viewPanelPreferredSize);

		// Auto state group helper buttons
		JPanel configButtonsPanel = new JPanel();
		configButtonsPanel.setBorder((new TitledBorder(new EtchedBorder(), "Automatic Configurations")));
		configButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.initialStateButton = new JButton("Initial Marking");
		this.allZeroButton = new JButton("All Zero Marking");
		this.clearStatesButton = new JButton("All Clear Marking");
		this.initialStateButton.addActionListener(this.autoSetState);
		this.allZeroButton.addActionListener(this.autoSetState);
		this.clearStatesButton.addActionListener(this.autoSetState);
		configButtonsPanel.add(this.initialStateButton);
		configButtonsPanel.add(this.allZeroButton);
		configButtonsPanel.add(this.clearStatesButton);

		JPanel stateButtonsPanel = new JPanel();
		stateButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.okButton = new JButton("OK");
		this.okButton.addActionListener(this.saveState);
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(this.saveState);
		stateButtonsPanel.add(this.okButton);
		stateButtonsPanel.add(this.cancelButton);

		// Put everything together
		stateViewPanel.add(viewPanel);
		stateViewPanel.add(configButtonsPanel);
		stateViewPanel.add(stateNamePanel);
		stateViewPanel.add(stateButtonsPanel);
		this.stateDialog.add(stateViewPanel);
		this.stateDialog.pack();
		this.stateDialog.setLocationRelativeTo(null);
		this.stateDialog.setVisible(true);
	}

	/**
	 * This method adds to the buffer of changes committed when the group is
	 * saved
	 * 
	 * @param placeA
	 * @param operator
	 * @param placeB
	 */
	public void addStateElement(final String placeA, String operator, final String placeB)
	{
		// Convert from unicode character to ASCII
		if (operator.equals("="))
			operator = "==";
		else if (operator.equals("\u2264"))
			operator = "<=";
		else if (operator.equals("\u2265"))
			operator = ">=";
		this.changeBuffer.add(new StateElement(placeA, operator, placeB));
	}

	/**
	 * This method applies the changes that occurs in the change buffer
	 */
	private void applyChanges()
	{
		// update label definitions if necessary
		String oldStateName = this.activeStateGroup.getName();
		String newStateName = this.stateNameTextField.getText();
		if (!oldStateName.equals(newStateName))
		{
			// change name of state
			this.activeStateGroup.setName(newStateName);
			// update state name in labels
			QueryManager.getData().updateLabelsWithNewStateName(oldStateName, newStateName);
		}
		// Apply each of the changes made to the state group
		for (StateElement currElement : this.changeBuffer)
			this.activeStateGroup.addState(currElement);
		// If a new state group, add it to the state group arrayList in
		// PetriNet
		if (this.newStateGroup)
			this._pnModel.addStateGroup(this.activeStateGroup);
		// Inform Pipe that the model has been modified
        ApplicationSettings.getApplicationView().getCurrentTab().netChanged = true;
	}

	private final ActionListener	saveState		= new ActionListener()
									{
										public void actionPerformed(ActionEvent event)
										{
											if (event.getSource() == StateGroupEditor.this.okButton)
											{
												String errormsg;
												try
												{
													if (StateGroupEditor.this.stateNameTextField.getText()
																								.equals(""))
													{
														errormsg = "Please specify a name for this state group";
														throw new QueryException(errormsg);
													}
													else
													{
														// check whether the
														// state exists already
														String specifiedStateName = StateGroupEditor.this.stateNameTextField.getText();
                                                        if (StateGroupEditor.this.newStateGroup &&
															ApplicationSettings.getApplicationView().getCurrentPetriNetView()
																		.stateGroupAlreadyExists(specifiedStateName) ||
															!StateGroupEditor.this.newStateGroup &&
															ApplicationSettings.getApplicationView().getCurrentPetriNetView()
																		.stateGroupAlreadyExists(specifiedStateName) &&
															!specifiedStateName.equals(StateGroupEditor.this.initialStateName))
														{
															errormsg = "A state group with this name has already been defined.\n"
																		+ "Please choose a different name.";
															throw new QueryException(errormsg);
														}
														else
														{
															if (StateGroupEditor.this.viewer.someConditionHasBeenSpecified())
															{
																applyChanges();
																if (StateLabelManager.popupDialog != null)
																	StateLabelManager.update();
																closeWindow();
															}
															else
															{
																errormsg = "For a state group definition to be valid, at least \n"
																			+ "one condition has to be specified.";
																throw new QueryException(errormsg);
															}
														}
													}
												}
												catch (QueryException e)
												{
													String msg = e.getMessage();
													JOptionPane.showMessageDialog(	QueryManager.getEditor()
																								.getContentPane(),
																					msg,
																					"Warning",
																					JOptionPane.ERROR_MESSAGE);
												}
											}
											else if (event.getSource() == StateGroupEditor.this.cancelButton)
											{
												StateLabelManager.update();
												closeWindow();
											}
										}

										private void closeWindow()
										{
											StateGroupEditor.this.stateDialog.setVisible(false);
											StateGroupEditor.this.stateDialog.dispose();
										}
									};

	private final ActionListener	autoSetState	= new ActionListener()
									{
										public void actionPerformed(ActionEvent event)
										{
											if (event.getSource() == StateGroupEditor.this.initialStateButton)
												StateGroupEditor.this.viewer.setInitialCond();
											else if (event.getSource() == StateGroupEditor.this.allZeroButton)
												StateGroupEditor.this.viewer.setEqualZeroCond();
											else if (event.getSource() == StateGroupEditor.this.clearStatesButton)
												StateGroupEditor.this.viewer.clearAllCond();
										}
									};

}
