/**
 * 
 */
package pipe.server.performancequery.structure;

import pipe.common.PetriNetNode;
import pipe.server.performancequery.QueryServerException;
import pipe.server.performancequery.SimpleNode;
import pipe.server.performancequery.StatusIndicatorUpdater;

/**
 * @author dazz
 * 
 */
public class RangeSubtree extends ParentSubtree
{

	public RangeSubtree(final SimpleNode thisNode,
						final StatusIndicatorUpdater updater,
						final ParentSubtree parent,
						final ResultSubtree root,
						final String roleForParent) throws QueryServerException {
		super(thisNode, updater, parent, root, roleForParent);

		if (thisNode.getType() != PetriNetNode.RANGE)
		{
			throw new QueryServerException("Range Subtree only supported for Range PTNodes, not " +
											thisNode.getType());
		}

	}
}
