/**
 * MacroView
 * 
 * - represents the canvas on which Performance Tree macros are drawn 
 * 
 * @author Tamas Suto
 * @date 06/10/07
 */

package pipe.modules.queryeditor.gui.performancetrees.macros;

import pipe.gui.Grid;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.PerformanceTreeArcHandler;
import pipe.modules.queryeditor.gui.PerformanceTreeNodeHandler;
import pipe.modules.queryeditor.gui.PerformanceTreeSelectionObject;
import pipe.modules.queryeditor.gui.PerformanceTreeZoomController;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArcPathPoint;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObject;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.*;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.*;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.*;

public class MacroView extends JLayeredPane implements Observer, QueryConstants, Printable
{

	private static final long						serialVersionUID	= 1L;

    public boolean									macroChanged		= false;
	public PerformanceTreeArc						arcBeingModified;
	private boolean											shiftDown			= false;
	private final PerformanceTreeSelectionObject	selection;
	private final PerformanceTreeZoomController		zoomControl;

	public MacroView(final String macro) {
		setLayout(null);
		setOpaque(true);
		setDoubleBuffered(true);
		setAutoscrolls(true);
		setBackground(QueryConstants.ELEMENT_FILL_COLOUR);
		this.zoomControl = new PerformanceTreeZoomController(100, this);
		MouseHandler handler = new MouseHandler();
		addMouseListener(handler);
		addMouseMotionListener(handler);
		this.selection = new PerformanceTreeSelectionObject(this);
	}

	/**
	 * Draws an initial Macro node onto the canvas
	 */
	public void drawMacroNode()
	{
		int canvasWidth = this.getWidth();
		int canvasHeight = this.getHeight();
		double xLoc = canvasWidth / 2 - QueryConstants.NODE_WIDTH / 2;
		double yLoc = canvasHeight * 0.05;

		// set mode
		MacroManager.setMode(QueryConstants.MACRO_NODE);

		// create a Macro node with a single arc
		MacroNode macroNode = new MacroNode(Grid.getModifiedX(xLoc), Grid.getModifiedX(yLoc));
		macroNode.setShowArcLabels(false);

		// add node to data structure in MacroEditor
		MacroManager.getEditor().addNode(macroNode);
		// draw node onto canvas
		addNewMacroObject(macroNode);

		// draw a single outgoing arc
		ArrayList<String> requiredNodeTypes = new ArrayList<String>();
		requiredNodeTypes.add(QueryConstants.DENS_TYPE);
		requiredNodeTypes.add(QueryConstants.DIST_TYPE);
		requiredNodeTypes.add(QueryConstants.STATES_TYPE);
		requiredNodeTypes.add(QueryConstants.ACTIONS_TYPE);
		requiredNodeTypes.add(QueryConstants.NUM_TYPE);
		requiredNodeTypes.add(QueryConstants.RANGE_TYPE);
		requiredNodeTypes.add(QueryConstants.BOOL_TYPE);
		requiredNodeTypes.add(QueryConstants.FUNC_TYPE);
		String arcRole = "query";
		macroNode.setRequiredChildNode(arcRole, requiredNodeTypes);
		macroNode.setupOutgoingArcs();

		// display info about the node in the InfoBox
		String msg = QueryManager.addColouring("The node on the canvas represents the fact that "
												+ "you are building a macro. \n\n "
												+ "The required argument can be any operation node.");
		MacroEditor.writeToInfoBox(msg);

		// reset mode
		MacroManager.setMode(QueryConstants.SELECT);
	}

	/**
	 * This method prints out details about the active macro
	 * 
	 */
	public void printMacroDetails()
	{
		System.out.println("Macro details:");
		System.out.println("---------------");
		if (MacroManager.getEditor().getActiveMacro() != null)
		{
			String macroName = MacroManager.getEditor().getActiveMacro().getName();
			ArrayList<PerformanceTreeNode> nodes = MacroManager.getEditor().getActiveMacro().getMacroNodes();
			ArrayList<PerformanceTreeArc> arcs = MacroManager.getEditor().getActiveMacro().getMacroArcs();
			System.out.println("Macro name: " + macroName);
			System.out.println("Nodes defined for macro: ");
			Iterator<PerformanceTreeNode> i = nodes.iterator();
			while (i.hasNext())
			{
				PerformanceTreeNode node = i.next();
				String nodeID = node.getId();
				System.out.println(" Node with ID " + nodeID);
				String incomingArcID = node.getIncomingArcID();
				if (incomingArcID != null)
				{
					System.out.println("   has an incoming arc with ID " + incomingArcID);
				}
				if (node instanceof OperationNode)
				{
					Collection outgoingArcIDs = ((OperationNode) node).getOutgoingArcIDs();
					Iterator<String> j = outgoingArcIDs.iterator();
					while (j.hasNext())
					{
						String outgoingArcID = j.next();
						System.out.println("   has an outgoing arc with ID " + outgoingArcID);
					}
				}
			}

			System.out.println("Arcs defined for macro: ");
			Iterator<PerformanceTreeArc> k = arcs.iterator();
			while (k.hasNext())
			{
				PerformanceTreeArc arc = k.next();
				String arcID = arc.getId();
				System.out.println(" Arc with ID " + arcID);
				String sourceID = arc.getSourceID();
				if (sourceID != null)
					System.out.println("  with source " + sourceID);
				String targetID = arc.getTargetID();
				if (targetID != null)
					System.out.println("  with target " + targetID);
			}
			System.out.println("------------");
		}
	}

