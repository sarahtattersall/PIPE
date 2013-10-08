/**
 * PerformanceTreeArc
 * 
 * Defines a Performance Tree arc that interconnects two nodes
 * 
 * @author Tamas Suto
 * @date 20/04/07
 */

package pipe.modules.queryeditor.gui.performancetrees;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.QueryView;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroDefinition;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroView;
import pipe.modules.queryeditor.gui.performancetrees.operationnodes.OperationNode;
import pipe.modules.queryeditor.io.MacroLoader;
import pipe.modules.queryeditor.io.QueryLoader;

public class PerformanceTreeArc extends PerformanceTreeObject
{

	private String						sourceID	= null;
	private String						targetID	= null;
	private PerformanceTreeObjectLabel	arcLabel	= new PerformanceTreeObjectLabel();
	private PerformanceTreeArcPath		myPath		= new PerformanceTreeArcPath(this);
	private boolean						required;										// indicate whether this is an arc for a
								// required child or not

	private static Point2D.Double		point;

	public PerformanceTreeArc(	final double startPositionXInput,
								final double startPositionYInput,
								final double endPositionXInput,
								final double endPositionYInput,
								final PerformanceTreeNode sourceInput,
								final PerformanceTreeNode targetInput,
								final String labelInput,
								final String idInput) {
		this(	startPositionXInput,
				startPositionYInput,
				endPositionXInput,
				endPositionYInput,
				sourceInput,
				labelInput,
				idInput);
		setTarget(targetInput);
	}

	private PerformanceTreeArc(final double startPositionXInput,
                               final double startPositionYInput,
                               final double endPositionXInput,
                               final double endPositionYInput,
                               final PerformanceTreeNode sourceInput,
                               final String labelInput,
                               final String idInput) {
		this(startPositionXInput, startPositionYInput, endPositionXInput, endPositionYInput, idInput);
		setSource(sourceInput);
		if (MacroManager.getEditor() == null)
		{
			if (QueryManager.getEditor() != null)
			{
				updateArcPosition();
				updateArcPosition();
			}
		}
		else
		{
			updateArcPosition();
			updateArcPosition();
		}
		boolean labelsRequired = ((OperationNode) sourceInput).getArcLabelsRequired();
		setArcLabel(labelInput, labelsRequired);
	}

	public PerformanceTreeArc(	final double startPositionXInput,
								final double startPositionYInput,
								final double endPositionXInput,
								final double endPositionYInput,
								final PerformanceTreeNode sourceInput,
								final PerformanceTreeNode targetInput,
								final String idInput) {
		this(startPositionXInput, startPositionYInput, endPositionXInput, endPositionYInput, idInput);
		setSource(sourceInput);
		setTarget(targetInput);
		if (MacroManager.getEditor() == null)
		{
			if (QueryManager.getEditor() != null)
			{
				updateArcPosition();
				updateArcPosition();
			}
		}
		else
		{
			updateArcPosition();
			updateArcPosition();
		}
	}

	public PerformanceTreeArc(	final double startPositionXInput,
								final double startPositionYInput,
								final double endPositionXInput,
								final double endPositionYInput,
								final PerformanceTreeNode sourceInput,
								final String labelInput,
								final boolean labelsRequired) {
		this(startPositionXInput, startPositionYInput, endPositionXInput, endPositionYInput);
		setSource(sourceInput);
		if (MacroManager.getEditor() == null)
		{
			if (QueryManager.getEditor() != null)
			{
				updateArcPosition();
				updateArcPosition();
			}
		}
		else
		{
			updateArcPosition();
			updateArcPosition();
		}
		setArcLabel(labelInput, labelsRequired);
	}

	// used in QueryLoader and MacroLoader
	public PerformanceTreeArc(	final double startPositionXInput,
								final double startPositionYInput,
								final double endPositionXInput,
								final double endPositionYInput,
								final String sourceID,
								final String targetID,
								final String labelInput,
								final boolean labelRequired,
								final boolean arcRequired,
								final String idInput) {
		this(	startPositionXInput,
				startPositionYInput,
				endPositionXInput,
				endPositionYInput,
				labelInput,
				labelRequired,
				idInput);
		setSourceID(sourceID);
		setTargetID(targetID);
		setRequired(arcRequired);
		if (MacroManager.getEditor() == null)
		{
			if (QueryManager.getEditor() != null)
			{
				updateArcPosition();
				updateArcPosition();
			}
		}
		else
		{
			updateArcPosition();
			updateArcPosition();
		}
	}

