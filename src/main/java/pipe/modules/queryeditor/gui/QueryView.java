/**
 * QueryView
 * 
 * - represents a tab on which Performance Tree queries are drawn 
 * - Implements drawing features for individual components of a query tree
 * 
 * @author Tamas Suto
 * @date 15/04/07
 */

package pipe.modules.queryeditor.gui;

import pipe.gui.Grid;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArcPathPoint;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObject;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.*;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.*;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.Observable;
import java.util.Observer;

public class QueryView extends JLayeredPane implements Observer, QueryConstants, Printable
{

	public boolean									queryChanged	= false;

	public PerformanceTreeArc						arcBeingModified;

	private boolean											shiftDown		= false;
	private final PerformanceTreeSelectionObject	selection;
	private final PerformanceTreeZoomController		zoomControl;

	public QueryView() {
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
	 * Draws an initial ResultNode onto the canvas
	 */
	public void drawResultNode()
	{
		int canvasWidth = this.getWidth();
		int canvasHeight = this.getHeight();
		double xLoc = canvasWidth / 2 - QueryConstants.NODE_WIDTH / 2;
		double yLoc = canvasHeight * 0.05;

		QueryManager.setMode(QueryConstants.RESULT_NODE);

		PerformanceTreeObject ptObject = new ResultNode(Grid.getModifiedX(xLoc), Grid.getModifiedX(yLoc));
		ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject); // add
		// to
		// data
		// structure
		addNewPerformanceTreeObject(ptObject); // draw on canvas

		// display info about the node in the InfoBox
		String msg = QueryManager.addColouring("The node on the canvas is the topmost node in a "
												+ "Performance Tree query and represents the overall result of the query.<br><br>"
												+ "The required argument can be any operation node.");
		QueryManager.writeToInfoBox(msg);

		QueryManager.setMode(QueryConstants.SELECT);
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
	public void addNewPerformanceTreeObject(final PerformanceTreeObject newObject)
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
					if (newObject instanceof OperationNode)
					{
						// need to add arcs to the canvas as well
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

	public void add(final PerformanceTreeObject c)
	{
		super.add(c);
		c.addedToGui();

		if (c instanceof PerformanceTreeNode)
			setLayer(c, JLayeredPane.DEFAULT_LAYER.intValue() + QueryConstants.NODE_LAYER_OFFSET);
		else if (c instanceof PerformanceTreeArc)
			setLayer(c, JLayeredPane.DEFAULT_LAYER.intValue() + QueryConstants.ARC_LAYER_OFFSET);
		else if (c instanceof PerformanceTreeArcPathPoint)
			setLayer(c, JLayeredPane.DEFAULT_LAYER.intValue() + QueryConstants.ARC_POINT_LAYER_OFFSET);
	}

	@Override
	public void validate()
	{
		Component[] children = getComponents();
		for (Component c : children)
		{

			if (c instanceof PerformanceTreeArc)
			{
				((PerformanceTreeArc) c).getArcPath().createPath();
			}
			else if (c instanceof PerformanceTreeNode)
			{
				if (c instanceof OperationNode)
				{
					c.repaint();
				}
				else if (c instanceof ValueNode)
				{
					c.repaint();
				}
				else
				{
					c.repaint();
				}
			}
		}
		super.validate();
	}

	public void update(final Observable o, final Object diffObj)
	{
		if (diffObj instanceof PerformanceTreeObject && diffObj != null)
		{
			if (QueryManager.getMode() == QueryConstants.LOADING)
			{
				addNewPerformanceTreeObject((PerformanceTreeObject) diffObj);
			}
			QueryManager.checkTextEditable();
			QueryManager.checkTextEditing();

			repaint();
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

	public void zoom()
	{
		Component[] children = getComponents();
        for(Component aChildren : children)
        {
            //	if (children[i] instanceof Zoomable)
            //		((Zoomable) children[i]).zoomUpdate();
        }
		validate();
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

	public PerformanceTreeZoomController getZoomController()
	{
		return this.zoomControl;
	}

	class MouseHandler extends MouseInputAdapter
	{

		private Point	dragStart;
		String			msg;

		@Override
		public void mousePressed(final MouseEvent e)
		{
			PerformanceTreeObject ptObject;
			if (e.getButton() == MouseEvent.BUTTON1)
			{
				Point start = e.getPoint();
				double startX = start.x - QueryConstants.NODE_WIDTH / 2;
				double startY = start.y - QueryConstants.NODE_HEIGHT / 2;

				switch (QueryManager.getMode())
				{
					case RESULT_NODE :
						if (QueryManager.getData().resultNodeAlreadyCreated())
						{
							this.msg = QueryManager.addColouring("Only a single Result node is allowed in a query.");
							QueryManager.writeToInfoBox(this.msg);
						}
						else
						{
							ptObject = new ResultNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
							// assign node an ID and add it to the array of
							// nodes in the query
							ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
							addNewPerformanceTreeObject(ptObject);
							// display info about the node in the InfoBox
							this.msg = QueryManager.addColouring("The Result node is the topmost node in a Performance "
																	+ "Tree query and represents the overall result of the query.<br><br>"
																	+ "The required argument can be any operation node.");
							QueryManager.writeToInfoBox(this.msg);
						}
						break;
					case SEQUENTIAL_NODE :
						if (QueryManager.getData().sequentialNodeAlreadyCreated())
						{
							String msg = QueryManager.addColouring("Only a single SequentialNode is allowed in a query.");
							QueryManager.writeToInfoBox(msg);
						}
						else
						{
							ptObject = new SequentialNode(	Grid.getModifiedX(startX),
															Grid.getModifiedY(startY));
							ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
							addNewPerformanceTreeObject(ptObject);
						}
						break;
					case PASSAGETIMEDENSITY_NODE :
						ptObject = new PassageTimeDensityNode(	Grid.getModifiedX(startX),
																Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case DISTRIBUTION_NODE :
						ptObject = new DistributionNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case CONVOLUTION_NODE :
						ptObject = new ConvolutionNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case PROBININTERVAL_NODE :
						ptObject = new ProbInIntervalNode(	Grid.getModifiedX(startX),
															Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case PROBINSTATES_NODE :
						ptObject = new ProbInStatesNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case MOMENT_NODE :
						ptObject = new MomentNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case PERCENTILE_NODE :
						ptObject = new PercentileNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case FIRINGRATE_NODE :
						ptObject = new FiringRateNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case STEADYSTATEPROB_NODE :
						ptObject = new SteadyStateProbNode(	Grid.getModifiedX(startX),
															Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case STEADYSTATESTATES_NODE :
						ptObject = new SteadyStateStatesNode(	Grid.getModifiedX(startX),
																Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case STATESATTIME_NODE :
						ptObject = new StatesAtTimeNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case ININTERVAL_NODE :
						ptObject = new InIntervalNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case SUBSET_NODE :
						ptObject = new SubsetNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case DISCON_NODE :
						ptObject = new DisconNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case NEGATION_NODE :
						ptObject = new NegationNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case ARITHCOMP_NODE :
						ptObject = new ArithCompNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case ARITHOP_NODE :
						ptObject = new ArithOpNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case RANGE_NODE :
						ptObject = new RangeNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case STATES_NODE :
						ptObject = new StatesNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case ACTIONS_NODE :
						ptObject = new ActionsNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case NUM_NODE :
						ptObject = new NumNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case BOOL_NODE :
						ptObject = new BoolNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case STATEFUNCTION_NODE :
						ptObject = new StateFunctionNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case MACRO_NODE :
						ptObject = new MacroNode(Grid.getModifiedX(startX), Grid.getModifiedY(startY));
						ptObject = QueryManager.getData().addPerformanceTreeObject(ptObject);
						addNewPerformanceTreeObject(ptObject);
						break;
					case DRAG :
						this.dragStart = new Point(start);
				}
			}
		}

		@Override
		public void mouseMoved(final MouseEvent e)
		{
			if (QueryView.this.arcBeingModified != null)
			{
				QueryView.this.arcBeingModified.setEndPoint(Grid.getModifiedX(e.getX()),
															Grid.getModifiedY(e.getY()),
															QueryView.this.shiftDown);
			}
		}

		@Override
		public void mouseDragged(final MouseEvent e)
		{
			if (QueryManager.getMode() == QueryConstants.DRAG)
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
