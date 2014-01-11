/**
 * PerformanceTreeNode
 * 
 * This is the generic Performance Tree node that we can have in a query.
 * More specific nodes, such as Operation and Value Nodes extend this class 
 * for more specific behaviour.
 * 
 * @author Tamas Suto
 * @date 18/04/07
 */

package pipe.modules.queryeditor.gui.performancetrees;

import pipe.common.PetriNetNode;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroDefinition;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.io.MacroLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public abstract class PerformanceTreeNode extends PerformanceTreeObject
{

	protected PetriNetNode nodeType;

	private String					returnType			= "Undefined";
	// represents the type that the node has in terms of what type its output
	// has or what concept it represents. This is used when checking for the
	// validity of a node assignment. The possible values are defined as
	// constants in QueryConstants

	private String					incomingArcID;
	// the ID of the incoming arc connecting the node to its parent

	// Visualisation stuff
    private URL						nodeImageURL;
	private BufferedImage				nodeImage;

	protected GeneralPath				node;
	protected double					componentWidth;
	protected double					componentHeight;
	private Shape						proximityNode;

    private final ArrayList					arcAngleList		= new ArrayList();

	private static PerformanceTreeArc	someArc;
	private static final double			rootThreeOverTwo	= 0.5 * Math.sqrt(3);

	protected PerformanceTreeNode(final double positionXInput,
                                  final double positionYInput,
                                  final String idInput,
                                  final PetriNetNode typeInput) {
		super(positionXInput, positionYInput, idInput);
		setNodeType(typeInput);
		setupNode();
	}

	protected PerformanceTreeNode(final double positionXInput, final double positionYInput, final String idInput) {
		super(positionXInput, positionYInput, idInput);
		setupNode();
	}

	protected PerformanceTreeNode(final double positionXInput, final double positionYInput) {
		super(positionXInput, positionYInput);
		setupNode();
	}

	protected PerformanceTreeNode(final PerformanceTreeNode inputNode) {
		this(inputNode.getPositionX(), inputNode.getPositionY(), inputNode.getId(), inputNode.getNodeType());
	}

	private void setupNode()
	{
		this.incomingArcID = null;
		this.componentWidth = QueryConstants.NODE_WIDTH;
		this.componentHeight = QueryConstants.NODE_HEIGHT;
		outlineNode();
		updateBounds();
	}

	/**
	 * Draws the shape of the node. Can be overridden by subclasses if
	 * individual node shapes are required.
	 */
	private void outlineNode()
	{
		this.node = new GeneralPath();
		this.node.append(new Rectangle2D.Double(0, 0, this.componentWidth, this.componentHeight), false);
		this.proximityNode = (new BasicStroke(QueryConstants.NODE_PROXIMITY_RADIUS)).createStrokedShape(this.node);
	}

	public String getIncomingArcID()
	{
		return this.incomingArcID;
	}

	public void setIncomingArcID(final String arcID)
	{
		this.incomingArcID = arcID;
	}

	public void removeIncomingArcID()
	{
		this.incomingArcID = null;
	}

	public PerformanceTreeArc getIncomingArc()
	{
		if (this.incomingArcID != null)
		{
			if (MacroManager.getEditor() == null)
			{
				if (QueryManager.getData().getArc(this.incomingArcID) != null)
					return QueryManager.getData().getArc(this.incomingArcID);
				else return null;
			}
			else
			{
				if (MacroManager.getEditor().getArc(this.incomingArcID) != null)
					return MacroManager.getEditor().getArc(this.incomingArcID);
				else return null;
			}
		}
		else return null;
	}

	public PerformanceTreeNode getParentNode()
	{
		if (this.incomingArcID != null)
		{
			PerformanceTreeArc incomingArc;
			if (MacroManager.getEditor() == null)
				incomingArc = QueryManager.getData().getArc(this.incomingArcID);
			else incomingArc = MacroManager.getEditor().getArc(this.incomingArcID);
            return incomingArc.getSource();

		}
		else return null;
	}

	public PetriNetNode getNodeType()
	{
		return this.nodeType;
	}

	protected void setNodeType(final PetriNetNode typeInput)
	{
		this.nodeType = typeInput;
	}

	public String getReturnType()
	{
		return this.returnType;
	}

	public void setReturnType(final String returnTypeInput)
	{
		this.returnType = returnTypeInput;
	}

	/**
	 * Returns the natural language representation of the node and its children
	 * 
	 * @return
	 */
	public abstract String printTextualRepresentation();

	public double getComponentWidth()
	{
		return this.componentWidth;
	}

	public double getComponentHeight()
	{
		return this.componentHeight;
	}

	@Override
	public void paintComponent(final Graphics g)
	{
		super.paintComponent(g);

		Graphics2D g2 = (Graphics2D) g;
		AffineTransform saveXform = g2.getTransform();
		AffineTransform scaledXform = getZoomController().getTransform();

		g2.translate(QueryConstants.COMPONENT_DRAW_OFFSET, QueryConstants.COMPONENT_DRAW_OFFSET);
		g2.transform(scaledXform);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		if (this.selected && !PerformanceTreeObject.ignoreSelection)
			g2.setColor(QueryConstants.SELECTION_FILL_COLOUR);
		else g2.setColor(QueryConstants.ELEMENT_FILL_COLOUR);
		g2.fill(this.node);

        boolean highlighted = false;
        if (highlighted)
			g2.setPaint(QueryConstants.HIGHLIGHTED_COLOUR);
		else if (this.selected && !PerformanceTreeObject.ignoreSelection)
			g2.setPaint(QueryConstants.SELECTION_LINE_COLOUR);
		else g2.setPaint(QueryConstants.ELEMENT_LINE_COLOUR);

		drawNode(g2);
		g2.setTransform(saveXform);
	}

	private void drawNode(final Graphics2D g2d)
	{
		if (this.nodeImageURL == null)
			this.nodeImageURL = Thread	.currentThread()
										.getContextClassLoader()
										.getResource(QueryManager.imgPath + this.nodeType + ".png");
		if (this.nodeImage == null)
			setNodeImage(this.nodeImageURL);
		int nodeImageWidth = this.nodeImage.getWidth(null);
		int nodeImageHeight = this.nodeImage.getHeight(null);
		g2d.drawImage(this.nodeImage, 0, 0, nodeImageWidth, nodeImageHeight, null);
	}

	protected void setNodeImage(final URL nodeImageURLInput)
	{
		try
		{
			this.nodeImage = ImageIO.read(nodeImageURLInput);
		}
		catch (IOException e)
		{
			System.out.println(e);
		}
		repaint();
	}

	public void updateEndPoint(final PerformanceTreeArc arc)
	{
		Iterator arcIterator = this.arcAngleList.iterator();
		boolean match = false;
		while (arcIterator.hasNext())
		{
			ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			if (thisArc.arc == arc)
			{
				thisArc.calcAngle();
				match = true;
				break;
			}
		}
		if (!match)
		{
			this.arcAngleList.add(new ArcAngleCompare(arc, this));
		}
		Collections.sort(this.arcAngleList);
		updateEndPoints();
	}

	private void updateEndPoints()
	{
		Iterator arcIterator = this.arcAngleList.iterator();
		ArrayList top = new ArrayList();
		ArrayList bottom = new ArrayList();
		ArrayList left = new ArrayList();
		ArrayList right = new ArrayList();

		arcIterator = this.arcAngleList.iterator();
		while (arcIterator.hasNext())
		{
			ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			double thisAngle = thisArc.angle;
			if (Math.cos(thisAngle) > PerformanceTreeNode.rootThreeOverTwo)
			{
				top.add(thisArc);
				thisArc.arc.setPathToNodeAngle(90);
			}
			else if (Math.cos(thisAngle) < -PerformanceTreeNode.rootThreeOverTwo)
			{
				bottom.add(thisArc);
				thisArc.arc.setPathToNodeAngle(270);
			}
			else if (Math.sin(thisAngle) > 0)
			{
				left.add(thisArc);
				thisArc.arc.setPathToNodeAngle(180);
			}
			else
			{
				right.add(thisArc);
				thisArc.arc.setPathToNodeAngle(0);
			}
		}

		AffineTransform transform = AffineTransform.getRotateInstance(Math.toRadians(Math.PI));
		Point2D.Double transformed = new Point2D.Double();

		if (getZoomController() != null)
		{
			AffineTransform zoomTransform = getZoomController().getTransform();
			transform.concatenate(zoomTransform);
		}

		arcIterator = top.iterator();
		transform.transform(new Point2D.Double(1, 0.5 * QueryConstants.NODE_HEIGHT), transformed); // +1
		// due
		// to
		// rounding
		// making
		// it
		// off
		// by 1
		while (arcIterator.hasNext())
		{
			ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			if (thisArc.sourceOrTarget())
				thisArc.arc.setTargetLocation(	this.positionX + centreOffsetLeft() + transformed.x,
												this.positionY + centreOffsetTop() + transformed.y);
			else thisArc.arc.setSourceLocation(	this.positionX + centreOffsetLeft() + transformed.x,
												this.positionY + centreOffsetTop() + transformed.y);
		}

		arcIterator = bottom.iterator();
		transform.transform(new Point2D.Double(0, -0.5 * QueryConstants.NODE_HEIGHT), transformed);
		while (arcIterator.hasNext())
		{
			ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			if (thisArc.sourceOrTarget())
				thisArc.arc.setTargetLocation(	this.positionX + centreOffsetLeft() + transformed.x,
												this.positionY + centreOffsetTop() + transformed.y);
			else thisArc.arc.setSourceLocation(	this.positionX + centreOffsetLeft() + transformed.x,
												this.positionY + centreOffsetTop() + transformed.y);
		}

		arcIterator = left.iterator();
		double inc = QueryConstants.NODE_HEIGHT / (left.size() + 1);
		double current = QueryConstants.NODE_HEIGHT / 2 - inc;
		while (arcIterator.hasNext())
		{
			ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			transform.transform(new Point2D.Double(-0.5 * QueryConstants.NODE_WIDTH, current + 1),
								transformed); // +1 due to rounding making it
			// off by 1
			if (thisArc.sourceOrTarget())
				thisArc.arc.setTargetLocation(	this.positionX + centreOffsetLeft() + transformed.x,
												this.positionY + centreOffsetTop() + transformed.y);
			else thisArc.arc.setSourceLocation(	this.positionX + centreOffsetLeft() + transformed.x,
												this.positionY + centreOffsetTop() + transformed.y);
			current -= inc;
		}

		inc = QueryConstants.NODE_HEIGHT / (right.size() + 1);
		current = -QueryConstants.NODE_HEIGHT / 2 + inc;
		arcIterator = right.iterator();
		while (arcIterator.hasNext())
		{
			ArcAngleCompare thisArc = (ArcAngleCompare) arcIterator.next();
			transform.transform(new Point2D.Double(+0.5 * QueryConstants.NODE_WIDTH, current), transformed);
			if (thisArc.sourceOrTarget())
				thisArc.arc.setTargetLocation(	this.positionX + centreOffsetLeft() + transformed.x,
												this.positionY + centreOffsetTop() + transformed.y);
			else thisArc.arc.setSourceLocation(	this.positionX + centreOffsetLeft() + transformed.x,
												this.positionY + centreOffsetTop() + transformed.y);
			current += inc;
		}
	}

	@Override
	public boolean contains(final int x, final int y)
	{
		int zoomPercentage = getZoomController().getPercent();
		double unZoomedX = (x - QueryConstants.COMPONENT_DRAW_OFFSET) / (zoomPercentage / 100.0);
		double unZoomedY = (y - QueryConstants.COMPONENT_DRAW_OFFSET) / (zoomPercentage / 100.0);
		if (MacroManager.getEditor() == null)
			PerformanceTreeNode.someArc = QueryManager.getView().arcBeingModified;
		else PerformanceTreeNode.someArc = MacroManager.getView().arcBeingModified;

		if (PerformanceTreeNode.someArc != null)
		{
			// an arc is being modified
			if ((this.proximityNode.contains((int) unZoomedX, (int) unZoomedY) || this.node.contains(	(int) unZoomedX,
																										(int) unZoomedY)) &&
				areNotSameType(PerformanceTreeNode.someArc.getSource()))
			{
				// the arc's endpoint falls somewhere inside the node, so adjust
				// endpoint with snap-to functionality
				PerformanceTreeNode.someArc.updateArcPosition();
				return true;
			}
			else
			{
				if (PerformanceTreeNode.someArc.getTarget() == this)
				{
					removeArcCompareObject(PerformanceTreeNode.someArc);
					updateConnected();
				}
				return false;
			}
		}
		else return this.node.contains((int) unZoomedX, (int) unZoomedY);
	}

	/** Handles selection for Nodes */
	@Override
	public void select()
	{
		if (this.selectable && !this.selected)
		{
			super.select();
		}
	}

	/**
	 * Returns the distance between the outside of the component to the centre,
	 * in order to position the centre of the node where the mouse clicks on the
	 * screen
	 * 
	 * @return Top offset of node
	 */
    private int centreOffsetTop()
	{
		double zoomBy = getZoomController().getPercent() * 0.01;
		return (int) (zoomBy * this.componentHeight / 2.0);
	}

	/**
	 * Returns the distance between the outside of the component to the centre,
	 * in order to position the centre of the node where the mouse clicks on the
	 * screen
	 * 
	 * @return Left offset of Node
	 */
    private int centreOffsetLeft()
	{
		double zoomBy = getZoomController().getPercent() * 0.01;
		return (int) (zoomBy * this.componentWidth / 2.0);
	}

	/**
	 * Returns the width bounds we want to use when initially creating the node
	 * on the gui
     * @return
     */
    protected int boundsWidth()
	{
		return ImageObserver.WIDTH + 25;
	}

	/**
	 * Returns the height bounds we want to use when initially creating the node
	 * on the gui
     * @return
     */
    protected int boundsHeight()
	{
		return ImageObserver.HEIGHT + 15;
	}

	/**
	 * Returns the distance between the outside of the component to the centre,
	 * in order to position the centre of the place where the mouse clicks on
	 * the screen
     * @return
     */
	public int topOffset()
	{
		return boundsHeight() / 2;
	}

	/**
	 * Returns the distance between the outside of the component to the centre,
	 * in order to position the centre of the place where the mouse clicks on
	 * the screen
     * @return
     */
	public int leftOffset()
	{
		return boundsWidth() / 2;
	}

	/** Gets the center of the component
     * @return*/
	public Point2D.Double getCentre()
	{
		return new Point2D.Double(this.positionX + getWidth() / 2.0, this.positionY + getHeight() / 2.0);
	}

	/** Sets the center of the component to position x, y
     * @param x
     * @param y*/
	public void setCentre(final double x, final double y)
	{
		setPositionX(x - getWidth() / 2.0);
		setPositionY(y - getHeight() / 2.0);
		updateBounds();
		updateConnected();
	}

	public void removeArcCompareObject(final PerformanceTreeArc a)
	{
		Iterator arcIterator = this.arcAngleList.iterator();
		while (arcIterator.hasNext())
		{
			if (((ArcAngleCompare) arcIterator.next()).arc == a)
				arcIterator.remove();
		}
	}

	@Override
	public void zoomUpdate()
	{
		updateBounds();
		updateConnected();
	}

	/** Calculates the BoundsOffsets used for setBounds() method */
	@Override
	public void updateBounds()
	{
		int scaleFactor = 100;
		if (getZoomController() != null)
		{
			scaleFactor = getZoomController().getPercent();
		}
		this.positionX = this.locationX * scaleFactor / 100.0;
		this.positionY = this.locationY * scaleFactor / 100.0;
		this.bounds.setBounds(	(int) this.positionX,
								(int) this.positionY,
								(int) (this.componentWidth * scaleFactor / 100.0),
								(int) (this.componentHeight * scaleFactor / 100.0));
		this.bounds.grow(QueryConstants.COMPONENT_DRAW_OFFSET, QueryConstants.COMPONENT_DRAW_OFFSET);
		setBounds(this.bounds);
	}

	/** Updates location of any attached arcs */
	public void updateConnected()
	{
		// update incoming arc
		if (this.incomingArcID != null)
		{
			PerformanceTreeArc incomingArc;
			if (MacroManager.getEditor() == null)
			{
				incomingArc = QueryManager.getData().getArc(this.incomingArcID);
				updateEndPoint(incomingArc);
				incomingArc.updateArcPosition();
				QueryManager.getData().updateArc(incomingArc);
			}
			else
			{
				if (MacroManager.getMode() == QueryConstants.LOADING &&
					QueryManager.getMode() != QueryConstants.LOADING)
				{
					// a macro is being loaded in from an XML file
					MacroDefinition macro = MacroLoader.getMacro();
					incomingArc = macro.getMacroArc(this.incomingArcID);
					updateEndPoint(incomingArc);
					incomingArc.updateArcPosition();
					macro.updateMacroArc(incomingArc);
				}
				else
				{
					// a macro is being created on the canvas
					incomingArc = MacroManager.getEditor().getArc(this.incomingArcID);
					updateEndPoint(incomingArc);
					incomingArc.updateArcPosition();
					MacroManager.getEditor().updateArc(incomingArc);
				}
			}
		}
	}

	@Override
	public void addedToGui()
	{
		// no function, but is called by QueryView.add()
	}

	public abstract boolean childAssignmentValid(PerformanceTreeArc arc, PerformanceTreeNode node);

	// this is only really implemented in OperationNode, since they are the only
	// kinds of nodes that
	// have children

	@Override
	public PerformanceTreeNode clone()
	{
        return (PerformanceTreeNode) super.clone();
	}

	class ArcAngleCompare implements Comparable
	{
		public final static boolean			SOURCE	= false;
		public final static boolean			TARGET	= true;
		final PerformanceTreeArc					arc;
		private final PerformanceTreeNode	node;
		double								angle;

		public ArcAngleCompare(final PerformanceTreeArc _arc, final PerformanceTreeNode _node) {
			this.arc = _arc;
			this.node = _node;
			calcAngle();
		}

		public int compareTo(final Object arg0)
		{
			double angle2 = ((ArcAngleCompare) arg0).angle;
			return this.angle < angle2 ? -1 : this.angle == angle2 ? 0 : 1;
		}

		void calcAngle()
		{
			int index = sourceOrTarget() ? this.arc.getArcPath().getEndIndex() - 1 : 1;
			Point2D.Double p1 = new Point2D.Double(	PerformanceTreeNode.this.positionX + centreOffsetLeft(),
													PerformanceTreeNode.this.positionY + centreOffsetTop());
			Point2D.Double p2 = new Point2D.Double(	this.arc.getArcPath().getPoint(index).x,
													this.arc.getArcPath().getPoint(index).y);

			if (p1.y <= p2.y)
				this.angle = Math.atan((p1.x - p2.x) / (p2.y - p1.y));
			else this.angle = Math.atan((p1.x - p2.x) / (p2.y - p1.y)) + Math.PI;

			// This makes sure the angle overlap lies at the intersection
			// between edges of a node
			if (this.angle < Math.toRadians(30))
				this.angle += 2 * Math.PI;

			// Needed to eliminate an exception on Windows
			if (p1.equals(p2))
				this.angle = 0;
		}

		public boolean sourceOrTarget()
		{
			return this.arc.getSource() == this.node ? ArcAngleCompare.SOURCE : ArcAngleCompare.TARGET;
		}
	}

}