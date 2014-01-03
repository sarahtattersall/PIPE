package pipe.views;

import pipe.actions.ActionEnum;
import pipe.actions.ExampleFileAction;
import pipe.actions.GuiAction;
import pipe.actions.ZoomAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.*;
import pipe.gui.widgets.FileBrowser;
import pipe.io.JarUtilities;
import pipe.models.PipeApplicationModel;
import pipe.models.component.Token;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class PipeApplicationView extends JFrame implements ActionListener, Observer, Serializable {
    public final StatusBar statusBar;
    private JToolBar animationToolBar, drawingToolBar;
    public JComboBox zoomComboBox;
    public JComboBox tokenClassComboBox;
    private HelpBox helpAction;

    private final JSplitPane _moduleAndAnimationHistoryFrame;
    private static JScrollPane _scroller;

    private final JTabbedPane frameForPetriNetTabs = new JTabbedPane();
    private final ArrayList<PetriNetTab> petriNetTabs;

    private static AnimationHistoryView animationHistoryView;
    private final PipeApplicationController applicationController;
    private final PipeApplicationModel applicationModel;

    /**
     * Constructor for unit testing only
     *
     * @author stevedoubleday (Oct 2013)
     */
    public PipeApplicationView() {
        statusBar = null;
        _moduleAndAnimationHistoryFrame = null;
        petriNetTabs = null;
        applicationController = null;
        applicationModel = null;
    }

    public PipeApplicationView(PipeApplicationController applicationController, PipeApplicationModel applicationModel) {
        ApplicationSettings.register(this);
        this.applicationController = applicationController;
        this.applicationModel = applicationModel;
        this.applicationModel.registerObserver(this);
        petriNetTabs = new ArrayList<PetriNetTab>();
        setTitle(null);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception exc) {
            System.err.println("Error loading L&F: " + exc);
        }

        this.setIconImage(new ImageIcon(Thread.currentThread().getContextClassLoader()
                .getResource(ApplicationSettings.getImagePath() + "icon.png")).getImage());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width * 80 / 100, screenSize.height * 80 / 100);
        this.setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        buildMenus();

        // Status bar...
        statusBar = new StatusBar();
        getContentPane().add(statusBar, BorderLayout.PAGE_END);

        // Build menus
        buildToolbar();

        addWindowListener(new WindowHandler());

        this.setForeground(java.awt.Color.BLACK);
        this.setBackground(java.awt.Color.WHITE);

        Grid.enableGrid();

        ModuleManager moduleManager = new ModuleManager();
        JTree moduleTree = moduleManager.getModuleTree();
        _moduleAndAnimationHistoryFrame = new JSplitPane(JSplitPane.VERTICAL_SPLIT, moduleTree, null);
        _moduleAndAnimationHistoryFrame.setContinuousLayout(true);
        _moduleAndAnimationHistoryFrame.setDividerSize(0);
        JSplitPane pane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, _moduleAndAnimationHistoryFrame, frameForPetriNetTabs);
        pane.setContinuousLayout(true);
        pane.setOneTouchExpandable(true);
        pane.setBorder(null); // avoid multiple borders
        pane.setDividerSize(8);
        getContentPane().add(pane);

        setVisible(true);
        this.applicationModel.setMode(Constants.SELECT);
        this.applicationModel.selectAction.actionPerformed(null);

        PetriNetTab tab = applicationController.createEmptyPetriNet();
        applicationController.setActiveTab(tab);
        setTab();
    }

    public JTabbedPane getFrameForPetriNetTabs() {
        return frameForPetriNetTabs;
    }


    public int numberOfTabs() {
        return petriNetTabs.size();
    }

    /**
     * This method does build the menus.
     *
     * @author unknown
     * @author Dave Patterson - fixed problem on OSX due to invalid character in
     * URI caused by unescaped blank. The code changes one blank
     * character if it exists in the string version of the URL. This way
     * works safely in both OSX and Windows. I also added a
     * printStackTrace if there is an exception caught in the setup for
     * the "extras.examples" folder.
     */
    private void buildMenus() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        addMenuItem(fileMenu, applicationController.getAction(ActionEnum.CREATE));
        addMenuItem(fileMenu, applicationController.getAction(ActionEnum.OPEN));
        addMenuItem(fileMenu, applicationController.getAction(ActionEnum.CLOSE));
        fileMenu.addSeparator();
        addMenuItem(fileMenu, applicationController.getAction(ActionEnum.SAVE));
        addMenuItem(fileMenu, applicationController.getAction(ActionEnum.SAVEAS));

        fileMenu.addSeparator();
        addMenuItem(fileMenu, applicationController.getAction(ActionEnum.IMPORT));

        // Export menu


        JMenu exportMenu = new JMenu("Export");
        exportMenu.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader()
                .getResource(ApplicationSettings.getImagePath() + "Export.png")));
        addMenuItem(exportMenu, applicationController.getAction(ActionEnum.EXPORTPNG));
        addMenuItem(exportMenu, applicationController.getAction(ActionEnum.EXPORTPS));
        addMenuItem(exportMenu, applicationController.getAction(ActionEnum.EXPORTTN));
        fileMenu.add(exportMenu);
        fileMenu.addSeparator();
        addMenuItem(fileMenu, applicationController.getAction(ActionEnum.PRINT));
        fileMenu.addSeparator();

        // Example files menu
        JMenu exampleMenu = createExampleFileMenu();

        fileMenu.add(exampleMenu);
        fileMenu.addSeparator();

        addMenuItem(fileMenu, applicationController.getAction(ActionEnum.EXIT));

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        addMenuItem(editMenu, applicationController.getAction(ActionEnum.UNDO));
        addMenuItem(editMenu, applicationController.getAction(ActionEnum.REDO));
        editMenu.addSeparator();
        addMenuItem(editMenu, applicationController.getAction(ActionEnum.CUT));
        addMenuItem(editMenu, applicationController.getAction(ActionEnum.COPY));
        addMenuItem(editMenu, applicationController.getAction(ActionEnum.PASTE));
        addMenuItem(editMenu, applicationController.getAction(ActionEnum.DELETE));

        JMenu drawMenu = new JMenu("Draw");
        drawMenu.setMnemonic('D');
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.SELECT));

        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");

        rootPane.getActionMap().put("ESCAPE", applicationController.getAction(ActionEnum.SELECT));

        drawMenu.addSeparator();
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.PLACE));
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.TRANSACTION));
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.TIMED_TRANSACTION));
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.ARC));
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.INHIBITOR_ARC));
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.ANNOTATION));
        drawMenu.addSeparator();
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.TOKEN));
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.DELETE_TOKEN));
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.SPECIFY_TOKEN));
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.GROUP_TRANSITIONS));
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.UNGROUP_TRANSITIONS));
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.UNFOLD));
        drawMenu.addSeparator();
        addMenuItem(drawMenu, applicationController.getAction(ActionEnum.RATE_PARAMETER));

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');

        JMenu zoomMenu = new JMenu("Zoom");
        zoomMenu.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader()
                .getResource(ApplicationSettings.getImagePath() + "Zoom.png")));
        addZoomMenuItems(zoomMenu);

        addMenuItem(viewMenu, applicationController.getAction(ActionEnum.ZOOM_OUT));

        addMenuItem(viewMenu, applicationController.getAction(ActionEnum.ZOOM_IN));
        viewMenu.add(zoomMenu);

        viewMenu.addSeparator();
        addMenuItem(viewMenu, applicationController.getAction(ActionEnum.TOGGLE_GRID));
        addMenuItem(viewMenu, applicationController.getAction(ActionEnum.DRAG));

        JMenu animateMenu = new JMenu("Animate");
        animateMenu.setMnemonic('A');
        addMenuItem(animateMenu, applicationController.getAction(ActionEnum.START));
        animateMenu.addSeparator();
        addMenuItem(animateMenu, applicationController.getAction(ActionEnum.STEP_BACK));
        addMenuItem(animateMenu, applicationController.getAction(ActionEnum.STEP_FORWARD));
        addMenuItem(animateMenu, applicationController.getAction(ActionEnum.RANDOM));
        addMenuItem(animateMenu, applicationController.getAction(ActionEnum.ANIMATE));

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        helpAction = new HelpBox("Help", "View documentation", "F1", "index.htm");
        addMenuItem(helpMenu, helpAction);

        JMenuItem aboutItem = helpMenu.add("About PIPE");
        aboutItem.addActionListener(this); // Help - About is implemented
        // differently

        URL iconURL = Thread.currentThread().getContextClassLoader()
                .getResource(ApplicationSettings.getImagePath() + "About.png");
        if (iconURL != null) {
            aboutItem.setIcon(new ImageIcon(iconURL));
        }

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(drawMenu);
        menuBar.add(animateMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

    }

    /**
     * Creates an example file menu
     */
    private JMenu createExampleFileMenu() {
        JMenu exampleMenu = new JMenu("Examples");
        exampleMenu.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader()
                .getResource(ApplicationSettings.getImagePath() + "Example.png")));
        try {
            URL examplesDirURL = Thread.currentThread().getContextClassLoader()
                    .getResource(ApplicationSettings.getExamplesDirectoryPath() + System.getProperty("file.separator"));

            if (JarUtilities.isJarFile(examplesDirURL)) {

                JarFile jarFile = new JarFile(JarUtilities.getJarName(examplesDirURL));

                ArrayList<JarEntry> nets =
                        JarUtilities.getJarEntries(jarFile, ApplicationSettings.getExamplesDirectoryPath());

                Arrays.sort(nets.toArray(), new Comparator() {
                    public int compare(Object one, Object two) {
                        return ((JarEntry) one).getName().compareTo(((JarEntry) two).getName());
                    }
                });

                if (nets.size() > 0) {
                    int index = 0;
                    for (JarEntry net : nets) {
                        if (net.getName().toLowerCase().endsWith(".xml")) {
                            addMenuItem(exampleMenu,
                                    new ExampleFileAction(net, (index < 10) ? ("ctrl " + index) : null));
                            index++;
                        }
                    }
                }
            } else {
                /**
                 * The next block fixes a problem that surfaced on Mac OSX with
                 * PIPE 2.4. In that environment (and not in Windows) any blanks
                 * in the project name in Eclipse are property converted to
                 * '%20' but the blank in "extras.examples" is not. The following
                 * code will do nothing on a Windows machine or if the logic on
                 * OSX changess. I also added a stack trace so if the problem
                 * occurs for another environment (perhaps multiple blanks need
                 * to be manually changed) it can be easily fixed. DP
                 */
                // examplesDir = new File(new URI(examplesDirURL.toString()));
                String dirURLString = examplesDirURL.toString();
                int index = dirURLString.indexOf(" ");
                if (index > 0) {
                    StringBuffer sb = new StringBuffer(dirURLString);
                    sb.replace(index, index + 1, "%20");
                    dirURLString = sb.toString();
                }

                File examplesDir = new File(new URI(dirURLString));

                File[] nets = examplesDir.listFiles();

                Arrays.sort(nets, new Comparator() {
                    public int compare(Object one, Object two) {
                        return ((File) one).getName().compareTo(((File) two).getName());
                    }
                });

                // Oliver Haggarty - fixed code here so that if folder contains
                // non
                // .xml file the Example x counter is not incremented when that
                // file
                // is ignored

                if (nets.length > 0) {
                    int k = 0;
                    for (File net : nets) {
                        if (net.getName().toLowerCase().endsWith(".xml")) {
                            addMenuItem(exampleMenu, new ExampleFileAction(net, (k < 10) ? "ctrl " + (k++) : null));
                        }
                    }

                }
                return exampleMenu;
            }
        } catch (Exception e) {
            System.err.println("Error getting example files:" + e);
            e.printStackTrace();
        } finally {
            return exampleMenu;
        }
    }

    private void buildToolbar() {
        // Create the toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);// Inhibit toolbar floating

        addButton(toolBar, applicationModel.createAction);
        addButton(toolBar, applicationModel.openAction);
        addButton(toolBar, applicationModel.saveAction);
        addButton(toolBar, applicationModel.saveAsAction);
        addButton(toolBar, applicationModel.closeAction);
        toolBar.addSeparator();
        addButton(toolBar, applicationModel.printAction);
        toolBar.addSeparator();
        addButton(toolBar, applicationModel.cutAction);
        addButton(toolBar, applicationModel.copyAction);
        addButton(toolBar, applicationModel.pasteAction);
        addButton(toolBar, applicationModel.deleteAction);
        addButton(toolBar, applicationModel.undoAction);
        addButton(toolBar, applicationModel.redoAction);
        toolBar.addSeparator();

        addButton(toolBar, applicationModel.zoomOutAction);
        addZoomComboBox(toolBar, applicationModel.zoomAction = new ZoomAction("Zoom", "Select zoom percentage ", ""));
        addButton(toolBar, applicationModel.zoomInAction);
        toolBar.addSeparator();
        addButton(toolBar, applicationModel.toggleGrid);
        addButton(toolBar, applicationModel.dragAction);
        addButton(toolBar, applicationModel.startAction);

        drawingToolBar = new JToolBar();
        drawingToolBar.setFloatable(false);

        toolBar.addSeparator();
        addButton(drawingToolBar, applicationModel.selectAction);
        drawingToolBar.addSeparator();
        addButton(drawingToolBar, applicationModel.placeAction);// Add Draw Menu Buttons
        addButton(drawingToolBar, applicationModel.transAction);
        addButton(drawingToolBar, applicationModel.timedtransAction);
        addButton(drawingToolBar, applicationModel.arcAction);
        addButton(drawingToolBar, applicationModel.inhibarcAction);
        addButton(drawingToolBar, applicationModel.annotationAction);
        drawingToolBar.addSeparator();
        addButton(drawingToolBar, applicationModel.tokenAction);
        addButton(drawingToolBar, applicationModel.deleteTokenAction);
        addTokenClassComboBox(drawingToolBar, applicationModel.chooseTokenClassAction);
        addButton(drawingToolBar, applicationModel.specifyTokenClasses);
        addButton(drawingToolBar, applicationModel.groupTransitions);
        addButton(drawingToolBar, applicationModel.ungroupTransitions);
        addButton(drawingToolBar, applicationModel.unfoldAction);
        drawingToolBar.addSeparator();
        addButton(drawingToolBar, applicationModel.rateAction);

        toolBar.add(drawingToolBar);

        animationToolBar = new JToolBar();
        animationToolBar.setFloatable(false);
        addButton(animationToolBar, applicationModel.stepbackwardAction);
        addButton(animationToolBar, applicationModel.stepforwardAction);
        addButton(animationToolBar, applicationModel.randomAction);
        addButton(animationToolBar, applicationModel.randomAnimateAction);

        toolBar.add(animationToolBar);
        animationToolBar.setVisible(false);

        toolBar.addSeparator();
        addButton(toolBar, helpAction);

        for (int i = 0; i < toolBar.getComponentCount(); i++) {
            toolBar.getComponent(i).setFocusable(false);
        }

        getContentPane().add(toolBar, BorderLayout.PAGE_START);
    }

    private void addButton(JToolBar toolBar, GuiAction action) {

        if (action.getValue("selected") != null) {
            toolBar.add(new ToggleButton(action));
        } else {
            toolBar.add(action);
        }
    }

    /**
     * @param -        the menu to add the submenu to
     * @param zoomMenu
     * @author Ben Kirby Takes the method of setting up the Zoom menu out of the
     * main buildMenus method.
     */
    private void addZoomMenuItems(JMenu zoomMenu) {
        for (ZoomAction zoomAction : applicationModel.getZoomActions()) {
            JMenuItem newItem = new JMenuItem(zoomAction);
            zoomMenu.add(newItem);
        }
    }

    /**
     * @param toolBar the JToolBar to add the button to
     * @param action  the action that the ZoomComboBox performs
     * @author Ben Kirby Just takes the long-winded method of setting up the
     * ComboBox out of the main buildToolbar method. Could be adapted
     * for generic addition of comboboxes
     */
    private void addZoomComboBox(JToolBar toolBar, Action action) {
        Dimension zoomComboBoxDimension = new Dimension(65, 28);
        String[] zoomExamples = applicationModel.getZoomExamples();
        zoomComboBox = new JComboBox(zoomExamples);
        zoomComboBox.setEditable(true);
        zoomComboBox.setSelectedItem("100%");
        zoomComboBox.setMaximumRowCount(zoomExamples.length);
        zoomComboBox.setMaximumSize(zoomComboBoxDimension);
        zoomComboBox.setMinimumSize(zoomComboBoxDimension);
        zoomComboBox.setPreferredSize(zoomComboBoxDimension);
        zoomComboBox.setAction(action);
        toolBar.add(zoomComboBox);
    }

    /**
     * @param toolBar
     * @param action
     * @author Alex Charalambous (June 2010): Creates a combo box used to choose
     * the current token class to be used.
     * @author Steve Doubleday (Sept 2013): refactored to simplify testing
     * Initially populated with a single option:  "Default"
     */
    protected void addTokenClassComboBox(JToolBar toolBar, Action action) {
//        String[] tokenClassChoices = buildTokenClassChoices(); // Steve Doubleday: can't be used until we have an active PetriNetView
        String[] tokenClassChoices = new String[]{"Default"};
        DefaultComboBoxModel model = new DefaultComboBoxModel(tokenClassChoices);
        tokenClassComboBox = new JComboBox(model);
        tokenClassComboBox.setEditable(true);
        tokenClassComboBox.setSelectedItem(tokenClassChoices[0]);
        tokenClassComboBox.setMaximumRowCount(100);
        tokenClassComboBox.setMaximumSize(new Dimension(125, 100));
        tokenClassComboBox.setEditable(false);
        tokenClassComboBox.setAction(action);
        toolBar.add(tokenClassComboBox);
    }

    protected String[] buildTokenClassChoices() {
//        LinkedList<TokenView> tokenViews = getCurrentPetriNetView().getTokenViews();
//        int size = tokenViews.size();
//        for (int i = 0; i < size; i++) {
//            tokenClassChoices[i] = tokenViews.get(i).getID();
//        }

        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        Collection<Token> tokens = petriNetController.getNetTokens();
        String[] tokenClassChoices = new String[tokens.size()];
        int index = 0;
        for (Token token : tokens) {
            tokenClassChoices[index] = token.getId();
            index++;
        }
        return tokenClassChoices;
    }

    private void addMenuItem(JMenu menu, Action action) {
        JMenuItem item = menu.add(action);
        KeyStroke keystroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);

        if (keystroke != null) {
            item.setAccelerator(keystroke);
        }
    }

    /* sets all buttons to enabled or disabled according to status. */
    public void enableActions(boolean status) {
        if (status) {
            drawingToolBar.setVisible(true);
            animationToolBar.setVisible(false);
        }

        if (!status) {
            drawingToolBar.setVisible(false);
            animationToolBar.setVisible(true);
        }
    }

    public void setObjectsNull(int index) {
        removeTab(index);
    }

    // set tabbed pane properties and add change listener that updates tab with
    // linked model and view
    void setTab() {
        frameForPetriNetTabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                CopyPasteManager copyPasteManager = applicationController.getCopyPasteManager();
                if (copyPasteManager.pasteInProgress()) {
                    copyPasteManager.cancelPaste();
                }

                PetriNetView _petriNetView = getCurrentPetriNetView();
                PetriNetTab petriNetTab = getCurrentTab();

                applicationController.setActiveTab(petriNetTab);
                if (petriNetTab != null) {
                    petriNetTab.setVisible(true);
                    petriNetTab.repaint();
                    updateZoomCombo();

                    applicationModel
                            .enableActions(!petriNetTab.isInAnimationMode(), applicationController.isPasteEnabled());

                    setTitle(getCurrentTab().getName());
                } else {
                    setTitle(null);
                }

//                if (_petriNetView != null) {
                refreshTokenClassChoices();
//                }
            }

        });
    }

    public void actionPerformed(ActionEvent e) {

        JOptionPane.showMessageDialog(this, "PIPE: Platform Independent Petri Net Ediror\n\n" + "Authors:\n" +
                "2003: Jamie Bloom, Clare Clark, Camilla Clifford, Alex Duncan, Haroun Khan and Manos Papantoniou\n" +
                "2004: Tom Barnwell, Michael Camacho, Matthew Cook, Maxim Gready, Peter Kyme and Michail Tsouchlaris\n" +
                "2005: Nadeem Akharware\n" + "????: Tim Kimber, Ben Kirby, Thomas Master, Matthew Worthington\n" +
                "????: Pere Bonet Bonet (Universitat de les Illes Balears)\n" +
                "????: Marc Meli\u00E0 Aguil\u00F3 (Universitat de les Illes Balears)\n" +
                "2010: Alex Charalambous (Imperial College London)\n" +
                "2011: Jan Vlasak (Imperial College London)\n\n" + "http://pipe2.sourceforge.net/", "About PIPE",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public void update(Observable o, Object obj) {
        PetriNetTab currentTab = getCurrentTab();
        if ((applicationModel.getMode() != Constants.CREATING) && (!currentTab.isInAnimationMode())) {
            currentTab.setNetChanged(true);
        }
    }

    public void saveOperation(boolean forceSaveAs) {

        if (getCurrentTab() == null) {
            return;
        }
        boolean saveFunctional = false;
        if (getCurrentPetriNetView().hasFunctionalRatesOrWeights()) {
            if (JOptionPane.showConfirmDialog(null, "This net has functional rates or weights expressions. \r\n" +
                    "Saving these expression will not allow this PNML file compatible with other tools. \r\n" +
                    "Press 'yes' to save them anyway. Press 'no' to save their constant values", "Request",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                saveFunctional = true;
            } else {
                saveFunctional = false;
            }
        }
        File modelFile = getFile();
        if (!forceSaveAs && modelFile != null) {
            saveNet(modelFile, saveFunctional);
        } else {
            String path;
            if (modelFile != null) {
                path = modelFile.toString();
            } else {
                path = frameForPetriNetTabs.getTitleAt(frameForPetriNetTabs.getSelectedIndex());
            }
            String filename = new FileBrowser(path).saveFile();
            if (filename != null) {
                saveNet(new File(filename), saveFunctional);
            }
        }
    }

    // Steve Doubleday:  public to simplify testing
    public void saveNet(File outFile, boolean saveFunctional) {
        try {

            applicationController.saveCurrentPetriNet(outFile, saveFunctional);
//
//            PNMLWriter saveModel = new PNMLWriter(getCurrentPetriNetView());
//            saveModel.saveTo(outFile, saveFunctional);

            setFile(outFile, frameForPetriNetTabs.getSelectedIndex());
            PetriNetTab currentTab = getCurrentTab();
            currentTab.setNetChanged(false);
            String name = outFile.getName().split(".xml")[0];
            frameForPetriNetTabs.setTitleAt(frameForPetriNetTabs.getSelectedIndex(), name);
            setTitle(outFile.getName());
            //TODO: WHY CLEAR THIS?
//            currentTab.getHistoryManager().clear();
            applicationModel.undoAction.setEnabled(false);
            applicationModel.redoAction.setEnabled(false);
        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString(), "File Output Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void addNewTab(String name, PetriNetTab tab) {
        JScrollPane scroller = new JScrollPane(tab);
        scroller.setBorder(new BevelBorder(BevelBorder.LOWERED));
        frameForPetriNetTabs.addTab(name, null, scroller, null);
        petriNetTabs.add(tab);
        frameForPetriNetTabs.setSelectedIndex(petriNetTabs.size() - 1);
    }

    /*
      * public class ExperimentRunner extends Thread{
      *
      * private String path;
      *
      * public ExperimentRunner(String path){ this.path=path; }
      *
      * public void run(){ Experiment exp = new Experiment(path,_petriNetView); try{
      * exp.Load(); } catch(org.xml.sax.SAXParseException spe) { //if the
      * experiment file does not fit the schema. String message =
      * spe.getMessage().replaceAll("\\. ",".\n"); message =
      * message.replaceAll(",",",\n");
      * JOptionPane.showMessageDialog(PipeApplicationView.this,
      * "The Experiment file is not valid."+
      * System.getProperty("line.separator")+ "Line "+spe.getLineNumber()+": "+
      * message, "Experiment Input Error", JOptionPane.ERROR_MESSAGE); }
      * catch(pipe.experiment.validation.NotMatchingException nme) { //if the
      * experiment file does not match with the current net.
      * JOptionPane.showMessageDialog(PipeApplicationView.this,
      * "The Experiment file is not valid."+
      * System.getProperty("line.separator")+ nme.getMessage(),
      * "Experiment Input Error", JOptionPane.ERROR_MESSAGE); }
      * catch(pipe.experiment.InvalidExpressionException iee) {
      * JOptionPane.showMessageDialog(PipeApplicationView.this,
      * "The Experiment file is not valid."+
      * System.getProperty("line.separator")+ iee.getMessage(),
      * "Experiment Input Error", JOptionPane.ERROR_MESSAGE); } } }
      */

    /**
     * If current net has modifications, asks if you want to save and does it if
     * you want.
     *
     * @return true if handled, false if cancelled
     */
    public boolean checkForSave() {
        if (getCurrentTab().getNetChanged()) {
            int result = JOptionPane.showConfirmDialog(this, "Current file has changed. Save current file?",
                    "Confirm Save Current File", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
            switch (result) {
                case JOptionPane.YES_OPTION:
                    saveOperation(false);
                    break;
                case JOptionPane.CLOSED_OPTION:
                case JOptionPane.CANCEL_OPTION:
                    return false;
            }
        }
        return true;
    }

    /**
     * If current net has modifications, asks if you want to save and does it if
     * you want.
     *
     * @return true if handled, false if cancelled
     */
    public boolean checkForSaveAll() {
        // Loop through all tabs and check if they have been saved
        for (int counter = 0; counter < frameForPetriNetTabs.getTabCount(); counter++) {
            frameForPetriNetTabs.setSelectedIndex(counter);
            if (!checkForSave()) {
                return false;
            }
        }
        return true;
    }

    public void setRandomAnimationMode(boolean on) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        Animator animator = petriNetController.getAnimator();
        if (!on) {
            applicationModel.stepforwardAction.setEnabled(animator.isStepForwardAllowed());
            applicationModel.stepbackwardAction.setEnabled(animator.isStepBackAllowed());
        } else {
            applicationModel.stepbackwardAction.setEnabled(false);
            applicationModel.stepforwardAction.setEnabled(false);
        }
        applicationModel.randomAction.setEnabled(!on);
        applicationModel.randomAnimateAction.setSelected(on);
    }

    public void setAnimationMode(boolean on) throws Exception {
        applicationModel.randomAnimateAction.setSelected(false);
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        Animator animator = petriNetController.getAnimator();
        applicationModel.startAction.setSelected(on);
        getCurrentTab().changeAnimationMode(on);
        if (on) {
            PetriNetView petriNetView = getCurrentPetriNetView();
//            animator.storeModel(petriNetView);
//            petriNetView.setEnabledTransitions();
//            animator.highlightEnabledTransitions();
            addAnimationHistory();
            applicationModel
                    .enableActions(false, applicationController.isPasteEnabled());// disables all non-animation buttons
            applicationModel.setEditionAllowed(false);
            statusBar.changeText(statusBar.textforAnimation);
        } else {
            applicationModel.setEditionAllowed(true);
            statusBar.changeText(statusBar.textforDrawing);
            animator.restoreModel();
            removeAnimationHistory();
            applicationModel
                    .enableActions(true, applicationController.isPasteEnabled()); // renables all non-animation buttons
        }
    }

    public void setTitle(String title) {
        String name = applicationModel.getName();
        super.setTitle((title == null) ? name : name + ": " + title);
    }


    /**
     * @author Ben Kirby Remove the listener from the zoomComboBox, so that when
     * the box's selected item is updated to keep track of ZoomActions
     * called from other sources, a duplicate ZoomAction is not called
     */
    public void updateZoomCombo() {
        ActionListener zoomComboListener = (zoomComboBox.getActionListeners())[0];
        zoomComboBox.removeActionListener(zoomComboListener);
        zoomComboBox.setSelectedItem(String.valueOf(getCurrentTab().getZoomController().getPercent()) + "%");
        zoomComboBox.addActionListener(zoomComboListener);
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    private Component c = null; // arreglantzoom
    private final Component p = new BlankLayer();

    /* */
    public void hideNet(boolean doHide) {
        if (doHide) {
            c = frameForPetriNetTabs.getComponentAt(frameForPetriNetTabs.getSelectedIndex());
            frameForPetriNetTabs.setComponentAt(frameForPetriNetTabs.getSelectedIndex(), p);
        } else {
            if (c != null) {
                frameForPetriNetTabs.setComponentAt(frameForPetriNetTabs.getSelectedIndex(), c);
                c = null;
            }
        }
        frameForPetriNetTabs.repaint();
    }

    /* This method can be used for simulating button clicks during testing
    *
    */
    public void executeAction(String action) {
        if (action.equals("toggleAnimation")) {
            applicationModel.startAction.actionPerformed(null);
        } else if (action.equals("groupTransitionsAction")) {
            applicationModel.groupTransitions.actionPerformed(null);
        } else if (action.equals("ungroupTransitionsAction")) {
            applicationModel.ungroupTransitions.actionPerformed(null);
        } else if (action.equals("exit")) {
            dispose();
            System.exit(0);
        }
    }

    /**
     * Refreshes the combo box that presents the Tokens available for use.
     */
    // stevedoubleday (Sept 2013):  refactored as part of TokenSetController implementation 
    public void refreshTokenClassChoices() {
        getCurrentPetriNetView().setActiveTokenView(getCurrentPetriNetView().getTokenViews().get(0));
        String[] tokenClassChoices = buildTokenClassChoices();
        DefaultComboBoxModel model = new DefaultComboBoxModel(tokenClassChoices);
        tokenClassComboBox.setModel(model);
    }

    public String getSelectedTokenName() {
        ComboBoxModel model = tokenClassComboBox.getModel();
        Object selected = model.getSelectedItem();
        return selected.toString();
    }


    public void removeTab(int index) {
        petriNetTabs.remove(index);
    }


    /**
     * Creates a new animationHistoryView text area, and returns a reference to it
     */
    void addAnimationHistory() {
        animationHistoryView = getCurrentTab().getAnimationView();
        animationHistoryView.setEditable(false);

        _scroller = new JScrollPane(animationHistoryView);
        _scroller.setBorder(new EmptyBorder(0, 0, 0, 0)); // make it less bad on XP

        _moduleAndAnimationHistoryFrame.setBottomComponent(_scroller);

        _moduleAndAnimationHistoryFrame.setDividerLocation(0.5);
        _moduleAndAnimationHistoryFrame.setDividerSize(8);
    }


    void removeAnimationHistory() {
        if (_scroller != null) {
            _moduleAndAnimationHistoryFrame.remove(_scroller);
            _moduleAndAnimationHistoryFrame.setDividerLocation(0);
            _moduleAndAnimationHistoryFrame.setDividerSize(0);
        }
    }

    public AnimationHistoryView getAnimationHistory() {
        return animationHistoryView;
    }


    public PetriNetTab getCurrentTab() {
        int index = frameForPetriNetTabs.getSelectedIndex();
        return getTab(index);
    }

    PetriNetTab getTab(int index) {
        if (index < 0 || index >= petriNetTabs.size()) {
            return null;
        }
        return petriNetTabs.get(index);
    }

    public PetriNetView getCurrentPetriNetView() {
        return getPetriNetView(frameForPetriNetTabs.getSelectedIndex());
    }


    PetriNetView getPetriNetView(int index) {
        if (index < 0 || index >= petriNetTabs.size()) {
            return null;
        }
        return petriNetTabs.get(index)._petriNetView;
    }


    public File getFile() {
        PetriNetTab petriNetTab = petriNetTabs.get(frameForPetriNetTabs.getSelectedIndex());
        return petriNetTab._appFile;
    }

    void setFile(File modelfile, int fileNo) {
        if (fileNo >= petriNetTabs.size()) {
            return;
        }
        PetriNetTab petriNetTab = petriNetTabs.get(fileNo);
        petriNetTab._appFile = modelfile;
    }
}

