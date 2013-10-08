/**
 * QueryManager
 *
 * - Responsible for setting up the user interface for the Query Editor module
 * - Maintains a list of tabs and their data
 * - The general point of reference for query editor visualisation elements
 *
 * @author Tamas Suto
 * @date 15/04/07
 */

package pipe.modules.queryeditor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;import java.util.Collection;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pipe.modules.interfaces.QueryConstants;
import pipe.modules.interfaces.IModule;
import pipe.gui.Grid;
import pipe.modules.queryeditor.evaluator.QueryEvaluator;
import pipe.modules.queryeditor.evaluator.SettingsManager;
import pipe.modules.queryeditor.evaluator.gui.ProgressView;
import pipe.modules.queryeditor.evaluator.gui.ProgressWindow;
import pipe.modules.queryeditor.gui.QueryEditor;
import pipe.modules.queryeditor.gui.QueryException;
import pipe.modules.queryeditor.gui.QueryView;
import pipe.modules.queryeditor.gui.TextQueryEditor;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroEditor;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ArithCompNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ArithOpNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.OperationNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ResultNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ActionsNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.BoolNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.NumNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StatesNode;
import pipe.modules.queryeditor.io.QueryData;


public class QueryManager implements IModule, QueryConstants
{

    private static final String MODULE_NAME = "Performance Query Editor";

    private static final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    public static final int minFrameWitdh = screenSize.width * 20 / 100;
    public static final int minFrameHeight = screenSize.height * 20 / 100;
    public static final int prefFrameWidth = screenSize.width * 88 / 100;
    public static final int prefFrameHeight = screenSize.height * 88 / 100;

    private static QueryEditor queryEditor;
    private static JEditorPane infoBox;
    private static JTabbedPane drawingCanvas;
    private static ProgressWindow progressWindow;
    private static ArrayList tabs = new ArrayList();
    private static int tabIndex;
    private QueryData queryData;  // Performance Tree query data

    public static JTabbedPane botPanel;

    private static int mode, prev_mode;

    private static boolean evaluatingQuery; // indicates whether we are in query evaluation mode or not

    public static String imgPath;
    public static String userPath;

    // text colouring-related variables
    private static final String[] availableTextColours = {
            "black", "blue", "red", "green", "purple", "olive"
    };
    private static int textColourIndex = 0;
    private static String textColour = availableTextColours[textColourIndex];

    // Required run-time variables
    private static boolean moduleActive = false;
    public static final boolean allowDeletionOfArcs = false;


