/**
 * 
 */
package pipe.server.performancequery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import pipe.modules.interfaces.QueryConstants;
import pipe.server.interfaces.ServerConstants;
import pipe.modules.queryresult.PointsResultWrapper;
import pipe.modules.queryresult.ResultWrapper;
import pipe.server.performancequery.structure.ParentSubtree;
import pipe.server.performancequery.structure.ResultSubtree;
import pipe.server.performancequery.structure.SequentialSubtree;

/**
 * @author dazz
 * 
 */
public class SimpleAnalysisExecutor extends AnalysisExecutor
{

	public SimpleAnalysisExecutor(final ParentSubtree subtree, final ResultSender resultSender) {
		super(subtree, resultSender);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see pipe.server.performancequery.AnalysisExecutor#doCall()
	 */
	@Override
	public ArrayList<ResultWrapper> doCall() throws QueryServerException,
											IOException,
											InterruptedException,
											ExecutionException
	{
		ArrayList<ResultWrapper> r = new ArrayList<ResultWrapper>();

		switch (this.subtree.getType())
		{
			case RESULT :
				r.add(((ResultSubtree) this.subtree).getResultGetterResult());

				break;
			case SEQUENTIAL :
				// wait for all results ignore return val
				this.subtree.getResult();

				// use other result which isn't returned by get result
				r = ((SequentialSubtree) this.subtree).getAllResults();
				break;
			case DISTRIBUTION :
			{
				final PointsResultWrapper p = (PointsResultWrapper) this.subtree.getChildByRole(QueryConstants.distChildDensity)
																				.getResult();
				final PointsResultWrapper result = new PointsResultWrapper(	ServerConstants.cdfResultsFileName,
																			p.getResultsDir(),
																			this.subtree.getID(),
																			this.subtree.getType());
				if (result.getPoints().getItemCount() == 0)
				{
					this.subtree.failed();
				}
				r.add(result);
				break;
			}
			default :
				throw new QueryServerException("Cannot use SimpleAnalysisExecutor for subtree type " +
												this.subtree.getType());
		}

		return r;
	}
}