	public PerformanceTreeArc getArcBeingModified()
	{
		return this.arcBeingModified;
	}

	public void setArcBeingModified(final PerformanceTreeArc arc)
	{
		this.arcBeingModified = arc;
	}

	/**
	 * This method is responsible for drawing the objects on the canvas
	 * 
	 * @param newObject
	 */
	public void addNewMacroObject(final PerformanceTreeObject newObject)
	{
		if (newObject != null)
		{
			int l = newObject.getMouseListeners().length;
			if (l == 0)
			{
				if (newObject instanceof PerformanceTreeNode)
				{
					PerformanceTreeNodeHandler nodeHandler = new PerformanceTreeNodeHandler(this,
																							(PerformanceTreeNode) newObject);
					newObject.addMouseListener(nodeHandler);
					newObject.addMouseMotionListener(nodeHandler);
					add(newObject);
					if (newObject instanceof OperationNode &&
						MacroManager.getMode() != QueryConstants.LOADING)
					{
						// need to add arcs to the canvas as well, but not when
						// loading, since in
						// that case, the arcs will be added individually
						((OperationNode) newObject).addArcsToContainer(this);
					}
				}
				else if (newObject instanceof PerformanceTreeArc)
				{
					PerformanceTreeArcHandler arcHandler = new PerformanceTreeArcHandler(	this,
																							(PerformanceTreeArc) newObject);
					newObject.addMouseListener(arcHandler);
					newObject.addMouseMotionListener(arcHandler);
					add(newObject);
					// make labels appear evenly distributed
					((PerformanceTreeArc) newObject).updateLabelPosition();
				}
			}
		}
		validate();
		repaint();
	}

	public PerformanceTreeObject add(final PerformanceTreeObject c)
	{
		super.add(c);
		c.addedToGui();

		if (c instanceof PerformanceTreeNode)
		{
			setLayer(c, JLayeredPane.DEFAULT_LAYER.intValue() + QueryConstants.NODE_LAYER_OFFSET);
		}
		else if (c instanceof PerformanceTreeArc)
			setLayer(c, JLayeredPane.DEFAULT_LAYER.intValue() + QueryConstants.ARC_LAYER_OFFSET);
		else if (c instanceof PerformanceTreeArcPathPoint)
			setLayer(c, JLayeredPane.DEFAULT_LAYER.intValue() + QueryConstants.ARC_POINT_LAYER_OFFSET);

		return c;
	}

	@Override
	public void validate()
	{
		Component[] children = getComponents();
        for(Component aChildren : children)
            if(aChildren instanceof PerformanceTreeArc)
            {
                ((PerformanceTreeArc) aChildren).getArcPath().createPath();
            }
		super.validate();
	}

	public void update(final Observable o, final Object diffObj)
	{
		if (diffObj instanceof PerformanceTreeObject && diffObj != null)
		{
			repaint();
		}
	}

	/**
	 * This method is called whenever the frame is moved, resized etc. It
	 * iterates over the existing query objects and repaints them.
	 */
	@Override
	public void paintComponent(final Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		super.paintComponent(g2);
		if (Grid.isEnabled())
		{
			Grid.updateSize(this);
			Grid.drawGrid(g);
		}
		this.selection.updateBounds();
	}

	public void updatePreferredSize()
	{
		// iterate over net objects
		// setPreferredSize() accordingly
		Component[] components = getComponents();
		Dimension d = new Dimension(0, 0);
        for(Component component : components)
        {
            if(component instanceof PerformanceTreeSelectionObject)
                continue; // PerformanceTreeSelectionObject not included
            Rectangle r = component.getBounds();
            int x = r.x + r.width + 100;
            int y = r.y + r.height + 100;
            if(x > d.width)
                d.width = x;
            if(y > d.height)
                d.height = y;
        }
		setPreferredSize(d);
		getParent().validate();
	}

	public PerformanceTreeSelectionObject getSelectionObject()
	{
		return this.selection;
	}

	public void setCursorType(final String type)
	{
		if (type.equals("arrow"))
			setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		else if (type.equals("crosshair"))
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		else if (type.equals("move"))
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
	}

	public void setShiftDown(final boolean down)
	{
		this.shiftDown = down;
		if (this.arcBeingModified != null)
		{
			this.arcBeingModified.getArcPath().setFinalPointType(this.shiftDown);
			this.arcBeingModified.getArcPath().createPath();
		}
	}

