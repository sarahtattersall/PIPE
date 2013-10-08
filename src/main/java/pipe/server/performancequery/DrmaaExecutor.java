/**
 * 
 */
package pipe.server.performancequery;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import pipe.common.AnalysisSettings;
import pipe.exceptions.UnexpectedResultException;
import pipe.modules.interfaces.QueryConstants;
import pipe.modules.queryresult.*;
import pipe.server.interfaces.ServerConstants;
import pipe.server.performancequery.nodeanalyser.InvalidNodeAnalyserException;
import pipe.server.performancequery.nodeanalyser.NumNode;
import pipe.server.performancequery.nodeanalyser.RangeNode;
import pipe.server.performancequery.structure.OperationSubtree;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;

/**
 * @author dazz
 * 
 */
public class DrmaaExecutor extends AnalysisExecutor implements ServerLoggingHandler
{
	private final String			toolPath, resultsDirPath, workPath;
	private final AnalysisSettings	settings;
	private final double			maxProcessors;

    public DrmaaExecutor(	final OperationSubtree subtree,
							final String toolPath,
							final String workPath,
							final String startResultsDirPath,
							final AnalysisSettings settings,
							final double maxProcessors,
							final ResultSender resultSender) {
		super(subtree, resultSender);
		this.toolPath = toolPath;

		this.workPath = workPath;

        String slash = System.getProperty("file.separator");
        this.resultsDirPath = startResultsDirPath + slash + subtree.getID();
		// make directory results/subtreeID
		final File resultsDir = new File(this.resultsDirPath);
		resultsDir.mkdir();

		this.maxProcessors = maxProcessors;

		this.settings = settings;
	}

