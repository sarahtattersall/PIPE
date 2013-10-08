/**
 * 
 */
package pipe.server.performancequery;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import pipe.modules.interfaces.QueryConstants;
import pipe.exceptions.UnexpectedResultException;
import pipe.handlers.StringHelper;
import pipe.modules.queryresult.ResultWrapper;
import pipe.modules.queryresult.TextFileResultWrapper;
import pipe.server.performancequery.nodeanalyser.NumNode;
import pipe.server.performancequery.structure.ParentSubtree;

/**
 * @author dazz
 * 
 */
public class MomentSSPExecutor extends AnalysisExecutor
{
	public MomentSSPExecutor(final ParentSubtree subtree, final ResultSender resultSender) {
		super(subtree, resultSender);
	}

	@Override
	public ArrayList<ResultWrapper> doCall() throws UnexpectedResultException,
											InterruptedException,
											ExecutionException
	{
		final ArrayList<ResultWrapper> r = new ArrayList<ResultWrapper>();
		final StringBuilder file = StringHelper.findSubStringPoints(((TextFileResultWrapper) this.subtree	.getChildByRole(QueryConstants.momentChildDensDist)
																											.getResult()).getFileString(),
																	"distribution");

		final int nthMoment = (int) ((NumNode) this.subtree	.getChildByRole(QueryConstants.momentChildNum)
															.getResult()
															.getResult()).getValue();

		final StringBuilder results = new StringBuilder();

		final ArrayList<Double> count = new ArrayList<Double>();
		final ArrayList<Double> value = new ArrayList<Double>();

		final Scanner s1 = new Scanner(file.toString());
		s1.useDelimiter("[\n\r]");

		int line = 1;

		while (s1.hasNext())
		{
			final Scanner s2 = new Scanner(s1.next());
			final double x = s2.hasNextDouble() ? s2.nextDouble() : StringHelper.SENTINEL;
			final double y = s2.hasNextDouble() ? s2.nextDouble() : StringHelper.SENTINEL;

			if (x != StringHelper.SENTINEL && y != StringHelper.SENTINEL)
			{
				count.add(x);
				value.add(y);
			}
			else if (x == StringHelper.SENTINEL ^ y == StringHelper.SENTINEL)
			{
				final String msg = "line:" + line +
									" elements not pair of doubles, double and another element detected";
				throw new UnexpectedResultException(msg);
			}
			else if (s2.hasNext())
			{
				throw new UnexpectedResultException("Line:" + line + "more than 2 tokens on line");
			}
			else
			{
				ServerLoggingHandler.logger.warning("Blank line detected on line " + line);
			}

			line++;
		}

		double currentMoment = 0;
		for (int moment = 1; moment <= nthMoment; moment++)
		{
			currentMoment = 0;
			for (int index = 0; index < count.size(); index++)
			{
				currentMoment += Math.pow(count.get(index), moment) * value.get(index);
			}
            results.append("Moment ").append(moment).append(" = ").append(currentMoment).append("\n");
		}

		r.add(new TextFileResultWrapper(currentMoment, results, this.subtree.getID(), this.subtree.getType()));

		return r;
	}
}