	public int print(final Graphics g, final PageFormat pageFormat, final int pageIndex) throws PrinterException
	{
		if (pageIndex > 0)
			return Printable.NO_SUCH_PAGE;
		Graphics2D g2D = (Graphics2D) g;
		// Move origin to page printing area corner
		g2D.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
		g2D.scale(0.5, 0.5);
		print(g2D); // Draw the query
		return Printable.PAGE_EXISTS;
	}

	public PerformanceTreeZoomController getZoomController()
	{
		return this.zoomControl;
	}

	public void zoom()
	{
		Component[] children = getComponents();
        for(Component aChildren : children)
        {
            //if (children[i] instanceof Zoomable)
            //	((Zoomable) children[i]).zoomUpdate();
        }
		validate();
	}

	class MouseHandler extends MouseInputAdapter
	{

		private Point	dragStart;
		String			msg;

		@Override
		public void mousePressed(final MouseEvent e)
		{
			PerformanceTreeObject ptObject = null;
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				Point start = e.getPoint();
				double startX = start.x - QueryConstants.NODE_WIDTH / 2;
				double startY = start.y - QueryConstants.NODE_HEIGHT / 2;

				switch (MacroManager.getMode())
				{
					case PASSAGETIMEDENSITY_NODE :
						ptObject = new PassageTimeDensityNode(	Grid.getModifiedX(startX),
																Grid.getModifiedY(startY));

						break;
					case DISTRIBUTION_NODE :
						ptObject = new DistributionNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case CONVOLUTION_NODE :
						ptObject = new ConvolutionNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case PROBININTERVAL_NODE :
						ptObject = new ProbInIntervalNode(	Grid.getModifiedX(startX),
															Grid.getModifiedY(startY));

						break;
					case PROBINSTATES_NODE :
						ptObject = new ProbInStatesNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case MOMENT_NODE :
						ptObject = new MomentNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case PERCENTILE_NODE :
						ptObject = new PercentileNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case FIRINGRATE_NODE :
						ptObject = new FiringRateNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case STEADYSTATEPROB_NODE :
						ptObject = new SteadyStateProbNode(	Grid.getModifiedX(startX),
															Grid.getModifiedY(startY));

						break;
					case STEADYSTATESTATES_NODE :
						ptObject = new SteadyStateStatesNode(	Grid.getModifiedX(startX),
																Grid.getModifiedY(startY));

						break;
					case STATESATTIME_NODE :
						ptObject = new StatesAtTimeNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case ININTERVAL_NODE :
						ptObject = new InIntervalNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case SUBSET_NODE :
						ptObject = new SubsetNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case DISCON_NODE :
						ptObject = new DisconNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						break;
					case NEGATION_NODE :
						ptObject = new NegationNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case ARITHCOMP_NODE :
						ptObject = new ArithCompNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case ARITHOP_NODE :
						ptObject = new ArithOpNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case RANGE_NODE :
						ptObject = new RangeNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case STATES_NODE :
						ptObject = new StatesNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case ACTIONS_NODE :
						ptObject = new ActionsNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case NUM_NODE :
						ptObject = new NumNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case BOOL_NODE :
						ptObject = new BoolNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case STATEFUNCTION_NODE :
						ptObject = new StateFunctionNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case ARGUMENT_NODE :
						ptObject = new ArgumentNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));

						break;
					case DRAG :
						this.dragStart = new Point(start);
				}
				if (MacroManager.getMode() != QueryConstants.DRAG)
				{
					ptObject = MacroManager.getEditor().addPerformanceTreeObject(ptObject);
					addNewMacroObject(ptObject);
				}
			}
		}

		@Override
		public void mouseMoved(final MouseEvent e)
		{
			if (MacroView.this.arcBeingModified != null)
			{
				MacroView.this.arcBeingModified.setEndPoint(Grid.getModifiedX(e.getX()),
															Grid.getModifiedY(e.getY()),
															MacroView.this.shiftDown);
			}
		}

		@Override
		public void mouseDragged(final MouseEvent e)
		{
			if (MacroManager.getMode() == QueryConstants.DRAG)
			{

				// Drag only works when the frame is smaller than the actual
				// viewport,
				// so that scrollbars appear at the sides, or when the image is
				// zoomed.
				// Otherwise, it doesn't make sense to drag.

				JViewport viewer = (JViewport) getParent();
				Point offScreen = viewer.getViewPosition();
				if (this.dragStart.x > e.getX())
				{
					offScreen.translate(viewer.getWidth(), 0);
				}
				if (this.dragStart.y > e.getY())
				{
					offScreen.translate(0, viewer.getHeight());
				}
				offScreen.translate(this.dragStart.x - e.getX(), this.dragStart.y - e.getY());
				Rectangle r = new Rectangle(offScreen.x, offScreen.y, 1, 1);
				scrollRectToVisible(r);
				super.mouseDragged(e);
			}
		}
	}

}
