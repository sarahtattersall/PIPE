/**
 * ValueNode
 * 
 * Implements methods common to Performance Tree Value Nodes
 * 
 * @author Tamas Suto
 * @date 24/04/07
 */

package pipe.modules.queryeditor.gui.performancetrees.valuenodes;

import pipe.common.PetriNetNode;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.gui.QueryView;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeArc;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeNode;
import pipe.modules.queryeditor.gui.performancetrees.PerformanceTreeObjectLabel;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroManager;
import pipe.modules.queryeditor.gui.performancetrees.macros.MacroView;

import java.awt.*;

public abstract class ValueNode extends PerformanceTreeNode
{

	private PerformanceTreeObjectLabel	nodeLabel	= null;
	private Graphics2D					g2d;

	public ValueNode(double positionXInput, double positionYInput, String idInput, PetriNetNode typeInput) {
		super(positionXInput, positionYInput, idInput, typeInput);
	}

	protected ValueNode(double positionXInput, double positionYInput, String idInput) {
		super(positionXInput, positionYInput, idInput);
	}

	protected ValueNode(double positionXInput, double positionYInput) {
		super(positionXInput, positionYInput);
	}

	public String getNodeLabel()
	{
		return nodeLabel.getText();
	}

	public void setNodeLabel(String labelInput)
	{
		if (nodeLabel == null)
		{
			nodeLabel = new PerformanceTreeObjectLabel();
		}
		nodeLabel.setText(labelInput);

		if (g2d != null) displayNodeLabel();
	}

	public PerformanceTreeObjectLabel getNodeLabelObject()
	{
		return nodeLabel;
	}

	void setNodeLabelObject(PerformanceTreeObjectLabel label)
	{
		nodeLabel = label;
	}

	public void displayNodeLabel()
	{
		if (g2d == null)
		{
			System.out.println("g2d is null");
		}
		else
		{
			int labelWidth = g2d.getFontMetrics().stringWidth(getNodeLabel());
			double nodeLabelPositionX = positionX + (componentWidth / 2) + (labelWidth / 2);
			double nodeLabelPositionY = positionY + componentHeight + 20;
			nodeLabel.setPosition((int) nodeLabelPositionX, (int) nodeLabelPositionY);
			nodeLabel.updateSize();
		}
		// add node label to container
		if (MacroManager.getEditor() == null)
		{
			QueryView queryView = QueryManager.getView();
			if (queryView != null)
			{
				if (!QueryManager.isEvaluatingQuery())
				{
					// If this is the first time the node label is being added
					// then
					// the remove operation will have no affect. However, for
					// subsequent
					// updates to the label, the remove will prevent it from
					// being added more than once.
					queryView.remove(nodeLabel);
					queryView.add(nodeLabel);
				}
				else
				{
					// we are evaluating a query so the label needs to be put
					// onto the
					// ProgressView
					QueryManager.getProgressView().remove(nodeLabel);
					QueryManager.getProgressView().add(nodeLabel);
				}
			}
		}
		else
		{
			MacroView macroView = MacroManager.getView();
			if (macroView != null)
			{
				// If this is the first time the node label is being added then
				// the remove operation will have no affect. However, for
				// subsequent
				// updates to the label, the remove will prevent it from
				// being added more than once.
				macroView.remove(nodeLabel);
				macroView.add(nodeLabel);
			}
		}
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
        g2d = (Graphics2D) g;

		// make sure node label follows node if node's being dragged around
		if (nodeLabel != null)
		{
			if (!QueryManager.isEvaluatingQuery()) displayNodeLabel();
		}
	}

	public boolean childAssignmentValid(PerformanceTreeArc arc, PerformanceTreeNode node)
	{
		// a value node doesn't have any children
		return false;
	}

	public String printTextualRepresentation()
	{
		return "";
	}

	public ValueNode clone()
	{
		ValueNode nodeCopy = (ValueNode) super.clone();
		PerformanceTreeObjectLabel nodeLabelCopy = getNodeLabelObject().clone();
		nodeCopy.setNodeLabelObject(nodeLabelCopy);
		return nodeCopy;
	}

}
