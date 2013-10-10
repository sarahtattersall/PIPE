/**
 * 
 */
package pipe.modules.queryresult;

import pipe.common.PetriNetNode;
import pipe.exceptions.UnexpectedResultException;
import pipe.handlers.StringHelper;
import pipe.server.interfaces.ServerConstants;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author dazz
 * 
 */
public class ProbInIntervalResultWrapper extends PointsResultWrapper implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 368396449662470868L;
	private final double				lowerBound;
    private final double upperBound;
    private final double lowerProb;
    private final double upperProb;

	public ProbInIntervalResultWrapper(	final double lowerBound,
										final double upperBound,
										final File resultsDir,
										final String nodeID,
										final PetriNetNode type) throws UnexpectedResultException, IOException {
		super(	ServerConstants.pdfResultsFileName,
				resultsDir,
				ServerConstants.probInIntervalNumResultPattern,
				ServerConstants.probInIntervalResultsFileName,
				nodeID,
				type);

		if (type != PetriNetNode.PROBININTERVAL)
		{
			throw new UnexpectedResultException(type + " not supported for ProbInIntervalResultWrapper");
		}
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;

		this.lowerProb = StringHelper.getNumResult(	ServerConstants.probInIntervalLowerProbPattern,
													this.getFileString().toString());

		this.upperProb = StringHelper.getNumResult(ServerConstants.probInIntervalUpperProbPattern,
                                                   this.getFileString().toString());

	}

	/**
	 * @return the lowerBound
	 */
	public double getLowerBound()
	{
		return this.lowerBound;
	}

	/**
	 * @return the lowerProb
	 */
	public double getLowerProb()
	{
		return this.lowerProb;
	}

	/**
	 * @return the upperBound
	 */
	public double getUpperBound()
	{
		return this.upperBound;
	}

	/**
	 * @return the upperProb
	 */
	public double getUpperProb()
	{
		return this.upperProb;
	}

}
