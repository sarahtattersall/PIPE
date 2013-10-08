/**
 * 
 */
package pipe.modules.queryeditor.evaluator.gui;

import java.awt.event.ActionEvent;
import java.util.logging.Level;

import pipe.modules.queryeditor.evaluator.QueryAnalysisException;

/**
 * @author dazz
 * 
 */
class TabOpenFileText extends TabOpenAction implements EvaluatorGuiLoggingHandler
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5329817187675344265L;

	public TabOpenFileText(final QueryOperationNode node) {
		super(node);
	}

	public void actionPerformed(final ActionEvent event)
	{
		try
		{
			String name;
			switch (this.node.getResult().getOrginalType())
			{
				case PERCENTILE :
					name = ResultProvider.PercentileName;
					break;
				case STEADYSTATEPROB :
					name = ResultProvider.SSPTextTabName;
					break;
				case FIRINGRATE :
					name = ResultProvider.FRTabName;
					break;
				case MOMENT :
					name = ResultProvider.MomentTabName;
					break;
				case PROBININTERVAL :
					name = ResultProvider.ProbInIntervalTabName;
					break;
				default :
					throw new QueryAnalysisException(this.node.getResult().getOrginalType().toString() +
														" currently not supported for file text display");
			}
			ResultProvider.setupTextTab(this.node, name);
		}
		catch (final QueryAnalysisException e)
		{
			EvaluatorGuiLoggingHandler.logger.log(Level.WARNING, "Couldn't add text file tab", e);
		}

	}

}
