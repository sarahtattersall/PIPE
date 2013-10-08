package pipe.modules.passageTimeForTaggedNet;

import pipe.common.dataLayer.StateGroup;
import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.ButtonBar;
import pipe.modules.interfaces.IModule;
import pipe.modules.passage.ProgressBarHTMLPane;
import pipe.views.PetriNetView;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.ArrayList;


public class Passage implements IModule
{

    private static final String MODULE_NAME = "Passage Time Analysis For Tagged Net";


    private PetriNetView _pnmlData;        // Petri Net to be analysed
    private StateGroup[] stateGroupData;

    JCheckBox clearCacheBox, autoTimeBox;
    private JList startStatesList;
    private JList endStatesList;
    private JButton addBtn;
    private JButton removeBtn;
    private JButton editBtn;
    JButton validate;
    //private JCheckBox jcbCumulative;


    // Time settings panel
    private JTextField startTime;
    private JTextField endTime;
    private JSpinner timeStep;
    private JTabbedPane tabbedPane;
    private ProgressBarHTMLPane progressPane = null;
    private JPanel resultsPanel = null;

    public Passage()
    {
    }

    /**
     * The module name
     */
    public String getName()
    {
        return MODULE_NAME;
    }


    public void start()
    {
        _pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
        stateGroupData = _pnmlData.getStateGroups();

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Build primary tab
        JDialog guiDialog = new JDialog(ApplicationSettings.getApplicationView(), MODULE_NAME, true);
        Container contentPane = guiDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));


        // 1 Set layout
        JPanel setupPanel = new JPanel();
        setupPanel.setLayout(new BoxLayout(setupPanel, BoxLayout.PAGE_AXIS));

        // 3 Add Source - Destination States selection panel
        setupPanel.add(getStatesPanel());


        // 4 Add Passage time parameters
        setupPanel.add(getAnalysisPanel());

        // setupPanel.add( jcbCumulative=new JCheckBox("Cumulative",false)  );


        //autoTimeBox = new JCheckBox("Automatically determine time range");
        //setupPanel.add(autoTimeBox);

        // 5 Add Analyse and Validate button
        setupPanel.add(new ButtonBar("Validate", validateClick));
        setupPanel.add(new ButtonBar("Analyse", analyseButtonClick));


        // 6 Add setup panel to tabbed pane, add tabbed pane to guiDialog
        tabbedPane.addTab("Setup", setupPanel);
        //System.out.println("get analysis panel done");
        contentPane.add(tabbedPane);


        // 7 Make window fit contents' preferred size, centre on screen, display
        guiDialog.pack();
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);


    }

    /*
     public void itemStateChanged(ItemEvent e) {
         Object source = e.getItemSelectable();

         if (source == autoTimeBox) {
             if(autoTimeBox.isSelected()){
                 startTime.setEnabled(false);
                 endTime.setEnabled(false);
                 timeStep.setEnabled(false);
             }else{
                 startTime.setEnabled(true);
                 endTime.setEnabled(true);
                 timeStep.setEnabled(true);
             }
         }
     }*/

    private final ActionListener validateClick = new ActionListener()
    {
        public void actionPerformed(ActionEvent arg0)
        {
            boolean result = _pnmlData.validTagStructure();
        }

    };

    private final ActionListener analyseButtonClick = new ActionListener()
    {
        public void actionPerformed(ActionEvent arg0)
        {
            // Returns currently selected source and destination states
            ArrayList sourceStateGroups = getSelectedStateGroups(startStatesList);
            ArrayList destinationStateGroups = getSelectedStateGroups(endStatesList);

            AnalysisSetting analysisSetting = getTimeSettings();

            // Create Progress Tab and set it as selected
            if(progressPane == null)
            {
                progressPane = new ProgressBarHTMLPane("Analysis Progress");
                tabbedPane.addTab("Progress", progressPane);
            }

            if(resultsPanel == null)
            {
                resultsPanel = new JPanel();
                //Add results pane
                tabbedPane.addTab("Results", resultsPanel);

            }


            tabbedPane.setSelectedComponent(progressPane);


            String statusMesg = "<h2>Passage Time Analysis</h2>";

            try
            {

                if(_pnmlData == null) return;

                else if(!_pnmlData.hasValidatedStructure())
                    statusMesg += "The structure contains tagged elements and needs to be validated first";

                else if(!_pnmlData.getPetriNetObjects().hasNext())
                    statusMesg += "No Petri net objects defined!";


                else if(analysisSetting == null)
                    statusMesg += "Please check the analysis settings are valid.";

                else if(sourceStateGroups == null)
                    statusMesg += "No source states selected!";

                else if(destinationStateGroups == null)
                    statusMesg += "No destination states selected!";

                else
                {
                    TransModel model = new TransModel(_pnmlData, sourceStateGroups, destinationStateGroups, analysisSetting);
                    ResultGeneration generateResult = new ResultGeneration(resultsPanel);
                    int generateOK = generateResult.init();
                    if(generateOK == 0)
                    {
                        statusMesg += "Analysis completed successfully";
                        tabbedPane.setSelectedComponent(resultsPanel);
                    }
                    else if(generateOK == 1)
                        statusMesg += " Fail to complete analysis <br> steady state vector status ... FAILED <br/> ";
                    else if(generateOK == 2)
                        statusMesg += " Fail to complete analysis <br>suggest, lowering the t-range or reducing rates <br/> ";

                }
            }
            catch(IOException e)
            {
                statusMesg = "Cannot run program 'hydra-s'.<br /> The system cannot find the file specified.";
                e.printStackTrace();
            }

            catch(Throwable e)
            {
                e.printStackTrace();
            }
            progressPane.setText(statusMesg);
        }

    };

    /**
     * @return AnalysisSetting object representing the selected options; null if any setting is invalid
     */
    private AnalysisSetting getTimeSettings()
    {
        String startString = startTime.getText();
        String endString = endTime.getText();
        Double step = (Double) timeStep.getValue();
        //String method = (String)methodCombo.getSelectedItem();
        //int numProcessors;

        try
        {

            //if(autoTimeBox.isSelected()) {
            //numProcessors = serverPanel.getNumProcessors();
            //return new AnalysisSetting(1.0, 1.0, 1.0);
            //}else
            {
                double startT = Double.valueOf(startString.trim()).doubleValue();
                double endT = Double.valueOf(endString.trim()).doubleValue();
                double stepT = step.doubleValue();

                //numProcessors = serverPanel.getNumProcessors();

                // Final check that the values are appropriate
                if((startT < endT && startT >= 0) || (startT == 1 && endT == 1 && stepT == 1))
                    return new AnalysisSetting(startT, endT, stepT);
                else
                    return null;
            }
        }
        catch(NumberFormatException nfe)
        {
            return null;
        }

    }

    /**
     * This ActionListener responds to the buttons within the
     * state group selection panel
     * i.e. the Add, Remove, Edit buttons
     */
    private final ActionListener stateButtonClick = new ActionListener()
    {
        public void actionPerformed(ActionEvent event)
        {
            if(event.getSource() == addBtn)
            {
                StateEditor addState = new StateEditor();
                addState.setParent(Passage.this);
                addState.addState(_pnmlData);
            }

            else if(event.getSource() == removeBtn)
            {
                int startSelected[] = startStatesList.getSelectedIndices();
                int endSelected[] = endStatesList.getSelectedIndices();

                int maxSize = startSelected.length + endSelected.length;
                int[] removeList = new int[maxSize];

                // These loops merges the two sets of selected indices, removing duplicates
                int i = 0, j = 0, k = 0;
                for(i = 0; i < startSelected.length; i++)
                {
                    while(j < endSelected.length && endSelected[j] < startSelected[i])
                        removeList[k++] = endSelected[j++];

                    if(j < endSelected.length && endSelected[j] == startSelected[i])
                        j++;

                    removeList[k++] = startSelected[i];
                }
                while(j < endSelected.length)
                    removeList[k++] = endSelected[j++];


                // Remove the state groups
                for(i = 0; i < k; i++)
                    _pnmlData.removeStateGroup(stateGroupData[removeList[i]]);

                // Update local array and UI
                updateStateLists();
            }

            else if(event.getSource() == editBtn)
            {
                int selectedState = startStatesList.getSelectedIndex();

                // If the state is not selected from the source states, retrieve from endStatesList
                if(selectedState == -1)
                    selectedState = endStatesList.getSelectedIndex();


                if(selectedState != -1)
                {
                    StateEditor editState = new StateEditor();
                    editState.setParent(Passage.this);
                    editState.editState(_pnmlData, stateGroupData[selectedState]);
                }

            }
        }

    };


    // Panel for Source and Destination state groups display and editing
    private JPanel getStatesPanel()
    {
        JPanel statesPanel = new JPanel();
        statesPanel.setBorder((new TitledBorder(new EtchedBorder(), "Source / Destination States")));
        statesPanel.setLayout(new BoxLayout(statesPanel, BoxLayout.PAGE_AXIS));


        // Create a panel for the labels
        JPanel labelPanel = new JPanel();
        labelPanel.setLayout(new GridLayout(1, 2));
        labelPanel.add(new JLabel("Source States"));
        labelPanel.add(new JLabel("Destination States"));
        labelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, labelPanel.getPreferredSize().height));


        startStatesList = new JList();
        endStatesList = new JList();
        populateLists();


        // Set the lists' alignment and selection properties
        startStatesList.setLayoutOrientation(JList.VERTICAL);
        startStatesList.setSelectionModel(new ToggleSelectionModel());
        startStatesList.setVisibleRowCount(-1);

        endStatesList.setLayoutOrientation(JList.VERTICAL);
        endStatesList.setSelectionModel(new ToggleSelectionModel());
        endStatesList.setVisibleRowCount(-1);

        // Add lists to scroller panes to allow scrolling of long lists
        JScrollPane startListScroller = new JScrollPane(startStatesList);
        JScrollPane endListScroller = new JScrollPane(endStatesList);

        // Set prefered UI size - this is changed by resizing the window
        startListScroller.setPreferredSize(new Dimension(300, 200));
        endListScroller.setPreferredSize(new Dimension(300, 200));


        // Create a panel to group the two lists
        JPanel stateListsPanel = new JPanel();
        stateListsPanel.setLayout(new GridLayout(1, 2));
        stateListsPanel.add(startListScroller);
        stateListsPanel.add(endListScroller);


        // Create buttons
        addBtn = new JButton("Add State");
        addBtn.setMnemonic(KeyEvent.VK_A);
        addBtn.addActionListener(stateButtonClick);

        removeBtn = new JButton("Remove State");
        removeBtn.setMnemonic(KeyEvent.VK_R);
        removeBtn.addActionListener(stateButtonClick);

        editBtn = new JButton("Edit State");
        editBtn.setMnemonic(KeyEvent.VK_E);
        editBtn.addActionListener(stateButtonClick);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(addBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(editBtn);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, buttonPanel.getPreferredSize().height));


        // Add components to panel
        statesPanel.add(labelPanel);
        statesPanel.add(stateListsPanel);
        statesPanel.add(buttonPanel);


        return statesPanel;
    }

    // This method loads the names of the state groups and adds them to the source / destination JLists
    private void populateLists()
    {
        // Load the name of the state groups
        String[] stateGrpNames = stateNames(stateGroupData);

        startStatesList.setListData(stateGrpNames);
        endStatesList.setListData(stateGrpNames);
    }


    private String[] stateNames(StateGroup[] states)
    {
        int size = states.length;
        String[] names = new String[size];

        for(int i = 0; i < size; i++)
            names[i] = states[i].getName();

        return names;

    }

    void updateStateLists()
    {
        // refresh state group array
        stateGroupData = _pnmlData.getStateGroups();

        // refresh the list of state group names
        populateLists();
    }


    // This method creates the panel for entering the required start and end time along with the step
    private JPanel getAnalysisPanel()
    {
        JPanel timePanel = new JPanel();

        timePanel.setBorder((new TitledBorder(new EtchedBorder(), "Analysis Settings")));
        timePanel.setLayout(new GridLayout(2, 2));

        JPanel startEndTime = new JPanel(new GridLayout(2, 2));

        startEndTime.add(new JLabel("Start Time:"));
        startTime = new JTextField(4);
        startEndTime.add(startTime);

        startEndTime.add(new JLabel("End Time:"));
        endTime = new JTextField(4);
        startEndTime.add(endTime);

        startEndTime.setMaximumSize(new Dimension(Integer.MAX_VALUE, startEndTime.getPreferredSize().height));

        // Create time / method panel holder
        JPanel timeMethodPanel = new JPanel(new GridLayout(2, 2));

        System.out.println("here");

        // Create time / method panel
        timeMethodPanel.add(new JLabel("Time Step:"));
        timeStep = new JSpinner(new SpinnerNumberModel(0.10, 0.0, 100, 0.1));
        timeMethodPanel.add(timeStep);

        /*
                timeMethodPanel.add(new JLabel("Method:"));

                methodCombo = new JComboBox();
                methodCombo.addItem("Laguerre");
                methodCombo.addItem("Euler");
                timeMethodPanel.add(methodCombo);
        */

        /*
          JPanel autoPanel = new JPanel(new GridLayout(1,1));

          autoTimeBox = new JCheckBox("Automatically determine time range");
          autoTimeBox.addItemListener((ItemListener) this);
          autoPanel.add(autoTimeBox);
          autoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, autoPanel.getPreferredSize().height));

          JPanel cachePanel = new JPanel(new GridLayout(1,1));

          clearCacheBox = new JCheckBox("Clear cached values from server");
          cachePanel.add(clearCacheBox);
          cachePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, cachePanel.getPreferredSize().height));
         */


        // Add to main analysis panel
        timePanel.add(startEndTime);
        timePanel.add(timeMethodPanel);
        //timePanel.add(autoPanel);
        //timePanel.add(cachePanel);
        timePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, timePanel.getPreferredSize().height));


        return timePanel;
    }

    /**
     * This method retrieves the selected state groups from the entered JList
     * and adds them to an ArrayList which is returned
     *
     * @param statesList
     * @return selectedstates ArrayList of the StateGroups that are selected; null if none are selected
     */
    private ArrayList getSelectedStateGroups(JList statesList)
    {
        // Retrieve selected State Groups
        int[] selectedIndices = statesList.getSelectedIndices();

        if(selectedIndices.length > 0)
        {
            // Create output ArrayList
            ArrayList<StateGroup> selectedStates = new ArrayList<StateGroup>(selectedIndices.length);

            // Copy selected states from StateGroupData
            for(int selectedIndice : selectedIndices) selectedStates.add(stateGroupData[selectedIndice]);

            return selectedStates;
        }

        else
            return null;
    }


    //This class allows a JList to operate in a click toggle fashion - see JList java doc
    class ToggleSelectionModel extends DefaultListSelectionModel
    {
        private static final long serialVersionUID = 1L;
        boolean gestureStarted = false;

        public void setSelectionInterval(int index0, int index1)
        {
            if(isSelectedIndex(index0) && !gestureStarted)
                super.removeSelectionInterval(index0, index1);

            else
                super.setSelectionInterval(index0, index1);

            gestureStarted = true;
        }

        public void setValueIsAdjusting(boolean isAdjusting)
        {
            if(!isAdjusting)
                gestureStarted = false;
        }


    }


}
