package pipe.views;

import pipe.actions.ZoomAction;
import pipe.actions.gui.ExampleFileAction;
import pipe.actions.gui.GuiAction;
import pipe.actions.gui.UnfoldAction;
import pipe.actions.gui.create.SelectAction;
import pipe.actions.gui.file.*;
import pipe.actions.gui.grid.GridAction;
import pipe.actions.gui.tokens.ChooseTokenClassAction;
import pipe.actions.gui.window.ExitAction;
import pipe.actions.gui.zoom.SetZoomAction;
import pipe.actions.gui.zoom.ZoomInAction;
import pipe.actions.gui.zoom.ZoomOutAction;
import pipe.actions.manager.*;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.*;
import pipe.gui.model.PipeApplicationModel;
import pipe.utilities.gui.GuiUtils;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentNotFoundException;
import uk.ac.imperial.pipe.models.petrinet.Token;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class PipeApplicationView extends JFrame implements ActionListener, Observer, Serializable {


    public final StatusBar statusBar;

    public final ComponentEditorManager componentEditorManager;

    public final GuiAction selectAction;

    public final ExitAction exitAction;

    final ZoomUI zoomUI = new ZoomUI(1, 0.1, 3, 0.4, this);

    private final ComponentCreatorManager componentCreatorManager;

    private final AnimateActionManager animateActionManager;

    private final PetriNetEditorManager editorManager;

    private final TokenActionManager tokenActionManager;

    private final JSplitPane moduleAndAnimationHistoryFrame;

    private final JTabbedPane frameForPetriNetTabs = new JTabbedPane();

    private final List<PetriNetTab> petriNetTabs = new ArrayList<>();

    private final PipeApplicationController applicationController;

    private final PipeApplicationModel applicationModel;


    public JComboBox<String> zoomComboBox;

    public JComboBox<String> tokenClassComboBox;

    public GuiAction printAction = new PrintAction();

    public GuiAction exportPNGAction = new ExportPNGAction();

    public GuiAction exportTNAction = new ExportTNAction();

    public GuiAction exportPSAction = new ExportPSAction();

    public GuiAction importAction = new ImportAction();

    public GridAction toggleGrid = new GridAction(this);

    public GuiAction zoomOutAction;

    public GuiAction zoomInAction;

    public GuiAction zoomAction;

    public UnfoldAction unfoldAction;

    public ChooseTokenClassAction chooseTokenClassAction = new ChooseTokenClassAction(this);

    private JToolBar animationToolBar, drawingToolBar;

    private HelpBox helpAction;

    private JScrollPane scroller;

    private List<JLayer<JComponent>> wrappedPetrinetTabs = new ArrayList<>();

    public PipeApplicationView(final PipeApplicationController applicationController, PipeApplicationModel applicationModel,
                               ComponentEditorManager componentManager, ComponentCreatorManager componentCreatorManager,
                               AnimateActionManager animateActionManager, TokenActionManager tokenActionManager) {

        applicationController.register(this);
        applicationModel.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PipeApplicationModel.TOGGLE_ANIMATION_MODE)) {
                    boolean oldMode = (boolean) evt.getOldValue();
                    boolean newMode = (boolean) evt.getNewValue();
                    if (oldMode != newMode) {
                        setAnimationMode(newMode);
                    }
                } else if (evt.getPropertyName().equals(PipeApplicationModel.TYPE_ACTION_CHANGE_MESSAGE)) {
                    PetriNetTab petriNetTab = getCurrentTab();
                    if (petriNetTab != null) {
                        petriNetTab.setCursorType("crosshair");
                        SelectionManager selectionManager = applicationController.getSelectionManager(petriNetTab);
                        selectionManager.disableSelection();
                    }
                }


            }
        });
        this.componentCreatorManager = componentCreatorManager;
        this.animateActionManager = animateActionManager;
        this.tokenActionManager = tokenActionManager;
        ApplicationSettings.register(this);
        this.componentEditorManager = componentManager;
        this.editorManager = new PetriNetEditorManager(this, applicationController);

        this.applicationController = applicationController;
        this.applicationModel = applicationModel;


        zoomOutAction = new ZoomOutAction(zoomUI);
        zoomInAction = new ZoomInAction(zoomUI);
        zoomAction = new SetZoomAction("Zoom", "Select zoom percentage ", "", applicationController);

        unfoldAction = new UnfoldAction(this, applicationController);

        selectAction = new SelectAction(applicationModel, this, applicationController);


        exitAction = new ExitAction(this, applicationController);

        setTitle(null);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }

        this.setIconImage(new ImageIcon(getImageURL("icon.png")).getImage());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width * 80 / 100, screenSize.height * 80 / 100);
        this.setLocationRelativeTo(null);

        setExitAction();

        buildMenus();

        // Status bar...
        statusBar = new StatusBar();
        getContentPane().add(statusBar, BorderLayout.PAGE_END);

        // Build menus
        buildToolbar();

        this.setForeground(java.awt.Color.BLACK);
        this.setBackground(java.awt.Color.WHITE);

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


        applicationController.createEmptyPetriNet();

        setTabChangeListener();

        setZoomChangeListener();
    }

    @Override
    public final void setTitle(String title) {
        String name = applicationModel.getName();
        super.setTitle((title == null) ? name : name + ": " + title);
    }

    // set tabbed pane properties and add change listener that updates tab with
    // linked model and view
    private void setTabChangeListener() {
        frameForPetriNetTabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                PetriNetTab petriNetTab = getCurrentTab();
                applicationController.setActiveTab(petriNetTab);

                if (areAnyTabsDisplayed()) {
                    PetriNetController controller = applicationController.getActivePetriNetController();
                    if (controller.isCopyInProgress()) {
                        controller.cancelPaste();
                    }

                    petriNetTab.setVisible(true);
                    petriNetTab.repaint();
                    updateZoomCombo();

                    enableActions(!controller.isInAnimationMode());

                    setTitle(petriNetTab.getName());

                    applicationModel.setInAnimationMode(controller.isInAnimationMode());
                }

                refreshTokenClassChoices();
            }
        });
    }

    private void enableActions(boolean editMode) {
        if (editMode) {
            drawingToolBar.setVisible(true);
            animationToolBar.setVisible(false);
            componentEditorManager.enableActions();
            componentCreatorManager.enableActions();
            tokenActionManager.enableActions();
            editorManager.enableActions();
            animateActionManager.disableActions();
        } else {
            drawingToolBar.setVisible(false);
            animationToolBar.setVisible(true);
            componentEditorManager.disableActions();
            componentCreatorManager.disableActions();
            tokenActionManager.disableActions();
            editorManager.disableActions();
            animateActionManager.enableActions();
        }

        selectAction.setEnabled(editMode);
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

    /**
     * Refreshes the combo box that presents the Tokens available for use.
     * If there are no Petri nets being displayed this clears it
     */
    public void refreshTokenClassChoices() {
        if (areAnyTabsDisplayed()) {
            String[] tokenClassChoices = buildTokenClassChoices();
            ComboBoxModel<String> model = new DefaultComboBoxModel<>(tokenClassChoices);
            tokenClassComboBox.setModel(model);

            if (tokenClassChoices.length > 0) {
                try {
                    PetriNetController controller = applicationController.getActivePetriNetController();
                    controller.selectToken(getSelectedTokenName());
                } catch (PetriNetComponentNotFoundException petriNetComponentNotFoundException) {
                    GuiUtils.displayErrorMessage(this, petriNetComponentNotFoundException.getMessage());
                }
            }
        } else {
            tokenClassComboBox.setModel(new DefaultComboBoxModel<String>());
        }
    }

    public String getSelectedTokenName() {
        ComboBoxModel<String> model = tokenClassComboBox.getModel();
        Object selected = model.getSelectedItem();
        return selected.toString();
    }

    /**
     * @return names of Tokens for the combo box
     */
    protected String[] buildTokenClassChoices() {
        if (areAnyTabsDisplayed()) {
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
        return new String[0];
    }

    /**
     * @return true if any tabs are displayed
     */
    public boolean areAnyTabsDisplayed() {
        return applicationController.getActivePetriNetController() != null;
    }

    /**
     * Remove the listener from the zoomComboBox, so that when
     * the box's selected item is updated to keep track of ZoomActions
     * called from other sources, a duplicate ZoomAction is not called
     */
    public void updateZoomCombo() {
        ActionListener zoomComboListener = (zoomComboBox.getActionListeners())[0];
        zoomComboBox.removeActionListener(zoomComboListener);

        String zoomPercentage = zoomUI.getPercentageZoom() + "%";
        zoomComboBox.setSelectedItem(zoomPercentage);
        zoomComboBox.addActionListener(zoomComboListener);
    }

    public void setAnimationMode(boolean animateMode) {
        enableActions(!animateMode);

        if (animateMode) {
            enableActions(false);// disables all non-animation buttons
            applicationModel.setEditionAllowed(false);
            statusBar.changeText(statusBar.textforAnimation);
            createAnimationViewPane();

        } else {
            applicationModel.setEditionAllowed(true);
            statusBar.changeText(statusBar.textforDrawing);
            removeAnimationViewPlane();
            enableActions(true); // renables all non-animation buttons
        }
    }

    void removeAnimationViewPlane() {
        if (scroller != null) {
            moduleAndAnimationHistoryFrame.remove(scroller);
            moduleAndAnimationHistoryFrame.setDividerLocation(0);
            moduleAndAnimationHistoryFrame.setDividerSize(0);
        }
    }

    /**
     * Creates a new currentAnimationView text area, and returns a reference to it
     */
    private void createAnimationViewPane() {
        AnimationHistoryView animationHistoryView = histories.get(getCurrentTab());
        scroller = new JScrollPane(animationHistoryView);
        scroller.setBorder(new EmptyBorder(0, 0, 0, 0)); // make it less bad on XP

        moduleAndAnimationHistoryFrame.setBottomComponent(scroller);

        moduleAndAnimationHistoryFrame.setDividerLocation(0.5);
        moduleAndAnimationHistoryFrame.setDividerSize(8);
    }

    /**
     * Builds the toolbar that holds actions for editin and creating Petri nets with PIPE
     */
    private void buildToolbar() {
        // Create the toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);// Inhibit toolbar floating


        for (GuiAction action : editorManager.getActions()) {
            addButton(toolBar, action);
        }

        toolBar.addSeparator();
        addButton(toolBar, printAction);
        toolBar.addSeparator();
        for (GuiAction action : componentEditorManager.getActions()) {
            addButton(toolBar, action);
        }
        toolBar.addSeparator();

        addButton(toolBar, zoomOutAction);
        addZoomComboBox(toolBar, zoomAction);
        addButton(toolBar, zoomInAction);
        toolBar.addSeparator();
        addButton(toolBar, toggleGrid);
        for (GuiAction action : animateActionManager.getEditActions()) {
            addButton(toolBar, action);
        }

        drawingToolBar = new JToolBar();
        drawingToolBar.setFloatable(false);

        toolBar.addSeparator();
        addButton(drawingToolBar, selectAction);
        drawingToolBar.addSeparator();
        for (GuiAction action : componentCreatorManager.getActions()) {
            addButton(drawingToolBar, action);
        }
        drawingToolBar.addSeparator();

        for (GuiAction action : tokenActionManager.getActions()) {
            addButton(drawingToolBar, action);
        }

        addTokenClassComboBox(drawingToolBar, chooseTokenClassAction);
        addButton(drawingToolBar, unfoldAction);
        drawingToolBar.addSeparator();

        toolBar.add(drawingToolBar);

        animationToolBar = new JToolBar();
        animationToolBar.setFloatable(false);

        for (GuiAction action : animateActionManager.getAnimateActions()) {
            addButton(animationToolBar, action);
        }

        toolBar.add(animationToolBar);
        animationToolBar.setVisible(false);

        toolBar.addSeparator();
        addButton(toolBar, helpAction);

        for (int i = 0; i < toolBar.getComponentCount(); i++) {
            toolBar.getComponent(i).setFocusable(false);
        }

        getContentPane().add(toolBar, BorderLayout.PAGE_START);
    }

    /**
     * Creates and adds the token view combo box to the view
     *
     * @param toolBar the JToolBar to add the combo box to
     * @param action  the action that the tokenClassComboBox performs when selected
     */
    protected void addTokenClassComboBox(JToolBar toolBar, Action action) {
        String[] tokenClassChoices = new String[]{"Default"};
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(tokenClassChoices);
        tokenClassComboBox = new JComboBox<>(model);
        tokenClassComboBox.setEditable(true);
        tokenClassComboBox.setSelectedItem(tokenClassChoices[0]);
        tokenClassComboBox.setMaximumRowCount(100);
        //        tokenClassComboBox.setMaximumSize(new Dimension(125, 100));
        tokenClassComboBox.setEditable(false);
        tokenClassComboBox.setAction(action);
        toolBar.add(tokenClassComboBox);
    }

    /**
     * @param toolBar the JToolBar to add the button to
     * @param action  the action that the ZoomComboBox performs
     */
    private void addZoomComboBox(JToolBar toolBar, Action action) {
        Dimension zoomComboBoxDimension = new Dimension(65, 28);
        String[] zoomExamples = applicationModel.getZoomExamples();
        zoomComboBox = new JComboBox<>(zoomExamples);
        zoomComboBox.setEditable(true);
        zoomComboBox.setSelectedItem("100%");
        zoomComboBox.setMaximumRowCount(zoomExamples.length);
        zoomComboBox.setMaximumSize(zoomComboBoxDimension);
        zoomComboBox.setMinimumSize(zoomComboBoxDimension);
        zoomComboBox.setPreferredSize(zoomComboBoxDimension);
        zoomComboBox.setAction(action);
        toolBar.add(zoomComboBox);
    }

    private void addButton(JToolBar toolBar, GuiAction action) {

        if (action.getValue("selected") != null) {
            toolBar.add(new ToggleButton(action));
        } else {
            toolBar.add(action);
        }
    }

    /**
     * This method builds the menus for the application
     */
    private void buildMenus() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        for (GuiAction action : editorManager.getActions()) {
            addMenuItem(fileMenu, action);
        }

        fileMenu.addSeparator();
        addMenuItem(fileMenu, importAction);

        // Export menu


        JMenu exportMenu = new JMenu("Export");
        exportMenu.setIcon(new ImageIcon(getImageURL("Export.png")));
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

        for (GuiAction action : componentEditorManager.getActions()) {
            addMenuItem(editMenu, action);
        }

        JMenu drawMenu = new JMenu("Draw");
        drawMenu.setMnemonic('D');
        addMenuItem(drawMenu, selectAction);

        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");

        rootPane.getActionMap().put("ESCAPE", selectAction);

        drawMenu.addSeparator();
        for (GuiAction action : componentCreatorManager.getActions()) {
            addMenuItem(drawMenu, action);
        }
        drawMenu.addSeparator();
        for (Action action : tokenActionManager.getActions()) {
            addMenuItem(drawMenu, action);
        }
        addMenuItem(drawMenu, unfoldAction);
        drawMenu.addSeparator();

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');

        JMenu zoomMenu = new JMenu("Zoom");
        zoomMenu.setIcon(new ImageIcon(getImageURL("Zoom.png")));
        addZoomMenuItems(zoomMenu);

        addMenuItem(viewMenu, zoomOutAction);

        addMenuItem(viewMenu, zoomInAction);
        viewMenu.add(zoomMenu);

        viewMenu.addSeparator();
        addMenuItem(viewMenu, toggleGrid);

        JMenu animateMenu = new JMenu("Animate");
        animateMenu.setMnemonic('A');

        for (GuiAction action : animateActionManager.getActions()) {
            addMenuItem(animateMenu, action);
        }

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        helpAction = new HelpBox("Help", "View documentation", "F1", "index.htm");
        addMenuItem(helpMenu, helpAction);

        JMenuItem aboutItem = helpMenu.add("About PIPE");
        aboutItem.addActionListener(this); // Help - About is implemented
        // differently
        aboutItem.setIcon(new ImageIcon(getImageURL("About.png")));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(drawMenu);
        menuBar.add(animateMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

    }

    private void addMenuItem(JMenu menu, Action action) {
        JMenuItem item = menu.add(action);
        KeyStroke keystroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);

        if (keystroke != null) {
            item.setAccelerator(keystroke);
        }
    }

    /**
     * @param zoomMenu to add to the applications menu bar
     */
    private void addZoomMenuItems(JMenu zoomMenu) {
        for (ZoomAction zoomAction : applicationModel.getZoomActions()) {
            JMenuItem newItem = new JMenuItem(zoomAction);
            zoomMenu.add(newItem);
        }
    }

    /**
     * Creates an example file menu based on examples in resources/extras/examples
     */
    private JMenu createExampleFileMenu() {
        if (isJar()) {
            try {
                return loadJarExamples();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        JMenu exampleMenu = new JMenu("Examples");
        exampleMenu.setIcon(new ImageIcon(getImageURL("Example.png")));
        URL examplesDirURL = this.getClass().getResource("/extras/examples/");
        try {
            URI uri = examplesDirURL.toURI();
            File directory = new File(uri);
          for (File entry : directory.listFiles()) {
              addMenuItem(exampleMenu, new ExampleFileAction(entry, this));
          }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return exampleMenu;
    }

    private JMenu loadJarExamples() throws IOException {
        JMenu exampleMenu = new JMenu("Examples");

        CodeSource src = PipeApplicationView.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            while (true) {
                ZipEntry e = zip.getNextEntry();
                if (e == null)
                    break;
                String name = e.getName();
                if (name.startsWith("foo/")) {
                    addMenuItem(exampleMenu, new ExampleFileAction(e, this));
      /* Do something with this entry. */
                }
            }
        }
        return exampleMenu;
    }

    private void setZoomChangeListener() {
        zoomUI.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                getTabComponent().repaint();
                updateZoomCombo();
            }
        });
    }

    private JComponent getTabComponent() {
        return wrappedPetrinetTabs.get(frameForPetriNetTabs.getSelectedIndex());
    }

    /**
     * Sets the default behaviour for exit for both Windows/Linux/Mac OS X
     */
    private void setExitAction() {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exitAction.tryToExit();
            }
        });
    }

    public ComponentEditorManager getComponentEditorManager() {
        return componentEditorManager;
    }

    public JTabbedPane getFrameForPetriNetTabs() {
        return frameForPetriNetTabs;
    }

    /**
     * Displays contributors
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        JOptionPane.showMessageDialog(this, "PIPE: Platform Independent Petri Net Ediror\n\n" + "Authors:\n" +
                "2003: Jamie Bloom, Clare Clark, Camilla Clifford, Alex Duncan, Haroun Khan and Manos Papantoniou\n" +
                "2004: Tom Barnwell, Michael Camacho, Matthew Cook, Maxim Gready, Peter Kyme and Michail Tsouchlaris\n"
                +
                "2005: Nadeem Akharware\n" + "????: Tim Kimber, Ben Kirby, Thomas Master, Matthew Worthington\n" +
                "????: Pere Bonet Bonet (Universitat de les Illes Balears)\n" +
                "????: Marc Meli\u00E0 Aguil\u00F3 (Universitat de les Illes Balears)\n" +
                "2010: Alex Charalambous (Imperial College London)\n" +
                "2011: Jan Vlasak (Imperial College London)\n\n" + "http://pipe2.sourceforge.net/", "About PIPE",
                JOptionPane.INFORMATION_MESSAGE);
    }

    //TODO: Find out if this actually ever gets called
    @Override
    public void update(Observable o, Object obj) {
    }

    /**
     * Adds the tab to the main application view in the tabbed view frame
     *
     * @param name name of tab
     * @param tab  tab to add
     */
    //TODO: ADD SCROLL PANE
    public void addNewTab(String name, PetriNetTab tab) {

        //        JScrollPane scroller = new JScrollPane(tab);
        //        scroller.setBorder(new BevelBorder(BevelBorder.LOWERED));

        JLayer<JComponent> jLayer = new JLayer<>(tab, zoomUI);
        wrappedPetrinetTabs.add(jLayer);

        petriNetTabs.add(tab);
        frameForPetriNetTabs.addTab(name, null, jLayer, null);
        frameForPetriNetTabs.setSelectedIndex(petriNetTabs.size() - 1);
    }

    public File getFile() {
        PetriNetTab petriNetTab = petriNetTabs.get(frameForPetriNetTabs.getSelectedIndex());
        return petriNetTab._appFile;
    }

    public StatusBar getStatusBar() {
        return statusBar;
    }

    public void close() {
        exitAction.actionPerformed(null);
    }

    public void removeCurrentTab() {
        removeTab(frameForPetriNetTabs.getSelectedIndex());
    }

    public void removeTab(int index) {
        if ((frameForPetriNetTabs.getTabCount() > 0)) {
            PetriNetTab tab = petriNetTabs.get(index);
            petriNetTabs.remove(index);
            frameForPetriNetTabs.remove(index);
        }
    }

    public void updateSelectedTabName(String title) {
        int index = frameForPetriNetTabs.getSelectedIndex();
        frameForPetriNetTabs.setTitleAt(index, title);
    }

    public AnimateActionManager getAnimateActionManager() {
        return animateActionManager;
    }

    public void registerNewPetriNet(PetriNet petriNet) {
        PropertyChangeListener zoomListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                updateZoomCombo();
            }
        };

        AnimationHistoryView animationHistoryView;
        try {
            animationHistoryView = new AnimationHistoryView("Animation History");
        } catch (BadLocationException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
        PetriNetTab petriNetTab = new PetriNetTab(this);
        histories.put(petriNetTab, animationHistoryView);
        UndoableEditListener undoListener = new SimpleUndoListener(componentEditorManager, applicationController);

        applicationController.registerTab(petriNet, petriNetTab, animationHistoryView, undoListener, zoomListener);
        addNewTab(petriNet.getNameValue(), petriNetTab);
    }

    private Map<PetriNetTab, AnimationHistoryView> histories = new HashMap<>();

    private URL getImageURL(String name) {
        return this.getClass().getResource(
                ApplicationSettings.getImagePath() + name);
    }

    public boolean isJar() {
        CodeSource src = PipeApplicationView.class.getProtectionDomain().getCodeSource();
        URL jar = src.getLocation();
        return jar.getPath().endsWith(".jar");
    }
}