    /**
     * This is the method that is executed when the module is invoked
     */
    public void start()
    {
        if(!moduleActive)
        {
            // We only want to enable the launching of the query designer module
            // if another instance is not already running, since we would get lots
            // of problems that way

            // Indicate that module is now running
            moduleActive = true;

            evaluatingQuery = false;

            userPath = null;
            String imgDir = "pipe" + System.getProperty("file.separator") + "modules" +
                    System.getProperty("file.separator") + "queryeditor" + System.getProperty("file.separator") +
                    "gui" + System.getProperty("file.separator") + "images";
            imgPath = imgDir + System.getProperty("file.separator");

            queryEditor = new QueryEditor(MODULE_NAME);

            // Create Info Box
            infoBox = new JEditorPane("text/html", "");
            infoBox.setBackground(Color.white);
            infoBox.setEditable(false);
            infoBox.setBorder(new TitledBorder(new EtchedBorder(), "Information"));
            JScrollPane infoPane = new JScrollPane(infoBox);
            int infoPaneHeight = prefFrameHeight * 25 / 100;
            Dimension infoPaneMinSize = new Dimension(minFrameWitdh, 1);
            Dimension infoPanePrefSize = new Dimension(prefFrameWidth, infoPaneHeight);
            infoPane.setMinimumSize(infoPaneMinSize);
            infoPane.setPreferredSize(infoPanePrefSize);


            // Create Query Builder
            JPanel queryButtonsPane = new JPanel();
            queryButtonsPane.setBorder(new TitledBorder(new EtchedBorder(), "Query Builder"));
            queryButtonsPane.setLayout(new BoxLayout(queryButtonsPane, BoxLayout.Y_AXIS));
            JToolBar[] queryBuilderToolbars = queryEditor.getQueryBuilderToolbars();
            queryButtonsPane.add(queryBuilderToolbars[0]);
            queryButtonsPane.add(queryBuilderToolbars[1]);
            JScrollPane queryBuilderPane = new JScrollPane(queryButtonsPane);
            URL iconURL = Thread.currentThread().getContextClassLoader().getResource(QueryManager.imgPath + "Select.png");
            ImageIcon icon = new ImageIcon(iconURL);
            int queryPaneHeight = icon.getIconHeight() * 4;
            Dimension queryBuilderPaneMinSize = new Dimension(minFrameWitdh, queryPaneHeight);
            Dimension queryBuilderPanePrefSize = new Dimension(prefFrameWidth, queryPaneHeight);
            queryBuilderPane.setMinimumSize(queryBuilderPaneMinSize);
            queryBuilderPane.setPreferredSize(queryBuilderPanePrefSize);


            // Create Drawing Canvas
            drawingCanvas = new JTabbedPane();
            drawingCanvas.setBorder(new TitledBorder(new EtchedBorder(), "Performance Query"));
            int drawingCanvasHeight = prefFrameHeight - infoPaneHeight - queryPaneHeight;
            Dimension drawingCanvasMinSize = new Dimension(minFrameWitdh, drawingCanvasHeight);
            Dimension drawingCanvasPrefSize = new Dimension(prefFrameWidth, drawingCanvasHeight);
            drawingCanvas.setMinimumSize(drawingCanvasMinSize);
            drawingCanvas.setPreferredSize(drawingCanvasPrefSize);
            queryEditor.setTab();
            Grid.enableGrid();

            // Put everything together
            Container contentPane = queryEditor.getContentPane();
            JSplitPane topPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, infoPane, drawingCanvas);
            topPane.setContinuousLayout(true);
            topPane.setOneTouchExpandable(true);
            topPane.setBorder(null);
            topPane.setDividerSize(8);
            topPane.setResizeWeight(1.0);
            contentPane.add(topPane, BorderLayout.CENTER);


            queryEditor.createNewTab(null);                        // Create a new tab
            getView().getSelectionObject().enableSelection();    // Enable Selection


            //Create bottom panel
            botPanel = new JTabbedPane();
            //botPanel.setLayout(new CardLayout());
            final TextQueryEditor textQueryEditor = new TextQueryEditor();

            //build bottom panel
            botPanel.add("Tree Editor", queryButtonsPane);
            botPanel.add("Text Query Editor", textQueryEditor);
            contentPane.add(botPanel, BorderLayout.PAGE_END);

            //change listener deals with switch between text and normal query editor
            botPanel.addChangeListener(new ChangeListener()
            {
                public void stateChanged(ChangeEvent e)
                {
                    JTabbedPane tabbedPane = (JTabbedPane) e.getSource();
                    int selectedIndex = tabbedPane.getSelectedIndex();
                    if(selectedIndex == 1)
                    {
                        QueryManager.checkTextEditable();
                        QueryManager.checkTextEditing();
                        textQueryEditor.clearText();
                    }
                    else
                    {
                        textQueryEditor.queryDone();
                        QueryManager.checkTextEditable();
                        QueryManager.checkTextEditing();
                    }
                }
            });

            queryEditor.pack();                          // Make window fit contents' preferred size
            queryEditor.setLocationRelativeTo(null);    // Center frame on screen
            queryEditor.setVisible(true);                // Make window appear

            getView().drawResultNode();                    // Draw an initial Result node
        }
        else
        {
            // Get the focus on the query editor
            queryEditor.requestFocus();
        }
    }

    public String getName()
    {
        return MODULE_NAME;
    }

    public static QueryEditor getEditor()
    {
        return queryEditor;
    }

    public static QueryView getView()
    {
        return getView(drawingCanvas.getSelectedIndex());
    }

    public static QueryView getView(int index)
    {
        if(index < 0)
            return null;
        TabData tab = (TabData) (tabs.get(index));
        if(tab.queryView == null)
            tab.queryView = new QueryView();
        return tab.queryView;
    }

    public static QueryData getData()
    {
        return getData(drawingCanvas.getSelectedIndex());
    }

    public static QueryData getData(int index)
    {
        if(index < 0)
            return null;
        TabData tab = (TabData) (tabs.get(index));
        if(tab.queryData == null)
            tab.queryData = new QueryData();
        return tab.queryData;
    }

    public static File getFile()
    {
        TabData tab = (TabData) (tabs.get(drawingCanvas.getSelectedIndex()));
        return tab.queryFile;
    }

    public static void setFile(File queryfile, int fileNo)
    {
        if(fileNo >= tabs.size()) return;
        TabData tab = (TabData) (tabs.get(fileNo));
        tab.queryFile = queryfile;
    }

    public static JTabbedPane getTabs()
    {
        return drawingCanvas;
    }

    public static int addTab()
    {
        tabs.add(new TabData());
        return (tabs.size() - 1);
    }

    public static void removeTab(int index)
    {
        tabs.remove(index);
    }

    public static int getMode()
    {
        return mode;
    }

    public static void setMode(int _mode)
    {
        if(mode != _mode)
        {        // Don't bother unless new mode is different.
            prev_mode = mode;
            mode = _mode;
        }
    }

    public static void restoreMode()
    {
        mode = prev_mode;
    }

    public static ProgressWindow getProgressWindow()
    {
        return progressWindow;
    }

    public static void setProgressWindow(ProgressWindow dialog)
    {
        progressWindow = dialog;
    }

    public static ProgressView getProgressView()
    {
        if(getProgressWindow() != null)
        {
            return getProgressWindow().getProgressView();
        }
        else
            return null;
    }

    public static boolean isEvaluatingQuery()
    {
        return evaluatingQuery;
    }

    public static void setEvaluatingQuery(boolean eval)
    {
        evaluatingQuery = eval;
    }

    public static void writeToInfoBox(String stuff)
    {
        if(MacroManager.getEditor() == null)
        {
            clearInfoBox();
            infoBox.setText(stuff);
        }
        else
        {
            MacroEditor.writeToInfoBox(stuff);
        }
    }

    public static void appendToInfoBox(String stuff)
    {
        if(MacroManager.getEditor() == null)
        {
            String bufferedText = infoBox.getText();
            String newText = bufferedText + " ";
            infoBox.setText(newText);
            infoBox.setCaretPosition(infoBox.getDocument().getLength());
        }
        else
            MacroEditor.appendToInfoBox(stuff);
    }

    public static void clearInfoBox()
    {
        infoBox.setText("");
    }

    public static void printStatusMessage(String message)
    {
        if(QueryManager.getProgressWindow() != null)
        {
            QueryManager.getProgressWindow().setProgressBarText(message);
        }
    }

    private static String getTextColour()
    {
        return textColour;
    }

    public static void resetTextColour()
    {
        textColourIndex = 0;
        textColour = availableTextColours[textColourIndex];
    }

    public static void colourUp()
    {
        if(textColourIndex == availableTextColours.length - 1)
            textColourIndex = 0;
        else
            textColourIndex++;
        textColour = availableTextColours[textColourIndex];
    }

    public static void colourDown()
    {
        if(textColourIndex == 0)
            textColourIndex = availableTextColours.length - 1;
        else
            textColourIndex--;
        textColour = availableTextColours[textColourIndex];
    }

    public static String addColouring(String input)
    {
        return "<font size=\"4\" face=\"Times\" color=" + getTextColour() + ">" + input + "</font>";
    }

    /**
     * This method prints out the current natural language representation of the tree
     *
     * @param sourceNode
     * @param nodeToLinkUpWith
     */
    public static void printNaturalLanguageRepresentation()
    {
        String textualRep;
        if(MacroManager.getEditor() == null)
        {
            ResultNode topNode = (ResultNode) getData().getNodes()[0];
            textualRep = topNode.printTextualRepresentation();
            if(!textualRep.equals("''''"))
            {
                resetTextColour();
                String introText = addColouring("Natural language equivalent of the current tree: <br><br>");
                writeToInfoBox(introText + textualRep);
            }
        }
        else
        {
            ArrayList macroNodes = MacroManager.getEditor().getActiveMacro().getMacroNodes();
            if(macroNodes != null)
            {
                MacroNode topNode = (MacroNode) macroNodes.get(0);
                textualRep = topNode.printTextualRepresentation();
                if(!textualRep.equals("''''"))
                {
                    resetTextColour();
                    String introText = addColouring("Natural language equivalent of the current tree: <br><br>");
                    writeToInfoBox(introText + textualRep);
                }
            }
        }
        resetTextColour();
    }

    /**
     * This method invokes the preference manager for query analysis
     */
    public static void startPreferenceManager()
    {
        SettingsManager.preferenceManagerDialog();
    }

    /**
     * This is the method that gets invoked when the "Evaluate Query" button is clicked.
     * It checks for whether the query tree is valid and if so, forwards the request to
     * QueryEvaluator.
     */
    public static void evaluateQuery()
    {
        if(queryTreeValid())
        {
            // check whether places have been defined on the underlying model
            boolean okToProceed = QueryManager.getData().checkCurrentData("States");
            if(okToProceed)
                QueryEvaluator.evaluateQuery();
        }
    }

    /**
     * This method checks whether we have a fully connected tree, i.e.
     * that all required arcs of each node on the query canvas have
     * been assigned to nodes.
     *
     * @return
     */
    private static boolean queryTreeValid()
    {
        String errormsg;
        PerformanceTreeNode[] retrievedTreeNodes = getData().getNodes();
        ArrayList<PerformanceTreeNode> treeNodes = new ArrayList<PerformanceTreeNode>();
        treeNodes.addAll(Arrays.asList(retrievedTreeNodes));

        try
        {
            if(treeNodes.size() > 1)
            {
                // we have more than just the Result node on the canvas, so potentially a valid tree
                Iterator<PerformanceTreeNode> i = treeNodes.iterator();
                while(i.hasNext())
                {
                    PerformanceTreeNode node = i.next();
                    if(!(node instanceof ResultNode))
                    {
                        // check that apart from the top node, each node has a parent
                        if(node.getIncomingArcID() == null)
                        {
                            errormsg = "Please ensure that your query tree is fully \n" +
                                    "connected by checking that all required (solid)\n" +
                                    "arcs have been assigned to nodes.";
                            throw new QueryException(errormsg);
                        }
                    }

                    if(node instanceof OperationNode)
                    {
                        // check that all required arcs have some node assigned to them
                        // Value nodes have no outgoing arcs, so don't worry about them
                        Collection<String> outgoingArcIDs = ((OperationNode) node).getOutgoingArcIDs();
                        Iterator<String> j = outgoingArcIDs.iterator();
                        while(j.hasNext())
                        {
                            String outgoingArcID = j.next();
                            PerformanceTreeArc outgoingArc = getData().getArc(outgoingArcID);
                            if(outgoingArc.isRequired() && outgoingArc.getTargetID() == null)
                            {
                                errormsg = "Please ensure that your query tree is fully \n" +
                                        "connected by checking that all required (solid)\n" +
                                        "arcs have been assigned to nodes.";
                                throw new QueryException(errormsg);
                            }
                        }
                        // check if an ArithCompNode has an operation assigned
                        if(node instanceof ArithCompNode)
                        {
                            String nodeOperation = ((ArithCompNode) node).getOperation();
                            if(nodeOperation.equals(""))
                            {
                                errormsg = "Please ensure that all arithmetic \n" +
                                        "comparison nodes in the query represent \n" +
                                        "valid comparisons. Comparison operations \n" +
                                        "have to be assigned to nodes by right- \n" +
                                        "clicking them.";
                                throw new QueryException(errormsg);
                            }
                        }
                        // check if an ArithOpNode has an operation assigned
                        if(node instanceof ArithOpNode)
                        {
                            String nodeOperation = ((ArithOpNode) node).getOperation();
                            if(nodeOperation.equals(""))
                            {
                                errormsg = "Please ensure that all arithmetic \n" +
                                        "operation nodes in the query represent \n" +
                                        "valid operations. Arithmetic operations \n" +
                                        "have to be assigned to nodes by right- \n" +
                                        "clicking them.";
                                throw new QueryException(errormsg);
                            }
                        }
                    }
                    else if(node instanceof StatesNode)
                    {
                        if(((StatesNode) node).getNodeLabelObject() == null)
                        {
                            errormsg = "Please ensure that your States nodes \n" +
                                    "all have state labels assigned to them.";
                            throw new QueryException(errormsg);
                        }
                    }
                    else if(node instanceof ActionsNode)
                    {
                        if(((ActionsNode) node).getNodeLabelObject() == null)
                        {
                            errormsg = "Please ensure that your Actions nodes \n" +
                                    "all have action labels assigned to them.";
                            throw new QueryException(errormsg);
                        }
                    }
                    else if(node instanceof BoolNode)
                    {
                        if(((BoolNode) node).getNodeLabelObject() == null)
                        {
                            errormsg = "Please ensure that your Bool nodes \n" +
                                    "all have boolean values assigned to them.";
                            throw new QueryException(errormsg);
                        }
                    }
                    else if(node instanceof NumNode)
                    {
                        if(((NumNode) node).getNodeLabelObject() == null)
                        {
                            errormsg = "Please ensure that your Num nodes \n" +
                                    "all have numerical values assigned to them.";
                            throw new QueryException(errormsg);
                        }
                    }
                }
                return true;
            }
            else
            {
                errormsg = "Please create a query that contains at least one \n" +
                        "operation node that connects to the Result node.";
                throw new QueryException(errormsg);
            }
        }
        catch(QueryException e)
        {
            String msg = e.getMessage();
            JOptionPane.showMessageDialog(QueryManager.getEditor().getContentPane(), msg, "Warning", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public static void resetQueryEditor()
    {
        // reset variables containing information
        tabs = null;
        tabs = new ArrayList();

        // indicate that the module is not running any more
        moduleActive = false;
    }


    private static class TabData
    {
        // structure for holding a tab's data
        public QueryData queryData;
        public QueryView queryView;
        public File queryFile;
    }

    //check whether to enable text query editor
    public static void checkTextEditable()
    {
        QueryData queryData = QueryManager.getData();
        PerformanceTreeNode[] nodes = queryData.getNodes();
        int index = botPanel.getTabCount() - 1;
        if(nodes.length == 1)
        {
            botPanel.setEnabledAt(index, true);
        }
        else
        {
            botPanel.setEnabledAt(index, false);
        }
    }

    //check whether enable the node pop-up menu
    public static void checkTextEditing()
    {
        boolean enablePopup = true;
        int currentTabIndex = botPanel.getSelectedIndex();
        enablePopup = currentTabIndex != botPanel.getTabCount() - 1;
        Iterator nodeIt = ((QueryManager.getData()).getTreeNodes()).iterator();

        while(nodeIt.hasNext())
        {
            PerformanceTreeNode node = (PerformanceTreeNode) nodeIt.next();
            node.setEnablePopup(enablePopup);
        }
    }

}