/**
 * QueryTreeNode
 * 
 * This extension of the PerformanceTreeNode object is used in the GUI for showing the
 * progress of evaluation of the query. 
 * 
 * The main difference is  the colouring of the node to indicate evaluation state changes 
 * and the corresponding mouse handler.  
 * 
 */

package pipe.modules.queryeditor.evaluator.gui;

import java.awt.image.ImageObserver;

import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;

public abstract class QueryTreeNode extends PerformanceTreeNode implements QueryConstants
{
	QueryTreeNode(final PerformanceTreeNode inputNode) {
		super(inputNode);
	}

	/**
	 * Returns the height bounds we want to use when initially creating the node
	 * on the gui
	 * 
	 * @return Height bounds of Place
	 */
	@Override
	public int boundsHeight()
	{
		return ImageObserver.HEIGHT + 1;
	}

	/**
	 * Returns the width bounds we want to use when initially creating the node
	 * on the gui
	 * 
	 * @return Width bounds of Place
	 */
	@Override
	public int boundsWidth()
	{
		return ImageObserver.WIDTH + 1;
	}

	@Override
	@Deprecated
	public boolean childAssignmentValid(final PerformanceTreeArc arc, final PerformanceTreeNode node)
	{
		return true;
	}

	/**
	 * Determines whether the point (x,y) is "in" this component. This method is
	 * called when mouse events occur and only events at points for which this
	 * method returns true will be dispatched to mouse listeners
	 */
	@Override
	public boolean contains(final int x, final int y)
	{
		final int zoomPercentage = this.getZoomController().getPercent();
		final double unZoomedX = (x - QueryConstants.COMPONENT_DRAW_OFFSET) / (zoomPercentage / 100.0);
		final double unZoomedY = (y - QueryConstants.COMPONENT_DRAW_OFFSET) / (zoomPercentage / 100.0);

		return this.node.contains((int) unZoomedX, (int) unZoomedY);
	}

	// stuff that had to be implemented because this class extends
	// PerformanceTreeNode

	@Override
	@Deprecated
	public String printTextualRepresentation()
	{
		return "";
	}

	@Override
	@Deprecated
	public void updateEndPoint(final PerformanceTreeArc arc)
	{
	}

}