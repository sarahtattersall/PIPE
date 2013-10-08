/**
 * QueryFrame
 * 
 * - Contains methods for the initialisation of the main Query Editor interface frame
 * - Manages Zoom functionality
 * - Sets up button and action listeners and handlers
 * - Enables tab manipulation, such as saving, creating and closing
 * 
 * @author Tamas Suto 
 * @date   15/04/07
 */

package pipe.modules.queryeditor.gui;

import pipe.gui.ApplicationSettings;
import pipe.gui.Grid;
import pipe.gui.HelpBox;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.*;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.*;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.labels.StateLabelManager;
import pipe.modules.queryeditor.io.*;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.*;

public class QueryEditor extends JFrame implements QueryConstants, ActionListener, Observer
{

	private final String			frameTitle;

	private QueryData				queryData;
	private QueryEditor				queryEditor;
	private QueryView				queryView;
	private JTabbedPane				drawingCanvas;
	private JMenuBar				menuBar;
	private final QueryStatusBar	statusBar;

	private int						newNameCounter	= 1;
	private final Map				actions			= new HashMap();
	private final String[]			zoomExamples	= {"40%",
			"60%",
			"80%",
			"100%",
			"120%",
			"140%",
			"160%",
			"180%",
			"200%",
			"300%",
			"350%",
			"400%"									};
	private JComboBox				zoomComboBox;

