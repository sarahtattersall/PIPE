/**
 * QueryEvaluator
 * 
 * This class contains all the code that is required for evaluating
 * a query
 * 
 * @author Tamas Suto
 * @date 23/11/2007
 */

package pipe.modules.queryeditor.evaluator;

import pipe.common.EvaluationStatus;
import pipe.modules.queryeditor.QueryManager;
import pipe.modules.queryeditor.evaluator.gui.ProgressView;
import pipe.modules.queryeditor.evaluator.gui.ProgressWindow;

public class QueryEvaluator
{

	private static AnalysisThread	analysisThread;
	private static ProgressWindow	progressWindow;

	public static void evaluateQuery()
	{
		try
		{
			// open query evaluation progress window
			QueryEvaluator.progressWindow = new ProgressWindow();
			QueryEvaluator.progressWindow.showDialogue();

			// start AnalysisThread
			QueryEvaluator.analysisThread = new AnalysisThread();
			final Thread aThread = new Thread(QueryEvaluator.analysisThread);
			aThread.start();
		}
		finally
		{
			QueryManager.getEditor().setEnabled(false);
		}
	}

	public static void fillProgressBar()
	{
		if (QueryEvaluator.progressWindow != null)
		{
			QueryEvaluator.progressWindow.fillProgressBar();
		}
	}

	public static void stopAnalysis()
	{
		QueryEvaluator.analysisThread.cleanUp();
	}

	public static void updateStatus(final NodeStatusUpdater updater)
	{
		if (QueryEvaluator.progressWindow != null)
		{
			QueryEvaluator.progressWindow.getProgressView().updateNodeStatus(updater);
		}
	}

	public static void updateStatusAll(final EvaluationStatus status)
	{
		if (QueryEvaluator.progressWindow != null)
		{
			final ProgressView p = QueryEvaluator.progressWindow.getProgressView();
			p.updateAllNodeStatus(status);
		}
	}
}
