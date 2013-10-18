package pipe.views;

import pipe.actions.ExampleFileAction;
import pipe.actions.GuiAction;
import pipe.actions.ZoomAction;
import pipe.controllers.PipeApplicationController;
import pipe.gui.*;
import pipe.gui.widgets.FileBrowser;
import pipe.historyActions.HistoryManager;
import pipe.io.JarUtilities;
import pipe.models.PipeApplicationModel;
import pipe.utilities.transformers.PNMLTransformer;
import pipe.utilities.transformers.TNTransformer;
import pipe.utilities.writers.PNMLWriter;

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
//
// Steve Doubleday (Sept 2013):  refactored to simplify testing of TokenSetController changes

public class PipeApplicationView extends JFrame implements ActionListener, Observer,Serializable
{
    public final StatusBar statusBar;
    private JToolBar animationToolBar, drawingToolBar;
    public JComboBox zoomComboBox;
    public JComboBox tokenClassComboBox;
    private HelpBox helpAction;

    private final JSplitPane _moduleAndAnimationHistoryFrame;
    private static JScrollPane _scroller;

    private final JTabbedPane _frameForPetriNetTabs = new JTabbedPane();
    private final ArrayList<PetriNetTab> _petriNetTabs;
    
    private static AnimationHistory _animationHistory;
    private final Animator _animator;

    private final PipeApplicationController _applicationController;
    private final PipeApplicationModel _applicationModel;
    /**
     * Constructor for unit testing only
     * @author stevedoubleday (Oct 2013)
     */
	public PipeApplicationView()
	{
		statusBar = null;
		_moduleAndAnimationHistoryFrame = null;
		_petriNetTabs = null; 
		_animator = null;
		_applicationController = null; 	
		_applicationModel = null; 
	}
	public PipeApplicationView(PipeApplicationController applicationController, PipeApplicationModel applicationModel)
    {
        ApplicationSettings.register(this);
        _applicationController = applicationController;
        _applicationModel = applicationModel;
        _applicationModel.registerObserver(this);
        _petriNetTabs = new ArrayList<PetriNetTab>();
        setTitle(null);
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch(Exception exc)
        {
            System.err.println("Error loading L&F: " + exc);
        }

        this.setIconImage(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(ApplicationSettings.getImagePath() + "icon.png")).getImage());

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

        _animator = new Animator();
        setTab();

