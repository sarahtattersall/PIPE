package pipe.views;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import pipe.actions.ZoomAction;
import pipe.actions.gui.ChooseTokenClassAction;
import pipe.actions.gui.ExampleFileAction;
import pipe.actions.gui.ExitAction;
import pipe.actions.gui.ExportPNGAction;
import pipe.actions.gui.ExportPSAction;
import pipe.actions.gui.ExportTNAction;
import pipe.actions.gui.GridAction;
import pipe.actions.gui.GuiAction;
import pipe.actions.gui.ImportAction;
import pipe.actions.gui.PipeApplicationModel;
import pipe.actions.gui.PrintAction;
import pipe.actions.gui.SelectAction;
import pipe.actions.gui.SetZoomAction;
import pipe.actions.gui.UnfoldAction;
import pipe.actions.gui.ZoomInAction;
import pipe.actions.gui.ZoomOutAction;
import pipe.actions.gui.ZoomUI;
import pipe.actions.manager.AnimateActionManager;
import pipe.actions.manager.ComponentCreatorManager;
import pipe.actions.manager.ComponentEditorManager;
import pipe.actions.manager.PetriNetEditorManager;
import pipe.actions.manager.SimpleUndoListener;
import pipe.actions.manager.TokenActionManager;
import pipe.controllers.PetriNetController;
import pipe.controllers.SelectionManager;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.LayoutAction;
import pipe.gui.PetriNetTab;
import pipe.gui.ToggleButton;
import pipe.gui.PipeResourceLocator;

/**
 * Builder class to set up the properties of the PIPE main application window
 * <p/>
 * This class does a bit too much, but it took the logic out of the {@link pipe.views.PipeApplicationView} class itself
 */
public final class PipeApplicationBuilder {

    /**
     * Class logger
     */
    private static final Logger LOGGER = Logger.getLogger(PipeApplicationBuilder.class.getName());

