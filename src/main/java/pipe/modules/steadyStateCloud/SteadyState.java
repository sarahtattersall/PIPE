package pipe.modules.steadyStateCloud;


/**
 * Steady State Analysis module.
 * @author Barry Kearns 
 */

import pipe.common.PerformanceMeasure;
import pipe.gui.ApplicationSettings;
import pipe.gui.widgets.ButtonBar;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.clientCommon.HTMLPane;
import pipe.modules.clientCommon.ServerInfo;
import pipe.modules.clientCommon.ServerPanel;
import pipe.modules.interfaces.IModule;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;
import pipe.views.TransitionView;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class SteadyState implements IModule
{

    private static final String MODULE_NAME = "Steady State Analysis";

    private PetriNetView _pnmlData;        // Petri Net to be analysed
    private JTabbedPane tabbedPane;
    private FileBrowserPanel sourceFilePanel;
    private HTMLPane progressPane = null;
    private ResultsHTMLPane resultsPane = null;
    private ServerPanel serverPanel = null;
    private JList _placesList;
    private JList transitionList;
    private JCheckBox meanStateCBx;
    private JCheckBox varianceStateCBx;
    private JCheckBox stddevStateCBx;
    private JCheckBox distrStateCBx;

    public SteadyState()
    {
    }

    public SteadyState(PetriNetView petriNetView)
    {
        _pnmlData = petriNetView;
    }

    public void start()
    {
        _pnmlData = ApplicationSettings.getApplicationView().getCurrentPetriNetView();

        // Create tabbed pane
        tabbedPane = new JTabbedPane();

        // Build interface
        JDialog guiDialog = new JDialog(ApplicationSettings.getApplicationView(), MODULE_NAME, true);

        Container contentPane = guiDialog.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));

        // 1 Set layout
        JPanel setupPanel = new JPanel();
        setupPanel.setLayout(new BoxLayout(setupPanel, BoxLayout.PAGE_AXIS));

        // 2 Add file browser
        sourceFilePanel = new FileBrowserPanel("Source net", _pnmlData);
        setupPanel.add(sourceFilePanel);

        // 3 Add Server selection panel
        serverPanel = new ServerPanel(guiDialog);
        setupPanel.add(serverPanel.getPanel());

        // 4 Add State Measure panel
        setupPanel.add(getStateMeasurePanel());

        // 5 Add Count Measure panel
        setupPanel.add(getCountMeasurePanel());

        // 6 Add Analyse button
        setupPanel.add(new ButtonBar("Analyse", analyseButtonClick));


        // 7 Add setup panel to tabbed pane, add tabbed pane to guiDialog
        tabbedPane.addTab("Setup", setupPanel);
        contentPane.add(tabbedPane);


        // 8 Make window fit contents' preferred size, centre on screen, display
        guiDialog.pack();
        guiDialog.setLocationRelativeTo(null);
        guiDialog.setVisible(true);

    }


    public String getName()
    {
        return MODULE_NAME;
    }

    private final ActionListener analyseButtonClick = new ActionListener()
    {
        public void actionPerformed(ActionEvent arg0)
        {
            // Returns reference to either current / selected P-N
            _pnmlData = sourceFilePanel.getDataLayer();

            // Returns the currently selected server (-1 if none selected)
            int selectedServer = serverPanel.getSelectedServerIndex();

            // Returns the selected places / transitions for analysis
            PerformanceMeasure selectedMeasures = getSelectedEstimators();

            // Create Progress Tab and set it as selected
            if(progressPane == null)
            {
                progressPane = new HTMLPane("Analysis Progress");
                tabbedPane.addTab("Progress", progressPane);
            }

            if(resultsPane == null)
                resultsPane = new ResultsHTMLPane(_pnmlData.getPNMLName());


            tabbedPane.setSelectedComponent(progressPane);

            String statusMesg = "<h2>Steady State Analysis</h2>";


            if(_pnmlData == null) return;

            else if(!_pnmlData.getPetriNetObjects().hasNext())
                statusMesg += "No Petri net objects defined!";

            else if(selectedServer == -1)
                statusMesg += "No server selected!";

            else if(selectedMeasures.getStatesSize() > 0 && selectedMeasures.getEstimatorsSize() < 1)
                statusMesg += "States selected but no estimator(s) choosen";

            else
            {
                ServerInfo serverInfo = serverPanel.getSelectedServer();

                Analyse analyse = new Analyse(_pnmlData, progressPane, resultsPane);
                analyse.setServer(serverInfo.getAddress(), serverInfo.getPort());
                analyse.setStateMeasure(selectedMeasures);
                analyse.setTabbedPane(tabbedPane);


                // Start Analyse thread
                Thread analyseTrd = new Thread(analyse);
                analyseTrd.start();
                return;
            }

            progressPane.setText(statusMesg);
        }
    };


    /**
     * This method reads the UI selections (State / Count JLists and Checkboxes)
     * and produces a Performance measure object containing these values
     *
     * @return The Performance measure object corresponding to the UI selections
     */
    public PerformanceMeasure getSelectedEstimators()
    {
        String name;
        PlaceView currPlaceView;
        TransitionView currTrans;
        PerformanceMeasure performanceMeasure = new PerformanceMeasure();

        // 1. Get State Measure information

        // Retrieve the names of selected state measures
        Object[] selectedStates = _placesList.getSelectedValues();

        // Convert the place names into IDs, and add to output
        for(Object selectedState : selectedStates)
        {
            name = (String) selectedState;
            currPlaceView = _pnmlData.getPlaceByName(name);
            performanceMeasure.addState(currPlaceView.getId());
        }

        // Add the set of selected state estimator checkboxes
        if(meanStateCBx.isSelected())
            performanceMeasure.addStateEstimator("mean");

        if(varianceStateCBx.isSelected())
            performanceMeasure.addStateEstimator("variance");

        if(stddevStateCBx.isSelected())
            performanceMeasure.addStateEstimator("stddev");

        if(distrStateCBx.isSelected())
            performanceMeasure.addStateEstimator("distribution");


        // 2. Get Count Measure information

        // Retrieve the names of selected count measures
        Object[] selectedCounts = transitionList.getSelectedValues();

        // Convert the transition names into IDs, and add to output
        for(Object selectedCount : selectedCounts)
        {
            name = (String) selectedCount;
            currTrans = _pnmlData.getTransitionByName(name);
            performanceMeasure.addCount(currTrans.getId());
        }

        // 3. Return the resulting stateMeaure
        return performanceMeasure;

    }

    // Panel for State Measure
    public JPanel getStateMeasurePanel()
    {
        JPanel serverPanel = new JPanel();
        serverPanel.setBorder((new TitledBorder(new EtchedBorder(), "State Measure")));
        serverPanel.setLayout(new BorderLayout());

        _placesList = new JList();
        // Load the list of place names
        sourceFilePanel.setPlaceList(_placesList);

        _placesList.setLayoutOrientation(JList.VERTICAL);
        _placesList.setSelectionModel(new ToggleSelectionModel());
        _placesList.setVisibleRowCount(-1);


        JScrollPane listScroller = new JScrollPane(_placesList);
        listScroller.setPreferredSize(new Dimension(250, 160));


        // Create the estimator check boxes
        meanStateCBx = new JCheckBox("Mean");
        varianceStateCBx = new JCheckBox("Variance");
        stddevStateCBx = new JCheckBox("Standard Deviation");
        distrStateCBx = new JCheckBox("Distribution");

        // Create a panel to group the checkboxes
        JPanel checkboxPanel = new JPanel();
        //checkboxPanel.setLayout(new BoxLayout(checkboxPanel, BoxLayout.Y_AXIS));
        checkboxPanel.add(meanStateCBx);
        checkboxPanel.add(stddevStateCBx);
        checkboxPanel.add(varianceStateCBx);
        checkboxPanel.add(distrStateCBx);


        // Add components to panel
        serverPanel.add(listScroller, BorderLayout.CENTER);
        serverPanel.add(checkboxPanel, BorderLayout.SOUTH);

        return serverPanel;
    }

    // Panel for Count Measure
    public JPanel getCountMeasurePanel()
    {
        JPanel serverPanel = new JPanel();
        serverPanel.setBorder((new TitledBorder(new EtchedBorder(), "Count Measure")));
        serverPanel.setLayout(new BorderLayout());

        transitionList = new JList();
        // Load the list of transition names
        sourceFilePanel.setTransitionList(transitionList);

        transitionList.setLayoutOrientation(JList.VERTICAL);
        transitionList.setSelectionModel(new ToggleSelectionModel());

        transitionList.setVisibleRowCount(-1);


        JScrollPane listScroller = new JScrollPane(transitionList);
        listScroller.setPreferredSize(new Dimension(250, 160));


        // Add components to panel
        serverPanel.add(listScroller, BorderLayout.CENTER);

        return serverPanel;
    }

    public void setFilePanel(FileBrowserPanel fbp)
    {
        sourceFilePanel = fbp;
    }

}


// This class allows a JList to operate in a click toggle fashion - see JList java doc
class ToggleSelectionModel extends DefaultListSelectionModel
{
    private static final long serialVersionUID = 1L;
    private boolean gestureStarted = false;

    public void setSelectionInterval(int index0, int index1)
    {
        if(isSelectedIndex(index0) && !gestureStarted)
        {
            super.removeSelectionInterval(index0, index1);
        }
        else
        {
            super.setSelectionInterval(index0, index1);
        }

        gestureStarted = true;
    }

    public void setValueIsAdjusting(boolean isAdjusting)
    {
        if(!isAdjusting)
        {
            gestureStarted = false;
        }
    }


}
