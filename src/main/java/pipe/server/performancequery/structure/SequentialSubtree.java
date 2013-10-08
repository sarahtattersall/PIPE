/**
 * 
 */
package pipe.server.performancequery.structure;

import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import pipe.common.PetriNetNode;
import pipe.modules.queryresult.ResultWrapper;
import pipe.server.performancequery.QueryServerException;
import pipe.server.performancequery.SimpleNode;
import pipe.server.performancequery.StatusIndicatorUpdater;

/**
 * @author dazz
 * 
 */
public class SequentialSubtree extends ParentSubtree
{

	private final Vector<ResultGetter>	resultGetters	= new Vector<ResultGetter>();

	public SequentialSubtree(	final SimpleNode thisNode,
								final StatusIndicatorUpdater updater,
								final ResultSubtree root,
								final String roleForParent) throws QueryServerException {
		super(thisNode, updater, root, root, roleForParent);

		if (thisNode.getType() != PetriNetNode.SEQUENTIAL)
		{
			throw new QueryServerException("Sequential Subtree only supported for Sequential PTNodes, not " +
											thisNode.getType());
		}

	}

	public void addResultGetter(final ResultGetter resultGetter)
	{
		this.resultGetters.add(resultGetter);
	}

	public ArrayList<ResultWrapper> getAllResults() throws ExecutionException, InterruptedException
	{
		final ArrayList<ResultWrapper> wrappers = new ArrayList<ResultWrapper>();
		for (final ResultGetter s : this.resultGetters)
		{
			wrappers.add(s.getResult().copyData(this.getID(), this.getType()));
		}
		return wrappers;
	}

	@Override
	public ResultWrapper getResult() throws ExecutionException, InterruptedException
	{
		ResultWrapper w = null;
		for (final ResultGetter r : this.resultGetters)
		{
			w = r.getResult();
		}
		return w;
	}

}
