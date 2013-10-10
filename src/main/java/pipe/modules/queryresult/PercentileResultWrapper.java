/**
 * 
 */
package pipe.modules.queryresult;

import pipe.common.PetriNetNode;
import pipe.exceptions.UnexpectedResultException;
import pipe.server.interfaces.ServerConstants;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

/**
 * @author dazz
 * 
 */
public class PercentileResultWrapper extends PointsResultWrapper implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 8107307676047666689L;
	private final double				percentile;

	public PercentileResultWrapper(	final double percentile,
									final File resultsDir,
									final String nodeID,
									final PetriNetNode type)	throws
            UnexpectedResultException,
														IOException {
		super(	ServerConstants.cdfResultsFileName,
				resultsDir,
				ServerConstants.percentileNumResultPattern,
				ServerConstants.percentileResultsFileName,
				nodeID,
				type);
		if (type != PetriNetNode.PERCENTILE)
		{
			throw new UnexpectedResultException(type + " not supported for PercentileResultWrapper");
		}
		this.percentile = percentile;
	}

	/**
	 * @return the percentile
	 */
	public double getPercentile()
	{
		return this.percentile;
	}

}