	private PerformanceTreeArc(final double startPositionXInput,
                               final double startPositionYInput,
                               final double endPositionXInput,
                               final double endPositionYInput,
                               final String labelInput,
                               final boolean labelRequired,
                               final String idInput) {
		this(startPositionXInput, startPositionYInput, endPositionXInput, endPositionYInput, idInput);
		setArcLabel(labelInput, labelRequired);
	}

	private PerformanceTreeArc(final double startPositionXInput,
                               final double startPositionYInput,
                               final double endPositionXInput,
                               final double endPositionYInput,
                               final String idInput) {
		this(startPositionXInput, startPositionYInput, endPositionXInput, endPositionYInput);
		this.id = idInput;
	}

	private PerformanceTreeArc(final double startPositionXInput,
                               final double startPositionYInput,
                               final double endPositionXInput,
                               final double endPositionYInput) {
		this.myPath.addPoint(	(float) startPositionXInput,
								(float) startPositionYInput,
								PerformanceTreeArcPathPoint.STRAIGHT);
		this.myPath.addPoint(	(float) endPositionXInput,
								(float) endPositionYInput,
								PerformanceTreeArcPathPoint.STRAIGHT);
		this.myPath.createPath();
		updateBounds();
	}

	@Override
	public String getId()
	{
		if (this.id != null)
			return this.id;
		else return null;
	}

	public String getSourceID()
	{
		return this.sourceID;
	}

	private void setSourceID(final String newID)
	{
		this.sourceID = newID;
	}

	public String getTargetID()
	{
		return this.targetID;
	}

	private void setTargetID(final String newID)
	{
		this.targetID = newID;
		updateArcPosition();
	}

	public PerformanceTreeNode getSource()
	{
		if (this.sourceID != null)
		{
			if (QueryManager.getMode() == QueryConstants.LOADING &&
				MacroManager.getMode() == QueryConstants.LOADING)
			{
				// when a macro is loaded in in the background
				MacroDefinition macro = QueryLoader.getMacro();
				return macro.getMacroNode(this.sourceID);
			}

			if (MacroManager.getEditor() == null)
				return QueryManager.getData().getNode(this.sourceID);
			else return MacroManager.getEditor().getNode(this.sourceID);
		}
		else return null;
	}

	public void setSource(final PerformanceTreeNode sourceNode)
	{
		if (sourceNode != null)
			setSourceID(sourceNode.getId());
		else setSourceID(null);
	}

	public PerformanceTreeNode getTarget()
	{
		if (this.targetID != null)
		{
			if (QueryManager.getMode() == QueryConstants.LOADING &&
				MacroManager.getMode() == QueryConstants.LOADING)
			{
				// when a macro is loaded in in the background
				MacroDefinition macro = QueryLoader.getMacro();
				return macro.getMacroNode(this.targetID);
			}

			if (MacroManager.getEditor() == null)
				return QueryManager.getData().getNode(this.targetID);
			else return MacroManager.getEditor().getNode(this.targetID);
		}
		else return null;
	}

	public void setTarget(final PerformanceTreeNode targetNode)
	{
		if (targetNode != null)
			setTargetID(targetNode.getId());
		else setTargetID(null);
	}

	public String getArcLabel()
	{
		return this.arcLabel.getText();
	}

	public PerformanceTreeObjectLabel getArcLabelObject()
	{
		return this.arcLabel;
	}

	private void setArcLabelObject(final PerformanceTreeObjectLabel label)
	{
		this.arcLabel = label;
	}

	public void setArcLabel(final String labelInput, final boolean labelRequired)
	{
		this.arcLabel.setText(labelInput);
		this.arcLabel.setRequired(labelRequired);
		setArcLabelPosition();
		this.arcLabel.updateSize();
		if (labelRequired)
		{
			if (!(QueryManager.getMode() == QueryConstants.LOADING && MacroManager.getMode() == QueryConstants.LOADING))
			{
				// We don't want to load in the arc labels of a macro arc when
				// we're just
				// loading in the query and add the macro definition to
				// QueryData in the
				// background
				addArcLabelToContainer();
			}
		}
	}

