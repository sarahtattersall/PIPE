/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import java.util.concurrent.ExecutionException;

import pipe.common.SimpleOperationNode;
import pipe.common.PTArithComp;
import pipe.common.PTArithOp;
import pipe.common.PTDisCon;
import pipe.modules.interfaces.QueryConstants;
import pipe.server.performancequery.SimpleValueNode;
import pipe.server.performancequery.structure.Subtree;
import pipe.server.performancequery.structure.ValueSubtree;

/**
 * @author dazz
 * 
 */
public class NodeAnalyserHelper implements NodeAnalyserLoggingHandler
{

	public static ValueNodeAnalyser analyseSubtree(final Subtree subtree)	throws InvalidNodeAnalyserException,
																			InterruptedException,
																			ExecutionException
	{
		return NodeAnalyserHelper.getStructure(subtree).calculate();
	}

	private static NodeAnalyser buildStructure(final Subtree subtree)	throws InvalidNodeAnalyserException,
																		InterruptedException,
																		ExecutionException
	{
		return NodeAnalyserHelper.buildStructure(subtree, true);
	}

	private static NodeAnalyser buildStructure(final Subtree subtree, final boolean useResult)	throws InvalidNodeAnalyserException,
																								InterruptedException,
																								ExecutionException

	{
		NodeAnalyser node;
		if (useResult)
		{
			node = subtree.getResult().getResult();
		}
		else
		{
			switch (subtree.getType())
			{
				case ININTERVAL :
					node = new InInterval(	NodeAnalyserHelper.buildStructure(subtree.getChildByRole(QueryConstants.inIntervalChildNum)),
											NodeAnalyserHelper.buildStructure(subtree.getChildByRole(QueryConstants.inIntervalChildRange)));

					break;
				case DISCON :
					node = new DisCon(	NodeAnalyserHelper.buildStructure(subtree.getChildByRole(QueryConstants.disConChildBool1)),
										NodeAnalyserHelper.buildStructure(subtree.getChildByRole(QueryConstants.disConChildBool2)),
										PTDisCon.fromString(((SimpleOperationNode) subtree.getNode()).getOperation()));

					break;

				case ARITHCOMP :
					node = new ArithComp(	NodeAnalyserHelper.buildStructure(subtree.getChildByRole(QueryConstants.arithCompChildNum1)),
											NodeAnalyserHelper.buildStructure(subtree.getChildByRole(QueryConstants.arithCompChildNum2)),
											PTArithComp.fromString(((SimpleOperationNode) subtree.getNode()).getOperation()));

					break;

				case ARITHOP :
					node = new ArithOp(	NodeAnalyserHelper.buildStructure(subtree.getChildByRole(QueryConstants.arithOpChildNum1)),
										NodeAnalyserHelper.buildStructure(subtree.getChildByRole(QueryConstants.arithOpChildNum2)),
										PTArithOp.fromString(((SimpleOperationNode) subtree.getNode()).getOperation()));

					break;

				case NEGATION :
					node = new Negation(NodeAnalyserHelper.buildStructure(subtree.getChildByRole(QueryConstants.negChildBool)));

					break;
				case NUM :
				case BOOL :
					node = NodeAnalyserHelper.buildValueNodeStructure((ValueSubtree) subtree);
					break;
				case RANGE :
					node = new RangeNode(	NodeAnalyserHelper.buildStructure(subtree.getChildByRole(QueryConstants.rangeChildFromNum)),
											NodeAnalyserHelper.buildStructure(subtree.getChildByRole(QueryConstants.rangeChildToNum)));
					break;
				default :
					throw new InvalidNodeAnalyserException("Can't use Node Analyser on " + subtree.getType());
			}
		}
		return node;
	}

	public static ValueNodeAnalyser buildValueNodeStructure(final ValueSubtree subtree) throws InvalidNodeAnalyserException
	{
		ValueNodeAnalyser node;
		switch (subtree.getType())
		{
			case BOOL :
				node = new BoolNode(Boolean.parseBoolean(((SimpleValueNode) subtree.getNode()).getValue()));
				break;
			case NUM :
				node = new NumNode(Double.parseDouble(((SimpleValueNode) subtree.getNode()).getValue()));
				break;
			default :
				throw new InvalidNodeAnalyserException("Can't use buildValueNodeStructure on " +
														subtree.getType());
		}
		return node;
	}

	private static NodeAnalyser getStructure(final Subtree subtree)	throws InvalidNodeAnalyserException,
																	InterruptedException,
																	ExecutionException
	{
		return NodeAnalyserHelper.buildStructure(subtree, false);
	}

	public static String getValue(final ValueNodeAnalyser n) throws InvalidNodeAnalyserException
	{
		final StringBuilder s = new StringBuilder();
		if (n instanceof RangeNode)
		{
			final RangeNode range = (RangeNode) n;
			s.append(range.getStart());
			s.append("-");
			s.append(range.getFinish());
		}
		else if (n instanceof BoolNode)
		{
			final BoolNode bool = (BoolNode) n;
			s.append(bool.getValue());
		}
		else if (n instanceof NumNode)
		{
			final NumNode num = (NumNode) n;
			s.append(num.getValue());
		}
		return s.toString();
	}
}
