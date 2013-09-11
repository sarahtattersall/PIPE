/**
 * ProgressView
 * 
 * Replicates the active Performance Tree query so that its evaluation
 * progress can be visualised
 * 
 * @author Tamas Suto
 * @date 24/11/07
 */

package pipe.modules.queryeditor.evaluator.gui;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;

import javax.swing.JDialog;
import javax.swing.JLayeredPane;

import pipe.modules.interfaces.Cleanable;
import pipe.common.EvaluationStatus;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryresult.ResultWrapper;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.evaluator.NodeStatusUpdater;
import pipe.modules.queryeditor.evaluator.QueryAnalysisException;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArcPathPoint;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObject;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObjectLabel;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.OperationNode;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.RangeNode;
import pipe.modules.queryeditor.gui.performancetrees.valuenodes.ValueNode;
import pipe.modules.queryeditor.io.QueryData;

public class ProgressView extends JLayeredPane implements QueryConstants, EvaluatorGuiLoggingHandler
{

	private static final long			serialVersionUID	= 1L;
    private ArrayList<QueryTreeNode>	queryTreeNodes;
	private JDialog						parent;

	public ProgressView() {
		this.setLayout(null);
		this.setOpaque(true);
		this.setDoubleBuffered(true);
		this.setAutoscrolls(true);
		this.setBackground(QueryConstants.ELEMENT_FILL_COLOUR);
		this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	@Override
	public Component add(final Component objectToAdd)
	{
		if (objectToAdd instanceof PerformanceTreeNode)
		{
			// convert PerformanceTreeNode to a QueryTreeNode
			QueryTreeNode node;
			if (objectToAdd instanceof ValueNode || objectToAdd instanceof RangeNode)
			{
				node = new QueryValueNode((PerformanceTreeNode) objectToAdd);
			}
			else
			{
				node = new QueryOperationNode((OperationNode) objectToAdd);
				node.addMouseListener(new QueryTreeNodeHandler(this.parent, (QueryOperationNode) node));
			}
			super.add(node);
			this.setLayer(node, JLayeredPane.DEFAULT_LAYER.intValue() + QueryConstants.NODE_LAYER_OFFSET);
			this.queryTreeNodes.add(node);

			// check if node labels need to be displayed
			if (objectToAdd instanceof ValueNode)
			{
				((ValueNode) objectToAdd).displayNodeLabel();
			}
			else if (objectToAdd instanceof MacroNode)
			{
				((MacroNode) objectToAdd).displayNodeLabel();
			}

			if (node instanceof QueryOperationNode)
			{
				// add status indicator to canvas
				((QueryOperationNode) node).showStatusIndicator();
				((QueryOperationNode) node).setStatus(QueryConstants.EVALNOTSTARTED);
			}

			return node;
		}
		else if (objectToAdd instanceof PerformanceTreeArc)
		{
			final PerformanceTreeArc arc = (PerformanceTreeArc) objectToAdd;
			super.add(arc);
			this.setLayer(arc, JLayeredPane.DEFAULT_LAYER.intValue() + QueryConstants.ARC_LAYER_OFFSET);
			final String arcLabelText = arc.getArcLabel();
			final boolean arcLabelRequired = arc.getArcLabelObject().getRequired();
			arc.setArcLabel(arcLabelText, arcLabelRequired);
			arc.updateLabelPosition();
			return arc;
		}
		else if (objectToAdd instanceof PerformanceTreeArcPathPoint)
		{
			final PerformanceTreeArcPathPoint arcPathPoint = (PerformanceTreeArcPathPoint) objectToAdd;
			super.add(arcPathPoint);
			this.setLayer(arcPathPoint, JLayeredPane.DEFAULT_LAYER.intValue() +
										QueryConstants.ARC_POINT_LAYER_OFFSET);
			return arcPathPoint;
		}
		else if (objectToAdd instanceof PerformanceTreeObjectLabel)
		{
			final PerformanceTreeObjectLabel label = (PerformanceTreeObjectLabel) objectToAdd;
			super.add(label);
			return label;
		}
		else if (objectToAdd instanceof StatusIndicator)
		{
			final StatusIndicator statusIndicator = (StatusIndicator) objectToAdd;
			super.add(statusIndicator);
			this.setLayer(statusIndicator, JLayeredPane.DEFAULT_LAYER.intValue() +
											QueryConstants.STATUS_INDICATOR_LAYER_OFFSET);
			return statusIndicator;
		}
		return null;
	}

	public void drawQueryTree(final Dimension progressViewPanelSize)
	{
		// progressView canvas info
		final double pvWidth = progressViewPanelSize.getWidth();
		final double pvHeight = progressViewPanelSize.getHeight();

		// points of reference for bounding box of current query tree on
		// queryView
		final Point2D.Double qvbbTopLeft = new Point2D.Double(0, 0);
		final Point2D.Double qvbbTopRight = new Point2D.Double(0, 0);
		final Point2D.Double qvbbBottomLeft = new Point2D.Double(0, 0);
		final Point2D.Double qvbbBottomRight = new Point2D.Double(0, 0);

		// displacement values
		double displacementX, displacementY;

		// find out exact coordinates of bounding box of query tree on queryView
		boolean boundingBoxInitialised = false;
		final double nodeHeight = QueryConstants.NODE_HEIGHT;
		final double nodeWidth = QueryConstants.NODE_WIDTH;
		this.queryTreeNodes = new ArrayList<QueryTreeNode>();
        QueryData queryData = QueryManager.getData().clone();
		final ArrayList<PerformanceTreeObject> ptObjects = queryData.getPerformanceTreeObjects();
		Iterator<PerformanceTreeObject> i = ptObjects.iterator();
		while (i.hasNext())
		{
			final PerformanceTreeObject ptObject = i.next();
			if (ptObject instanceof PerformanceTreeNode)
			{
				final PerformanceTreeNode node = (PerformanceTreeNode) ptObject;
				if (!boundingBoxInitialised)
				{
					// set up bounding box to be the first node's dimensions
					qvbbTopLeft.x = node.getPositionX();
					qvbbTopLeft.y = node.getPositionY();
					qvbbBottomLeft.x = node.getPositionX();
					qvbbBottomLeft.y = node.getPositionY() + nodeHeight;
					qvbbTopRight.x = node.getPositionX() + nodeWidth;
					qvbbTopRight.y = node.getPositionY();
					qvbbBottomRight.x = node.getPositionX() + nodeWidth;
					qvbbBottomRight.y = node.getPositionY() + nodeHeight;
					boundingBoxInitialised = true;
				}
				else
				{
					// gradually expand bounding box
					if (qvbbTopLeft.x > node.getPositionX())
					{
						qvbbTopLeft.x = node.getPositionX();
					}
					if (qvbbTopLeft.y > node.getPositionY())
					{
						qvbbTopLeft.y = node.getPositionY();
					}
					if (qvbbBottomLeft.x > node.getPositionX())
					{
						qvbbBottomLeft.x = node.getPositionX();
					}
					if (qvbbBottomLeft.y < node.getPositionY() + nodeHeight)
					{
						qvbbBottomLeft.y = node.getPositionY() + nodeHeight;
					}
					if (qvbbTopRight.x < node.getPositionX() + nodeWidth)
					{
						qvbbTopRight.x = node.getPositionX() + nodeWidth;
					}
					if (qvbbTopRight.y > node.getPositionY())
					{
						qvbbTopRight.y = node.getPositionY();
					}
					if (qvbbBottomRight.x < node.getPositionX() + nodeWidth)
					{
						qvbbBottomRight.x = node.getPositionX() + nodeWidth;
					}
					if (qvbbBottomRight.y < node.getPositionY() + nodeHeight)
					{
						qvbbBottomRight.y = node.getPositionY() + nodeHeight;
					}
				}
			}
		}

		final double bbWidth = qvbbTopRight.x - qvbbTopLeft.x;
		final double bbHeight = qvbbBottomLeft.y - qvbbTopLeft.y;

		// align bounding box to (0,0)
		displacementX = 0.0 - qvbbTopLeft.x;
		displacementY = 0.0 - qvbbTopLeft.y + QueryConstants.STATUS_INDICATOR_HEIGHT / 2;

		// align to center position
		if (bbWidth <= pvWidth && bbHeight <= pvHeight)
		{
			// Only do this if the bounding box's size doesn't exceed the
			// viewport's size.
			// If that were the case, because of the centering, some stuff would
			// be cut out
			displacementX += (pvWidth - bbWidth) / 2.0;
			displacementY += (pvHeight - bbHeight) / 2.0;
		}

		// translate all objects' coordinates according to the displacement
		// value
		i = ptObjects.iterator();
		while (i.hasNext())
		{
			final PerformanceTreeObject ptObject = i.next();
			if (ptObject instanceof PerformanceTreeNode)
			{
				final double newStartXPosition = ptObject.getPositionX() + displacementX;
				final double newStartYPosition = ptObject.getPositionY() + displacementY;
				ptObject.setPositionX(newStartXPosition);
				ptObject.setPositionY(newStartYPosition);
				this.add(ptObject);
			}
			else if (ptObject instanceof PerformanceTreeArc)
			{
				final PerformanceTreeArc arc = (PerformanceTreeArc) ptObject;
				if (!(!arc.isRequired() && arc.getTargetID() == null))
				{
					arc.translatePoints(displacementX, displacementY);
					this.add(arc);
				}
			}
			this.repaint();
		}

		this.updatePreferredSize();
	}

	public void ensurePainted()
	{
		super.invalidate();
		this.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#getName()
	 */
	@Override
	public String getName()
	{
		return "Evaluation";
	}

	QueryOperationNode getNode(final String nodeID) throws QueryAnalysisException
	{
		for (final QueryTreeNode node : this.queryTreeNodes)
		{
			if (node.getId().equals(nodeID))
			{
				if (node instanceof QueryOperationNode)
				{
					return (QueryOperationNode) node;
				}
				else
				{
					return null;
				}
			}
		}
		throw new QueryAnalysisException(nodeID + " not found");
	}

	public int getNumOpNodes()
	{
		int count = 0;
		for (final QueryTreeNode node : this.queryTreeNodes)
		{
			count += node instanceof QueryOperationNode ? 1 : 0;
		}
		return count;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.Component#setName(java.lang.String)
	 */
	@Override
	public void setName(final String name)
	{
		// TODO Auto-generated method stub
		super.setName(name);
	}

	public void setNodeResult(final ResultWrapper w, final Cleanable caller)
	{
		QueryOperationNode n = null;
		try
		{
			if (w.getType().isOpNode())
			{
				final String nodeId = w.getNodeID();
				n = this.getNode(nodeId);
				n.setResult(w);
			}
		}
		catch (final QueryAnalysisException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(Level.WARNING, "Couldn't set result for " + w.getType() +
																	" " + w.getNodeID(), e);
		}

	}

	public void setParent(final JDialog parent)
	{
		this.parent = parent;
	}

	public void updateAllNodeStatus(final EvaluationStatus status)
	{
		for (final QueryTreeNode node : this.queryTreeNodes)
		{
			if (node instanceof QueryOperationNode)
			{
				((QueryOperationNode) node).setStatus(status.toString());
				node.repaint();
			}
		}
	}

	public void updateNodeStatus(final NodeStatusUpdater updater)
	{
		QueryOperationNode node = null;
		try
		{
			node = this.getNode(updater.getId());
			if (node != null)
			{
				final String newStatus = updater.getEvalStatus().toString();
				if (!node.getStatus().equals(newStatus))
				{
					QueryManager.getProgressWindow().incrementProgressBar(newStatus);
				}
				node.setStatus(newStatus);
				node.repaint();
			}
		}
		catch (final QueryAnalysisException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(Level.WARNING, "Couldn't update node status " +
																	updater.getId(), e);
		}
	}

	void updatePreferredSize()
	{
		// iterate over tree objects and setPreferredSize() accordingly
		final Component[] components = this.getComponents();
		final Dimension d = new Dimension(0, 0);
		int x, y;
		for (final Component element : components)
		{
			final Rectangle r = element.getBounds();
			x = r.x + r.width + 100;
			y = r.y + r.height + 100;
			if (x > d.width)
			{
				d.width = x;
			}
			if (y > d.height)
			{
				d.height = y;
			}
		}
		this.setPreferredSize(d);
	}

}
