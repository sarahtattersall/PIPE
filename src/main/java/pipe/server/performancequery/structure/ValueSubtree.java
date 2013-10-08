package pipe.server.performancequery.structure;

import pipe.modules.queryresult.NodeAnalyserResultWrapper;
import pipe.modules.queryresult.ResultWrapper;
import pipe.server.performancequery.QueryServerException;
import pipe.server.performancequery.SimpleNode;
import pipe.server.performancequery.nodeanalyser.InvalidNodeAnalyserException;
import pipe.server.performancequery.nodeanalyser.NodeAnalyserHelper;

public class ValueSubtree extends Subtree
{

	private ResultWrapper	result	= null;

	public ValueSubtree(final SimpleNode thisNode,
						final ParentSubtree parent,
						final ResultSubtree root,
						final String roleForParent) throws QueryServerException, InvalidNodeAnalyserException {
		super(thisNode, null, parent, root, roleForParent);
		if (this.getType().usesNodeAnalyser())
		{
			this.result = new NodeAnalyserResultWrapper(NodeAnalyserHelper.buildValueNodeStructure(this),
														this.getID(),
														this.getType());
		}
		if (!thisNode.getType().isValueNode())
		{
			throw new QueryServerException("Value Subtree only supported for Value PTNodes, not " +
											thisNode.getType());
		}

	}

	@Override
	public ResultWrapper getResult()
	{
		return this.result;
	}

	@Override
	public boolean hasResult()
	{
		return this.result != null;
	}

}
