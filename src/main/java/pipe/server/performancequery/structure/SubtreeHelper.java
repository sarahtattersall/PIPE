/**
 * 
 */
package pipe.server.performancequery.structure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;

import pipe.common.SimpleOperationNode;
import pipe.common.PetriNetNode;
import pipe.server.performancequery.QueryServerException;
import pipe.server.performancequery.SimpleNode;
import pipe.server.performancequery.StatusIndicatorUpdater;
import pipe.server.performancequery.nodeanalyser.InvalidNodeAnalyserException;

/**
 * @author dazz
 * 
 */
public class SubtreeHelper implements StructureLoggingHandler
{

	/**
	 * This method derives all possible subtrees from the hierarchy of the query
	 * nodes
     * @param queryNodes
     * @param s
     * @throws pipe.server.performancequery.QueryServerException
     * @throws pipe.server.performancequery.nodeanalyser.InvalidNodeAnalyserException
     * @return
     */
	public static ResultSubtree constructSubtrees(	final ArrayList<SimpleNode> queryNodes,
													final StatusIndicatorUpdater s)	throws InvalidNodeAnalyserException,
																					QueryServerException

	{
		StructureLoggingHandler.logger.log(Level.INFO, "Deriving subtrees for dependency analysis");
		// starting from the top node, create all possible subtrees
		final LinkedList<SimpleNode> nodeQueue = new LinkedList<SimpleNode>(queryNodes);
        return SubtreeHelper.createSubtrees(nodeQueue, s);
	}

	public static ArrayList<ParentSubtree> createExecutionSchedule(final ResultSubtree root)
	{
		StructureLoggingHandler.logger.log(Level.INFO, "Performing execution schedule analysis");
		final ArrayList<ParentSubtree> executionSchedule = new ArrayList<ParentSubtree>();
		executionSchedule.add(root);
		for (final Subtree s : root.getDecendantSubtrees())
		{
			if (s instanceof ParentSubtree)
			{
				executionSchedule.add((ParentSubtree) s);
			}
		}
		return executionSchedule;
	}

	/**
	 * This method creates a nes subtree
	 * 
	 * @param node
     * @param s
     * @param parent
     * @param root
     * @param roleForParent
     * @return
     * @throws pipe.server.performancequery.QueryServerException
     * @throws pipe.server.performancequery.nodeanalyser.InvalidNodeAnalyserException
	 */
	private static Subtree createNewSubtree(final SimpleNode node,
											final StatusIndicatorUpdater s,
											final ParentSubtree parent,
											final ResultSubtree root,
											final String roleForParent)	throws InvalidNodeAnalyserException,
																		QueryServerException

	{
		Subtree newSubtree;
		switch (node.getType())
		{
			case RANGE :
				newSubtree = new RangeSubtree(node, s, parent, root, roleForParent);
				break;
			case SEQUENTIAL :
				newSubtree = new SequentialSubtree(node, s, root, roleForParent);
				break;
			case RESULT :
				throw new QueryServerException("Can't make more than two result subtrees");
			case PERCENTILE :
			case DISTRIBUTION :
			case PASSAGETIMEDENSITY :
			case CONVOLUTION :
			case PROBININTERVAL :
			case PROBINSTATES :
			case MOMENT :
			case FIRINGRATE :
			case STEADYSTATEPROB :
			case STEADYSTATESTATES :
			case STATESATTIME :
			case ININTERVAL :
			case DISCON :
			case ARITHCOMP :
			case ARITHOP :
			case NEGATION :
			case SUBSET :
				newSubtree = new OperationSubtree(node, s, parent, root, roleForParent);
				break;
			default :
				newSubtree = new ValueSubtree(node, parent, root, roleForParent);
		}
		StructureLoggingHandler.logger.info(String.format(	"Created %s %s",
															newSubtree.getType(),
															newSubtree.getID()));
		return newSubtree;
	}

	private static ResultSubtree createSubtrees(final Queue<SimpleNode> queryNodes,
												final StatusIndicatorUpdater s)	throws InvalidNodeAnalyserException,
																				QueryServerException

	{
		ResultSubtree root = null;

		final SimpleNode topNode = queryNodes.remove();
		if (topNode.getType() != PetriNetNode.RESULT)
		{
			throw new QueryServerException("topNode isn't result node. Can't continue!");
		}
		root = new ResultSubtree(topNode, s);
		SubtreeHelper.createSubtrees(queryNodes, root, s, root);
		return root;
	}

	/**
	 * This method creates subtrees from the current node. If the node is a
	 * SimpleOperationNode, it also creates subtrees recursively.
	 * 
	 * @param node
     * @throws pipe.server.performancequery.QueryServerException
     * @throws pipe.server.performancequery.nodeanalyser.InvalidNodeAnalyserException
     * @param queryNodes
     * @param parent
     * @param updater
     * @param root
	 */
	private static void createSubtrees(	final Queue<SimpleNode> queryNodes,
										final Subtree parent,
										final StatusIndicatorUpdater updater,
										final ResultSubtree root)	throws InvalidNodeAnalyserException,
																	QueryServerException

	{
		// create subtrees for each child
		if (parent instanceof ParentSubtree)
		{
			final SimpleNode node = parent.getNode();
			final HashMap<String, String> children = ((SimpleOperationNode) node).getChildren();
			for (final String childNodeRole : children.keySet())
			{
				final String childNodeID = children.get(childNodeRole);
				final SimpleNode childNode = SubtreeHelper.getSimpleNode(queryNodes, childNodeID);

				// create new subtree for child node
				final Subtree childSubtree = SubtreeHelper.createNewSubtree(childNode,
																			updater,
																			(ParentSubtree) parent,
																			root,
																			childNodeRole);
				// recursively create subtrees of child
				SubtreeHelper.createSubtrees(queryNodes, childSubtree, updater, root);

			}
		}
	}

	private static SimpleNode getSimpleNode(final Queue<SimpleNode> queryNodes, final String nodeID)
	{
		SimpleNode node = null;
		try
		{
			for (final SimpleNode n : queryNodes)
			{
				if (n.getID().equals(nodeID))
				{
					return node = n;
				}
			}
		}
		finally
		{
			queryNodes.remove(node);
		}
		return node;
	}

	private static void printSubtree(final Subtree subtree)
	{
		final StringBuilder t = new StringBuilder();
        t.append("Subtree with ID:").append(subtree.getID()).append(" ");

		final ArrayList<Subtree> childSubtrees = subtree.getDecendantSubtrees();

		// print out details of each node that is associated with the subtree
		t.append("PTNodes:");
		final String nodeNames = "";
		t.append(nodeNames + " ");

		// print out details of each child subtree that is associated with the
		// subtree
		t.append("child subtrees:");
		String subtreeNames = "";
		for (final Subtree s : childSubtrees)
		{
			subtreeNames += s.getID() + ", ";
		}
        t.append(subtreeNames).append(" ");
		StructureLoggingHandler.logger.log(Level.INFO, t.toString());
	}

	/**
	 * This method prints out the contents of all subtrees
     * @param querySubtrees
     */
	public static void printSubtrees(final ArrayList<Subtree> querySubtrees)
	{
		StructureLoggingHandler.logger.log(Level.INFO, "Subtrees extracted from the data:");
		for (final Subtree subtree : querySubtrees)
		{
			SubtreeHelper.printSubtree(subtree);
		}
	}
}