	private void setArcLabelPosition()
	{
		if (this.myPath.getEndIndex() > 0)
		{
			Point2D.Float startPoint = this.myPath.getPoint(this.myPath.getEndIndex() - 1);
			Point2D.Float endPoint = this.myPath.getEndPoint();
			double pointX = 0.0;
			double pointY = 0.0;

			if (startPoint.x < endPoint.x)
			{
				// arc's endpoint is to the right of its startpoint
				pointX = startPoint.x + (endPoint.x - startPoint.x) * 3 / 5;
			}
			else
			{
				// arc's endpoint is to the left of its startpoint
				pointX = endPoint.x + (startPoint.x - endPoint.x) * 2 / 5;
			}

			if (startPoint.y < endPoint.y)
			{
				// arc's endpoint is below of its startpoint
				pointY = startPoint.y + (endPoint.y - startPoint.y) * 3 / 5;
			}
			else
			{
				// arc's endpoint is above of its startpoint
				pointY = endPoint.y + (startPoint.y - endPoint.y) * 2 / 5;
			}

			this.arcLabel.setPosition((int) pointX, (int) pointY);
		}
	}

	private void addArcLabelToContainer()
	{
		if (MacroManager.getEditor() == null)
		{
			// we want the label to be put on the main drawing canvas
			QueryView queryView = QueryManager.getView();
			if (queryView != null)
			{
				// If this is the first time the arc label is being added then
				// the remove operation will have no affect. However, for
				// subsequent
				// updates to the weight label, the remove will prevent it from
				// being added more than once.
				if (!QueryManager.isEvaluatingQuery())
				{
					queryView.remove(this.arcLabel);
					queryView.add(this.arcLabel);
				}
				else
				{
					// we are evaluating a query so the label needs to be put
					// onto the
					// ProgressView
					QueryManager.getProgressView().remove(this.arcLabel);
					QueryManager.getProgressView().add(this.arcLabel);
				}
			}
		}
		else
		{
			// we're in macro mode, so we want the label to be put on MacroView
			MacroView macroView = MacroManager.getView();
			if (macroView != null)
			{
				macroView.remove(this.arcLabel);
				macroView.add(this.arcLabel);
			}
		}
	}

	public boolean isRequired()
	{
		return this.required;
	}

	public void setRequired(final boolean req)
	{
		this.required = req;
	}

	public double getStartPositionX()
	{
		return this.myPath.getPoint(0).getX();
	}

	public double getStartPositionY()
	{
		return this.myPath.getPoint(0).getY();
	}

	public double getEndPositionX()
	{
		return this.myPath.getPoint(this.myPath.getEndIndex()).getX();
	}

	public double getEndPositionY()
	{
		return this.myPath.getPoint(this.myPath.getEndIndex()).getY();
	}

	public PerformanceTreeArcPath getArcPath()
	{
		return this.myPath;
	}

	@Override
	public void paintComponent(final Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.translate(	QueryConstants.COMPONENT_DRAW_OFFSET - this.myPath.getBounds().getX(),
						QueryConstants.COMPONENT_DRAW_OFFSET - this.myPath.getBounds().getY());

		if (!isRequired())
		{
			// This is an arc for an optional node, so draw it dashed
			Stroke drawingStroke = new BasicStroke(	1,
													BasicStroke.CAP_BUTT,
													BasicStroke.JOIN_ROUND,
													0,
													new float[]{9},
													0);
			g2.setStroke(drawingStroke);
		}

		if (this.selected && !PerformanceTreeObject.ignoreSelection)
			g2.setPaint(QueryConstants.SELECTION_LINE_COLOUR);
		else g2.setPaint(QueryConstants.ELEMENT_LINE_COLOUR);
		g2.draw(this.myPath);
	}

	/** Updates the bounding box of the arc component based on the arcs bounds */
	@Override
    void updateBounds()
	{
		this.bounds = this.myPath.getBounds();
		this.bounds.grow(QueryConstants.COMPONENT_DRAW_OFFSET, QueryConstants.COMPONENT_DRAW_OFFSET);
		setBounds(this.bounds);
	}

