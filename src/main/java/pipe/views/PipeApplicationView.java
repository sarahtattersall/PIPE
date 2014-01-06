package pipe.views;

import pipe.actions.*;
import pipe.actions.animate.*;
import pipe.actions.edit.*;
import pipe.actions.file.*;
import pipe.actions.type.*;
import pipe.actions.zoom.SetZoomAction;
import pipe.actions.zoom.ZoomInAction;
import pipe.actions.zoom.ZoomOutAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.*;
import pipe.gui.widgets.FileBrowser;
import pipe.io.JarUtilities;
import pipe.models.PipeApplicationModel;
import pipe.models.component.Token;
import pipe.models.visitor.connectable.arc.InhibitorCreatorVisitor;
import pipe.models.visitor.connectable.arc.InhibitorSourceVisitor;
import pipe.models.visitor.connectable.arc.NormalArcCreatorVisitor;
import pipe.models.visitor.connectable.arc.NormalArcSourceVisitor;

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

    private final JSplitPane moduleAndAnimationHistoryFrame;
    private JScrollPane scroller;

    private final JTabbedPane frameForPetriNetTabs = new JTabbedPane();
    private final ArrayList<PetriNetTab> petriNetTabs;

    private AnimationHistoryView currentAnimationView;
    private final PipeApplicationController applicationController;
    private final PipeApplicationModel applicationModel;

    private FileAction createAction = new CreateAction();
    private FileAction openAction = new OpenAction();
    private FileAction closeAction = new CloseAction();
    private FileAction saveAction = new SaveAction();
    private FileAction saveAsAction = new SaveAsAction();
    public FileAction printAction = new PrintAction();
    public FileAction exportPNGAction = new ExportPNGAction();
    public FileAction exportTNAction = new ExportTNAction();
    public FileAction exportPSAction = new ExportPSAction();
    public FileAction importAction = new ImportAction();
    public GuiAction exitAction = new ExitAction(this);
    public GuiAction undoAction = new UndoAction();
    public GuiAction redoAction = new RedoAction();
    public GuiAction copyAction = new CopyAction("Copy", "Copy (Ctrl-C)", "ctrl C");
    public GuiAction cutAction = new CutAction("Cut", "Cut (Ctrl-X)", "ctrl X");
    public GuiAction pasteAction = new PasteAction("Paste", "Paste (Ctrl-V)", "ctrl V");
    public DeleteAction deleteAction = new DeleteAction("Delete", "Delete selection", "DELETE");
    public TypeAction selectAction = new SelectAction("Select", Constants.SELECT, "Select components", "S");
    public TypeAction placeAction = new PlaceAction("Place", Constants.PLACE, "Add a place", "P");
    public TypeAction transAction = new ImmediateTransitionAction("Immediate transition", Constants.IMMTRANS, "Add an immediate transition", "I");
    public TypeAction timedtransAction = new TimedTransitionAction("Timed transition", Constants.TIMEDTRANS, "Add a timed transition", "T");

    public final TypeAction arcAction;
    public final TypeAction inhibarcAction;

    public TypeAction annotationAction = new AnnotationAction("Annotation", Constants.ANNOTATION, "Add an annotation", "N");
    public TypeAction tokenAction = new AddTokenAction("Add token", Constants.ADDTOKEN, "Add a token", "ADD");
    public TypeAction deleteTokenAction = new DeleteTokenAction("Delete token", Constants.DELTOKEN, "Delete a token", "SUBTRACT");
    public TypeAction dragAction = new DragAction("Drag", Constants.DRAG, "Drag the drawing", "D");
    public TypeAction rateAction = new RateAction("Rate Parameter", Constants.RATE, "Rate Parameter", "R");

    public GridAction toggleGrid = new GridAction("Cycle grid", "Change the grid size", "G");;
    public GuiAction zoomOutAction;
    public GuiAction zoomInAction;
    public GuiAction zoomAction;

    public AnimateAction startAction = new ToggleAnimateAction("Animation mode", "Toggle Animation Mode", "Ctrl A");
    public AnimateAction stepbackwardAction = new StepBackwardAction("Back", "Step backward a firing", "4");
    public AnimateAction stepforwardAction = new StepForwardAction("Forward", "Step forward a firing", "6");
    public AnimateAction randomAction = new RandomAnimateAction("Random", "Randomly fire a transition", "5");
    public AnimateAction multipleRandomAction = new MultiRandomAnimateAction("Animate", "Randomly fire a number of transitions", "7");
    public SpecifyTokenAction specifyTokenClasses = new SpecifyTokenAction();

    public GroupTransitionsAction groupTransitions = new GroupTransitionsAction();
    public UnfoldAction unfoldAction = new UnfoldAction("unfoldAction", "Unfold Petri Net", "shift ctrl U");
    public UngroupTransitionsAction ungroupTransitions = new UngroupTransitionsAction("ungroupTransitions", "Ungroup any possible transitions", "shift ctrl H");
    public ChooseTokenClassAction chooseTokenClassAction = new ChooseTokenClassAction("chooseTokenClass", "Select current token", null);

    /**
     * Constructor for unit testing only
     *
     * @author stevedoubleday (Oct 2013)
     */
    public PipeApplicationView() {
        statusBar = null;
        moduleAndAnimationHistoryFrame = null;
        petriNetTabs = null;
        applicationController = null;
        applicationModel = null;

        inhibarcAction = null;
        arcAction = null;
    }

    public PipeApplicationView(PipeApplicationController applicationController, PipeApplicationModel applicationModel) {
        ApplicationSettings.register(this);
        this.applicationController = applicationController;
        this.applicationModel = applicationModel;

        petriNetTabs = new ArrayList<PetriNetTab>();

        inhibarcAction = new ArcAction("Inhibitor Arc", Constants.INHIBARC, "Add an inhibitor arc", "H", new InhibitorSourceVisitor(), new InhibitorCreatorVisitor(applicationController));
        arcAction = new ArcAction("Arc", Constants.ARC, "Add an arc", "A", new NormalArcSourceVisitor(), new NormalArcCreatorVisitor(applicationController));
        zoomOutAction = new ZoomOutAction("Zoom out", "Zoom out by 10% ", "ctrl MINUS", applicationController);
        zoomInAction = new ZoomInAction("Zoom in", "Zoom in by 10% ", "ctrl PLUS", applicationController);
        zoomAction = new SetZoomAction("Zoom", "Select zoom percentage ", "", applicationController);


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

        addWindowListener(new WindowHandler(this));

        this.setForeground(java.awt.Color.BLACK);
        this.setBackground(java.awt.Color.WHITE);

        Grid.enableGrid();

        ModuleManager moduleManager = new ModuleManager();
        JTree moduleTree = moduleManager.getModuleTree();
        moduleAndAnimationHistoryFrame = new JSplitPane(JSplitPane.VERTICAL_SPLIT, moduleTree, null);
        moduleAndAnimationHistoryFrame.setContinuousLayout(true);
        moduleAndAnimationHistoryFrame.setDividerSize(0);
        JSplitPane pane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, moduleAndAnimationHistoryFrame, frameForPetriNetTabs);
        pane.setContinuousLayout(true);
        pane.setOneTouchExpandable(true);
        pane.setBorder(null); // avoid multiple borders
        pane.setDividerSize(8);
        getContentPane().add(pane);

        setVisible(true);
        this.applicationModel.setMode(Constants.SELECT);
        selectAction.actionPerformed(null);

        PetriNetTab tab = applicationController.createEmptyPetriNet();
        applicationController.setActiveTab(tab);
        setTabChangeListener();
    }

    public JTabbedPane getFrameForPetriNetTabs() {
        return frameForPetriNetTabs;
    }

    /**
     * This method builds the menus for the application
     */
    private void buildMenus() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        addMenuItem(fileMenu, createAction);
        addMenuItem(fileMenu, openAction);
        addMenuItem(fileMenu, closeAction);
        fileMenu.addSeparator();
        addMenuItem(fileMenu, saveAction);
        addMenuItem(fileMenu, saveAsAction);

        fileMenu.addSeparator();
        addMenuItem(fileMenu, importAction);

        // Export menu


        JMenu exportMenu = new JMenu("Export");
        exportMenu.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader()
                .getResource(ApplicationSettings.getImagePath() + "Export.png")));
        addMenuItem(exportMenu, exportPNGAction);
        addMenuItem(exportMenu, exportPSAction);
        addMenuItem(exportMenu, exportTNAction);
        fileMenu.add(exportMenu);
        fileMenu.addSeparator();
        addMenuItem(fileMenu, printAction);
        fileMenu.addSeparator();

        // Example files menu
        JMenu exampleMenu = createExampleFileMenu();

        fileMenu.add(exampleMenu);
        fileMenu.addSeparator();

        addMenuItem(fileMenu, exitAction);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        addMenuItem(editMenu, undoAction);
        addMenuItem(editMenu, redoAction);
        editMenu.addSeparator();
        addMenuItem(editMenu, cutAction);
        addMenuItem(editMenu, copyAction);
        addMenuItem(editMenu, pasteAction);
        addMenuItem(editMenu, deleteAction);

        JMenu drawMenu = new JMenu("Draw");
        drawMenu.setMnemonic('D');
        addMenuItem(drawMenu, selectAction);

        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");

        rootPane.getActionMap().put("ESCAPE", selectAction);

        drawMenu.addSeparator();
        addMenuItem(drawMenu, placeAction);
        addMenuItem(drawMenu, transAction);
        addMenuItem(drawMenu, timedtransAction);
        addMenuItem(drawMenu, arcAction);
        addMenuItem(drawMenu, inhibarcAction);
        addMenuItem(drawMenu, annotationAction);
        drawMenu.addSeparator();
        addMenuItem(drawMenu, tokenAction);
        addMenuItem(drawMenu, deleteTokenAction);
        addMenuItem(drawMenu, specifyTokenClasses);
        addMenuItem(drawMenu, groupTransitions);
        addMenuItem(drawMenu, ungroupTransitions);
        addMenuItem(drawMenu, unfoldAction);
        drawMenu.addSeparator();
        addMenuItem(drawMenu, rateAction);

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');

        JMenu zoomMenu = new JMenu("Zoom");
        zoomMenu.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader()
                .getResource(ApplicationSettings.getImagePath() + "Zoom.png")));
        addZoomMenuItems(zoomMenu);

        addMenuItem(viewMenu, zoomOutAction);

        addMenuItem(viewMenu, zoomInAction);
        viewMenu.add(zoomMenu);

        viewMenu.addSeparator();
        addMenuItem(viewMenu, toggleGrid);
        addMenuItem(viewMenu, dragAction);

        JMenu animateMenu = new JMenu("Animate");
        animateMenu.setMnemonic('A');
        addMenuItem(animateMenu, startAction);
        animateMenu.addSeparator();
        addMenuItem(animateMenu, stepbackwardAction);
        addMenuItem(animateMenu, stepforwardAction);
        addMenuItem(animateMenu, randomAction);
        addMenuItem(animateMenu, multipleRandomAction);

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
     * Creates an example file menu based on examples in resources/extras/examples
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

        addButton(toolBar, createAction);
        addButton(toolBar, openAction);
        addButton(toolBar, saveAction);
        addButton(toolBar, saveAsAction);
        addButton(toolBar, closeAction);
        toolBar.addSeparator();
        addButton(toolBar, printAction);
        toolBar.addSeparator();
        addButton(toolBar, cutAction);
        addButton(toolBar, copyAction);
        addButton(toolBar, pasteAction);
        addButton(toolBar, deleteAction);
        addButton(toolBar, undoAction);
        addButton(toolBar, redoAction);
        toolBar.addSeparator();

        addButton(toolBar, zoomOutAction);
        addZoomComboBox(toolBar, zoomAction);
        addButton(toolBar, zoomInAction);
        toolBar.addSeparator();
        addButton(toolBar, toggleGrid);
        addButton(toolBar, dragAction);
        addButton(toolBar, startAction);

        drawingToolBar = new JToolBar();
        drawingToolBar.setFloatable(false);

        toolBar.addSeparator();
        addButton(drawingToolBar, selectAction);
        drawingToolBar.addSeparator();
        addButton(drawingToolBar, placeAction);// Add Draw Menu Buttons
        addButton(drawingToolBar, transAction);
        addButton(drawingToolBar, timedtransAction);
        addButton(drawingToolBar, arcAction);
        addButton(drawingToolBar, inhibarcAction);
        addButton(drawingToolBar, annotationAction);
        drawingToolBar.addSeparator();
        addButton(drawingToolBar, tokenAction);
        addButton(drawingToolBar, deleteTokenAction);
        addTokenClassComboBox(drawingToolBar, chooseTokenClassAction);
        addButton(drawingToolBar, specifyTokenClasses);
        addButton(drawingToolBar, groupTransitions);
        addButton(drawingToolBar, ungroupTransitions);
        addButton(drawingToolBar, unfoldAction);
        drawingToolBar.addSeparator();
        addButton(drawingToolBar, rateAction);

        toolBar.add(drawingToolBar);

        animationToolBar = new JToolBar();
        animationToolBar.setFloatable(false);
        addButton(animationToolBar, stepbackwardAction);
        addButton(animationToolBar, stepforwardAction);
        addButton(animationToolBar, randomAction);
        addButton(animationToolBar, multipleRandomAction);

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
     * @param zoomMenu
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

    /**
     * @return names of Tokens for the combo box
     */
    protected String[] buildTokenClassChoices() {
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

    // set tabbed pane properties and add change listener that updates tab with
    // linked model and view
    private void setTabChangeListener() {
        frameForPetriNetTabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                CopyPasteManager copyPasteManager = applicationController.getCopyPasteManager();
                if (copyPasteManager.pasteInProgress()) {
                    copyPasteManager.cancelPaste();
                }

                PetriNetTab petriNetTab = getCurrentTab();
                applicationController.setActiveTab(petriNetTab);
                petriNetTab.setVisible(true);
                petriNetTab.repaint();
                updateZoomCombo();

                enableActions(!petriNetTab.isInAnimationMode(), applicationController.isPasteEnabled());

                setTitle(petriNetTab.getName());

                setAnimationMode(petriNetTab.isInAnimationMode());

                refreshTokenClassChoices();
            }
        });
    }

    /**
     * Displays contributors
     */
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

    //TODO: Find out if this actually ever gets called
    public void update(Observable o, Object obj) {
        PetriNetTab currentTab = getCurrentTab();
        if ((applicationModel.getMode() != Constants.CREATING) && (!currentTab.isInAnimationMode())) {
            currentTab.setNetChanged(true);
        }
    }

    //TODO: Move out into save actions
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
            setFile(outFile, frameForPetriNetTabs.getSelectedIndex());
            PetriNetTab currentTab = getCurrentTab();
            currentTab.setNetChanged(false);
            String name = outFile.getName().split(".xml")[0];
            frameForPetriNetTabs.setTitleAt(frameForPetriNetTabs.getSelectedIndex(), name);
            setTitle(outFile.getName());
            //TODO: WHY CLEAR THIS?
