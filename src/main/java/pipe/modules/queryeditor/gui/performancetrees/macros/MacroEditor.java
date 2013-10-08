/**
 * StateEditor
 * 
 * This is the popup that allows the user to edit a state of the underlying SPN model
 * 
 * @author Barry Kearns
 * @author Tamas Suto
 * @date 18/08/07
 */

package pipe.modules.queryeditor.gui.performancetrees.macros;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SpringLayout;
import javax.swing.WindowConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.w3c.dom.Document;

import pipe.modules.interfaces.QueryConstants;
import pipe.gui.Grid;
import pipe.gui.HelpBox;
import pipe.gui.widgets.FileBrowser;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.PerformanceTreeZoomController;
import pipe.modules.queryeditor.gui.QueryAction;
import pipe.modules.queryeditor.gui.QueryException;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObject;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ArithCompNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ArithOpNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ConvolutionNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.DisconNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.DistributionNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.FiringRateNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.InIntervalNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.MomentNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.NegationNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.OperationNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.PassageTimeDensityNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.PercentileNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ProbInIntervalNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.ProbInStatesNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.RangeNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.StatesAtTimeNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SteadyStateProbNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SteadyStateStatesNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.SubsetNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ActionsNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.BoolNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.NumNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StateFunctionNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.StatesNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.labels.SpringLayoutUtilities;
import pipe.modules.queryeditor.io.MacroLoader;
import pipe.modules.queryeditor.io.MacroTransformer;
import pipe.modules.queryeditor.io.MacroWriter;
import pipe.modules.queryeditor.io.PerformanceTreeExporter;

public class MacroEditor extends JDialog implements QueryConstants
{

	private static final long		serialVersionUID	= 1L;

	private MacroDefinition					activeMacro;

	private boolean							newMacro			= false;
	private String					initialMacroName;

	private static JEditorPane		infoBox;
	private JScrollPane				macroEditorPanel;
	private MacroView				macroView;
	private final Map				actions				= new HashMap();
    private JTextField				macroNameTextField, macroDescriptionTextField;
	private JButton					okButton, cancelButton;

	private static final Dimension	screenSize			= Toolkit.getDefaultToolkit().getScreenSize();
	private static final int			minFrameWitdh		= MacroEditor.screenSize.width * 20 / 100;
	private static final int			prefFrameWidth		= MacroEditor.screenSize.width * 86 / 100;
	private static final int			minFrameHeight		= MacroEditor.screenSize.height * 20 / 100;
	private static final int			prefFrameHeight		= MacroEditor.screenSize.height * 86 / 100;

	private JComboBox				zoomComboBox;
	private final String[]			zoomExamples		= {"40%",
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
			"400%"										};

	// this is for argument specification
	private static JDialog			argumentDialog;
	private static ArgumentNode		argumentNode;

	public MacroEditor() {
		// important to set the modal attribute to false, otherwise no code will
		// be
		// executed beyond setVisible(true), which we don't want
		super(MacroManager.popupDialog, "Macro Editor", false);

		Dimension minFrameSize = new Dimension(MacroEditor.minFrameWitdh, MacroEditor.minFrameHeight);
		Dimension prefFrameSize = new Dimension(MacroEditor.prefFrameWidth, MacroEditor.prefFrameHeight);
		setMinimumSize(minFrameSize);
		setPreferredSize(prefFrameSize);
		addWindowListener(new WindowHandler());
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

		// set up actions
		initialiseActions();
	}

	public MacroEditor(final MacroDefinition macroToLoadIn) {
		this();
		this.activeMacro = macroToLoadIn;
	}

	/**
	 * This is being called by MacroManager when the "Define Macro" button is
	 * clicked.
	 */
	public void createMacro()
	{
		String macroName = "New Macro";
		this.activeMacro = new MacroDefinition(macroName);
		this.macroView = new MacroView(this.activeMacro.getName());
		this.newMacro = true;
		initLayout();

		// draw initial macro node to link stuff up to
		this.macroView.drawMacroNode();

		// Set selection mode at startup
		MacroManager.setMode(QueryConstants.SELECT);
		((QueryAction) this.actions.get("Draw Select")).setSelected(true);
	}

