package pipe.server.performancequery.structure;

import java.util.concurrent.ExecutionException;

import pipe.common.PetriNetNode;
import pipe.modules.queryresult.ResultWrapper;
import pipe.server.performancequery.QueryServerException;
import pipe.server.performancequery.SimpleNode;
import pipe.server.performancequery.StatusIndicatorUpdater;

public class ResultSubtree extends ParentSubtree
{

	private ResultGetter	resultGetter	= null;

	public ResultSubtree(final SimpleNode thisNode, final StatusIndicatorUpdater updater) throws QueryServerException {
		super(thisNode, updater, null, null, null);

		if (thisNode.getType() != PetriNetNode.RESULT)
		{
			throw new QueryServerException("Result Subtree only supported for Result PTNodes, not " +
											thisNode.getType());
		}

	}

	@Override
	protected void addDecendantSubtree(final Subtree subtree)
	{
		if (!this.getDecendantSubtrees().contains(subtree))
		{
			this.getDecendantSubtrees().add(subtree);
		}
	}

	public ResultWrapper getResultGetterResult() throws ExecutionException, InterruptedException
	{
		return this.resultGetter.getResult().copyData(this.getID(), this.getType());

	}

	public void setResultGetter(final ResultGetter resultGetter)
	{
		this.resultGetter = resultGetter;
	}

}
