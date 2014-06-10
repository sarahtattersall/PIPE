package pipe.views;

import pipe.actions.gui.PipeApplicationModel;
import pipe.actions.gui.ZoomManager;
import pipe.constants.GUIConstants;
import pipe.controllers.PetriNetController;
import pipe.controllers.SelectionManager;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.*;
import pipe.handlers.PetriNetMouseHandler;
import pipe.utilities.gui.GuiUtils;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentNotFoundException;
import uk.ac.imperial.pipe.models.manager.PetriNetManagerImpl;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.Token;
import uk.ac.imperial.pipe.models.petrinet.name.PetriNetName;

import javax.swing.*;
import javax.swing.border.BevelBorder;
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
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class PipeApplicationView extends JFrame implements ActionListener, Observer {


    private static final Logger LOGGER = Logger.getLogger(PipeApplicationView.class.getName());

    public final StatusBar statusBar;

    private final ZoomManager zoomManager;

    private final JSplitPane moduleAndAnimationHistoryFrame;

    private final JTabbedPane frameForPetriNetTabs = new JTabbedPane();

    private final List<PetriNetTab> petriNetTabs = new ArrayList<>();

    private final PipeApplicationController applicationController;

    private final PipeApplicationModel applicationModel;

    public JComboBox<String> zoomComboBox;

    public JComboBox<String> tokenClassComboBox;

    private UndoableEditListener undoListener;

    private JScrollPane scroller;

    private List<JLayer<JComponent>> wrappedPetrinetTabs = new ArrayList<>();

    private Map<PetriNetTab, AnimationHistoryView> histories = new HashMap<>();


    public PipeApplicationView(ZoomManager zoomManager, final PipeApplicationController applicationController,
                               PipeApplicationModel applicationModel) {
        this.zoomManager = zoomManager;

        this.applicationModel = applicationModel;
        this.applicationController = applicationController;
        applicationController.registerToManager(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals(PetriNetManagerImpl.NEW_PETRI_NET_MESSAGE)) {
                    PetriNet petriNet = (PetriNet) evt.getNewValue();
                    registerNewPetriNet(petriNet);
                } else if (evt.getPropertyName().equals(PetriNetManagerImpl.REMOVE_PETRI_NET_MESSAGE)) {
                    removeCurrentTab();
                }

            }
        });
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
                        SelectionManager selectionManager =
                                applicationController.getActivePetriNetController().getSelectionManager();
                        selectionManager.disableSelection();
                    }
                }
            }
        });
        setTitle(null);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (IllegalAccessException | ClassNotFoundException | InstantiationException | UnsupportedLookAndFeelException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }

        this.setIconImage(new ImageIcon(getImageURL("icon.png")).getImage());

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize(screenSize.width * 80 / 100, screenSize.height * 80 / 100);
        this.setLocationRelativeTo(null);

        // Status bar...
        statusBar = new StatusBar();
        getContentPane().add(statusBar, BorderLayout.PAGE_END);

        this.setForeground(java.awt.Color.BLACK);
        this.setBackground(java.awt.Color.WHITE);

        ModuleManager moduleManager = new ModuleManager(this, applicationController);
        JTree moduleTree = moduleManager.getModuleTree();
        moduleAndAnimationHistoryFrame = new JSplitPane(JSplitPane.VERTICAL_SPLIT, moduleTree, null);
        moduleAndAnimationHistoryFrame.setContinuousLayout(true);
        moduleAndAnimationHistoryFrame.setDividerSize(0);
        JSplitPane pane =
                new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, moduleAndAnimationHistoryFrame, frameForPetriNetTabs);
        pane.setContinuousLayout(true);
        pane.setOneTouchExpandable(true);
        // avoid multiple borders
        pane.setBorder(null);
        pane.setDividerSize(8);
        getContentPane().add(pane);

        setVisible(true);
        applicationModel.setMode(GUIConstants.SELECT);
        //TODO: DO YOU NEED TO DO THIS?
        //        selectAction.actionPerformed(null);

        setTabChangeListener();

        setZoomChangeListener();
    }

    public void setUndoListener(UndoableEditListener listener) {
        undoListener = listener;
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
                    setTitle(petriNetTab.getName());
                    applicationModel.setInAnimationMode(controller.isInAnimationMode());
                }
                refreshTokenClassChoices();
            }
        });
    }

    public void setTabChangeListener(ChangeListener listener) {
        frameForPetriNetTabs.addChangeListener(listener);
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
        ActionListener zoomComboListener = zoomComboBox.getActionListeners()[0];
        zoomComboBox.removeActionListener(zoomComboListener);

        String zoomPercentage = zoomManager.getPercentageZoom() + "%";
        zoomComboBox.setSelectedItem(zoomPercentage);
        zoomComboBox.addActionListener(zoomComboListener);
    }

    public void setAnimationMode(boolean animateMode) {
        if (animateMode) {
            statusBar.changeText(statusBar.TEXT_FOR_ANIMATION);
            createAnimationViewPane();

        } else {
            statusBar.changeText(statusBar.TEXT_FOR_DRAWING);
            removeAnimationViewPlane();
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
        scroller.setBorder(new EmptyBorder(0, 0, 0, 0));

        moduleAndAnimationHistoryFrame.setBottomComponent(scroller);

        moduleAndAnimationHistoryFrame.setDividerLocation(0.5);
        moduleAndAnimationHistoryFrame.setDividerSize(8);
    }

    public void setToolBar(JToolBar toolBar) {
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
     * Sets pipes menu
     *
     * @param menu
     */
    public void setMenu(JMenuBar menu) {
        setJMenuBar(menu);
    }

    private void setZoomChangeListener() {
        zoomManager.addPropertyChangeListener(new PropertyChangeListener() {
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
    public void setExitAction(WindowListener adapter) {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(adapter);
    }

    /**
     * Displays contributors
     */
    @Override
    public void actionPerformed(ActionEvent e) {

        JOptionPane.showMessageDialog(this, "PIPE: Platform Independent Petri Net Ediror\n\n" + "Authors:\n" +
                        "2003: Jamie Bloom, Clare Clark, Camilla Clifford, Alex Duncan, Haroun Khan and Manos Papantoniou\n"
                        +
                        "2004: Tom Barnwell, Michael Camacho, Matthew Cook, Maxim Gready, Peter Kyme and Michail Tsouchlaris\n"
                        +
                        "2005: Nadeem Akharware\n" + "????: Tim Kimber, Ben Kirby, Thomas Master, Matthew Worthington\n"
                        +
                        "????: Pere Bonet Bonet (Universitat de les Illes Balears)\n" +
                        "????: Marc Meli\u00E0 Aguil\u00F3 (Universitat de les Illes Balears)\n" +
                        "2010: Alex Charalambous (Imperial College London)\n" +
                        "2011: Jan Vlasak (Imperial College London)\n\n" + "http://pipe2.sourceforge.net/",
                "About PIPE", JOptionPane.INFORMATION_MESSAGE
        );
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
    //TODO: ADD ZOOMING
    public void addNewTab(String name, PetriNetTab tab) {

        JScrollPane tabScroller = new JScrollPane(tab, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        tabScroller.setBorder(new BevelBorder(BevelBorder.LOWERED));

        //        JLayer<JComponent> jLayer = new JLayer<>(tab, zoomUI);
        //        wrappedPetrinetTabs.add(jLayer);

        petriNetTabs.add(tab);
        frameForPetriNetTabs.addTab(name, tabScroller);
        frameForPetriNetTabs.setSelectedIndex(petriNetTabs.size() - 1);
    }

    public File getFile() {
        PetriNetTab petriNetTab = petriNetTabs.get(frameForPetriNetTabs.getSelectedIndex());
        return petriNetTab.appFile;
    }

    public void removeCurrentTab() {
        removeTab(frameForPetriNetTabs.getSelectedIndex());
    }

    public void removeTab(int index) {
        if (frameForPetriNetTabs.getTabCount() > 0) {
            petriNetTabs.remove(index);
            applicationController.setActiveTab(petriNetTabs.get(index - 1));
            frameForPetriNetTabs.remove(index);
        }
    }

    public void updateSelectedTabName(String title) {
        int index = frameForPetriNetTabs.getSelectedIndex();
        frameForPetriNetTabs.setTitleAt(index, title);
    }

    public void registerNewPetriNet(PetriNet petriNet) {


        petriNet.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String msg = evt.getPropertyName();
                if (msg.equals(PetriNet.PETRI_NET_NAME_CHANGE_MESSAGE)) {
                    PetriNetName name = (PetriNetName) evt.getNewValue();
                    updateSelectedTabName(name.getName());
                } else if (msg.equals(PetriNet.NEW_TOKEN_CHANGE_MESSAGE) || msg.equals(
                        PetriNet.DELETE_TOKEN_CHANGE_MESSAGE)) {
                    refreshTokenClassChoices();
                }
            }
        });

        AnimationHistoryView animationHistoryView;
        try {
            animationHistoryView = new AnimationHistoryView("Animation History");
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }
        PetriNetTab petriNetTab = new PetriNetTab();
        histories.put(petriNetTab, animationHistoryView);

        PropertyChangeListener zoomListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                updateZoomCombo();
            }
        };
        applicationController.registerTab(petriNet, petriNetTab, animationHistoryView, undoListener, zoomListener);
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        petriNetTab.setMouseHandler(
                new PetriNetMouseHandler(applicationModel, petriNetController, petriNetTab));
        petriNetTab.updatePreferredSize();

        addNewTab(petriNet.getNameValue(), petriNetTab);
    }

    private URL getImageURL(String name) {
        return this.getClass().getResource(PIPEConstants.IMAGE_PATH + name);
    }

    public void register(JComboBox<String> tokenClassComboBox) {
        this.tokenClassComboBox = tokenClassComboBox;
    }

    public void registerZoom(JComboBox<String> zoomComboBox) {
        this.zoomComboBox = zoomComboBox;
    }
}