	@Override
	public ArrayList<ResultWrapper> doCall() throws IOException,
											InterruptedException,
											UnexpectedResultException,
											InvalidNodeAnalyserException,
											QueryServerException,
											DrmaaException,
											ExecutionException
	{

		final ArrayList<ResultWrapper> r = new ArrayList<ResultWrapper>();
		PerformanceQueryDrmaaSession drmSession;
		JobInfo exitInfo;

		try
		{
			// Create DRMAA session
			ServerLoggingHandler.logger.log(Level.INFO, "Creating Drmaa sesion");
			drmSession = new PerformanceQueryDrmaaSession();
			final String pathToModFile = ((OperationSubtree) this.subtree).getModFilePath();

			switch (this.subtree.getType())
			{
				case MOMENT :
					ServerLoggingHandler.logger.log(Level.INFO, "MOMA session");

					exitInfo = drmSession.submitJob(this.toolPath,
													pathToModFile,
													new String[]{this.workPath},
													this.resultsDirPath);

					break;
				case PASSAGETIMEDENSITY :

					ServerLoggingHandler.logger.log(Level.INFO, "Smarta session");

					// start smarta with created .mod file
					if (this.settings.numProcessors > this.maxProcessors)
					{
						final String failure = "The number of processors specified exceeds the server limit";
						throw new QueryServerException(failure);
					}
					else
					{
						// Convert number of processors int to string
						final String numProc = Integer.toString(this.settings.numProcessors);
						ServerLoggingHandler.logger.log(Level.INFO, "Running job with " + numProc +
																	" processors");
						// Submit the job passing the executable, input file,
						// number of processors + temp directory, working
						// directory
						final String[] extraArgs = new String[0];
						final ArrayList<String> extraParams = new ArrayList<String>();
						extraParams.add(numProc);
						extraParams.add(this.settings.inversionMethod);
						extraParams.add(this.workPath);
						extraParams.add(String.valueOf(this.settings.clearCache));

						exitInfo = drmSession.submitJob(this.toolPath,
														pathToModFile,
														extraParams.toArray(extraArgs),
														this.resultsDirPath);

					}
					break;
				case STEADYSTATEPROB :
				case FIRINGRATE :

					ServerLoggingHandler.logger.log(Level.INFO, "Dnamaca session");
					// start dnamaca with created .mod file
					exitInfo = drmSession.submitJob(this.toolPath, pathToModFile, null, this.resultsDirPath);
					break;
				case PROBINSTATES :
					ServerLoggingHandler.logger.log(Level.INFO, "Hydra session");
					// start dnamaca with created .mod file
					exitInfo = drmSession.submitJob(this.toolPath, pathToModFile, null, this.resultsDirPath);
					break;
				case PROBININTERVAL :
				{
					ServerLoggingHandler.logger.log(Level.INFO, "ProbInInterval session");

					final NodeAnalyserResultWrapper range = (NodeAnalyserResultWrapper) this.subtree.getChildByRole(QueryConstants.probInIntervalChildRange)
																									.getResult();
					final RangeNode rangeNode = (RangeNode) range.getResult();

					final PointsResultWrapper p = (PointsResultWrapper) this.subtree.getChildByRole(QueryConstants.probInIntervalChildDens)
																					.getResult();
					final String coeffFilePath = TextFileResultWrapper	.getFile(	p.getResultsDir(),
																					ServerConstants.ptdCoeffFileName)
																		.getAbsolutePath();

					final String cdfPointsFilePath = TextFileResultWrapper	.getFile(	p.getResultsDir(),
																						ServerConstants.pdfResultsFileName)
																			.getAbsolutePath();

					final String[] extraArgs = {coeffFilePath,
							cdfPointsFilePath,
							String.valueOf(rangeNode.getStart()),
							String.valueOf(rangeNode.getFinish()),
							this.workPath};

					exitInfo = drmSession.submitJob(this.toolPath, null, extraArgs, this.resultsDirPath);
					break;
				}
				case PERCENTILE :
				{
					ServerLoggingHandler.logger.log(Level.INFO, "Percentile session");
					// start dnamaca with created .mod file
					final PointsResultWrapper p = (PointsResultWrapper) this.subtree.getChildByRole(QueryConstants.percentileChildDensity)
																					.getResult();
					final String coeffFilePath = TextFileResultWrapper	.getFile(	p.getResultsDir(),
																					ServerConstants.ptdCoeffFileName)
																		.getAbsolutePath();
					final String cdfPointsFilePath = TextFileResultWrapper	.getFile(	p.getResultsDir(),
																						ServerConstants.cdfResultsFileName)
																			.getAbsolutePath();

					final NodeAnalyserResultWrapper num = (NodeAnalyserResultWrapper) this.subtree	.getChildByRole(QueryConstants.percentileChildNum)
																									.getResult();
					final NumNode numNode = (NumNode) num.getResult();

					final String[] extraArgs = {coeffFilePath,
							cdfPointsFilePath,
							String.valueOf(numNode.getValue()),
							this.workPath};

					exitInfo = drmSession.submitJob(this.toolPath, null, extraArgs, this.resultsDirPath);
					break;
				}
				case CONVOLUTION :
				{
					ServerLoggingHandler.logger.log(Level.INFO, "Convolution session");

					final PointsResultWrapper density1 = (PointsResultWrapper) this.subtree	.getChildByRole(QueryConstants.convChildDensity1)
																							.getResult();
					final PointsResultWrapper density2 = (PointsResultWrapper) this.subtree	.getChildByRole(QueryConstants.convChildDensity2)
																							.getResult();
					final String coeffFilePath1 = TextFileResultWrapper	.getFile(	density1.getResultsDir(),
																					ServerConstants.ptdCoeffFileName)
																		.getAbsolutePath();

					final String coeffFilePath2 = TextFileResultWrapper	.getFile(	density2.getResultsDir(),
																					ServerConstants.ptdCoeffFileName)
																		.getAbsolutePath();
					final String[] extraArgs = {coeffFilePath1, coeffFilePath2, this.workPath};

					exitInfo = drmSession.submitJob(this.toolPath, null, extraArgs, this.resultsDirPath);
					break;
				}
				default :
					final String msg = ": This type of node is not yet supported for evaluation (as yet!)";
					throw new QueryServerException(this.subtree.getType() + msg);
			}
			if (exitInfo != null && exitInfo.getExitStatus() == 0)
			{
				ServerLoggingHandler.logger.info("Drmaa session completed Successfully for " +
													this.subtree.getType() + " " + this.subtree.getID());
			}
			else
			{
				this.subtree.failed();
				throw new QueryServerException("Drmaa session failed to complete successfully for " +
												this.subtree.getType() + " " + this.subtree.getID());
			}
			ServerLoggingHandler.logger.log(Level.INFO, "Closing drmaaSession for subtree " +
														this.subtree.getID());

			r.add(this.getResults());
		}
		catch (final UnexpectedResultException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Couldn't create ResultWrapper for " +
															this.subtree.getType() + " " +
															this.subtree.getID());
			throw e;
		}
		catch (final InvalidNodeAnalyserException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Couldn't calculate node for " +
															this.subtree.getType() + " " +
															this.subtree.getID());
			throw e;
		}
		catch (final FileNotFoundException e)
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Couldn't find file for " +
															this.subtree.getType() + " " +
															this.subtree.getID());
			throw e;
		}
		return r;
	}

	private ResultWrapper getResults()	throws UnexpectedResultException,
										InterruptedException,
										InvalidNodeAnalyserException,
										IOException,
										ExecutionException
	{
		final File resultsDirectory = new File(this.resultsDirPath);
		ServerLoggingHandler.logger.info("retreiving results from file for " + this.subtree.getType() + "" +
											this.subtree.getID());
		switch (this.subtree.getType())
		{
			case MOMENT :
			{
				return new TextFileResultWrapper(	ServerConstants.momentResultsFileName,
													resultsDirectory,
													ServerConstants.momentNumResultPattern,
													this.subtree.getID(),
													this.subtree.getType());
			}
			case PASSAGETIMEDENSITY :
			{
				return new PointsResultWrapper(	ServerConstants.pdfResultsFileName,
												resultsDirectory,
												this.subtree.getID(),
												this.subtree.getType());

			}
			case STEADYSTATEPROB :
			{
				return new FilePointsResultWrapper(	ServerConstants.sspResultsFileName,
													resultsDirectory,
													ServerConstants.sspNumResultPattern,
													ServerConstants.sspPointsResultPattern,
													this.subtree.getID(),
													this.subtree.getType());
			}
			case FIRINGRATE :
			{
				return new TextFileResultWrapper(	ServerConstants.frResultsFileName,
													resultsDirectory,
													ServerConstants.frNumResultPattern,
													this.subtree.getID(),
													this.subtree.getType());
			}
			case PROBINSTATES :
			{
				return new PointsResultWrapper(	ServerConstants.probInStatesResultsFileName,
												resultsDirectory,
												this.subtree.getID(),
												this.subtree.getType());
			}
			case PROBININTERVAL :
				final NodeAnalyserResultWrapper range = (NodeAnalyserResultWrapper) this.subtree.getChildByRole(QueryConstants.probInIntervalChildRange)
																								.getResult();
				final RangeNode rangeNode = (RangeNode) range.getResult();
				return new ProbInIntervalResultWrapper(	rangeNode.getStart(),
														rangeNode.getFinish(),
														resultsDirectory,
														this.subtree.getID(),
														this.subtree.getType());
			case PERCENTILE :
				final NodeAnalyserResultWrapper num = (NodeAnalyserResultWrapper) this.subtree	.getChildByRole(QueryConstants.percentileChildNum)
																								.getResult();
				final NumNode numNode = (NumNode) num.getResult();
				return new PercentileResultWrapper(	numNode.getValue(),
													resultsDirectory,
													this.subtree.getID(),
													this.subtree.getType());
			case CONVOLUTION :
				return new PointsResultWrapper(	ServerConstants.pdfResultsFileName,
												resultsDirectory,
												this.subtree.getID(),
												this.subtree.getType());
			default :
				final String msg = ": This type of node is not yet supported for evaluation";
				throw new UnsupportedOperationException(this.subtree.getType() + msg);
		}
	}
}