//            currentTab.getHistoryManager().clear();
            undoAction.setEnabled(false);
            redoAction.setEnabled(false);
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

    public void setAnimationMode(boolean animateMode) {
        enableActions(!animateMode);

        stepforwardAction.setEnabled(false);
        stepbackwardAction.setEnabled(false);
        multipleRandomAction.setSelected(false);
        startAction.setSelected(animateMode);

        PetriNetTab petriNetTab = getCurrentTab();
        petriNetTab.changeAnimationMode(animateMode);

        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        Animator animator = petriNetController.getAnimator();
        if (animateMode) {
            enableActions(false, applicationController.isPasteEnabled());// disables all non-animation buttons
            applicationModel.setEditionAllowed(false);
            statusBar.changeText(statusBar.textforAnimation);
            createAnimationViewPane();

        } else {
            applicationModel.setEditionAllowed(true);
            statusBar.changeText(statusBar.textforDrawing);
            animator.restoreModel();
            removeAnimationViewPlane();
            enableActions(true, applicationController.isPasteEnabled()); // renables all non-animation buttons
        }
    }

    /**
     * Creates a new currentAnimationView text area, and returns a reference to it
     */
    private void createAnimationViewPane() {
        AnimationHistoryView animationHistoryView = getCurrentTab().getAnimationView();
        scroller = new JScrollPane(animationHistoryView);
        scroller.setBorder(new EmptyBorder(0, 0, 0, 0)); // make it less bad on XP

        moduleAndAnimationHistoryFrame.setBottomComponent(scroller);

        moduleAndAnimationHistoryFrame.setDividerLocation(0.5);
        moduleAndAnimationHistoryFrame.setDividerSize(8);
    }


    void removeAnimationViewPlane() {
        if (scroller != null) {
            moduleAndAnimationHistoryFrame.remove(scroller);
            moduleAndAnimationHistoryFrame.setDividerLocation(0);
            moduleAndAnimationHistoryFrame.setDividerSize(0);
        }
    }


    public void setTitle(String title) {
        String name = applicationModel.getName();
        super.setTitle((title == null) ? name : name + ": " + title);
    }


    /**
     * Remove the listener from the zoomComboBox, so that when
     * the box's selected item is updated to keep track of ZoomActions
     * called from other sources, a duplicate ZoomAction is not called
     */
    public void updateZoomCombo() {
        ActionListener zoomComboListener = (zoomComboBox.getActionListeners())[0];
        zoomComboBox.removeActionListener(zoomComboListener);
        PetriNetController controller = applicationController.getActivePetriNetController();
        ZoomController zoomController = controller.getZoomController();
        zoomComboBox.setSelectedItem(String.valueOf(zoomController.getPercent()) + "%");
        zoomComboBox.addActionListener(zoomComboListener);
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    /* This method can be used for simulating button clicks during testing
    *
    */
    public void executeAction(String action) {
        if (action.equals("toggleAnimation")) {
            startAction.actionPerformed(null);
        } else if (action.equals("groupTransitionsAction")) {
            groupTransitions.actionPerformed(null);
        } else if (action.equals("ungroupTransitionsAction")) {
            ungroupTransitions.actionPerformed(null);
        } else if (action.equals("exit")) {
            dispose();
            System.exit(0);
        }
    }

    /**
     * Refreshes the combo box that presents the Tokens available for use.
     */
    // stevedoubleday (Sept 2013):  refactored as part of TokenSetControlxler implementation
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

    public void restoreMode() {
//        setPreviousMode();
//        placeAction.setSelected(mode == Constants.PLACE);
//        transAction.setSelected(mode == Constants.IMMTRANS);
//        timedtransAction.setSelected(mode == Constants.TIMEDTRANS);
//        arcAction.setSelected(mode == Constants.ARC);
//        inhibarcAction.setSelected(mode == Constants.INHIBARC);
//        tokenAction.setSelected(mode == Constants.ADDTOKEN);
//        deleteTokenAction.setSelected(mode == Constants.DELTOKEN);
//        rateAction.setSelected(mode == Constants.RATE);
//        selectAction.setSelected(mode == Constants.SELECT);
//        annotationAction.setSelected(mode == Constants.ANNOTATION);
    }

    private void enableActions(boolean editMode, boolean pasteEnabled) {
        saveAction.setEnabled(editMode);
        saveAsAction.setEnabled(editMode);

        placeAction.setEnabled(editMode);
        arcAction.setEnabled(editMode);
        inhibarcAction.setEnabled(editMode);
        annotationAction.setEnabled(editMode);
        transAction.setEnabled(editMode);
        timedtransAction.setEnabled(editMode);
        tokenAction.setEnabled(editMode);
        deleteAction.setEnabled(editMode);
        selectAction.setEnabled(editMode);
        deleteTokenAction.setEnabled(editMode);
        rateAction.setEnabled(editMode);
        //toggleGrid.setEnabled(status);

        if (editMode) {
            startAction.setSelected(false);
            multipleRandomAction.setSelected(false);
            stepbackwardAction.setEnabled(false);
            stepforwardAction.setEnabled(false);
            pasteAction.setEnabled(pasteEnabled);
        } else {
            pasteAction.setEnabled(true);
            undoAction.setEnabled(true);
            redoAction.setEnabled(true);
        }
        randomAction.setEnabled(!editMode);
        multipleRandomAction.setEnabled(!editMode);
        copyAction.setEnabled(editMode);
        cutAction.setEnabled(editMode);
        deleteAction.setEnabled(editMode);

    }

    public void close() {
        exitAction.actionPerformed(null);
    }

    public void setUndoActionEnabled(boolean flag) {
        undoAction.setEnabled(flag);
    }

    public void setRedoActionEnabled(boolean flag) {
        redoAction.setEnabled(flag);
    }
}