    public QueryEditor(final String title) {

        ApplicationSettings applicationSettings = new ApplicationSettings();
		String osName = System.getProperty("os.name").toLowerCase();
		try
		{
			if (osName.indexOf("windows") > -1)
			{
				// it's a Windows system, so safe to use the system L&F
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			else
			{
				// Unix or MacOS - Need to use cross-platform L&F, since the
				// system L&F
				// can be dodgy in Ubuntu and in Mac OS
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			}
		}
		catch (UnsupportedLookAndFeelException e)
		{
			System.err.println("Unsupported L&F Exception: " + e);
		}
		catch (ClassNotFoundException e)
		{
			System.err.println("L&F CLass Not Found Exception: " + e);
		}
		catch (InstantiationException e)
		{
			System.err.println("L&F Instantiation Exception: " + e);
		}
		catch (IllegalAccessException e)
		{
			System.err.println("Illegal L&F Access Exception: " + e);
		}
		catch (Exception e)
		{
			System.err.println("Error loading L&F: " + e);
		}

		// Basic frame setup
		this.setIconImage(new ImageIcon(Thread.currentThread().getContextClassLoader().getResource(ApplicationSettings.getImagePath() + "icon.png")).getImage());

		this.frameTitle = title;
		setTitle(null);
		Dimension minFrameSize = new Dimension(QueryManager.minFrameWitdh, QueryManager.minFrameHeight);
		Dimension prefFrameSize = new Dimension(QueryManager.prefFrameWidth, QueryManager.prefFrameHeight);
		this.setMinimumSize(minFrameSize);
		this.setPreferredSize(prefFrameSize);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		// Add elements to frame
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		initialiseActions();
		buildMenus();
		buildToolbar();
		this.statusBar = new QueryStatusBar();
		// getContentPane().add(statusBar, BorderLayout.PAGE_END);
		addWindowListener(new WindowHandler());
	}

	/**
	 * This is where all actions are introduced. Some of them are available as
	 * both menu entries and toolbar buttons, while some are only available in
	 * the query builder toolbar. The actions are only created and added to a
	 * HashMap. Other methods take care of making them accessible to the user.
	 */
	private void initialiseActions()
	{
		// Menubar and Toolbar actions
		this.actions.put("New", new FileAction("New", "Create new query", "ctrl N"));
		this.actions.put("Open", new FileAction("Open", "Open existing query", "ctrl O"));
		this.actions.put("Close", new FileAction("Close", "Close current query", "ctrl F4"));
		this.actions.put("Save", new FileAction("Save", "Save query", "ctrl S"));
		this.actions.put("SaveAs", new FileAction("Save As", "Save query as", "F12"));
		this.actions.put("PNG", new FileAction("PNG", "Export query to PNG format", ""));
		this.actions.put("EPS", new FileAction("EPS", "Export query to EPS format", ""));
		this.actions.put("Print", new FileAction("Print", "Print query", "ctrl P"));
		this.actions.put("Exit", new FileAction("Exit", "Close query editor", "alt F4"));
		this.actions.put("Help", new HelpBox("Help", "Help", "F1", "index.htm"));

		// Toolbar actions
		this.actions.put("Grid", new GridAction("Cycle Grid", "Change grid size", "G"));
		this.actions.put("Zoom", new ZoomAction("Zoom", "Select zoom percentage ", ""));
		this.actions.put("ZoomIn", new ZoomAction("Zoom In", "Zoom in by 10% ", "ctrl +"));
		this.actions.put("ZoomOut", new ZoomAction("Zoom Out", "Zoom out by 10% ", "ctrl -"));
		this.actions.put("Delete", new DeleteAction("Delete", "Delete selection", "DELETE"));
		this.actions.put("Draw Drag", new TypeAction("Drag", QueryConstants.DRAG, "Drag query", "DRAG"));
		Action select = new TypeAction("Select", QueryConstants.SELECT, "Select query components", "S");
		select.putValue("default", Boolean.TRUE);
		this.actions.put("Draw Select", select);
		this.actions.put("State Label Manager", new LabelAction("State Label Manager",
																"Create and edit state labels",
																null));
		this.actions.put("Macro Manager", new MacroAction("Macro Manager", "Create and edit macros", null));
		this.actions.put("Evaluate Query", new EvaluateAction(	"Evaluate Query",
																"Submit performance query for evaluation",
																null));
		this.actions.put("Settings", new SettingsAction("Settings", "Set query evaluation preferences", null));

		// Query Builder toolbar actions

		// operation nodes
		String tooltip = ResultNode.getTooltip();
		this.actions.put("Draw Result", new TypeAction("Result", QueryConstants.RESULT_NODE, tooltip, ""));

		tooltip = SequentialNode.getTooltip();
		this.actions.put("Draw Sequential", new TypeAction(	"Sequential",
															QueryConstants.SEQUENTIAL_NODE,
															tooltip,
															""));

		tooltip = PassageTimeDensityNode.getTooltip();
		this.actions.put("Draw PassageTimeDensity", new TypeAction(	"PassageTimeDensity",
																	QueryConstants.PASSAGETIMEDENSITY_NODE,
																	tooltip,
																	""));

		tooltip = DistributionNode.getTooltip();
		this.actions.put("Draw Distribution", new TypeAction(	"Distribution",
																QueryConstants.DISTRIBUTION_NODE,
																tooltip,
																""));

		tooltip = ConvolutionNode.getTooltip();
		this.actions.put("Draw Convolution", new TypeAction("Convolution",
															QueryConstants.CONVOLUTION_NODE,
															tooltip,
															""));

		tooltip = ProbInIntervalNode.getTooltip();
		this.actions.put("Draw ProbInInterval", new TypeAction(	"ProbInInterval",
																QueryConstants.PROBININTERVAL_NODE,
																tooltip,
																""));

		tooltip = ProbInStatesNode.getTooltip();
		this.actions.put("Draw ProbInStates", new TypeAction(	"ProbInStates",
																QueryConstants.PROBINSTATES_NODE,
																tooltip,
																""));

		tooltip = MomentNode.getTooltip();
		this.actions.put("Draw Moment", new TypeAction("Moment", QueryConstants.MOMENT_NODE, tooltip, ""));

		tooltip = PercentileNode.getTooltip();
		this.actions.put("Draw Percentile", new TypeAction(	"Percentile",
															QueryConstants.PERCENTILE_NODE,
															tooltip,
															""));

		tooltip = FiringRateNode.getTooltip();
		this.actions.put("Draw FiringRate", new TypeAction(	"FiringRate",
															QueryConstants.FIRINGRATE_NODE,
															tooltip,
															""));

		tooltip = SteadyStateProbNode.getTooltip();
		this.actions.put("Draw SteadyStateProb", new TypeAction("SteadyStateProb",
																QueryConstants.STEADYSTATEPROB_NODE,
																tooltip,
																""));

		tooltip = SteadyStateStatesNode.getTooltip();
		this.actions.put("Draw SteadyStateStates", new TypeAction(	"SteadyStateStates",
																	QueryConstants.STEADYSTATESTATES_NODE,
																	tooltip,
																	""));

		tooltip = StatesAtTimeNode.getTooltip();
		this.actions.put("Draw StatesAtTime", new TypeAction(	"StatesAtTime",
																QueryConstants.STATESATTIME_NODE,
																tooltip,
																""));

		tooltip = InIntervalNode.getTooltip();
		this.actions.put("Draw InInterval", new TypeAction(	"InInterval",
															QueryConstants.ININTERVAL_NODE,
															tooltip,
															""));

		tooltip = SubsetNode.getTooltip();
		this.actions.put("Draw Subset", new TypeAction("Subset", QueryConstants.SUBSET_NODE, tooltip, ""));

		tooltip = DisconNode.getTooltip();
		this.actions.put("Draw Discon", new TypeAction("Discon", QueryConstants.DISCON_NODE, tooltip, ""));

		tooltip = NegationNode.getTooltip();
		this.actions.put("Draw Negation", new TypeAction(	"Negation",
															QueryConstants.NEGATION_NODE,
															tooltip,
															""));

		tooltip = ArithCompNode.getTooltip();
		this.actions.put("Draw ArithComp", new TypeAction(	"ArithComp",
															QueryConstants.ARITHCOMP_NODE,
															tooltip,
															""));

		tooltip = ArithOpNode.getTooltip();
		this.actions.put("Draw ArithOp", new TypeAction("ArithOp", QueryConstants.ARITHOP_NODE, tooltip, ""));

		tooltip = RangeNode.getTooltip();
		this.actions.put("Draw Range", new TypeAction("Range", QueryConstants.RANGE_NODE, tooltip, ""));

		// value nodes
		tooltip = StatesNode.getTooltip();
		this.actions.put("Draw States", new TypeAction("States", QueryConstants.STATES_NODE, tooltip, ""));

		tooltip = ActionsNode.getTooltip();
		this.actions.put("Draw Actions", new TypeAction("Actions", QueryConstants.ACTIONS_NODE, tooltip, ""));

		tooltip = NumNode.getTooltip();
		this.actions.put("Draw Num", new TypeAction("Num", QueryConstants.NUM_NODE, tooltip, ""));

		tooltip = BoolNode.getTooltip();
		this.actions.put("Draw Bool", new TypeAction("Bool", QueryConstants.BOOL_NODE, tooltip, ""));

		tooltip = StateFunctionNode.getTooltip();
		this.actions.put("Draw StateFunction", new TypeAction(	"StateFunction",
																QueryConstants.STATEFUNCTION_NODE,
																tooltip,
																""));

		tooltip = MacroNode.getTooltip();
		this.actions.put("Draw Macro", new TypeAction("Macro", QueryConstants.MACRO_NODE, tooltip, ""));
	}

	public void currentTabSetEnabled(final boolean enabled)
	{
		this.drawingCanvas.getSelectedComponent().setEnabled(enabled);
	}

	/**
	 * This method takes the instantiated actions and makes them accessible via
	 * a menu bar
	 */
	private void buildMenus()
	{
		this.menuBar = new JMenuBar();

		// File
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		addMenuItem(fileMenu, ((Action) this.actions.get("New")));
		addMenuItem(fileMenu, ((Action) this.actions.get("Open")));
		addMenuItem(fileMenu, ((Action) this.actions.get("Close")));
		fileMenu.addSeparator();
		addMenuItem(fileMenu, ((Action) this.actions.get("Save")));
		addMenuItem(fileMenu, ((Action) this.actions.get("SaveAs")));

		// File->QueryExporter
		JMenu exportMenu = new JMenu("Export");
		exportMenu.setIcon(new ImageIcon(Thread	.currentThread()
												.getContextClassLoader()
												.getResource(QueryManager.imgPath + "Export.png")));
		addMenuItem(exportMenu, ((Action) this.actions.get("EPS")));
		addMenuItem(exportMenu, ((Action) this.actions.get("PNG")));
		fileMenu.add(exportMenu);

		// File
		fileMenu.addSeparator();
		addMenuItem(fileMenu, ((Action) this.actions.get("Print")));
		fileMenu.addSeparator();
		addMenuItem(fileMenu, ((Action) this.actions.get("Exit")));

		// Edit
		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		addMenuItem(editMenu, ((Action) this.actions.get("Delete")));

		// View
		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');
		addMenuItem(viewMenu, ((Action) this.actions.get("Grid")));

		// View->Zoom
		JMenu zoomMenu = new JMenu("Zoom");
		zoomMenu.setMnemonic('Z');
		zoomMenu.setIcon(new ImageIcon(Thread	.currentThread()
												.getContextClassLoader()
												.getResource(QueryManager.imgPath + "Zoom.png")));
		addZoomMenuItems(zoomMenu);
		viewMenu.add(zoomMenu);

		// Tools
		JMenu toolsMenu = new JMenu("Tools");
		toolsMenu.setMnemonic('T');
		addMenuItem(toolsMenu, ((Action) this.actions.get("State Label Manager")));
		addMenuItem(toolsMenu, ((Action) this.actions.get("Macro Manager")));

		// Analysis
		JMenu analysisMenu = new JMenu("Analysis");
		analysisMenu.setMnemonic('A');
		addMenuItem(analysisMenu, ((Action) this.actions.get("Settings")));
		addMenuItem(analysisMenu, ((Action) this.actions.get("Evaluate Query")));

		// Help
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		addMenuItem(helpMenu, ((Action) this.actions.get("Help")));

		this.menuBar.add(fileMenu);
		this.menuBar.add(editMenu);
		this.menuBar.add(viewMenu);
		this.menuBar.add(toolsMenu);
		this.menuBar.add(analysisMenu);
		this.menuBar.add(helpMenu);
		setJMenuBar(this.menuBar);
	}

	private void addMenuItem(final JMenu menu, final Action action)
	{
		JMenuItem item = menu.add(action);
		KeyStroke keystroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
		if (keystroke != null)
			item.setAccelerator(keystroke);
	}

	private void addZoomMenuItems(final JMenu zoomMenu)
	{
		for (int i = 0; i <= this.zoomExamples.length - 1; i++)
		{
			JMenuItem newItem = new JMenuItem(new ZoomAction(	this.zoomExamples[i],
																"Select zoom percentage",
																""));
			zoomMenu.add(newItem);
		}
	}

	/**
	 * This method makes the actions accessible through a toolbar, which is
	 * essentially a shortcut.
	 */
	private void buildToolbar()
	{
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.add((Action) this.actions.get("New"));
		toolBar.add((Action) this.actions.get("Open"));
		toolBar.add((Action) this.actions.get("Save"));
		toolBar.add((Action) this.actions.get("SaveAs"));
		toolBar.add((Action) this.actions.get("Close"));
		toolBar.addSeparator();
		toolBar.add((Action) this.actions.get("Print"));
		toolBar.addSeparator();
		toolBar.add((Action) this.actions.get("Grid"));
		toolBar.addSeparator();
		toolBar.add((Action) this.actions.get("ZoomIn"));
		toolBar.add((Action) this.actions.get("ZoomOut"));
		addZoomComboBox(toolBar, (Action) this.actions.get("Zoom"));
		toolBar.addSeparator();
		toolBar.add((Action) this.actions.get("Help"));
		toolBar.addSeparator();
		toolBar.add((Action) this.actions.get("Evaluate Query"));

		getContentPane().add(toolBar, BorderLayout.PAGE_START);
	}

	/** This instantiates the Query Builder buttons
     * @return*/
	public JToolBar[] getQueryBuilderToolbars()
	{
        JToolBar toolBar1 = new JToolBar();
		toolBar1.setRollover(true);
		toolBar1.setFloatable(false);
        JToolBar toolBar2 = new JToolBar();
		toolBar2.setRollover(true);
		toolBar2.setFloatable(false);

		ButtonGroup drawButtons = new ButtonGroup();
		drawButtons.add(addIntelligentButton(toolBar1, (Action) this.actions.get("Draw Select")));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) this.actions.get("Draw Drag")));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) this.actions.get("Draw Sequential")));
		drawButtons.add(addIntelligentButton(toolBar1,
												(Action) this.actions.get("Draw PassageTimeDensity")));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) this.actions.get("Draw Convolution")));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) this.actions.get("Draw Distribution")));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) this.actions.get("Draw Percentile")));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) this.actions.get("Draw SteadyStateProb")));
		drawButtons.add(addIntelligentButton(toolBar1,
												(Action) this.actions.get("Draw SteadyStateStates")));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) this.actions.get("Draw StatesAtTime")));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) this.actions.get("Draw ProbInInterval")));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) this.actions.get("Draw ProbInStates")));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) this.actions.get("Draw Moment")));
		drawButtons.add(addIntelligentButton(toolBar1, (Action) this.actions.get("Draw FiringRate")));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw InInterval")));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw Subset")));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw Negation")));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw Discon")));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw ArithComp")));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw ArithOp")));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw Num")));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw Range")));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw Actions")));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw States")));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw StateFunction")));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw Bool")));
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw Macro")));

		JToolBar[] toolbars = new JToolBar[2];
		toolbars[0] = toolBar1;
		toolbars[1] = toolBar2;
		return toolbars;
	}

	/**
	 * Creates a button that can keep in synch with its associated action i.e.
	 * will be automatically pressed if the equivalent menu option is clicked
	 * The new button is added to the "toolBar" parameter
	 * 
	 *            the JToolBar to add the button to
	 * @param toolbar
     * @param action -
	 *            the action that the button should perform
	 * @return
	 */
	private AbstractButton addIntelligentButton(final JToolBar toolbar, final Action action)
	{
		URL selectedIconURL = (URL) action.getValue("selectedIconURL");
		ImageIcon selectedIcon = new ImageIcon(selectedIconURL);

		final AbstractButton b = new JToggleButton(action);
		b.setText(null);
		b.setSelectedIcon(selectedIcon);
		String actionName = (String) action.getValue(Action.NAME);
		b.setActionCommand(actionName);

		ActionListener actionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String actionName = e.getActionCommand();
				String msg = "";
				if (actionName.equals("Select"))
				{
					msg = QueryManager.addColouring("Select individual objects, arc endpoints or groups of objects.");
				}
				else if (actionName.equals("Drag"))
				{
					msg = QueryManager.addColouring("Drag around the contents of the canvas by holding down the mouse "
													+ "button and moving the mouse.");
				}
				else if (actionName.equals("Sequential"))
				{
					msg = SequentialNode.getNodeInfo();
				}
				else if (actionName.equals("PassageTimeDensity"))
				{
					msg = PassageTimeDensityNode.getNodeInfo();
				}
				else if (actionName.equals("Distribution"))
				{
					msg = DistributionNode.getNodeInfo();
				}
				else if (actionName.equals("Convolution"))
				{
					msg = ConvolutionNode.getNodeInfo();
				}
				else if (actionName.equals("ProbInInterval"))
				{
					msg = ProbInIntervalNode.getNodeInfo();
				}
				else if (actionName.equals("ProbInStates"))
				{
					msg = ProbInStatesNode.getNodeInfo();
				}
				else if (actionName.equals("Moment"))
				{
					msg = MomentNode.getNodeInfo();
				}
				else if (actionName.equals("Percentile"))
				{
					msg = PercentileNode.getNodeInfo();
				}
				else if (actionName.equals("FiringRate"))
				{
					msg = FiringRateNode.getNodeInfo();
				}
				else if (actionName.equals("SteadyStateProb"))
				{
					msg = SteadyStateProbNode.getNodeInfo();
				}
				else if (actionName.equals("SteadyStateStates"))
				{
					msg = SteadyStateStatesNode.getNodeInfo();
				}
				else if (actionName.equals("StatesAtTime"))
				{
					msg = StatesAtTimeNode.getNodeInfo();
				}
				else if (actionName.equals("InInterval"))
				{
					msg = InIntervalNode.getNodeInfo();
				}
				else if (actionName.equals("Subset"))
				{
					msg = SubsetNode.getNodeInfo();
				}
				else if (actionName.equals("Discon"))
				{
					msg = DisconNode.getNodeInfo();
				}
				else if (actionName.equals("Negation"))
				{
					msg = NegationNode.getNodeInfo();
				}
				else if (actionName.equals("ArithComp"))
				{
					msg = ArithCompNode.getNodeInfo();
				}
				else if (actionName.equals("ArithOp"))
				{
					msg = ArithOpNode.getNodeInfo();
				}
				else if (actionName.equals("Range"))
				{
					msg = RangeNode.getNodeInfo();
				}
				else if (actionName.equals("States"))
				{
					msg = StatesNode.getNodeInfo();
				}
				else if (actionName.equals("Actions"))
				{
					msg = ActionsNode.getNodeInfo();
				}
				else if (actionName.equals("Num"))
				{
					msg = NumNode.getNodeInfo();
				}
				else if (actionName.equals("Bool"))
				{
					msg = BoolNode.getNodeInfo();
				}
				else if (actionName.equals("StateFunction"))
				{
					msg = StateFunctionNode.getNodeInfo();
				}
				else if (actionName.equals("Macro"))
				{
					msg = MacroNode.getNodeInfo();
				}
				QueryManager.writeToInfoBox(msg);
			}
		};

		b.addActionListener(actionListener);
		toolbar.add(b);

		action.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(final PropertyChangeEvent pce)
			{
				if (pce.getPropertyName().equals("selected"))
				{
					b.setSelected(((Boolean) pce.getNewValue()).booleanValue());
				}
			}
		});
		return b;
	}

	/**
	 * Just takes the long-winded method of setting up the ComboBox out of the
	 * main buildToolbar method. Could be adapted for generic addition of
	 * comboboxes.
	 * 
	 * @param toolBar
	 *            the JToolBar to add the button to
	 * @param action
	 *            the action that the ZoomComboBox performs
	 */
	private void addZoomComboBox(final JToolBar toolBar, final Action action)
	{
		this.zoomComboBox = new JComboBox(this.zoomExamples);
		this.zoomComboBox.setEditable(true);
		this.zoomComboBox.setSelectedItem("100%");
		this.zoomComboBox.setMaximumRowCount(this.zoomExamples.length);
		this.zoomComboBox.setMaximumSize(new Dimension(75, 28));
		this.zoomComboBox.setAction(action);
		toolBar.add(this.zoomComboBox);
	}

	public void actionPerformed(final ActionEvent e)
	{
		// not used for anything at the moment, but has to be present
	}

	public QueryStatusBar getStatusBar()
	{
		return this.statusBar;
	}

	/**
	 * Set tabbed pane properties and add change listener that updates tab with
	 * linked model and view
	 */
	public void setTab()
	{
		this.drawingCanvas = QueryManager.getTabs();
		this.drawingCanvas.addChangeListener(new ChangeListener()
		{
			public void stateChanged(final ChangeEvent e)
			{
				// update and refresh selected tab
				int index = QueryEditor.this.drawingCanvas.getSelectedIndex();
				setObjects(index);
				if (QueryEditor.this.queryView != null)
				{
					QueryEditor.this.queryView.setVisible(true);
					QueryEditor.this.queryView.repaint();
					updateZoomCombo();
					setTitle(QueryEditor.this.drawingCanvas.getTitleAt(index));
				}
				else
				{
					setTitle(null);
				}
			}
		});
		this.queryEditor = QueryManager.getEditor();
	}

	/** Set current objects in Frame */
	public void setObjects()
	{
		this.queryData = QueryManager.getData();
		this.queryView = QueryManager.getView();
	}

	/**
	 * Set frame objects based on which tab and hence which model is selected
	 * 
	 * @param index -
	 *            which tab
	 */
    private void setObjects(final int index)
	{
		this.queryData = QueryManager.getData(index);
		this.queryView = QueryManager.getView(index);
	}

	private void setObjectsNull(final int index)
	{
		QueryManager.removeTab(index);
	}

	public void update(final Observable o, final Object obj)
	{
		if (QueryManager.getMode() != QueryConstants.LOADING)
			this.queryView.queryChanged = true;
	}

	private void saveOperation(boolean forceSaveAs)
	{
		if (this.queryView == null)
			return;
		File queryFile = QueryManager.getFile();
		if (!forceSaveAs && queryFile != null)
		{
			// ordinary save
			saveQuery(queryFile);
		}
		else
		{
			// save as
			String path = null;
			if (queryFile != null)
				path = queryFile.toString();
			else path = this.drawingCanvas.getTitleAt(this.drawingCanvas.getSelectedIndex());
			String filename = new QueryFileBrowser(path).saveFile();
			if (filename != null)
				saveQuery(new File(filename));
		}
	}

	private void saveQuery(final File outFile)
	{
		try
		{
			QueryWriter.saveQuery(outFile);
			QueryManager.setFile(outFile, this.drawingCanvas.getSelectedIndex());
			this.queryView.queryChanged = false;
			this.drawingCanvas.setTitleAt(this.drawingCanvas.getSelectedIndex(), outFile.getName());
			setTitle(outFile.getName()); // Change the window title
		}
		catch (Exception e)
		{
			System.err.println(e);
			JOptionPane.showMessageDialog(	QueryEditor.this,
											e.toString(),
											"File Output Error",
											JOptionPane.ERROR_MESSAGE);
        }
	}

	/**
	 * Creates a new tab with the selected file, or a new file if filename is
	 * null
	 * 
	 * @param filename -
	 *            filename of query to load, or null to create a new empty tab
	 */
	public void createNewTab(final String filename)
	{
		// add a new tab in QueryManager and set up its data
		int index = QueryManager.addTab();
		setObjects(index);
		this.queryData.addObserver(this.queryView); // Add the view as Observer
		this.queryData.addObserver(this.queryEditor); // Add the app window as
		// observer
		JScrollPane scroller = new JScrollPane(this.queryView);
		scroller.setBorder(new BevelBorder(BevelBorder.LOWERED)); // make it
		// less bad
		// on XP
		this.drawingCanvas.addTab("", null, scroller, null);
		this.drawingCanvas.setSelectedIndex(index);
		String name = "";

		if (filename == null)
		{
			name = "New Query " + this.newNameCounter++ + ".xml";
			QueryData.queryName = name;
		}
		else
		{
			try
			{
				File inFile = new File(filename);
				QueryTransformer transformer = new QueryTransformer();
				this.queryData = QueryLoader.loadQueryFromXML(	transformer.transformPTML(inFile.getPath()),
																this.queryData);
				//
				queryData.printQueryDataContents();
				//
				QueryData.queryName = inFile.getName();
				QueryManager.getData(index).updateData(this.queryData);
				QueryManager.setFile(inFile, index);
				name = inFile.getName();
			}
			catch (Exception e)
			{
				JOptionPane.showMessageDialog(	QueryEditor.this,
												"Error loading file:\n" + filename + "\nGuru meditation:\n" +
												e.toString(),
												"File load error",
												JOptionPane.ERROR_MESSAGE);
				e.printStackTrace();
				return;
			}
		}
		ViewExpansionComponent expand = new ViewExpansionComponent(getWidth(), getHeight());
		expand.addZoomController(this.queryView.getZoomController());
		this.queryView.add(expand);
		this.queryView.repaint();
		this.queryView.queryChanged = false; // Status is unchanged

		// Change the program caption
		setTitle(name);
		this.drawingCanvas.setTitleAt(index, name);

		// Set select mode
		Action a = (Action) this.actions.get("Draw Select");
		QueryManager.setMode(QueryConstants.SELECT);
		a.actionPerformed(null);

		if (filename != null)
		{
			this.queryView.updatePreferredSize();
		}
	}

	/**
	 * If current net has modifications, asks if you want to save and does it if
	 * you want.
	 * 
	 * @return true if handled, false if cancelled
	 */
    private boolean checkForSave()
	{
		if (this.queryView.queryChanged)
		{
			int result = JOptionPane.showConfirmDialog(	QueryEditor.this,
														"Current file has changed. Save current file?",
														"Confirm Save Current File",
														JOptionPane.YES_NO_CANCEL_OPTION,
														JOptionPane.WARNING_MESSAGE);
			switch (result)
			{
				case JOptionPane.YES_OPTION :
					saveOperation(false);
					break;
				case JOptionPane.CLOSED_OPTION :
				case JOptionPane.CANCEL_OPTION :
					return false;
			}
		}
		return true;
	}

	/**
	 * On frame close, loop through all tabs and check if they've been saved
     * @return
     */
    private boolean checkForSaveAll()
	{
		for (int counter = 0; counter < this.drawingCanvas.getTabCount(); counter++)
		{
			this.drawingCanvas.setSelectedIndex(counter);
			if (!checkForSave())
				return false;
		}
		return true;
	}

	/**
	 * Remove the listener from the zoomComboBox, so that when the box's
	 * selected item is updated to keep track of ZoomActions called from other
	 * sources, a duplicate ZoomAction is not called
	 */
    private void updateZoomCombo()
	{
		ActionListener zoomComboListener = (this.zoomComboBox.getActionListeners())[0];
		this.zoomComboBox.removeActionListener(zoomComboListener);
		this.zoomComboBox.setSelectedItem(String.valueOf(this.queryView.getZoomController().getPercent()) +
											"%");
		this.zoomComboBox.addActionListener(zoomComboListener);
	}

	/**
	 * Updates the value of the "selected" property in all drawing related
	 * Action objects when a new one is selected
	 * 
	 * @param selected
	 *            the newly selected Action
	 */
	private void resetDrawingActions(final Object selected)
	{
		Set actionNames = this.actions.keySet();
		Iterator iter = actionNames.iterator();
		while (iter.hasNext())
		{
			String nextActionKey = (String) iter.next();
			QueryAction nextAction = null;
			if (nextActionKey.startsWith("Draw"))
			{
				// drawing-related actions, which are all JToggleButtons
				nextAction = (QueryAction) this.actions.get(nextActionKey);
				if (nextAction != selected)
				{
					nextAction.setSelected(false);
				}
			}
		}
	}

	/**
	 * This method refreshes the query tree's display on the canvas
	 */
	private void repaintQueryTree()
	{
		QueryView canvas = QueryManager.getView();
		Component[] treeObjects = canvas.getComponents();
        for(Component treeObject : treeObjects)
        {
            if(treeObject instanceof PerformanceTreeNode)
            {
                PerformanceTreeNode aNode = (PerformanceTreeNode) treeObject;
                aNode.updateBounds();
                aNode.updateConnected();
            }
            else if(treeObject instanceof PerformanceTreeArc)
            {
                PerformanceTreeArc anArc = (PerformanceTreeArc) treeObject;
                anArc.updateArcPosition();
                anArc.updateLabelPosition();
            }
        }
		canvas.updatePreferredSize();
		canvas.repaint();
	}

	public void disableGuiMenu()
	{
		this.menuBar.setEnabled(false);
	}

	public void enableGuiMenu()
	{
		this.menuBar.setEnabled(true);
	}

	@Override
	public void setTitle(final String title)
	{
		if (title == null)
			super.setTitle(this.frameTitle);
		else super.setTitle(this.frameTitle + "  ---  " + title);
	}

	private class WindowHandler extends WindowAdapter
	{
		// Handler for window closing event
		@Override
		public void windowClosing(final WindowEvent e)
		{
			// clear data when closing, in case we launch the module again
			QueryManager.resetQueryEditor();
			dispose();
		}
	}

	class FileAction extends QueryAction
	{

		FileAction(final String name, final String tooltip, final String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(final ActionEvent e)
		{
			String actionName = (String) getValue(Action.NAME);
			if (actionName.equals("Save"))
				saveOperation(false);
			else if (actionName.equals("Save As"))
				saveOperation(true);
			else if (actionName.equals("Open"))
			{
				File filePath = new QueryFileBrowser(QueryManager.userPath).openFile();
				if (filePath != null && filePath.exists() && filePath.isFile() && filePath.canRead())
				{
					QueryManager.userPath = filePath.getParent();
					createNewTab(filePath.toString());
					QueryEditor.this.queryView.getSelectionObject().enableSelection();
					QueryEditor.this.queryView.getSelectionObject().enableSelection();
					QueryEditor.this.queryEditor.pack(); // Make window fit
					// contents'
					// preferred size
					QueryEditor.this.queryEditor.setLocationRelativeTo(null); // Move
					// window
					// to
					// the
					// middle
					// of
					// the
					// screen
					QueryEditor.this.queryEditor.setVisible(true); // Make
					// window
					// appear
					QueryEditor.this.queryEditor.repaintQueryTree(); // Have
					// query
					// tree
					// realign
					// itself
					// after
					// being
					// loaded
					// in
				}
			}
			else if (actionName.equals("New"))
			{
				createNewTab(null);
				QueryEditor.this.queryView.getSelectionObject().enableSelection();
				QueryEditor.this.queryEditor.pack(); // Make window fit
				// contents' preferred
				// size
				QueryEditor.this.queryEditor.setLocationRelativeTo(null); // Move
				// window
				// to
				// the
				// middle
				// of
				// the
				// screen
				QueryEditor.this.queryEditor.setVisible(true); // Make window
				// appear
				QueryEditor.this.queryView.drawResultNode();
			}
			else if (actionName.equals("Exit") && checkForSaveAll())
			{
				dispose();
				System.exit(0);
			}
			else if (actionName.equals("Close") && QueryEditor.this.drawingCanvas.getTabCount() > 0 &&
						checkForSave())
			{
				setObjectsNull(QueryEditor.this.drawingCanvas.getSelectedIndex());
				QueryEditor.this.drawingCanvas.remove(QueryEditor.this.drawingCanvas.getSelectedIndex());
			}
			else if (actionName.equals("PNG"))
				PerformanceTreeExporter.exportQueryView(QueryEditor.this.queryView,
														PerformanceTreeExporter.PNG);
			else if (actionName.equals("EPS"))
				PerformanceTreeExporter.exportQueryView(QueryEditor.this.queryView,
														PerformanceTreeExporter.EPS);
			else if (actionName.equals("Print"))
				PerformanceTreeExporter.exportQueryView(QueryEditor.this.queryView,
														PerformanceTreeExporter.PRINTER);
		}
	}

	class GridAction extends QueryAction
	{

		GridAction(final String name, final String tooltip, final String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(final ActionEvent e)
		{
			Grid.increment();
			repaint();
		}
	}

	class ZoomAction extends QueryAction
	{
		ZoomAction(final String name, final String tooltip, final String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(final ActionEvent e)
		{
			String actionName = (String) getValue(Action.NAME);
			PerformanceTreeZoomController zoomer = QueryEditor.this.queryView.getZoomController();
			JViewport thisView = ((JScrollPane) QueryEditor.this.drawingCanvas.getSelectedComponent()).getViewport();
			double currentXNoZoom, currentYNoZoom;
			currentXNoZoom = zoomer.getUnzoomedValue(thisView.getViewPosition().x + thisView.getWidth() * 0.5);
			currentYNoZoom = zoomer.getUnzoomedValue(thisView.getViewPosition().y + thisView.getHeight() *
														0.5);
			String selection = null;
			String strToTest = null;

			if (actionName.equals("Zoom In"))
			{
				zoomer.zoomIn();
				updateZoomCombo();
			}
			else
			{
				if (actionName.equals("Zoom Out"))
				{
					zoomer.zoomOut();
					updateZoomCombo();
				}
				else
				{
					if (actionName.equals("Zoom"))
						selection = (String) QueryEditor.this.zoomComboBox.getSelectedItem();
					if (e.getSource() instanceof JMenuItem)
					{
						selection = ((JMenuItem) e.getSource()).getText();
					}

					strToTest = validatePercent(selection);
					if (strToTest != null)
					{
						// BK: no need to zoom if already at that level
						if (zoomer.getPercent() == Integer.parseInt(strToTest))
							return;
						else zoomer.setZoom(Integer.parseInt(strToTest));

						updateZoomCombo();
					}
					else return;
				}
			}

			QueryEditor.this.queryView.zoom();
			QueryEditor.this.queryView.repaint();
			QueryEditor.this.queryView.updatePreferredSize();
			QueryEditor.this.queryView.getParent().validate();

			double newZoomedX = zoomer.getZoomPositionForXLocation(currentXNoZoom);
			double newZoomedY = zoomer.getZoomPositionForYLocation(currentYNoZoom);

			int newViewX = (int) (newZoomedX - thisView.getWidth() * 0.5);
			if (newViewX < 0)
			{
				newViewX = 0;
			}
			int newViewY = (int) (newZoomedY - thisView.getHeight() * 0.5);
			if (newViewY < 0)
			{
				newViewY = 0;
			}

			thisView.setViewPosition(new Point(newViewX, newViewY));
		}

		private String validatePercent(final String selection)
		{
			try
			{
				String toTest = selection;

				if (selection.endsWith("%"))
				{
					toTest = selection.substring(0, selection.length() - 1);
				}

				if (Integer.parseInt(toTest) < 40 || Integer.parseInt(toTest) > 2000)
					throw new Exception();
				else return toTest;
			}
			catch (Exception e)
			{
				QueryEditor.this.zoomComboBox.setSelectedItem("");
				return null;
			}
		}
	}

	class DeleteAction extends QueryAction
	{
		DeleteAction(final String name, final String tooltip, final String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(final ActionEvent e)
		{
			QueryEditor.this.queryView.getSelectionObject().deleteSelection();
		}
	}

	class TypeAction extends QueryAction
	{

		private final int	typeID;

		TypeAction(final String name, final int typeID, final String tooltip, final String keystroke) {
			super(name, tooltip, keystroke);
			this.typeID = typeID;
		}

		public void actionPerformed(final ActionEvent e)
		{
			if (!isSelected())
			{
				setSelected(true);
				// reset all other drawing actions (need for toggle buttons)
				resetDrawingActions(this);

				if (QueryEditor.this.queryView == null)
					return;

				// This is the important bit! When the mode is set, the
				// MouseHandler
				// in QueryView is going to check for it and the relevant method
				// invocations will take place there
				QueryManager.setMode(this.typeID);

				// set cursor
				if (this.typeID == QueryConstants.SELECT)
					QueryEditor.this.queryView.setCursorType("arrow");
				else if (this.typeID == QueryConstants.DRAG)
					QueryEditor.this.queryView.setCursorType("move");
				else QueryEditor.this.queryView.setCursorType("crosshair");

				QueryEditor.this.queryView.getSelectionObject().disableSelection();
				QueryEditor.this.queryView.getSelectionObject().clearSelection();
				QueryEditor.this.statusBar.changeText(this.typeID);

				// if we've clicked on any button to perform an action, we don't
				// want to be in the arc modification mode
				if (QueryManager.getView().arcBeingModified != null)
				{
					QueryEditor.this.queryView.arcBeingModified.delete();
					QueryEditor.this.queryView.arcBeingModified = null;
					QueryEditor.this.queryView.repaint();
				}

				if (this.typeID == QueryConstants.SELECT)
				{
					// disable drawing to eliminate possiblity of connecting arc
					// to old coord of moved component
					QueryEditor.this.statusBar.changeText(this.typeID);
					QueryEditor.this.queryView.getSelectionObject().enableSelection();
				}
			}
		}
	}

	class LabelAction extends QueryAction
	{

		LabelAction(final String name, final String tooltip, final String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(final ActionEvent e)
		{
			StateLabelManager.stateLabelManagerDialog();
		}
	}

	class MacroAction extends QueryAction
	{

		MacroAction(final String name, final String tooltip, final String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(final ActionEvent e)
		{
			MacroManager.macroManagerDialog();
		}
	}

	class EvaluateAction extends QueryAction
	{

		EvaluateAction(final String name, final String tooltip, final String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(final ActionEvent e)
		{
			QueryManager.evaluateQuery();
		}
	}

	class SettingsAction extends QueryAction
	{

		SettingsAction(final String name, final String tooltip, final String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(final ActionEvent e)
		{
			QueryManager.startPreferenceManager();
		}
	}

}
