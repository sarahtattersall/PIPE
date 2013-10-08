/**
 * QueryTreeNodeHandler
 * 
 * @author Tamas Suto
 * @date 25/11/07
 */

package pipe.modules.queryeditor.evaluator.gui;

import java.awt.Container;
import java.awt.event.MouseEvent;

import javax.swing.event.MouseInputAdapter;

import pipe.modules.interfaces.QueryConstants;

class QueryTreeNodeHandler extends MouseInputAdapter
implements
        QueryConstants,
	EvaluatorGuiLoggingHandler
{

    private QueryOperationNode	node	= null;

	public QueryTreeNodeHandler(final Container contentpane, final QueryOperationNode treeNode) {
		this.node = treeNode;
	}

	void getComponentForLeftClick(final MouseEvent event)
	{
		if (this.node.hasResult())
		{
			switch (this.node.getNodeType())
			{
				case RESULT :
				case SEQUENTIAL :
				{
					ResultProvider.setupResult(this.node, event);
					break;
				}
				case DISTRIBUTION :
				case PASSAGETIMEDENSITY :
				{
					ResultProvider.setupGraphTab(this.node);
					break;
				}

				case CONVOLUTION :

					break;
				case PROBININTERVAL :
					ResultProvider.setupProbInInterval(this.node, event);
					break;
				case PROBINSTATES :

					break;
				case PERCENTILE :
				{
					ResultProvider.setupPercentile(this.node, event);
					break;
				}
				case MOMENT :
				{
					ResultProvider.setupMoment(this.node, event);
					break;
				}
				case FIRINGRATE :
				{
					ResultProvider.setupFiringRate(this.node, event);
					break;
				}
				case STEADYSTATEPROB :
				{
					ResultProvider.setupSSP(this.node, event);
					break;
				}
				case STEADYSTATESTATES :

					break;
				case STATESATTIME :

					break;
				case ININTERVAL :
				case DISCON :
				case ARITHCOMP :
				case ARITHOP :
				case NEGATION :
				{
					ResultProvider.setupNodeAnalyser(this.node, event);
					break;
				}
				case SUBSET :

					break;
				default :
					EvaluatorGuiLoggingHandler.logger.warning("Unexpected type " + this.node.getNodeType());
			}
		}
	}

	@Override
	public void mouseClicked(final MouseEvent e)
	{
		super.mouseClicked(e);

		switch (e.getButton())
		{
			case MouseEvent.BUTTON1 :
				if (this.node.hasResult())
				{
					this.getComponentForLeftClick(e);
				}
				break;
		}
	}
}