	/** Updates the start position of the arc and the bounds */
	public void updateArcPosition()
	{
		if (MacroManager.getMode() == QueryConstants.LOADING &&
			QueryManager.getMode() != QueryConstants.LOADING)
		{
			// a macro is being loaded in from an XML file and visualised
			MacroDefinition macro = MacroLoader.getMacro();
			if (macro.getMacroNode(getSourceID()) != null)
			{
				PerformanceTreeNode sourceNode = macro.getMacroNode(getSourceID());
				sourceNode.updateEndPoint(this);
				macro.updateMacroNode(sourceNode);
			}
			if (macro.getMacroNode(getTargetID()) != null)
			{
				PerformanceTreeNode targetNode = macro.getMacroNode(getTargetID());
				targetNode.updateEndPoint(this);
				macro.updateMacroNode(targetNode);
			}
		}
		else if (MacroManager.getMode() == QueryConstants.LOADING &&
					QueryManager.getMode() == QueryConstants.LOADING)
		{
			// a macro is being loaded in from an XML file in the background -
			// no visualisation
			MacroDefinition macro = QueryLoader.getMacro();
			if (macro.getMacroNode(getSourceID()) != null)
			{
				PerformanceTreeNode sourceNode = macro.getMacroNode(getSourceID());
				sourceNode.updateEndPoint(this);
				macro.updateMacroNode(sourceNode);
			}
			if (macro.getMacroNode(getTargetID()) != null)
			{
				PerformanceTreeNode targetNode = macro.getMacroNode(getTargetID());
				targetNode.updateEndPoint(this);
				macro.updateMacroNode(targetNode);
			}
		}
		else
		{
			if (getSource() != null)
				getSource().updateEndPoint(this);
			if (getTarget() != null)
				getTarget().updateEndPoint(this);
		}
		this.myPath.createPath();
	}

	public void updateLabelPosition()
	{
		setArcLabelPosition();
		this.arcLabel.updateSize();
	}

	public void setEndPoint(final double x, final double y, final boolean type)
	{
		this.myPath.setPointLocation(this.myPath.getEndIndex(), x, y);
		this.myPath.setPointType(this.myPath.getEndIndex(), type);
		updateArcPosition();
	}

	public void setSourceLocation(final double x, final double y)
	{
		this.myPath.setPointLocation(0, x, y);
		this.myPath.createPath();
		updateBounds();
		repaint();
	}

	public void setTargetLocation(final double x, final double y)
	{
		this.myPath.setPointLocation(this.myPath.getEndIndex(), x, y);
		this.myPath.createPath();
		updateBounds();
		repaint();
	}

	@Override
	public boolean contains(final int x, final int y)
	{
		PerformanceTreeArc.point = new Point2D.Double(	x + this.myPath.getBounds().getX() -
														QueryConstants.COMPONENT_DRAW_OFFSET,
														y + this.myPath.getBounds().getY() -
														QueryConstants.COMPONENT_DRAW_OFFSET);
		if (MacroManager.getEditor() == null)
		{
			if (QueryManager.getMode() == QueryConstants.SELECT)
			{
				if (this.myPath.proximityContains(PerformanceTreeArc.point) || this.selected)
					// show also if PerformanceTreeArc itself selected
					this.myPath.showPoints();
				else this.myPath.hidePoints();
			}
		}
		else
		{
			if (MacroManager.getMode() == QueryConstants.SELECT)
			{
				if (this.myPath.proximityContains(PerformanceTreeArc.point) || this.selected)
					// show also if PerformanceTreeArc itself selected
					this.myPath.showPoints();
				else this.myPath.hidePoints();
			}
		}
		return this.myPath.contains(PerformanceTreeArc.point);
	}

	@Override
	public void addedToGui()
	{
		// called by the respective drawing canvas upon adding component.
		if (MacroManager.getEditor() == null)
		{
			// we're calling this method from QueryView
			this.myPath.addPointsToGui((QueryView) getParent());
		}
		else
		{
			// we're calling this method from MacroView
			this.myPath.addPointsToGui((MacroView) getParent());
		}
		this.myPath.createPath();
		updateArcPosition();
	}

	public void setPathToNodeAngle(final int angle)
	{
		this.myPath.setNodeAngle(angle);
	}

	public void split(final Point2D.Float mouseposition)
	{
		this.myPath.splitSegment(mouseposition);
	}

	@Override
	public void zoomUpdate()
	{
		updateLabelPosition();
	}

	/**
	 * This method translates all ArcPathPoints by the given displacement
	 * 
	 * @param displacementX
	 * @param displacementY
	 */
	public void translatePoints(final double displacementX, final double displacementY)
	{
		this.myPath.translatePoints((float) displacementX, (float) displacementY);
		this.myPath.createPath();
		updateBounds();
		repaint();
	}

	@Override
	public PerformanceTreeArc clone()
	{
		PerformanceTreeArc arcCopy = (PerformanceTreeArc) super.clone();
		arcCopy.selected = false; // don't want highlighted arcs
		arcCopy.myPath = this.myPath.clone(arcCopy);
		PerformanceTreeObjectLabel arcLabelCopy = getArcLabelObject().clone();
		arcCopy.setArcLabelObject(arcLabelCopy);
		PerformanceTreeArc.point = (Point2D.Double) PerformanceTreeArc.point.clone();
		return arcCopy;
	}

}