    /**
     *
     * @param controller
     * @param model
     * @return created PipeApplicationView
     */
    public PipeApplicationView build(PipeApplicationController controller, PipeApplicationModel model) {
        ZoomUI zoomUI = new ZoomUI(1, 0.1, 3, 0.4, controller);
        PipeApplicationView view = new PipeApplicationView(zoomUI, controller, model);
        final PIPEComponents pipeComponents = buildComponents(view, model, controller, zoomUI);
        JToolBar drawingToolBar = getDrawingToolBar(pipeComponents, view);
        JToolBar animationToolBar = getAnimationToolBar(pipeComponents);
        JToolBar jToolBar = getToolBar(view, pipeComponents, model.getZoomExamples(), drawingToolBar, animationToolBar);
        JMenuBar menuBar = buildMenu(pipeComponents, view, controller, model.getZoomExamples());
        view.setUndoListener(pipeComponents.undoListener);
        view.setMenu(menuBar);
        view.setToolBar(jToolBar);
        view.setExitAction(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                pipeComponents.exitAction.tryToExit();
            }
        });
        pipeComponents.layoutAction.addUndoableEditListener(pipeComponents.undoListener);
        setTabChangeListener(view, controller, pipeComponents, drawingToolBar, animationToolBar);
        listenForAnimationMode(pipeComponents, model, controller, drawingToolBar, animationToolBar);
        return view;
    }


    /**
     * Creates all the components that should go in the view
     * This contains the tool bars, menu bars, canvas etc.
     * @param view
     * @param model
     * @param controller
     * @param zoomUI
     * @return
     */
    private PIPEComponents buildComponents(PipeApplicationView view, PipeApplicationModel model,
                                           PipeApplicationController controller, ZoomUI zoomUI) {
        ComponentEditorManager componentEditorManager = new ComponentEditorManager(controller);
        SimpleUndoListener undoListener =
                new SimpleUndoListener(componentEditorManager.redoAction, componentEditorManager.undoAction,
                        controller);
        ComponentCreatorManager componentCreatorManager = new ComponentCreatorManager(undoListener, model, controller);
        AnimateActionManager animateActionManager = new AnimateActionManager(model, controller);
        PetriNetEditorManager editorManager = new PetriNetEditorManager(view, controller);
        TokenActionManager tokenActionManager = new TokenActionManager(undoListener, model, controller, view);

        PrintAction printAction = new PrintAction();

        ExportPNGAction exportPNGAction = new ExportPNGAction();
        ExportTNAction exportTNAction = new ExportTNAction();
        ExportPSAction exportPSAction = new ExportPSAction();
        ImportAction importAction = new ImportAction();
        GridAction toggleGrid = new GridAction(controller);
        ZoomOutAction zoomOutAction = new ZoomOutAction(zoomUI);
        ZoomInAction zoomInAction = new ZoomInAction(zoomUI);
        SetZoomAction zoomAction = new SetZoomAction("Zoom", "Select zoom percentage ", "", controller, view);
        UnfoldAction unfoldAction = new UnfoldAction(controller);
        SelectAction selectAction = new SelectAction(model, view, controller);
        ExitAction exitAction = new ExitAction(view, controller);
        LayoutAction layoutAction = new LayoutAction(controller, view);
        ChooseTokenClassAction chooseTokenClassAction = new ChooseTokenClassAction(view, controller);
        return new PIPEComponents(chooseTokenClassAction, componentEditorManager, undoListener, componentCreatorManager,
                animateActionManager, editorManager, tokenActionManager, printAction, exportPNGAction, selectAction,
                exitAction, zoomAction, unfoldAction, zoomOutAction, zoomInAction, toggleGrid, importAction,
                exportPSAction, exportTNAction, layoutAction);
    }

    /**
     *
     * @param pipeComponents
     * @param view
     * @return tool bar involved in drawing and all its actions
     */
    private JToolBar getDrawingToolBar(PIPEComponents pipeComponents, PipeApplicationView view) {
        JToolBar drawingToolBar = new JToolBar();
        drawingToolBar.setFloatable(false);

        addButton(drawingToolBar, pipeComponents.selectAction);
        drawingToolBar.addSeparator();
        for (GuiAction action : pipeComponents.componentCreatorManager.getActions()) {
            addButton(drawingToolBar, action);
        }
        drawingToolBar.addSeparator();

        for (GuiAction action : pipeComponents.tokenActionManager.getActions()) {
            addButton(drawingToolBar, action);
        }

        addTokenClassComboBox(drawingToolBar, pipeComponents.chooseTokenClassAction, view);
        addButton(drawingToolBar, pipeComponents.unfoldAction);
        addButton(drawingToolBar, pipeComponents.layoutAction);
        drawingToolBar.addSeparator();
        return drawingToolBar;
    }

    /**
     *
     * @param pipeComponents
     * @return tool bar involved in animation and all its actions
     */
    private JToolBar getAnimationToolBar(PIPEComponents pipeComponents) {
        JToolBar animationToolBar = new JToolBar();
        animationToolBar.setFloatable(false);

        for (GuiAction action : pipeComponents.animateActionManager.getAnimateActions()) {
            addButton(animationToolBar, action);
        }
        animationToolBar.setVisible(false);
        return animationToolBar;
    }

    /**
     * @return the toolbar that holds actions for editing and creating Petri nets with PIPE
     */
    private JToolBar getToolBar(PipeApplicationView view, PIPEComponents pipeComponents, String[] examples,
                                JToolBar drawingToolBar, JToolBar animationToolBar) {


        JToolBar toolBar = new JToolBar();
        // Inhibit toolbar floating
        toolBar.setFloatable(false);


        for (GuiAction action : pipeComponents.editorManager.getActions()) {
            addButton(toolBar, action);
        }

        toolBar.addSeparator();
        addButton(toolBar, pipeComponents.printAction);
        toolBar.addSeparator();
        for (GuiAction action : pipeComponents.componentEditorManager.getActions()) {
            addButton(toolBar, action);
        }
        toolBar.addSeparator();

        addButton(toolBar, pipeComponents.zoomOutAction);
        addZoomComboBox(toolBar, pipeComponents.zoomAction, examples, view);
        addButton(toolBar, pipeComponents.zoomInAction);
        toolBar.addSeparator();
        addButton(toolBar, pipeComponents.toggleGrid);
        for (GuiAction action : pipeComponents.animateActionManager.getEditActions()) {
            addButton(toolBar, action);
        }


        toolBar.addSeparator();
        toolBar.add(drawingToolBar);


        toolBar.add(animationToolBar);

        toolBar.addSeparator();

        for (int i = 0; i < toolBar.getComponentCount(); i++) {
            toolBar.getComponent(i).setFocusable(false);
        }

        return toolBar;
    }

    /**
     *
     * @param pipeComponents
     * @param view
     * @param controller
     * @param zoomActions
     * @return PIPE menu with all its items
     */
    private JMenuBar buildMenu(PIPEComponents pipeComponents, PipeApplicationView view,
                               PipeApplicationController controller, String[] zoomActions) {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        for (GuiAction action : pipeComponents.editorManager.getActions()) {
            addMenuItem(fileMenu, action);
        }

        fileMenu.addSeparator();
        addMenuItem(fileMenu, pipeComponents.importAction);

        // Export menu


        JMenu exportMenu = new JMenu("Export");
        exportMenu.setIcon(new ImageIcon(getImageURL("Export")));
        addMenuItem(exportMenu, pipeComponents.exportPNGAction);
        addMenuItem(exportMenu, pipeComponents.exportPSAction);
        addMenuItem(exportMenu, pipeComponents.exportTNAction);
        fileMenu.add(exportMenu);
        fileMenu.addSeparator();
        addMenuItem(fileMenu, pipeComponents.printAction);
        fileMenu.addSeparator();

        // Example files menu
        JMenu exampleMenu = createExampleFileMenu(view, controller);

        fileMenu.add(exampleMenu);
        fileMenu.addSeparator();

        addMenuItem(fileMenu, pipeComponents.exitAction);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');

        for (GuiAction action : pipeComponents.componentEditorManager.getActions()) {
            addMenuItem(editMenu, action);
        }

        JMenu drawMenu = new JMenu("Draw");
        drawMenu.setMnemonic('D');
        addMenuItem(drawMenu, pipeComponents.selectAction);

        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        InputMap inputMap = view.getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");

        view.getRootPane().getActionMap().put("ESCAPE", pipeComponents.selectAction);

        drawMenu.addSeparator();
        for (GuiAction action : pipeComponents.componentCreatorManager.getActions()) {
            addMenuItem(drawMenu, action);
        }
        drawMenu.addSeparator();
        for (Action action : pipeComponents.tokenActionManager.getActions()) {
            addMenuItem(drawMenu, action);
        }
        addMenuItem(drawMenu, pipeComponents.unfoldAction);
        drawMenu.addSeparator();

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');

        JMenu zoomMenu = new JMenu("Zoom");
        zoomMenu.setIcon(new ImageIcon(getImageURL("Zoom")));
        addZoomMenuItems(zoomMenu, zoomActions);

        addMenuItem(viewMenu, pipeComponents.zoomOutAction);

        addMenuItem(viewMenu, pipeComponents.zoomInAction);
        viewMenu.add(zoomMenu);

        viewMenu.addSeparator();
        addMenuItem(viewMenu, pipeComponents.toggleGrid);

        JMenu animateMenu = new JMenu("Animate");
        animateMenu.setMnemonic('A');

        for (GuiAction action : pipeComponents.animateActionManager.getActions()) {
            addMenuItem(animateMenu, action);
        }

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');

        JMenuItem aboutItem = helpMenu.add("About PIPE");
        // Help - About is implemented
        aboutItem.addActionListener(view);
        // differently
        aboutItem.setIcon(new ImageIcon(getImageURL("About")));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(drawMenu);
        menuBar.add(animateMenu);
        menuBar.add(helpMenu);
        return menuBar;
    }

    /**
     * Action performed in the application view when tabs are changed
     * @param view
     * @param controller
     * @param pipeComponents
     * @param drawingToolBar
     * @param animationToolBar
     */
    private void setTabChangeListener(PipeApplicationView view, final PipeApplicationController controller,
                                      final PIPEComponents pipeComponents, final JToolBar drawingToolBar,
                                      final JToolBar animationToolBar) {
        view.setTabChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (controller.getActiveTab() != null) {
                    PetriNetController petriNetController = controller.getActivePetriNetController();
                    enableActions(pipeComponents, !petriNetController.isInAnimationMode(), drawingToolBar,
                            animationToolBar);
                }
            }
        });
    }

    /**
     * Listens to the pipe application model for changes in animation mode and toggles this change in the view
     * showing the animation tool bar instead of the drawing tool bar.
     *
     * It disables other tool bars that are visible.
     * @param pipeComponents
     * @param model
     * @param applicationController
     * @param drawingToolBar
     * @param animationToolBar
     */
    private void listenForAnimationMode(final PIPEComponents pipeComponents, final PipeApplicationModel model,
                                        final PipeApplicationController applicationController,
                                        final JToolBar drawingToolBar, final JToolBar animationToolBar) {
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PipeApplicationModel.TOGGLE_ANIMATION_MODE)) {
                    boolean oldMode = (boolean) evt.getOldValue();
                    boolean newMode = (boolean) evt.getNewValue();
                    if (oldMode != newMode) {
                        setAnimationMode(model, pipeComponents, drawingToolBar, animationToolBar, newMode);
                    }
                } else if (evt.getPropertyName().equals(PipeApplicationModel.TYPE_ACTION_CHANGE_MESSAGE)) {
                    PetriNetTab petriNetTab = applicationController.getActiveTab();
                    if (petriNetTab != null) {
                        petriNetTab.setCursorType("crosshair");
                        SelectionManager selectionManager =
                                applicationController.getActivePetriNetController().getSelectionManager();
                        selectionManager.disableSelection();
                    }
                }
            }
        });
    }

    /**
     * Add a button to the tool bar
     * @param toolBar tool bar to add the button to
     * @param action button to add to the tool bar
     */
    private void addButton(JToolBar toolBar, GuiAction action) {
        if (action.getValue("selected") != null) {
            toolBar.add(new ToggleButton(action));
        } else {
            toolBar.add(action);
        }
    }

    /**
     * Creates and adds the token view combo box to the view
     *
     * @param toolBar the JToolBar to add the combo box to
     * @param action  the action that the tokenClassComboBox performs when selected
     * @param view
     */
    private void addTokenClassComboBox(JToolBar toolBar, Action action, PipeApplicationView view) {
        String[] tokenClassChoices = new String[]{"Default"};
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>(tokenClassChoices);
        JComboBox<String> tokenClassComboBox = new JComboBox<>(model);
        tokenClassComboBox.setEditable(true);
        tokenClassComboBox.setSelectedItem(tokenClassChoices[0]);
        tokenClassComboBox.setMaximumRowCount(100);
        tokenClassComboBox.setEditable(false);
        tokenClassComboBox.setAction(action);
        view.register(tokenClassComboBox);
        toolBar.add(tokenClassComboBox);
    }

    /**
     * Adds a zoom combo box to the toolbar
     *
     * @param toolBar the JToolBar to add the button to
     * @param action  the action that the ZoomComboBox performs
     * @param view
     */
    private void addZoomComboBox(JToolBar toolBar, Action action, String[] zoomExamples, PipeApplicationView view) {
        Dimension zoomComboBoxDimension = new Dimension(65, 28);
        JComboBox<String> zoomComboBox = new JComboBox<>(zoomExamples);
        zoomComboBox.setEditable(true);
        zoomComboBox.setSelectedItem("100%");
        zoomComboBox.setMaximumRowCount(zoomExamples.length);
        zoomComboBox.setMaximumSize(zoomComboBoxDimension);
        zoomComboBox.setMinimumSize(zoomComboBoxDimension);
        zoomComboBox.setPreferredSize(zoomComboBoxDimension);
        zoomComboBox.setAction(action);
        view.registerZoom(zoomComboBox);
        toolBar.add(zoomComboBox);
    }

    /**
     * Adds the action to the menu item
     *
     * @param menu
     * @param action
     */
    private void addMenuItem(JMenu menu, Action action) {
        JMenuItem item = menu.add(action);
        KeyStroke keystroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);

        if (keystroke != null) {
            item.setAccelerator(keystroke);
        }
    }

    /**
     * @param name file name of image
     * @return path of image as URL
     */
    private URL getImageURL(String name) {
		PipeResourceLocator locator = new PipeResourceLocator(); 
		return locator.getImage(name);
    }

    /**
     * Creates an example file menu based on examples in resources/extras/examples
     */
    private JMenu createExampleFileMenu(PipeApplicationView view, PipeApplicationController controller) {
        if (isJar()) {
            try {
                return loadJarExamples(controller, view);
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, e.getMessage());
            }
        }
        JMenu exampleMenu = new JMenu("Examples");
        exampleMenu.setIcon(new ImageIcon(getImageURL("Example")));
		PipeResourceLocator locator = new PipeResourceLocator(); 
		URL examplesDirURL = locator.getExamplePath();
        try {
            URI uri = examplesDirURL.toURI();
            File directory = new File(uri);
            for (File entry : directory.listFiles()) {
                addMenuItem(exampleMenu, new ExampleFileAction(entry, view, controller));
            }
        } catch (URISyntaxException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }
        return exampleMenu;
    }

    /**
     * @param zoomMenu to add to the applications menu bar
     */
    private void addZoomMenuItems(JMenu zoomMenu, String[] zoomExamples) {
        int i = 0;
        for (String zoomExample : zoomExamples) {
            JMenuItem newItem = new JMenuItem(
                    new ZoomAction(zoomExample, "Select zoom percentage", i < 10 ? "ctrl shift " + i : ""));
            zoomMenu.add(newItem);
            i++;
        }
    }

    /**
     * Enable actions for edit or animation mode
     * @param pipeComponents
     * @param editMode true if in edit mode, false animation mode
     * @param drawingToolBar
     * @param animationToolBar
     */
    private void enableActions(PIPEComponents pipeComponents, boolean editMode, Component drawingToolBar,
                               Component animationToolBar) {
        if (editMode) {
            drawingToolBar.setVisible(true);
            animationToolBar.setVisible(false);
            pipeComponents.componentEditorManager.enableActions();
            pipeComponents.componentCreatorManager.enableActions();
            pipeComponents.tokenActionManager.enableActions();
            pipeComponents.editorManager.enableActions();
            pipeComponents.animateActionManager.disableActions();
        } else {
            drawingToolBar.setVisible(false);
            animationToolBar.setVisible(true);
            pipeComponents.componentEditorManager.disableActions();
            pipeComponents.componentCreatorManager.disableActions();
            pipeComponents.tokenActionManager.disableActions();
            pipeComponents.editorManager.disableActions();
            pipeComponents.animateActionManager.enableActions();
        }

        pipeComponents.selectAction.setEnabled(editMode);
    }

    /**
     * Sets the animation mode and enables the correct actions and tool bars
     * @param model
     * @param pipeComponents
     * @param drawingToolBar
     * @param animationToolBar
     * @param animateMode
     */
    public void setAnimationMode(PipeApplicationModel model, PIPEComponents pipeComponents, JToolBar drawingToolBar,
                                 JToolBar animationToolBar, boolean animateMode) {
        enableActions(pipeComponents, !animateMode, drawingToolBar, animationToolBar);
        model.setEditionAllowed(!animateMode);
    }

    /**
     * @return true if this class is loaded from within a jar
     */
    private boolean isJar() {
        CodeSource src = PipeApplicationView.class.getProtectionDomain().getCodeSource();
        URL jar = src.getLocation();
        return jar.getPath().endsWith(".jar");
    }

    /**
     * Loads the examples embedded within the PIPE jar appliaction.
     *
     * This method will be called if the uber-jar is running
     * @param controller
     * @param view
     * @return
     * @throws IOException
     */
    private JMenu loadJarExamples(PipeApplicationController controller, PipeApplicationView view) throws IOException {
        JMenu exampleMenu = new JMenu("Examples");

        CodeSource src = PipeApplicationView.class.getProtectionDomain().getCodeSource();
        if (src != null) {
            URL jar = src.getLocation();
            ZipInputStream zip = new ZipInputStream(jar.openStream());
            while (true) {
                ZipEntry e = zip.getNextEntry();
                if (e == null) {
                    break;
                }
                String name = e.getName();
                if (name.startsWith("foo/")) {
                    addMenuItem(exampleMenu, new ExampleFileAction(e, view, controller));
      /* Do something with this entry. */
                }
            }
        }
        return exampleMenu;
    }

    /**
     * Components needed to build pipe tool bars and menus
     */
    private static final class PIPEComponents {
        /**
         * Drop down token chooser
         */
        public final ChooseTokenClassAction chooseTokenClassAction;

        /**
         * Holds all actions for editing the Petri net
         */
        public final ComponentEditorManager componentEditorManager;

        /**
         * Listener for undo action creation
         */
        public final SimpleUndoListener undoListener;

        /**
         * Holds all actions for creating components
         */
        public final ComponentCreatorManager componentCreatorManager;

        /**
         * Holds all actions for animating the Petri net
         */
        public final AnimateActionManager animateActionManager;

        /**
         * Holds actions for io/delete etc of a Petri net and its components
         */
        public final PetriNetEditorManager editorManager;

        /**
         * Holds token editing actions
         */
        public final TokenActionManager tokenActionManager;

        /**
         * The print action
         */
        public final PrintAction printAction;

        /**
         * The export to PNG action
         */
        public final ExportPNGAction exportPNGAction;

        /**
         * Select action for selecting items on the currently displayed canvas
         */
        public final SelectAction selectAction;

        /**
         * Exit action, for quitting PIPE
         */
        public final ExitAction exitAction;

        /**
         * Zoom action for specifing the zoom in the drop down
         */
        public final SetZoomAction zoomAction;

        /**
         * Unfold action, for turning a colored Petri net into a single token Petri net
         */
        public final UnfoldAction unfoldAction;

        public final LayoutAction layoutAction;

        /**
         * Zoom out action
         */
        public final ZoomOutAction zoomOutAction;

        /**
         * Zoom in action
         */
        public final ZoomInAction zoomInAction;

        /**
         * Toggle the grid width and height
         */
        public final GridAction toggleGrid;

        /**
         * Import a module action
         */
        public final ImportAction importAction;

        /**
         * Export the Petri net to a postscript
         */
        public final ExportPSAction exportPSAction;

        /**
         * Export the Petri net to a TN
         */
        public final ExportTNAction exportTNAction;

        /**
         * Constructor
         * @param chooseTokenClassAction
         * @param componentEditorManager
         * @param undoListener
         * @param componentCreatorManager
         * @param animateActionManager
         * @param editorManager
         * @param tokenActionManager
         * @param printAction
         * @param exportPNGAction
         * @param selectAction
         * @param exitAction
         * @param zoomAction
         * @param unfoldAction
         * @param zoomOutAction
         * @param zoomInAction
         * @param toggleGrid
         * @param importAction
         * @param exportPSAction
         * @param exportTNAction
         */
        private PIPEComponents(ChooseTokenClassAction chooseTokenClassAction,
                               ComponentEditorManager componentEditorManager, SimpleUndoListener undoListener,
                               ComponentCreatorManager componentCreatorManager,
                               AnimateActionManager animateActionManager, PetriNetEditorManager editorManager,
                               TokenActionManager tokenActionManager, PrintAction printAction,
                               ExportPNGAction exportPNGAction, SelectAction selectAction, ExitAction exitAction,
                               SetZoomAction zoomAction, UnfoldAction unfoldAction, ZoomOutAction zoomOutAction,
                               ZoomInAction zoomInAction, GridAction toggleGrid, ImportAction importAction,
                               ExportPSAction exportPSAction, ExportTNAction exportTNAction, LayoutAction layoutAction) {
            this.chooseTokenClassAction = chooseTokenClassAction;
            this.componentEditorManager = componentEditorManager;
            this.undoListener = undoListener;
            this.componentCreatorManager = componentCreatorManager;
            this.animateActionManager = animateActionManager;
            this.editorManager = editorManager;
            this.tokenActionManager = tokenActionManager;
            this.printAction = printAction;
            this.exportPNGAction = exportPNGAction;
            this.selectAction = selectAction;
            this.exitAction = exitAction;
            this.zoomAction = zoomAction;
            this.unfoldAction = unfoldAction;
            this.zoomOutAction = zoomOutAction;
            this.zoomInAction = zoomInAction;
            this.toggleGrid = toggleGrid;
            this.importAction = importAction;
            this.exportPSAction = exportPSAction;
            this.exportTNAction = exportTNAction;
            this.layoutAction = layoutAction;
        }
    }
}


