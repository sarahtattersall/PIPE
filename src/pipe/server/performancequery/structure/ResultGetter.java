package pipe.server.performancequery.structure;

import java.util.concurrent.ExecutionException;

import pipe.modules.queryresult.ResultWrapper;

interface ResultGetter
{

	public ResultWrapper getResult() throws ExecutionException, InterruptedException;

}