	/**
	 * This is being called by MacroManager when the "Edit Macro" button is
	 * clicked
	 * 
	 * @param macroToEditName
	 */
	public void editMacro(final String macroToEditName)
	{
		// prepare macro display stuff
		String fileName = MacroManager.macroSaveLocation + System.getProperty("file.separator") +
							macroToEditName + ".xml";
		File inFile = new File(fileName);
		if (inFile.exists())
		{
			this.macroView = new MacroView(macroToEditName);
			this.initialMacroName = macroToEditName;
			this.newMacro = false;
			initLayout();

			// load in macro to edit from the XML that was created of the macro
			// when it
			// was created and saved originially - using this approach saves us
			// having
			// to implement a separate feature that loads in from a
			// MacroDefitnition
			MacroTransformer transformer = new MacroTransformer();
			Document macroDocument = transformer.transformPTML(inFile.getPath());
			MacroLoader.loadMacroFromXML(macroToEditName, macroDocument);

			// display description of macro
			this.macroDescriptionTextField.setText(this.activeMacro.getDescription());

			// set selection mode so that objects can be moved around
			MacroManager.setMode(QueryConstants.SELECT);
			((QueryAction) this.actions.get("Draw Select")).setSelected(true);
		}
		else
		{
			// the macro XML doesn't exist, because it might have been deleted.
			// In this case,
			// since the XML is needed for constructing the macro tree, we need
			// to write the
			// macro into an XML document from the data structure, just like
			// with an export
			MacroDefinition macro = QueryManager.getData().getMacro(macroToEditName);
			try
			{
				MacroWriter.saveMacro(macro, fileName);
				editMacro(macroToEditName);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	private void initLayout()
	{
		// Menu Bar
		JMenuBar menuBar = new JMenuBar();
		JMenu fileMenu = new JMenu("Macro");
		fileMenu.setMnemonic('M');
		JMenu exportMenu = new JMenu("Export");
		exportMenu.setIcon(new ImageIcon(Thread	.currentThread()
												.getContextClassLoader()
												.getResource(QueryManager.imgPath + "Export.png")));
		addMenuItem(exportMenu, ((Action) this.actions.get("XML")));
		addMenuItem(exportMenu, ((Action) this.actions.get("EPS")));
		addMenuItem(exportMenu, ((Action) this.actions.get("PNG")));
		fileMenu.add(exportMenu);
		addMenuItem(fileMenu, ((Action) this.actions.get("Print")));

		JMenu editMenu = new JMenu("Edit");
		editMenu.setMnemonic('E');
		addMenuItem(editMenu, ((Action) this.actions.get("Delete")));

		JMenu viewMenu = new JMenu("View");
		viewMenu.setMnemonic('V');
		addMenuItem(viewMenu, ((Action) this.actions.get("Grid")));
// JMenu zoomMenu = new JMenu("Zoom");
// zoomMenu.setMnemonic('Z');
// zoomMenu.setIcon(new
// ImageIcon(Thread.currentThread().getContextClassLoader().getResource(QueryManager._imgPath+"Zoom.png")));
// addZoomMenuItems(zoomMenu);
// viewMenu.add(zoomMenu);

		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic('H');
		addMenuItem(helpMenu, ((Action) this.actions.get("Help")));

		menuBar.add(fileMenu);
		menuBar.add(editMenu);
		menuBar.add(viewMenu);
		menuBar.add(helpMenu);
		setJMenuBar(menuBar);

		// Macro information panel
		MacroEditor.infoBox = new JEditorPane("text/html", "");
		MacroEditor.infoBox.setBackground(Color.white);
		MacroEditor.infoBox.setEditable(false);
		MacroEditor.infoBox.setBorder(new TitledBorder(new EtchedBorder(), "Information"));
		JScrollPane macroInfoPanel = new JScrollPane(MacroEditor.infoBox);
		int macroInfoPanelHeight = MacroEditor.prefFrameHeight * 20 / 100;
		Dimension infoPaneMinSize = new Dimension(MacroEditor.minFrameWitdh, 1);
		Dimension infoPanePrefSize = new Dimension(MacroEditor.prefFrameWidth, macroInfoPanelHeight);
		macroInfoPanel.setMinimumSize(infoPaneMinSize);
		macroInfoPanel.setPreferredSize(infoPanePrefSize);

		// Macro builder panel
		JPanel macroBuilderButtonsPanel = new JPanel();
		macroBuilderButtonsPanel.setBorder(new TitledBorder(new EtchedBorder(), "Macro Builder"));
		macroBuilderButtonsPanel.setLayout(new BoxLayout(macroBuilderButtonsPanel, BoxLayout.Y_AXIS));
		JToolBar[] queryBuilderToolbars = getMacroBuilderToolbars();
		macroBuilderButtonsPanel.add(queryBuilderToolbars[0]);
		macroBuilderButtonsPanel.add(queryBuilderToolbars[1]);
		JScrollPane macroBuilderPanel = new JScrollPane(macroBuilderButtonsPanel);
		URL iconURL = Thread.currentThread().getContextClassLoader().getResource(QueryManager.imgPath +
																					"Select.png");
		ImageIcon icon = new ImageIcon(iconURL);
		int macroBuilderPanelHeight = icon.getIconHeight() * 4;
		Dimension macroBuilderPanelMinSize = new Dimension(MacroEditor.minFrameWitdh, macroBuilderPanelHeight);
		Dimension macroBuilderPanelPrefSize = new Dimension(MacroEditor.prefFrameWidth,
															macroBuilderPanelHeight);
		macroBuilderPanel.setMinimumSize(macroBuilderPanelMinSize);
		macroBuilderPanel.setPreferredSize(macroBuilderPanelPrefSize);

		// Macro details panel
		JPanel macroDetailsPanel = new JPanel();
		macroDetailsPanel.setBorder(new EtchedBorder());
		macroDetailsPanel.setLayout(new SpringLayout());
		JLabel macroNameLabel = new JLabel("Macro Name: ");
		this.macroNameTextField = new JTextField(30);
		this.macroNameTextField.setText(this.activeMacro.getName());
		macroDetailsPanel.add(macroNameLabel);
		macroDetailsPanel.add(this.macroNameTextField);
		JLabel macroDescriptionLabel = new JLabel("Description: ");
		this.macroDescriptionTextField = new JTextField(30);
		macroDetailsPanel.add(macroDescriptionLabel);
		macroDetailsPanel.add(this.macroDescriptionTextField);
		SpringLayoutUtilities.makeCompactGrid(macroDetailsPanel, 2, 2, 6, 6, 6, 12);
		int macroDetailsPanelHeight = icon.getIconHeight() * 2; // about the
		// same height
		// as two
		// buttons

		// Macro buttons panel
		JPanel macroButtonsPanel = new JPanel();
		macroButtonsPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		this.okButton = new JButton("OK");
		this.okButton.addActionListener(this.saveMacro);
		this.cancelButton = new JButton("Cancel");
		this.cancelButton.addActionListener(this.saveMacro);
		macroButtonsPanel.add(this.okButton);
		macroButtonsPanel.add(this.cancelButton);
		int macroButtonsPanelHeight = icon.getIconHeight(); // about the same
		// height as a
		// button

		// Macro editor panel
		this.macroView.setBorder((new TitledBorder(new EtchedBorder(), "Macro Definition")));
		this.macroEditorPanel = new JScrollPane(this.macroView);
		int macroEditorPanelHeight = MacroEditor.prefFrameHeight - macroInfoPanelHeight -
										macroBuilderPanelHeight - macroDetailsPanelHeight -
										macroButtonsPanelHeight;
		Dimension macroEditorPanelMinSize = new Dimension(MacroEditor.minFrameWitdh, macroEditorPanelHeight);
		Dimension macroEditorPanelPrefSize = new Dimension(MacroEditor.prefFrameWidth, macroEditorPanelHeight);
		this.macroEditorPanel.setMinimumSize(macroEditorPanelMinSize);
		this.macroEditorPanel.setPreferredSize(macroEditorPanelPrefSize);
		Grid.enableGrid();
		// add macroView as an observer, so that it updates itself automatically
		this.activeMacro.addObserver(this.macroView);

		// Put everything together
		JSplitPane topPanel = new JSplitPane(JSplitPane.VERTICAL_SPLIT, macroInfoPanel, this.macroEditorPanel);
		topPanel.setContinuousLayout(true);
		topPanel.setOneTouchExpandable(true);
		topPanel.setBorder(null);
		topPanel.setDividerSize(8);
		topPanel.setResizeWeight(1.0);

		JPanel bottomPanel = new JPanel();
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
		bottomPanel.add(macroBuilderPanel);
		bottomPanel.add(macroDetailsPanel);
		bottomPanel.add(macroButtonsPanel);

		getContentPane().add(topPanel, BorderLayout.CENTER);
		getContentPane().add(bottomPanel, BorderLayout.PAGE_END);
		pack();
		setLocationRelativeTo(null);
		setVisible(true);

		// enable selection of objects on the canvas
		this.macroView.getSelectionObject().enableSelection();
	}

	private void initialiseActions()
	{
		this.actions.put("XML", new FileAction("XML", "Export macro to an XML file", ""));
		this.actions.put("EPS", new FileAction("EPS", "Export macro to EPS format", ""));
		this.actions.put("PNG", new FileAction("PNG", "Export macro to PNG format", ""));
		this.actions.put("Print", new FileAction("Print", "Print query", "ctrl P"));
		this.actions.put("Help", new HelpBox("Help", "Help", "F1", "index.htm"));

		// Toolbar actions
		this.actions.put("Grid", new GridAction("Cycle Grid", "Change grid size", "G"));
		this.actions.put("Zoom", new ZoomAction("Zoom", "Select zoom percentage ", ""));
		this.actions.put("ZoomIn", new ZoomAction("Zoom In", "Zoom in by 10% ", "ctrl +"));
		this.actions.put("ZoomOut", new ZoomAction("Zoom Out", "Zoom out by 10% ", "ctrl -"));

		this.actions.put("Draw Drag", new TypeAction("Drag", QueryConstants.DRAG, "Drag query", "DRAG"));
		TypeAction select = new TypeAction("Select", QueryConstants.SELECT, "Select query components", "S");
		select.putValue("default", Boolean.TRUE);
		this.actions.put("Draw Select", select);
		this.actions.put("Delete", new DeleteAction("Delete", "Delete selection", "DELETE"));

		// Operation nodes
		String tooltip = PassageTimeDensityNode.getTooltip();
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

		tooltip = ArgumentNode.getTooltip();
		this.actions.put("Draw Argument", new TypeAction(	"Argument",
															QueryConstants.ARGUMENT_NODE,
															tooltip,
															""));
	}

	private void addMenuItem(final JMenu menu, final Action action)
	{
		JMenuItem item = menu.add(action);
		KeyStroke keystroke = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
		if (keystroke != null)
			item.setAccelerator(keystroke);
	}

    private void updateZoomCombo()
	{
		ActionListener zoomComboListener = (this.zoomComboBox.getActionListeners())[0];
		this.zoomComboBox.removeActionListener(zoomComboListener);
		this.zoomComboBox.setSelectedItem(String.valueOf(this.macroView.getZoomController().getPercent()) +
											"%");
		this.zoomComboBox.addActionListener(zoomComboListener);
	}

	/** This instantiates the Query Builder buttons
     * @return*/
    private JToolBar[] getMacroBuilderToolbars()
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
		drawButtons.add(addIntelligentButton(toolBar2, (Action) this.actions.get("Draw Argument")));

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
	 * @param toolBar -
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
				else if (actionName.equals("Argument"))
				{
					msg = ArgumentNode.getNodeInfo();
				}
				MacroEditor.writeToInfoBox(msg);
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

	public static void writeToInfoBox(final String stuff)
	{
		MacroEditor.infoBox.setText(stuff);
	}

	public MacroView getView()
	{
		return this.macroView;
	}

	public void setView(final MacroView view)
	{
		this.macroView = view;
	}

	public static void appendToInfoBox(final String stuff)
	{
		String bufferedText = MacroEditor.infoBox.getText();
		String newText = bufferedText + " ";
		MacroEditor.infoBox.setText(newText);
		MacroEditor.infoBox.setCaretPosition(MacroEditor.infoBox.getDocument().getLength());
	}

	public void update(final Observable o, final Object obj)
	{
		if (MacroManager.getMode() != QueryConstants.LOADING)
			this.macroView.macroChanged = true;
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

	public MacroDefinition getActiveMacro()
	{
		return this.activeMacro;
	}

	public void setActiveMacro(final MacroDefinition newMacro)
	{
		this.activeMacro = newMacro;
	}

	/**
	 * This method adds a PerformanceTreeObject into the temporary storage for
	 * the macro that is being edited on the canvas (activeMacro)
	 * 
	 * @param ptObject
     * @return
	 */
	public PerformanceTreeObject addPerformanceTreeObject(final PerformanceTreeObject ptObject)
	{
		if (ptObject instanceof PerformanceTreeNode)
			return addNode((PerformanceTreeNode) ptObject);
		else if (ptObject instanceof PerformanceTreeArc)
			return addArc((PerformanceTreeArc) ptObject);
		else return null;
	}

	public void removePerformanceTreeObject(final PerformanceTreeObject ptObject)
	{
		if (ptObject instanceof PerformanceTreeNode)
			deleteNode((PerformanceTreeNode) ptObject);
		else if (ptObject instanceof PerformanceTreeArc)
			deleteArc((PerformanceTreeArc) ptObject);
	}

	public PerformanceTreeNode addNode(final PerformanceTreeNode node)
	{
		if (getActiveMacro() != null)
			return getActiveMacro().addMacroNode(node);
		else return null;
	}

	public void updateNode(final PerformanceTreeNode node)
	{
		if (getActiveMacro() != null)
			getActiveMacro().updateMacroNode(node);
	}

	private void deleteNode(final PerformanceTreeNode node)
	{
		getActiveMacro().deleteMacroNode(node);
	}

	private PerformanceTreeArc addArc(final PerformanceTreeArc arc)
	{
		if (getActiveMacro() != null)
			return getActiveMacro().addMacroArc(arc);
		else return null;
	}

	public void updateArc(final PerformanceTreeArc arc)
	{
		if (getActiveMacro() != null)
		{
			getActiveMacro().updateMacroArc(arc);
		}
	}

	private void deleteArc(final PerformanceTreeArc arc)
	{
		getActiveMacro().deleteMacroArc(arc);
	}

	public PerformanceTreeNode getNode(final String nodeID)
	{
		if (getActiveMacro() != null)
			return getActiveMacro().getMacroNode(nodeID);
		else return null;
	}

	public PerformanceTreeArc getArc(final String arcID)
	{
		if (getActiveMacro() != null)
			return getActiveMacro().getMacroArc(arcID);
		else return null;
	}

	private final ActionListener	saveMacro	= new ActionListener()
								{
									public void actionPerformed(ActionEvent event)
									{
										String errormsg;
										if (event.getSource() == MacroEditor.this.okButton)
										{
											try
											{
												if (MacroEditor.this.macroNameTextField.getText().equals("") ||
													!MacroEditor.containsText(MacroEditor.this.macroNameTextField.getText()))
												{
													// check if anything has
													// been input into the name
													// text field
													errormsg = "Please specify a name for this macro.";
													throw new QueryException(errormsg);
												}
												else
												{
													// check whether the macro
													// exists already
													String specifiedMacroName = MacroEditor.this.macroNameTextField.getText();
													if (MacroEditor.this.newMacro &&
														QueryManager.getData()
																	.macroExistsAlready(specifiedMacroName) ||
														!MacroEditor.this.newMacro &&
														!specifiedMacroName.equals(MacroEditor.this.initialMacroName) &&
														QueryManager.getData()
																	.macroExistsAlready(specifiedMacroName))
													{
														errormsg = "A macro has already been defined with this name.\n"
																	+ "Please choose a different name.";
														throw new QueryException(errormsg);
													}
													else
													{
														if (MacroEditor.this.macroDescriptionTextField	.getText()
																										.equals("") ||
															!MacroEditor.containsText(MacroEditor.this.macroDescriptionTextField.getText()))
														{
															errormsg = "Please supply a description for the macro in \n"
																		+ "order to be able to identify it conveniently \n"
																		+ "at a later stage.";
															throw new QueryException(errormsg);
														}
														else
														{
															if (macroTreeValid())
															{
																// save macro in
																// QueryData
																MacroEditor.this.activeMacro.setName(MacroEditor.this.macroNameTextField.getText());
																MacroEditor.this.activeMacro.setDescription(MacroEditor.this.macroDescriptionTextField.getText());
																MacroEditor.this.activeMacro.setReturnType(MacroEditor.this.activeMacro.determineMacroReturnType());
																QueryManager.getData()
																			.saveMacro(MacroEditor.this.activeMacro);

																// save macro as
																// an XML
																// locally
																try
																{
																	MacroWriter.saveMacro(	MacroEditor.this.activeMacro,
																							"");
																}
																catch (Exception e)
																{
																	e.printStackTrace();
																}

																if (!MacroEditor.this.newMacro &&
																	MacroEditor.this.initialMacroName != null &&
																	!MacroEditor.this.activeMacro	.getName()
																									.equals(MacroEditor.this.initialMacroName))
																{
																	// an
																	// existing
																	// macro is
																	// edited
																	// remove
																	// old
																	// macroDescription
																	// version
																	// from
																	// QueryData
																	QueryManager.getData()
																				.deleteMacro(MacroEditor.this.initialMacroName);

																	// delete
																	// XML of
																	// old
																	// version
																	// of macro
																	String oldMacroPath = MacroManager.macroSaveLocation +
																							MacroEditor.this.initialMacroName +
																							".xml";
																	File oldMacroFile = new File(oldMacroPath);
																	if (oldMacroFile.exists())
																		oldMacroFile.delete();
																}

																// update
																// MacroManager
																MacroManager.update();

																// close
																// dialogue
																closeWindow();
															}
														}
													}
												}
											}
											catch (QueryException e)
											{
												String msg = e.getMessage();
												JOptionPane.showMessageDialog(	MacroManager.getEditor()
																							.getContentPane(),
																				msg,
																				"Warning",
																				JOptionPane.ERROR_MESSAGE);
											}
										}
										else if (event.getSource() == MacroEditor.this.cancelButton)
										{
											// close dialogue
											closeWindow();
										}
									}

									private void closeWindow()
									{
										setVisible(false);
										dispose();
										MacroManager.resetEditor();
										MacroManager.update();
									}
								};

	/**
	 * Checks if a string contains anything but spaces - needed for text field
	 * validation
	 * 
	 * @param inputSting
	 * @return
     * @param inputString
	 */
	public static boolean containsText(final String inputString)
	{
		boolean stringContainsText = false;
		if (!inputString.equals(""))
		{
			for (int i = 0; i < inputString.length(); i++)
			{
				char chr = inputString.charAt(i);
				if (Character.isDigit(chr) || Character.isLetter(chr))
					stringContainsText = true;
			}
		}
		return stringContainsText;
	}

	public static boolean containsLetters(final String inputString)
	{
		boolean stringContainsText = false;
		if (!inputString.equals(""))
		{
			for (int i = 0; i < inputString.length(); i++)
			{
				char chr = inputString.charAt(i);
				if (Character.isLetter(chr))
					stringContainsText = true;
			}
		}
		return stringContainsText;
	}

	/**
	 * This method checks whether we have a fully connected tree, i.e. that all
	 * required arcs of each node on the macro canvas have been assigned to
	 * nodes.
	 * 
	 * @return
	 */
	private boolean macroTreeValid()
	{
		String errormsg;
		try
		{
			if (this.activeMacro != null)
			{
				ArrayList<PerformanceTreeNode> macroNodes = this.activeMacro.getMacroNodes();
				Iterator<PerformanceTreeNode> i = macroNodes.iterator();
				while (i.hasNext())
				{
					PerformanceTreeNode node = i.next();
					if (!(node instanceof MacroNode))
					{
						// check that apart from the top node, each node has a
						// parent
						if (node.getIncomingArcID() == null)
						{
							errormsg = "Please ensure that your macro tree is fully \n"
										+ "connected by checking that all required arcs\n"
										+ "have been assigned to nodes.";
							throw new QueryException(errormsg);
						}
					}

					if (node instanceof OperationNode)
					{
						// check that all required arc has a node assigned to it
						// Value nodes have no outgoing arcs, so don't worry
						// about them
						Collection<String> outgoingArcIDs = ((OperationNode) node).getOutgoingArcIDs();
						Iterator<String> j = outgoingArcIDs.iterator();
						while (j.hasNext())
						{
							String outgoingArcID = j.next();
							PerformanceTreeArc outgoingArc = this.activeMacro.getMacroArc(outgoingArcID);
							if (outgoingArc.isRequired() && outgoingArc.getTargetID() == null)
							{
								errormsg = "Please ensure that your macro tree is fully \n"
											+ "connected by checking that all required arcs\n"
											+ "have been assigned to nodes.";
								throw new QueryException(errormsg);
							}
						}
					}
					else if (node instanceof StatesNode)
					{
						if (((StatesNode) node).getNodeLabelObject() == null)
						{
							errormsg = "Please ensure that your States nodes \n"
										+ "all have state labels assigned to them.";
							throw new QueryException(errormsg);
						}
					}
					else if (node instanceof ActionsNode)
					{
						if (((ActionsNode) node).getNodeLabelObject() == null)
						{
							errormsg = "Please ensure that your Actions nodes \n"
										+ "all have action labels assigned to them.";
							throw new QueryException(errormsg);
						}
					}
					else if (node instanceof BoolNode)
					{
						if (((BoolNode) node).getNodeLabelObject() == null)
						{
							errormsg = "Please ensure that your Bool nodes all\n"
										+ "have boolean values assigned to them.";
							throw new QueryException(errormsg);
						}
					}
					else if (node instanceof NumNode)
					{
						if (((NumNode) node).getNodeLabelObject() == null)
						{
							errormsg = "Please ensure that your Num nodes all\n"
										+ "have numerical values assigned to them.";
							throw new QueryException(errormsg);
						}
					}
					else if (node instanceof ArgumentNode)
					{
						if (((ArgumentNode) node).getNodeLabelObject() == null)
						{
							errormsg = "Please ensure that your Argument (X) nodes \n"
										+ "all have argument names assigned to them.";
							throw new QueryException(errormsg);
						}
					}
				}
				return true;
			}
			else return false;
		}
		catch (QueryException e)
		{
			String msg = e.getMessage();
			JOptionPane.showMessageDialog(	MacroManager.getEditor().getContentPane(),
											msg,
											"Warning",
											JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private class WindowHandler extends WindowAdapter
	{
		// Handler for window closing event
		@Override
		public void windowClosing(final WindowEvent e)
		{
			dispose();
			MacroManager.resetEditor();
			MacroManager.update();
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
			if (actionName.equals("XML"))
				exportMacroToXML();
			else if (actionName.equals("PNG"))
				PerformanceTreeExporter.exportMacroView(MacroEditor.this.macroView,
														PerformanceTreeExporter.PNG);
			else if (actionName.equals("EPS"))
				PerformanceTreeExporter.exportMacroView(MacroEditor.this.macroView,
														PerformanceTreeExporter.EPS);
			else if (actionName.equals("Print"))
				PerformanceTreeExporter.exportMacroView(MacroEditor.this.macroView,
														PerformanceTreeExporter.PRINTER);
		}

		private void exportMacroToXML()
		{
			String errormsg;
			try
			{
				if (MacroEditor.this.macroNameTextField.getText().equals("") ||
					!MacroEditor.containsText(MacroEditor.this.macroNameTextField.getText()))
				{
					// check if anything has been input into the name text field
					errormsg = "Please specify a name for this macro.";
					throw new QueryException(errormsg);
				}
				else
				{
					if (MacroEditor.this.macroDescriptionTextField.getText().equals("") ||
						!MacroEditor.containsText(MacroEditor.this.macroDescriptionTextField.getText()))
					{
						errormsg = "Please supply a description for the macro in \n"
									+ "order to be able to identify it conveniently \n" + "at a later stage.";
						throw new QueryException(errormsg);
					}
					else
					{
						if (macroTreeValid())
						{
							// all good, so save macro in QueryData
							MacroEditor.this.activeMacro.setName(MacroEditor.this.macroNameTextField.getText());
							MacroEditor.this.activeMacro.setDescription(MacroEditor.this.macroDescriptionTextField.getText());
							MacroEditor.this.activeMacro.setReturnType(MacroEditor.this.activeMacro.determineMacroReturnType());

							// save macro as an XML
							String filename = new FileBrowser(	"XML Document",
																"xml",
																MacroEditor.this.activeMacro.getName() +
																".xml").saveFile();
							try
							{
								MacroWriter.saveMacro(MacroEditor.this.activeMacro, filename);
							}
							catch (Exception e)
							{
								e.printStackTrace();
							}
						}
					}
				}
			}
			catch (QueryException e)
			{
				String msg = e.getMessage();
				JOptionPane.showMessageDialog(	MacroManager.getEditor().getContentPane(),
												msg,
												"Warning",
												JOptionPane.ERROR_MESSAGE);
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
			MacroEditor.this.macroView.getSelectionObject().deleteSelection();
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

				if (MacroEditor.this.macroView == null)
					return;

				// This is the important bit! When the mode is set, the
				// MouseHandler
				// in QueryView is going to check for it and the relevant method
				// invocations will take place there
				MacroManager.setMode(this.typeID);

				// set cursor
				if (this.typeID == QueryConstants.SELECT)
					MacroEditor.this.macroView.setCursorType("arrow");
				else if (this.typeID == QueryConstants.DRAG)
					MacroEditor.this.macroView.setCursorType("move");
				else MacroEditor.this.macroView.setCursorType("crosshair");

				MacroEditor.this.macroView.getSelectionObject().disableSelection();
				MacroEditor.this.macroView.getSelectionObject().clearSelection();

				// if we've clicked on any button to perform an action, we don't
				// want to be in the arc modification mode
				if (MacroManager.getView().arcBeingModified != null)
				{
					MacroEditor.this.macroView.arcBeingModified.delete();
					MacroEditor.this.macroView.arcBeingModified = null;
					MacroEditor.this.macroView.repaint();
				}

				if (this.typeID == QueryConstants.SELECT)
				{
					// disable drawing to eliminate possiblity of connecting arc
					// to old coord of moved component
					MacroEditor.this.macroView.getSelectionObject().enableSelection();
				}
			}
		}
	}

	private class MacroImportAction extends QueryAction
	{

		MacroImportAction(final String name, final String tooltip, final String keystroke) {
			super(name, tooltip, keystroke);
		}

		public void actionPerformed(final ActionEvent e)
		{
			// MacroManager.macroManagerDialog();
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
			PerformanceTreeZoomController zoomer = MacroEditor.this.macroView.getZoomController();
			JViewport thisView = MacroEditor.this.macroEditorPanel.getViewport();
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
						selection = (String) MacroEditor.this.zoomComboBox.getSelectedItem();
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

			MacroEditor.this.macroView.zoom();
			MacroEditor.this.macroView.repaint();
			MacroEditor.this.macroView.updatePreferredSize();
			MacroEditor.this.macroView.getParent().validate();

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
				MacroEditor.this.zoomComboBox.setSelectedItem("");
				return null;
			}
		}
	}

}
