/**
 * StateLabelManager
 * 
 * This class contains methods for dealing with the management and
 * assignment of state labels.
 * 
 * @author Tamas Suto
 * @date 23/07/07
 */

package pipe.modules.queryeditor.gui.performancetrees.valuenodes.labels;

import pipe.common.dataLayer.StateGroup;
import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.ButtonBar;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StatesNode;
import pipe.views.PetriNetView;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;

public class StateLabelManager
{

	private static StatesNode	node;
	private static StateGroup[]	availableStateGroups;
	private static String		currentStateLabel;

	public static JDialog		popupDialog;
    private static JDialog stateLabelDialog;
	private static JTextField	stateLabelTextField			= new JTextField(30);
	private static JComboBox	stateLabelDropdown			= new JComboBox();
	private static JList		stateGroupsList				= new JList();
	private static JList		statesAssignedToLabelList	= new JList();

	/**
	 * This method launches a popup that enables the assignment of a state label
	 * to a States node
	 * 
	 * @param nodeInput
	 */
	public static void stateLabelAssignmentDialog(final StatesNode nodeInput)
	{
		// update our local copy of the node object. Need this to be able to
		// refer to the node from the ActionListeners
		StateLabelManager.node = nodeInput;

		// build popup
		StateLabelManager.popupDialog = new JDialog(QueryManager.getEditor(), "State Label Assignment", true);
		StateLabelManager.popupDialog.setMinimumSize(new Dimension(730, 450));
		Container contentPane = StateLabelManager.popupDialog.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		contentPane.add(StateLabelManager.getStateLabelManagementPanel(false));
		contentPane.add(StateLabelManager.getStatesAssignmentManagerPanel());
		String[] buttonNames = {"OK", "Cancel"};
		ActionListener okButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (StateLabelManager.currentStateLabel != null)
				{
					if (StateLabelManager.stateLabelHasStateGroupAssigned())
					{
						// assign state label to node
						StateLabelManager.node.setStateLabel(StateLabelManager.currentStateLabel);
						StateLabelManager.node.setNodeLabel(StateLabelManager.currentStateLabel);

						if (MacroManager.getEditor() == null)
							QueryManager.getData().updateNode(StateLabelManager.node);
						else MacroManager.getEditor().updateNode(StateLabelManager.node);

						StateLabelManager.clearAll();
						StateLabelManager.popupDialog.dispose();
					}
					else
					{
						JOptionPane.showMessageDialog(	QueryManager.getEditor().getContentPane(),
														"Please ensure that the state label you have selected refers to \n"
														+ "at least one state group before trying to assign the label to \n"
														+ "the States node.",
														"Warning",
														JOptionPane.ERROR_MESSAGE);
					}
				}
				else
				{
					JOptionPane.showMessageDialog(	QueryManager.getEditor().getContentPane(),
													"Please select a state label from the dropdown menu \n"
													+ "that you wish to assign to the node.",
													"Warning",
													JOptionPane.ERROR_MESSAGE);
				}
			}
		};
		ActionListener cancelButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				StateLabelManager.clearAll();
				StateLabelManager.popupDialog.dispose();
			}
		};
		ActionListener[] buttonListeners = {okButtonListener, cancelButtonListener};
		contentPane.add(new ButtonBar(buttonNames, buttonListeners));

		// take care of popup closing
		StateLabelManager.popupDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		StateLabelManager.popupDialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(final WindowEvent we)
			{
				StateLabelManager.clearAll();
				StateLabelManager.popupDialog.dispose();
			}
		});

		// load in the latest info
		StateLabelManager.update();

		// show popup
		StateLabelManager.popupDialog.pack();
		StateLabelManager.popupDialog.setLocationRelativeTo(null);
		StateLabelManager.popupDialog.setVisible(true);
	}

	/**
	 * This method verifies whether the selected state label has at least one
	 * state group assigned. This is necessary, because we don't want the user
	 * to specify a label that refers no state group at all.
	 * 
	 * @return
	 */
	private static boolean stateLabelHasStateGroupAssigned()
	{
		ArrayList<String> assignedStateGroups = QueryManager.getData()
															.getStatesAssignedToStateLabel(StateLabelManager.currentStateLabel);
        return assignedStateGroups != null && assignedStateGroups.size() > 0;
	}

	/**
	 * Resets everything after the editor is exited
	 */
	private static void clearAll()
	{
		StateLabelManager.node = null;
		StateLabelManager.availableStateGroups = null;
		StateLabelManager.currentStateLabel = null;
		StateLabelManager.stateLabelTextField = new JTextField(30);
		StateLabelManager.stateLabelDropdown = new JComboBox();
		StateLabelManager.stateGroupsList = new JList();
		StateLabelManager.statesAssignedToLabelList = new JList();
	}

	/**
	 * Creates the top bit of the interface, which is choosing a state label
	 * name
	 * 
	 * @param withStateLabelControlButtons
     * @return
	 */
	private static JPanel getStateLabelManagementPanel(boolean withStateLabelControlButtons)
	{
		JPanel stateLabelManagementPanel = new JPanel();
		stateLabelManagementPanel.setLayout(new BoxLayout(stateLabelManagementPanel, BoxLayout.Y_AXIS));
		stateLabelManagementPanel.setBorder((new TitledBorder(new EtchedBorder(), "State Label")));

		JPanel stateLabelSelectionPanel = new JPanel();
		stateLabelSelectionPanel.setLayout(new SpringLayout());
		if (StateLabelManager.node != null)
		{
			String assignedStateLabel = StateLabelManager.node.getStateLabel();
			if (assignedStateLabel != null)
				StateLabelManager.setCurrentStateLabel(assignedStateLabel);
		}
		StateLabelManager.populateStateLabelDropdown();
		ActionListener stateLabelsComboListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				JComboBox cb = (JComboBox) e.getSource();
				String stateLabel = (String) cb.getSelectedItem();
				if (!stateLabel.equals("-- Select --"))
				{
					// indicate that this is the state label that we're dealing
					// with now
					StateLabelManager.setCurrentStateLabel(stateLabel);
					StateLabelManager.populateLists();
				}
			}
		};
		StateLabelManager.stateLabelDropdown.addActionListener(stateLabelsComboListener);
		stateLabelSelectionPanel.add(StateLabelManager.stateLabelDropdown);
		SpringLayoutUtilities.makeCompactGrid(stateLabelSelectionPanel, 1, 1, 6, 6, 6, 6);
		stateLabelManagementPanel.add(stateLabelSelectionPanel);

		ActionListener createStateLabelButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				StateLabelManager.createNewLabelPopup();
			}
		};

		if (!withStateLabelControlButtons)
		{
			// just a create state label button - used when right-clicking on a
			// StatesNode
			JPanel stateLabelButtonPanel = new JPanel();
			stateLabelButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			String[] buttonNames = {"Create State Label"};
			ActionListener[] buttonListeners = {createStateLabelButtonListener};
			stateLabelButtonPanel.add(new ButtonBar(buttonNames, buttonListeners));
			stateLabelManagementPanel.add(stateLabelButtonPanel);
		}
		else
		{
			// have a delete and edit state label button as well - used when the
			// state label manager is
			// invoked from the Tools menu
			JPanel stateLabelButtonPanel = new JPanel();
			stateLabelButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
			String[] buttonNames = {"Create State Label", "Edit State Label", "Delete State Label"};
			ActionListener editStateLabelButtonListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					String selectedStateLabel = (String) StateLabelManager.stateLabelDropdown.getSelectedItem();
					if (!selectedStateLabel.equals("-- Select --") &&
						!selectedStateLabel.equals("CREATE NEW STATE LABEL"))
					{
						StateLabelManager.createEditLabelPopup();
					}
				}
			};
			ActionListener deleteStateLabelButtonListener = new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					String selectedStateLabel = (String) StateLabelManager.stateLabelDropdown.getSelectedItem();
					if (!selectedStateLabel.equals("-- Select --") &&
						!selectedStateLabel.equals("CREATE NEW STATE LABEL"))
					{
						QueryManager.getData().removeStateLabel(selectedStateLabel);
						StateLabelManager.setCurrentStateLabel(null);
						StateLabelManager.update();
					}
				}
			};
			ActionListener[] buttonListeners = {createStateLabelButtonListener,
					editStateLabelButtonListener,
					deleteStateLabelButtonListener};
			stateLabelButtonPanel.add(new ButtonBar(buttonNames, buttonListeners));
			stateLabelManagementPanel.add(stateLabelButtonPanel);
		}

		return stateLabelManagementPanel;
	}

	private static void setCurrentStateLabel(final String stateLabel)
	{
		StateLabelManager.currentStateLabel = stateLabel;
	}

	/**
	 * Sets up the state label dropdown menu
	 * 
	 * @param selectedLabel
	 */
	private static void populateStateLabelDropdown()
	{
		DefaultComboBoxModel model = new DefaultComboBoxModel();
		if (StateLabelManager.currentStateLabel == null)
			model.addElement("-- Select --");
		ArrayList<String> stateLabels = QueryManager.getData().getStateLabelNames();
		Iterator<String> i = stateLabels.iterator();
		while (i.hasNext())
		{
			String stateLabelName = i.next();
			model.addElement(stateLabelName);
		}
		if (StateLabelManager.currentStateLabel == null)
			model.setSelectedItem("-- Select --");
		else model.setSelectedItem(StateLabelManager.currentStateLabel);
		StateLabelManager.stateLabelDropdown.setModel(model);
	}

	/**
	 * This method loads the names of the state groups and adds them to the
	 * source / destination JLists
	 */
	private static void populateLists()
	{
		StateLabelManager.populateStateGroupsList();
		StateLabelManager.populateStatesAssignedToLabelList(StateLabelManager.currentStateLabel);
	}

	/**
	 * This method loads the names of the state groups that have been defined so
	 * far
	 */
	private static void populateStateGroupsList()
	{
		StateLabelManager.updateAvailableStateGroups();
		String[] stateGrpNames = StateLabelManager.getStateNames(StateLabelManager.availableStateGroups);
		DefaultListModel model = new DefaultListModel();
        for(String stateName : stateGrpNames)
        {
            model.addElement(stateName);
        }
		StateLabelManager.stateGroupsList.setModel(model);
	}

	private static void updateAvailableStateGroups()
	{
        StateLabelManager.availableStateGroups = ApplicationSettings.getApplicationView().getCurrentPetriNetView().getStateGroups();
	}

	private static String[] getStateNames(final StateGroup[] states)
	{
		int size = states.length;
		ArrayList<String> stateNames = new ArrayList<String>();
		for (int i = 0; i < size; i++)
			stateNames.add(states[i].getName());
		stateNames = QueryManager.getData().sortArrayList(stateNames);
		String[] names = new String[size];
		int j = 0;
		Iterator<String> i = stateNames.iterator();
		while (i.hasNext())
		{
			String stateName = i.next();
			names[j] = stateName;
			j++;
		}
		return names;
	}

	/**
	 * This method loads in the state definitions that have been assigned to a
	 * particular state label
     * @param stateLabel
     */
	private static void populateStatesAssignedToLabelList(final String stateLabel)
	{
		if (stateLabel != null)
		{
			DefaultListModel model = new DefaultListModel();
			ArrayList<String> assignedStateNames = QueryManager	.getData()
																.getStatesAssignedToStateLabel(stateLabel);
			if (assignedStateNames != null)
			{
				assignedStateNames = QueryManager.getData().sortArrayList(assignedStateNames);
				Iterator<String> i = assignedStateNames.iterator();
				while (i.hasNext())
				{
					String assignedStateName = i.next();
					model.addElement(assignedStateName);
				}
			}
			StateLabelManager.statesAssignedToLabelList.setModel(model);
		}
		else
		{
			DefaultListModel model = new DefaultListModel();
			StateLabelManager.statesAssignedToLabelList.setModel(model);
		}
	}

	public static void update()
	{
		// clear the new label text field
		StateLabelManager.stateLabelTextField.setText("");
		// refresh state group array
        StateLabelManager.availableStateGroups = ApplicationSettings.getApplicationView().getCurrentPetriNetView().getStateGroups();
		// refresh dropdown menu
		StateLabelManager.populateStateLabelDropdown();
		// refresh the list of state group names
		StateLabelManager.populateLists();
	}

	/**
	 * This is the popup for creating a new state label
	 */
	private static void createNewLabelPopup()
	{
		StateLabelManager.stateLabelDialog = new JDialog(	StateLabelManager.popupDialog,
															"Create New State Label",
															true);

		// text panel
		JPanel stateLabelNamePanel = new JPanel();
		stateLabelNamePanel.setLayout(new SpringLayout());
		JLabel stateLabelSelectionLabel = new JLabel("New State Label: ", SwingConstants.TRAILING);
		stateLabelNamePanel.add(stateLabelSelectionLabel);
		stateLabelNamePanel.add(StateLabelManager.stateLabelTextField);
		StateLabelManager.stateLabelDialog.add(stateLabelNamePanel);
		SpringLayoutUtilities.makeCompactGrid(stateLabelNamePanel, 1, 2, 6, 6, 6, 6);

		// button panel
		JPanel stateLabelButtonsPanel = new JPanel();
		JButton createNewLabelButton = new JButton("Create State Label");
		StateLabelManager.stateLabelDialog.getRootPane().setDefaultButton(createNewLabelButton);
		ActionListener createNewLabelButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				// create state label
				if (StateLabelManager.stateLabelTextField.getText().equals("") ||
					!StateLabelManager.containsText(StateLabelManager.stateLabelTextField.getText()))
					JOptionPane.showMessageDialog(null, "Please specify a state label name");
				else
				{
					if (!QueryManager	.getData()
										.stateLabelExistsAlready(StateLabelManager.stateLabelTextField.getText()))
					{
						String newStateLabelName = StateLabelManager.stateLabelTextField.getText();
						QueryManager.getData().addStateLabel(newStateLabelName, null);
						StateLabelManager.setCurrentStateLabel(newStateLabelName);
						StateLabelManager.update();
						StateLabelManager.stateLabelDialog.dispose();
					}
					else
					{
						JOptionPane.showMessageDialog(	QueryManager.getEditor().getContentPane(),
														"The state state label name you have specified exists \n"
														+ "already. Please choose a different name for your label.",
														"Warning",
														JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		};
		createNewLabelButton.addActionListener(createNewLabelButtonListener);
		JButton cancelButton = new JButton("Cancel");
		ActionListener cancelButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				StateLabelManager.update();
				StateLabelManager.stateLabelDialog.dispose();
			}
		};
		cancelButton.addActionListener(cancelButtonListener);
		stateLabelButtonsPanel.add(createNewLabelButton);
		stateLabelButtonsPanel.add(cancelButton);

		// main panel
		JPanel stateLabelPanel = new JPanel();
		stateLabelPanel.setBorder(new EtchedBorder());
		stateLabelPanel.setLayout(new BoxLayout(stateLabelPanel, BoxLayout.Y_AXIS));
		stateLabelPanel.add(stateLabelNamePanel);
		stateLabelPanel.add(stateLabelButtonsPanel);
		StateLabelManager.stateLabelDialog.add(stateLabelPanel);

		// make popup visible
		StateLabelManager.stateLabelDialog.pack();
		StateLabelManager.stateLabelDialog.setLocationRelativeTo(null);
		StateLabelManager.stateLabelDialog.setVisible(true);
	}

	private static void createEditLabelPopup()
	{
		StateLabelManager.stateLabelDialog = new JDialog(	StateLabelManager.popupDialog,
															"Edit State Label",
															true);

		// text panel
		JPanel stateLabelNamePanel = new JPanel();
		stateLabelNamePanel.setLayout(new SpringLayout());
		JLabel stateLabelSelectionLabel = new JLabel("State Label: ", SwingConstants.TRAILING);
		stateLabelNamePanel.add(stateLabelSelectionLabel);
		StateLabelManager.stateLabelTextField.setText(StateLabelManager.currentStateLabel);
		stateLabelNamePanel.add(StateLabelManager.stateLabelTextField);
		StateLabelManager.stateLabelDialog.add(stateLabelNamePanel);
		SpringLayoutUtilities.makeCompactGrid(stateLabelNamePanel, 1, 2, 6, 6, 6, 6);

		// button panel
		JPanel stateLabelButtonsPanel = new JPanel();
		String[] buttonNames = {"OK", "Cancel"};
		ActionListener okButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				// create state label
				if (StateLabelManager.stateLabelTextField.getText().equals("") ||
					!StateLabelManager.containsText(StateLabelManager.stateLabelTextField.getText()))
					JOptionPane.showMessageDialog(null, "Please specify a state label name");
				else
				{
					if (!StateLabelManager.stateLabelTextField	.getText()
																.equals(StateLabelManager.currentStateLabel) &&
						!QueryManager	.getData()
										.stateLabelExistsAlready(StateLabelManager.stateLabelTextField.getText()))
					{
						// we're trying to rename the state label to something
						// that doesn't exist yet
						String newStateLabelName = StateLabelManager.stateLabelTextField.getText();
						QueryManager.getData().renameStateLabel(StateLabelManager.currentStateLabel,
																newStateLabelName);
						StateLabelManager.setCurrentStateLabel(newStateLabelName);
						StateLabelManager.update();
						StateLabelManager.stateLabelDialog.dispose();
					}
					else if (!StateLabelManager.stateLabelTextField	.getText()
																	.equals(StateLabelManager.currentStateLabel) &&
								QueryManager.getData()
											.stateLabelExistsAlready(StateLabelManager.stateLabelTextField.getText()))
					{
						// we're trying to rename the state label to something
						// that is not itself and exists
						// already
						JOptionPane.showMessageDialog(	QueryManager.getEditor().getContentPane(),
														"The name you are trying to rename the state label to \n"
														+ "exists already. Please choose a different name.",
														"Warning",
														JOptionPane.ERROR_MESSAGE);
					}
					else if (StateLabelManager.stateLabelTextField	.getText()
																	.equals(StateLabelManager.currentStateLabel))
					{
						// no change
						StateLabelManager.stateLabelDialog.dispose();
					}
				}
			}
		};
		ActionListener cancelButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				StateLabelManager.clearAll();
				StateLabelManager.stateLabelDialog.dispose();
			}
		};
		ActionListener[] buttonListeners = {okButtonListener, cancelButtonListener};
		stateLabelButtonsPanel.add(new ButtonBar(buttonNames, buttonListeners));

		// main panel
		JPanel stateLabelPanel = new JPanel();
		stateLabelPanel.setBorder(new EtchedBorder());
		stateLabelPanel.setLayout(new BoxLayout(stateLabelPanel, BoxLayout.Y_AXIS));
		stateLabelPanel.add(stateLabelNamePanel);
		stateLabelPanel.add(stateLabelButtonsPanel);
		StateLabelManager.stateLabelDialog.add(stateLabelPanel);

		// make popup visible
		StateLabelManager.stateLabelDialog.pack();
		StateLabelManager.stateLabelDialog.setLocationRelativeTo(null);
		StateLabelManager.stateLabelDialog.setVisible(true);
	}

	/**
	 * Creates the state label assignment manager
     * @return
     */
	private static JPanel getStatesAssignmentManagerPanel()
	{
		JPanel stateAssignmentManagerPanel = new JPanel();
		stateAssignmentManagerPanel.setBorder((new TitledBorder(new EtchedBorder(), "State Label Definition")));
		stateAssignmentManagerPanel.setLayout(new BoxLayout(stateAssignmentManagerPanel, BoxLayout.Y_AXIS));

		// panel to hold the state definitions and state label panels, as well
		// as the assignment buttons
		JPanel stateAssignmentPanel = new JPanel();
		stateAssignmentPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

		// Create button panel
		JPanel stateButtonsPanel = new JPanel();
		JButton createStateDefinitionButton = new JButton("Define State Group");
		ActionListener createStateDefinitionButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				// check whether places have been defined on the underlying
				// model
				boolean okToProceed = QueryManager.getData().checkCurrentData("States");
				if (okToProceed)
				{
					// bring up the state editor popup
					StateGroupEditor stateEditor = new StateGroupEditor();
                    PetriNetView pnModel = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
					stateEditor.addState(pnModel);
					StateLabelManager.update();
				}
			}
		};
		createStateDefinitionButton.addActionListener(createStateDefinitionButtonListener);
		JButton editStateDefinitionButton = new JButton("Edit State Group");
		ActionListener editStateDefinitionButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				int[] selectedIndices = StateLabelManager.stateGroupsList.getSelectedIndices();
				if (selectedIndices.length > 0)
				{
					if (selectedIndices.length > 1)
					{
						JOptionPane.showMessageDialog(	QueryManager.getEditor().getContentPane(),
														"To edit a state group, please select a single state \n"
														+ "group from the list of states defined on the model.",
														"Warning",
														JOptionPane.INFORMATION_MESSAGE);
					}
					else
					{
						int selectedIndex = selectedIndices[0];
						// bring up the state editor popup
						String selectedStateName = (String) StateLabelManager.stateGroupsList	.getModel()
																								.getElementAt(selectedIndex);
						StateGroup selectedState = StateLabelManager.getStateGroup(selectedStateName);
						StateGroupEditor stateEditor = new StateGroupEditor();
                        PetriNetView pnModel = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
						stateEditor.editState(pnModel, selectedState);
						StateLabelManager.update();
					}
				}
				else
				{
					JOptionPane.showMessageDialog(	QueryManager.getEditor().getContentPane(),
													"To edit a state group, please select a single state \n"
													+ "group from the list of states defined on the model.",
													"Warning",
													JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
		editStateDefinitionButton.addActionListener(editStateDefinitionButtonListener);
		JButton deleteStateDefinitionButton = new JButton("Delete State Group");
		ActionListener deleteStateDefinitionButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				int[] stateGroupsListSelectedIndices = StateLabelManager.stateGroupsList.getSelectedIndices();
				if (stateGroupsListSelectedIndices.length > 0)
				{
                    PetriNetView pnModel = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
                    for(int selectionIndex : stateGroupsListSelectedIndices)
                    {
                        String selectedStateName = (String) StateLabelManager.stateGroupsList.getModel()
                                .getElementAt(selectionIndex);
                        StateGroup state = StateLabelManager.getStateGroup(selectedStateName);
                        // remove state from _dataLayer
                        pnModel.removeStateGroup(state);
                        // remove all references to the state in all state
                        // labels
                        QueryManager.getData().removeStateFromAllLabels(selectedStateName);
                    }
					StateLabelManager.update();
				}
				else
				{
					JOptionPane.showMessageDialog(	QueryManager.getEditor().getContentPane(),
													"To delete a state group, please select it from the list \n"
													+ "of defined state groups on the left panel.",
													"Warning",
													JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
		deleteStateDefinitionButton.addActionListener(deleteStateDefinitionButtonListener);
		stateButtonsPanel.add(createStateDefinitionButton);
		stateButtonsPanel.add(editStateDefinitionButton);
		stateButtonsPanel.add(deleteStateDefinitionButton);

		// panel for the state definitions
		JPanel stateDefinitionsPanel = new JPanel();
		stateDefinitionsPanel.setBorder((new TitledBorder(	new EtchedBorder(),
															"State Groups Defined On The Model")));
		StateLabelManager.stateGroupsList.setLayoutOrientation(JList.VERTICAL);
		StateLabelManager.stateGroupsList.setSelectionModel(new ToggleSelectionModel());
		StateLabelManager.stateGroupsList.setVisibleRowCount(-1);
		StateLabelManager.populateStateGroupsList();
		JScrollPane stateDefinitionsListScroller = new JScrollPane(StateLabelManager.stateGroupsList);
		stateDefinitionsListScroller.setPreferredSize(new Dimension(300, 200));
		stateDefinitionsPanel.add(stateDefinitionsListScroller);
		stateAssignmentPanel.add(stateDefinitionsPanel);

		// panel for the assignment buttons
		JPanel assignmentButtonsPanel = new JPanel();
		assignmentButtonsPanel.setLayout(new BoxLayout(assignmentButtonsPanel, BoxLayout.Y_AXIS));
		JButton assignStateToStateLabelButton = new JButton("->");
		ActionListener assignStateToStateLabelButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (StateLabelManager.currentStateLabel != null)
				{
					int[] stateGroupsListSelectedIndices = StateLabelManager.stateGroupsList.getSelectedIndices();
					if (stateGroupsListSelectedIndices.length > 0)
					{
                        for(int selectionIndex : stateGroupsListSelectedIndices)
                        {
                            String selectedStateName = (String) StateLabelManager.stateGroupsList.getModel()
                                    .getElementAt(selectionIndex);
                            // assign state to state label
                            QueryManager.getData().addStateLabel(StateLabelManager.currentStateLabel,
                                                                 selectedStateName);
                        }
						StateLabelManager.update();
					}
				}
				else
				{
					JOptionPane.showMessageDialog(	QueryManager.getEditor().getContentPane(),
													"Before you can assign state groups to a state label, you have \n"
													+ "to specify the state label. Please choose a label from the \n"
													+ "dropdown menu.\n",
													"Warning",
													JOptionPane.INFORMATION_MESSAGE);
				}
			}
		};
		assignStateToStateLabelButton.addActionListener(assignStateToStateLabelButtonListener);
		assignmentButtonsPanel.add(assignStateToStateLabelButton);
		JButton removeStateFromStateLabelButton = new JButton("<-");
		ActionListener removeStateFromStateLabelButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (StateLabelManager.currentStateLabel != null)
				{
					int[] statesAssignedSelectedIndices = StateLabelManager.statesAssignedToLabelList.getSelectedIndices();
					if (statesAssignedSelectedIndices.length > 0)
					{
                        for(int selectionIndex : statesAssignedSelectedIndices)
                        {
                            String selectedStateName = (String) StateLabelManager.statesAssignedToLabelList.getModel()
                                    .getElementAt(selectionIndex);
                            // remove state from label
                            QueryManager.getData()
                                    .removeStateFromStateLabel(StateLabelManager.currentStateLabel,
                                                               selectedStateName);
                        }
					}
					StateLabelManager.update();
				}
			}
		};
		removeStateFromStateLabelButton.addActionListener(removeStateFromStateLabelButtonListener);
		assignmentButtonsPanel.add(removeStateFromStateLabelButton);
		stateAssignmentPanel.add(assignmentButtonsPanel);

		// panel for the states assigned to the state label
		JPanel statesAssignedToStateLabelPanel = new JPanel();
		statesAssignedToStateLabelPanel.setBorder((new TitledBorder(new EtchedBorder(),
																	"State Groups Assigned To Label")));
		StateLabelManager.statesAssignedToLabelList.setLayoutOrientation(JList.VERTICAL);
		StateLabelManager.statesAssignedToLabelList.setSelectionModel(new ToggleSelectionModel());
		StateLabelManager.statesAssignedToLabelList.setVisibleRowCount(-1);
		StateLabelManager.populateStatesAssignedToLabelList(null);
		JScrollPane statesAssignedListScroller = new JScrollPane(StateLabelManager.statesAssignedToLabelList);
		statesAssignedListScroller.setPreferredSize(new Dimension(300, 200));
		statesAssignedToStateLabelPanel.add(statesAssignedListScroller);
		stateAssignmentPanel.add(statesAssignedToStateLabelPanel);

		// Add components to panel
		stateAssignmentManagerPanel.add(stateButtonsPanel);
		stateAssignmentManagerPanel.add(stateAssignmentPanel);

		return stateAssignmentManagerPanel;
	}

	/**
	 * Checks if a string contains anything but spaces - needed for text field
	 * validation
	 * 
	 * @param inputSting
	 * @return
     * @param inputString
	 */
	private static boolean containsText(final String inputString)
	{
		boolean stringContainsText = false;
		if (!inputString.equals(""))
		{
			for (int i = 0; i < inputString.length(); i++)
			{
				char chr = inputString.charAt(i);
				if (Character.isDigit(chr) || Character.isLetter(chr))
					stringContainsText = true;
			}
		}
		return stringContainsText;
	}

	private static StateGroup getStateGroup(final String stateName)
	{
		if (StateLabelManager.availableStateGroups != null)
		{
			for (int i = 0; i < StateLabelManager.availableStateGroups.length; i++)
			{
				StateGroup stateGroup = StateLabelManager.availableStateGroups[i];
				String stateGroupName = stateGroup.getName();
				if (stateName.equals(stateGroupName))
					return stateGroup;
			}
		}
		return null;
	}

	public static void stateLabelManagerDialog()
	{
		// build popup
		StateLabelManager.popupDialog = new JDialog(QueryManager.getEditor(), "State Label Manager", true);
		StateLabelManager.popupDialog.setMinimumSize(new Dimension(730, 450));
		Container contentPane = StateLabelManager.popupDialog.getContentPane();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
		contentPane.add(StateLabelManager.getStateLabelManagementPanel(true));
		contentPane.add(StateLabelManager.getStatesAssignmentManagerPanel());
		JPanel okButtonPanel = new JPanel();
		okButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		JButton okButton = new JButton("OK");
		StateLabelManager.popupDialog.getRootPane().setDefaultButton(okButton);
		ActionListener okButtonListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				StateLabelManager.clearAll();
				StateLabelManager.popupDialog.dispose();
			}
		};
		okButton.addActionListener(okButtonListener);
		okButtonPanel.add(okButton);
		contentPane.add(okButtonPanel);

		// take care of popup closing
		StateLabelManager.popupDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		StateLabelManager.popupDialog.addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(final WindowEvent we)
			{
				StateLabelManager.clearAll();
				StateLabelManager.popupDialog.dispose();
			}
		});

		// load in the latest info
		StateLabelManager.update();

		// show popup
		StateLabelManager.popupDialog.pack();
		StateLabelManager.popupDialog.setLocationRelativeTo(null);
		StateLabelManager.popupDialog.setVisible(true);

	}

	/**
	 * This class allows a JList to operate in a click toggle fashion - see
	 * JList java doc
	 */
	static class ToggleSelectionModel extends DefaultListSelectionModel
	{

		private static final long	serialVersionUID	= 1L;
		boolean						gestureStarted		= false;

		public ToggleSelectionModel() {

		}

		@Override
		public void setSelectionInterval(final int index0, final int index1)
		{
			if (isSelectedIndex(index0) && !this.gestureStarted)
			{
				super.removeSelectionInterval(index0, index1);
			}
			else
			{
				super.setSelectionInterval(index0, index1);
			}
			this.gestureStarted = true;
		}

		@Override
		public void setValueIsAdjusting(final boolean isAdjusting)
		{
			if (!isAdjusting)
			{
				this.gestureStarted = false;
			}
		}
	}

}