        ModuleManager moduleManager = new ModuleManager();
        JTree moduleTree = moduleManager.getModuleTree();
        _moduleAndAnimationHistoryFrame = new JSplitPane(JSplitPane.VERTICAL_SPLIT, moduleTree, null);
        _moduleAndAnimationHistoryFrame.setContinuousLayout(true);
        _moduleAndAnimationHistoryFrame.setDividerSize(0);
        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, _moduleAndAnimationHistoryFrame, _frameForPetriNetTabs);
        pane.setContinuousLayout(true);
        pane.setOneTouchExpandable(true);
        pane.setBorder(null); // avoid multiple borders
        pane.setDividerSize(8);
        getContentPane().add(pane);

        setVisible(true);
        _applicationModel.setMode(Constants.SELECT);
        _applicationModel.selectAction.actionPerformed(null);


        //_applicationController.createNewPetriNet();
        createNewTab(null, false);
    }

    public JTabbedPane getFrameForPetriNetTabs() {
        return _frameForPetriNetTabs;
    }


    public int numberOfTabs()
    {
        return _petriNetTabs.size();
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
    private void buildMenus()
    {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');

        addMenuItem(fileMenu, _applicationModel.createAction);
        addMenuItem(fileMenu, _applicationModel.openAction);
        addMenuItem(fileMenu, _applicationModel.closeAction);
        fileMenu.addSeparator();
        addMenuItem(fileMenu, _applicationModel.saveAction);
        addMenuItem(fileMenu, _applicationModel.saveAsAction);

        fileMenu.addSeparator();
        addMenuItem(fileMenu, _applicationModel.importAction);
        // Export menu
        JMenu exportMenu = new JMenu("Export");
        exportMenu.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(ApplicationSettings.getImagePath() + "Export.png")));
        addMenuItem(exportMenu, _applicationModel.exportPNGAction);
        addMenuItem(exportMenu, _applicationModel.exportPSAction);
        addMenuItem(exportMenu, _applicationModel.exportTNAction);
        fileMenu.add(exportMenu);
        fileMenu.addSeparator();
        addMenuItem(fileMenu, _applicationModel.printAction);
        fileMenu.addSeparator();

        // Example files menu
        try
        {
            URL examplesDirURL = Thread.currentThread().getContextClassLoader().getResource(ApplicationSettings.getExamplesDirectoryPath()+ System.getProperty("file.separator"));

            if(JarUtilities.isJarFile(examplesDirURL))
            {

                JarFile jarFile = new JarFile(JarUtilities.getJarName(examplesDirURL));

                ArrayList<JarEntry> nets = JarUtilities.getJarEntries(jarFile, ApplicationSettings.getExamplesDirectoryPath());

                Arrays.sort(nets.toArray(), new Comparator()
                {
                    public int compare(Object one, Object two)
                    {
                        return ((JarEntry) one).getName().compareTo(
                                ((JarEntry) two).getName());
                    }
                });

                if(nets.size() > 0)
                {
                    JMenu exampleMenu = new JMenu("Examples");
                    exampleMenu.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(ApplicationSettings.getImagePath() + "Example.png")));
                    int index = 0;
                    for(JarEntry net : nets)
                    {
                        if(net.getName().toLowerCase()
                                .endsWith(".xml"))
                        {
                            addMenuItem(exampleMenu, new ExampleFileAction(net, (index < 10) ? ("ctrl " + index) : null));
                            index++;
                        }
                    }
                    fileMenu.add(exampleMenu);
                    fileMenu.addSeparator();
                }
            }
            else
            {
                File examplesDir = new File(examplesDirURL.toURI());
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
                if(index > 0)
                {
                    StringBuffer sb = new StringBuffer(dirURLString);
                    sb.replace(index, index + 1, "%20");
                    dirURLString = sb.toString();
                }

                examplesDir = new File(new URI(dirURLString));

                File[] nets = examplesDir.listFiles();

                Arrays.sort(nets, new Comparator()
                {
                    public int compare(Object one, Object two)
                    {
                        return ((File) one).getName().compareTo(
                                ((File) two).getName());
                    }
                });

                // Oliver Haggarty - fixed code here so that if folder contains
                // non
                // .xml file the Example x counter is not incremented when that
                // file
                // is ignored
                if(nets.length > 0)
                {
                    JMenu exampleMenu = new JMenu("Examples");
                    exampleMenu.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(ApplicationSettings.getImagePath() + "Example.png")));
                    int k = 0;
                    for(File net : nets)
                    {
                        if(net.getName().toLowerCase().endsWith(".xml"))
                        {
                            addMenuItem(exampleMenu, new ExampleFileAction(net, (k < 10) ? "ctrl " + (k++) : null));
                        }
                    }
                    fileMenu.add(exampleMenu);
                    fileMenu.addSeparator();
                }
            }
        }
        catch(Exception e)
        {
            System.err.println("Error getting example files:" + e);
            e.printStackTrace();
        }

        addMenuItem(fileMenu, _applicationModel.exitAction);

        JMenu editMenu = new JMenu("Edit");
        editMenu.setMnemonic('E');
        addMenuItem(editMenu, _applicationModel.undoAction);
        addMenuItem(editMenu, _applicationModel.redoAction);
        editMenu.addSeparator();
        addMenuItem(editMenu, _applicationModel.cutAction);
        addMenuItem(editMenu, _applicationModel.copyAction);
        addMenuItem(editMenu, _applicationModel.pasteAction);
        addMenuItem(editMenu, _applicationModel.deleteAction);

        JMenu drawMenu = new JMenu("Draw");
        drawMenu.setMnemonic('D');
        addMenuItem(drawMenu, _applicationModel.selectAction);

        KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
        InputMap inputMap = rootPane
                .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(stroke, "ESCAPE");

        rootPane.getActionMap().put("ESCAPE", _applicationModel.selectAction);

        drawMenu.addSeparator();
        addMenuItem(drawMenu, _applicationModel.placeAction);
        addMenuItem(drawMenu, _applicationModel.transAction);
        addMenuItem(drawMenu, _applicationModel.timedtransAction);
        addMenuItem(drawMenu, _applicationModel.arcAction);
        addMenuItem(drawMenu, _applicationModel.inhibarcAction);
        addMenuItem(drawMenu, _applicationModel.annotationAction);
        drawMenu.addSeparator();
        addMenuItem(drawMenu, _applicationModel.tokenAction);
        addMenuItem(drawMenu, _applicationModel.deleteTokenAction);
        addMenuItem(drawMenu, _applicationModel.specifyTokenClasses);
        addMenuItem(drawMenu, _applicationModel.groupTransitions);
        addMenuItem(drawMenu, _applicationModel.ungroupTransitions);
        addMenuItem(drawMenu, _applicationModel.unfoldAction);
        drawMenu.addSeparator();
        addMenuItem(drawMenu, _applicationModel.rateAction );

        JMenu viewMenu = new JMenu("View");
        viewMenu.setMnemonic('V');

        JMenu zoomMenu = new JMenu("Zoom");
        zoomMenu.setIcon(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(ApplicationSettings.getImagePath() + "Zoom.png")));
        addZoomMenuItems(zoomMenu);

        addMenuItem(viewMenu, _applicationModel.zoomOutAction);

        addMenuItem(viewMenu, _applicationModel.zoomInAction);
        viewMenu.add(zoomMenu);

        viewMenu.addSeparator();
        addMenuItem(viewMenu, _applicationModel.toggleGrid);
        addMenuItem(viewMenu, _applicationModel.dragAction);

        JMenu animateMenu = new JMenu("Animate");
        animateMenu.setMnemonic('A');
        addMenuItem(animateMenu, _applicationModel.startAction);
        animateMenu.addSeparator();
        addMenuItem(animateMenu, _applicationModel.stepbackwardAction);
        addMenuItem(animateMenu, _applicationModel.stepforwardAction);
        addMenuItem(animateMenu, _applicationModel.randomAction);
        addMenuItem(animateMenu, _applicationModel.randomAnimateAction);

        JMenu helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('H');
        helpAction = new HelpBox("Help", "View documentation", "F1", "index.htm");
        addMenuItem(helpMenu, helpAction);

        JMenuItem aboutItem = helpMenu.add("About PIPE");
        aboutItem.addActionListener(this); // Help - About is implemented
        // differently

        URL iconURL = Thread.currentThread().getContextClassLoader().getResource(ApplicationSettings.getImagePath()+ "About.png");
        if(iconURL != null)
            aboutItem.setIcon(new ImageIcon(iconURL));

        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(viewMenu);
        menuBar.add(drawMenu);
        menuBar.add(animateMenu);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

    }

    private void buildToolbar()
    {
        // Create the toolbar
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);// Inhibit toolbar floating

        addButton(toolBar, _applicationModel.createAction);
        addButton(toolBar, _applicationModel.openAction);
        addButton(toolBar, _applicationModel.saveAction);
        addButton(toolBar, _applicationModel.saveAsAction);
        addButton(toolBar, _applicationModel.closeAction);
        toolBar.addSeparator();
        addButton(toolBar, _applicationModel.printAction);
        toolBar.addSeparator();
        addButton(toolBar, _applicationModel.cutAction);
        addButton(toolBar, _applicationModel.copyAction);
        addButton(toolBar, _applicationModel.pasteAction);
        addButton(toolBar, _applicationModel.deleteAction);
        addButton(toolBar, _applicationModel.undoAction);
        addButton(toolBar, _applicationModel.redoAction);
        toolBar.addSeparator();

        addButton(toolBar, _applicationModel.zoomOutAction);
        addZoomComboBox(toolBar, _applicationModel.zoomAction = new ZoomAction("Zoom", "Select zoom percentage ", ""));
        addButton(toolBar, _applicationModel.zoomInAction);
        toolBar.addSeparator();
        addButton(toolBar, _applicationModel.toggleGrid);
        addButton(toolBar, _applicationModel.dragAction);
        addButton(toolBar, _applicationModel.startAction);

        drawingToolBar = new JToolBar();
        drawingToolBar.setFloatable(false);

        toolBar.addSeparator();
        addButton(drawingToolBar, _applicationModel.selectAction);
        drawingToolBar.addSeparator();
        addButton(drawingToolBar, _applicationModel.placeAction);// Add Draw Menu Buttons
        addButton(drawingToolBar, _applicationModel.transAction);
        addButton(drawingToolBar, _applicationModel.timedtransAction);
        addButton(drawingToolBar, _applicationModel.arcAction);
        addButton(drawingToolBar, _applicationModel.inhibarcAction);
        addButton(drawingToolBar, _applicationModel.annotationAction);
        drawingToolBar.addSeparator();
        addButton(drawingToolBar, _applicationModel.tokenAction);
        addButton(drawingToolBar, _applicationModel.deleteTokenAction);
        addTokenClassComboBox(drawingToolBar,_applicationModel.chooseTokenClassAction);
        addButton(drawingToolBar, _applicationModel.specifyTokenClasses);
        addButton(drawingToolBar, _applicationModel.groupTransitions);
        addButton(drawingToolBar, _applicationModel.ungroupTransitions);
        addButton(drawingToolBar, _applicationModel.unfoldAction);
        drawingToolBar.addSeparator();
        addButton(drawingToolBar, _applicationModel.rateAction);

        toolBar.add(drawingToolBar);

        animationToolBar = new JToolBar();
        animationToolBar.setFloatable(false);
        addButton(animationToolBar, _applicationModel.stepbackwardAction);
        addButton(animationToolBar, _applicationModel.stepforwardAction);
        addButton(animationToolBar, _applicationModel.randomAction);
        addButton(animationToolBar, _applicationModel.randomAnimateAction);

        toolBar.add(animationToolBar);
        animationToolBar.setVisible(false);

        toolBar.addSeparator();
        addButton(toolBar, helpAction);

        for(int i = 0; i < toolBar.getComponentCount(); i++)
        {
            toolBar.getComponent(i).setFocusable(false);
        }

        getContentPane().add(toolBar, BorderLayout.PAGE_START);
    }

    private void addButton(JToolBar toolBar, GuiAction action)
    {

        if(action.getValue("selected") != null)
        {
            toolBar.add(new ToggleButton(action));
        }
        else
        {
            toolBar.add(action);
        }
    }

    /**
     * @param - the menu to add the submenu to
     * @author Ben Kirby Takes the method of setting up the Zoom menu out of the
     * main buildMenus method.
     * @param zoomMenu
     */
    private void addZoomMenuItems(JMenu zoomMenu)
    {
        for(ZoomAction zoomAction : _applicationModel.getZoomActions())
        {
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
    private void addZoomComboBox(JToolBar toolBar, Action action)
    {
        Dimension zoomComboBoxDimension = new Dimension(65, 28);
        String[] zoomExamples = _applicationModel.getZoomExamples();
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
     * @author	Steve Doubleday (Sept 2013): refactored to simplify testing
     *   Initially populated with a single option:  "Default"  
     */
    protected void addTokenClassComboBox(JToolBar toolBar, Action action)
    {
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
	protected String[] buildTokenClassChoices()
	{
		LinkedList<TokenView> tokenViews = getCurrentPetriNetView().getTokenViews();
		int size = tokenViews.size(); 
		String[] tokenClassChoices = new String[size];
		for (int i = 0; i < size; i++)
		{
			tokenClassChoices[i] = tokenViews.get(i).getID();
		}
		return tokenClassChoices;
	}

    private void addMenuItem(JMenu menu, Action action)
    {
        JMenuItem item = menu.add(action);
        KeyStroke keystroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);

        if(keystroke != null)
        {
            item.setAccelerator(keystroke);
        }
    }

    /* sets all buttons to enabled or disabled according to status. */
    public void enableActions(boolean status)
    {
        if(status)
        {
            drawingToolBar.setVisible(true);
            animationToolBar.setVisible(false);
        }

        if(!status)
        {
            drawingToolBar.setVisible(false);
            animationToolBar.setVisible(true);
        }
    }

    public void setObjectsNull(int index)
    {
        removeTab(index);
    }

    // set tabbed pane properties and add change listener that updates tab with
    // linked model and view
    void setTab()
    {
        _frameForPetriNetTabs.addChangeListener(new ChangeListener()
        {
            public void stateChanged(ChangeEvent e)
            {
                CopyPasteManager copyPasteManager = _applicationController.getCopyPasteManager();
                if(copyPasteManager.pasteInProgress())
                    copyPasteManager.cancelPaste();

                PetriNetView _petriNetView = getCurrentPetriNetView();
                PetriNetTab _appView = getCurrentTab();

                if(_appView != null)
                {
                    _appView.setVisible(true);
                    _appView.repaint();
                    updateZoomCombo();

                    _applicationModel.enableActions(!_appView.isInAnimationMode(), _applicationController.isPasteEnabled());

                    setTitle(getCurrentTab().getName());
                }
                else
                    setTitle(null);

                if(_petriNetView != null)
                    refreshTokenClassChoices();
            }

        });
    }

    public void actionPerformed(ActionEvent e)
    {

        JOptionPane.showMessageDialog(this,
                        "PIPE: Platform Independent Petri Net Ediror\n\n"
                                + "Authors:\n"
                                + "2003: Jamie Bloom, Clare Clark, Camilla Clifford, Alex Duncan, Haroun Khan and Manos Papantoniou\n"
                                + "2004: Tom Barnwell, Michael Camacho, Matthew Cook, Maxim Gready, Peter Kyme and Michail Tsouchlaris\n"
                                + "2005: Nadeem Akharware\n"
                                + "????: Tim Kimber, Ben Kirby, Thomas Master, Matthew Worthington\n"
                                + "????: Pere Bonet Bonet (Universitat de les Illes Balears)\n"
                                + "????: Marc Meli\u00E0 Aguil\u00F3 (Universitat de les Illes Balears)\n"
                                + "2010: Alex Charalambous (Imperial College London)\n"
                                + "2011: Jan Vlasak (Imperial College London)\n\n"
                                + "http://pipe2.sourceforge.net/",
                        "About PIPE", JOptionPane.INFORMATION_MESSAGE);
    }

    public void update(Observable o, Object obj)
    {
        PetriNetTab currentTab = getCurrentTab();
        if((_applicationModel.getMode() != Constants.CREATING) && (!currentTab.isInAnimationMode()))
        {
            currentTab.setNetChanged(true);
        }
    }

    public void saveOperation(boolean forceSaveAs)
    {
    	
        if(getCurrentTab() == null)
        {
            return;
        }
        boolean saveFunctional = false;
        if(getCurrentPetriNetView().hasFunctionalRatesOrWeights()){
    		if (JOptionPane.showConfirmDialog(null, "This net has functional rates or weights expressions. \r\n"
    				+ "Saving these expression will not allow this PNML file compatible with other tools. \r\n" +
    				"Press 'yes' to save them anyway. Press 'no' to save their constant values", "Request", 
    			    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)
    			    == JOptionPane.YES_OPTION)
    			{
    				saveFunctional=true;
    			}
    			else
    			{
    				saveFunctional=false;
    			}
    	}
        File modelFile = getFile();
        if(!forceSaveAs && modelFile != null)
        {
            saveNet(modelFile,saveFunctional);
        }
        else
        {
            String path;
            if(modelFile != null)
            {
                path = modelFile.toString();
            }
            else
            {
                path = _frameForPetriNetTabs.getTitleAt(_frameForPetriNetTabs.getSelectedIndex());
            }
            String filename = new FileBrowser(path).saveFile();
            if(filename != null)
            {
                saveNet(new File(filename),saveFunctional);
            }
        }
    }
    // Steve Doubleday:  public to simplify testing
    public void saveNet(File outFile, boolean saveFunctional)
    {
        try
        {
            PNMLWriter saveModel = new PNMLWriter(getCurrentPetriNetView());
            saveModel.saveTo(outFile, saveFunctional);

            setFile(outFile, _frameForPetriNetTabs.getSelectedIndex());
            PetriNetTab currentTab = getCurrentTab();
            currentTab.setNetChanged(false);
            String name = outFile.getName().split(".xml")[0];
            _frameForPetriNetTabs.setTitleAt(_frameForPetriNetTabs.getSelectedIndex(), name);
            setTitle(outFile.getName());
            currentTab.getHistoryManager().clear();
            _applicationModel.undoAction.setEnabled(false);
            _applicationModel.redoAction.setEnabled(false);
        }
        catch(Exception e)
        {
            System.err.println(e);
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, e.toString(),
                                          "File Output Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void createNewTab(File file, boolean isTN)
    {
         int freeSpace = _applicationController.addEmptyPetriNetTo(_petriNetTabs);;

        String name = "";
        if(_applicationController.isPasteInProgress())
        {
            _applicationController.cancelPaste();
        }

        PetriNetView petriNetView = getPetriNetView(freeSpace);
        PetriNetTab petriNetTab = getTab(freeSpace);

        petriNetView.addObserver(petriNetTab); // Add the view as Observer
        petriNetView.addObserver(this); // Add the app window as
        // observer

        if(file == null)
        {
            name = "Petri net " + (_applicationModel.newPetriNetNumber());
        }
        else
        {
        	setFile(file, freeSpace);
            name = file.getName().split(".xml")[0];
        }

        petriNetTab.setNetChanged(false); // Status is unchanged

        JScrollPane scroller = new JScrollPane(petriNetTab);
        // make it less bad on XP
        scroller.setBorder(new BevelBorder(BevelBorder.LOWERED));
        _frameForPetriNetTabs.addTab(name, null, scroller, null);
        _frameForPetriNetTabs.setSelectedIndex(freeSpace);

        petriNetTab.updatePreferredSize();
        
        // CH Aug 2, 2012: Shifted this to the bottom, so that it
        // will wait until the tab has been created before loading
        // data. This was causing a crash when no tabs were open.
        if( file != null ){
        	try
            {
                // BK 10/02/07: Changed loading of PNML to accomodate new
                // PNMLTransformer class
                if(isTN)
                {
                    TNTransformer transformer = new TNTransformer();
                    petriNetView.createFromPNML(transformer.transformTN(file.getPath()));
                }
                else
                {
                    // ProgressBar pb = new ProgressBar("test");
                    PNMLTransformer transformer = new PNMLTransformer();
                    petriNetView.createFromPNML(transformer.transformPNML(file
                                                                                  .getPath()));
                    petriNetTab.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
                }

                
            }
            catch(Exception e)
            {
                JOptionPane.showMessageDialog(this,
                                              "Error loading file:\n" + name + "\n"
                                                      + e.toString(), "File load error",
                                              JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
                return;
            }
        }
        refreshTokenClassChoices(); // Steve Doubleday: ensure combo box reflects tokens that were loaded
        setTitle(name);// Change the program caption
        _frameForPetriNetTabs.setTitleAt(freeSpace, name);
        _applicationModel.selectAction.actionPerformed(null);
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
    public boolean checkForSave()
    {
        if(getCurrentTab().getNetChanged())
        {
            int result = JOptionPane.showConfirmDialog(this,
                                                       "Current file has changed. Save current file?",
                                                       "Confirm Save Current File",
                                                       JOptionPane.YES_NO_CANCEL_OPTION,
                                                       JOptionPane.WARNING_MESSAGE);
            switch(result)
            {
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
    public boolean checkForSaveAll()
    {
        // Loop through all tabs and check if they have been saved
        for(int counter = 0; counter < _frameForPetriNetTabs.getTabCount(); counter++)
        {
            _frameForPetriNetTabs.setSelectedIndex(counter);
            if(!checkForSave())
            {
                return false;
            }
        }
        return true;
    }

    public void setRandomAnimationMode(boolean on)
    {
        if(!on)
        {
            _applicationModel.stepforwardAction.setEnabled(getAnimationHistory()
                                                 .isStepForwardAllowed());
            _applicationModel.stepbackwardAction.setEnabled(getAnimationHistory()
                                                  .isStepBackAllowed());
        }
        else
        {
            _applicationModel.stepbackwardAction.setEnabled(false);
            _applicationModel.stepforwardAction.setEnabled(false);
        }
        _applicationModel.randomAction.setEnabled(!on);
        _applicationModel.randomAnimateAction.setSelected(on);
    }

    public void setAnimationMode(boolean on)
    {
        _applicationModel.randomAnimateAction.setSelected(false);
        getAnimator().setNumberSequences(0);
        _applicationModel.startAction.setSelected(on);
        getCurrentTab().changeAnimationMode(on);
        if(on)
        {
            getAnimator().storeModel();
            getCurrentPetriNetView().setEnabledTransitions();
            getAnimator().highlightEnabledTransitions();
            addAnimationHistory();
            _applicationModel.enableActions(false, _applicationController.isPasteEnabled());// disables all non-animation buttons
            _applicationModel.setEditionAllowed(false);
            statusBar.changeText(statusBar.textforAnimation);
        }
        else
        {
            _applicationModel.setEditionAllowed(true);
            statusBar.changeText(statusBar.textforDrawing);
            getAnimator().restoreModel();
            removeAnimationHistory();
            _applicationModel.enableActions(true, _applicationController.isPasteEnabled()); // renables all non-animation buttons
        }
        getAnimator().updateArcAndTran();
        
    }

    public void setTitle(String title)
    {
        String name = _applicationModel.getName();
        super.setTitle((title == null) ? name : name + ": " + title);
    }


    /**
     * @author Ben Kirby Remove the listener from the zoomComboBox, so that when
     * the box's selected item is updated to keep track of ZoomActions
     * called from other sources, a duplicate ZoomAction is not called
     */
    public void updateZoomCombo()
    {
        ActionListener zoomComboListener = (zoomComboBox.getActionListeners())[0];
        zoomComboBox.removeActionListener(zoomComboListener);
        zoomComboBox.setSelectedItem(String.valueOf(getCurrentTab().getZoomController().getPercent())
                                             + "%");
        zoomComboBox.addActionListener(zoomComboListener);
    }

    public StatusBar getStatusBar()
    {
        return statusBar;
    }

    private Component c = null; // arreglantzoom
    private final Component p = new BlankLayer();

    /* */
    public void hideNet(boolean doHide)
    {
        if(doHide)
        {
            c = _frameForPetriNetTabs.getComponentAt(_frameForPetriNetTabs.getSelectedIndex());
            _frameForPetriNetTabs.setComponentAt(_frameForPetriNetTabs.getSelectedIndex(), p);
        }
        else
        {
            if(c != null)
            {
                _frameForPetriNetTabs.setComponentAt(_frameForPetriNetTabs.getSelectedIndex(), c);
                c = null;
            }
        }
        _frameForPetriNetTabs.repaint();
    }

    /* This method can be used for simulating button clicks during testing
    *
    */
    public void executeAction(String action)
    {
        if(action.equals("toggleAnimation"))
        {
            _applicationModel.startAction.actionPerformed(null);
        }
        else if(action.equals("groupTransitionsAction"))
        {
            _applicationModel.groupTransitions.actionPerformed(null);
        }
        else if(action.equals("ungroupTransitionsAction"))
        {
            _applicationModel.ungroupTransitions.actionPerformed(null);
        }
        else if(action.equals("exit"))
        {
            dispose();
            System.exit(0);
        }
    }
    /**
     * Refreshes the combo box that presents the Tokens available for use. 
     */
    // stevedoubleday (Sept 2013):  refactored as part of TokenSetController implementation 
    public void refreshTokenClassChoices()
    {
        getCurrentPetriNetView().setActiveTokenView(getCurrentPetriNetView().getTokenViews().get(0));
        String[] tokenClassChoices = buildTokenClassChoices();
        DefaultComboBoxModel model = new DefaultComboBoxModel(tokenClassChoices);
        tokenClassComboBox.setModel(model);
    }



    public void removeTab(int index)
    {
        _petriNetTabs.remove(index);
    }


    /**
     * Creates a new animationHistory text area, and returns a reference to it
     */
    void addAnimationHistory()
    {
        try
        {
            _animationHistory = new AnimationHistory("Animation history\n");
            _animationHistory.setEditable(false);

            _scroller = new JScrollPane(_animationHistory);
            _scroller.setBorder(new EmptyBorder(0, 0, 0, 0)); // make it less bad on XP

            _moduleAndAnimationHistoryFrame.setBottomComponent(_scroller);

            _moduleAndAnimationHistoryFrame.setDividerLocation(0.5);
            _moduleAndAnimationHistoryFrame.setDividerSize(8);
        }
        catch(javax.swing.text.BadLocationException be)
        {
            be.printStackTrace();
        }
    }


    void removeAnimationHistory()
    {
        if(_scroller != null)
        {
            _moduleAndAnimationHistoryFrame.remove(_scroller);
            _moduleAndAnimationHistoryFrame.setDividerLocation(0);
            _moduleAndAnimationHistoryFrame.setDividerSize(0);
        }
    }

    public AnimationHistory getAnimationHistory()
    {
        return _animationHistory;
    }


    public PetriNetTab getCurrentTab()
    {
        if(_frameForPetriNetTabs == null)
            return null;
        return getTab(_frameForPetriNetTabs.getSelectedIndex());
    }

    public HistoryManager getCurrentHistoryManager()
    {
        return getCurrentTab().getHistoryManager();
    }

    PetriNetTab getTab(int index)
    {
        if(index < 0 || index >= _petriNetTabs.size())
            return null;
        return _petriNetTabs.get(index);
    }

    public PetriNetView getCurrentPetriNetView()
    {
        if(_frameForPetriNetTabs == null)
            return null;
        return getPetriNetView(_frameForPetriNetTabs.getSelectedIndex());
    }


    PetriNetView getPetriNetView(int index)
    {
        if(index < 0 || index >= _petriNetTabs.size())
            return null;
        return _petriNetTabs.get(index)._petriNetView;
    }


    public  File getFile()
    {
        PetriNetTab petriNetTab = _petriNetTabs.get(_frameForPetriNetTabs.getSelectedIndex());
        return petriNetTab._appFile;
    }

    void setFile(File modelfile, int fileNo)
    {
        if(fileNo >= _petriNetTabs.size())
            return;
        PetriNetTab petriNetTab = _petriNetTabs.get(fileNo);
        petriNetTab._appFile = modelfile;
    }

    public Animator getAnimator()
    {
        return _animator;
    }
}

