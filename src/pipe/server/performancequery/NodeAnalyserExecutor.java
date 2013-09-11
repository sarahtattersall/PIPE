/**
 * 
 */
package pipe.server.performancequery;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

import pipe.modules.queryresult.NodeAnalyserResultWrapper;
import pipe.modules.queryresult.ResultWrapper;
import pipe.server.performancequery.nodeanalyser.InvalidNodeAnalyserException;
import pipe.server.performancequery.nodeanalyser.NodeAnalyserHelper;
import pipe.server.performancequery.nodeanalyser.ValueNodeAnalyser;
import pipe.server.performancequery.structure.ParentSubtree;

/**
 * @author dazz
 * 
 */
public class NodeAnalyserExecutor extends AnalysisExecutor implements ServerLoggingHandler
{

	public NodeAnalyserExecutor(final ParentSubtree subtree, final ResultSender sender) {
		super(subtree, sender);
	}

	@Override
	public ArrayList<ResultWrapper> doCall() throws InvalidNodeAnalyserException,
											InterruptedException,
											ExecutionException
	{
		final ArrayList<ResultWrapper> r = new ArrayList<ResultWrapper>();
		final ValueNodeAnalyser result = NodeAnalyserHelper.analyseSubtree(this.subtree);

		if (result == null)
		{
			this.subtree.failed();
		}
		ServerLoggingHandler.logger.log(Level.INFO, "Result is " + NodeAnalyserHelper.getValue(result));

		r.add(new NodeAnalyserResultWrapper(result, this.subtree.getID(), this.subtree.getType()));

		return r;
	}

}
