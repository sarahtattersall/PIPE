/**
 * 
 */
package pipe.server.performancequery.structure;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import pipe.modules.queryresult.ResultWrapper;
import pipe.server.performancequery.SimpleNode;
import pipe.server.performancequery.StatusIndicatorUpdater;

/**
 * @author dazz
 * 
 */
public abstract class ParentSubtree extends Subtree implements Comparable<ParentSubtree>
{

	private Future<ResultWrapper>	futureResult;

	ParentSubtree(final SimpleNode thisNode,
                  final StatusIndicatorUpdater updater,
                  final ParentSubtree parent,
                  final ResultSubtree root,
                  final String roleForParent) {
		super(thisNode, updater, parent, root, roleForParent);
	}

	public int compareTo(final ParentSubtree that)
	{
		return this.canBeEvaluated() - that.canBeEvaluated();
	}

	@Override
	public ResultWrapper getResult() throws ExecutionException, InterruptedException
	{
		ResultWrapper r = null;
		try
		{
			r = this.futureResult.get();
		}
		catch (final ExecutionException e)
		{
			StructureLoggingHandler.logger.log(	Level.WARNING,
												"Subtree:" + this.getID() + " Execution failed",
												e);
			this.failed();
			throw e;
		}
		catch (final InterruptedException e)
		{
			StructureLoggingHandler.logger.info("Subtree:" + this.getID() + " result wait interrupted");
			throw e;
		}
		return r;
	}

	@Override
	public boolean hasResult()
	{
		return this.futureResult.isDone();
	}

	public void setFutureResult(final Future<ResultWrapper> futureResult)
	{
		this.futureResult = futureResult;
	}
}
