package pipe.server.performancequery.structure;

import pipe.modules.queryresult.ResultWrapper;

import java.util.concurrent.ExecutionException;

interface ResultGetter
{

	public ResultWrapper getResult() throws ExecutionException, InterruptedException;

}
