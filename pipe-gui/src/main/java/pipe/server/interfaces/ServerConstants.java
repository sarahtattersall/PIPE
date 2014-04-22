/**
 * 
 */
package pipe.server.interfaces;

/**
 * @author dazz
 * 
 */
public interface ServerConstants
{
	public static final String	cdfResultsFileName				= "points.cdf.dat";
	public static final String	pdfResultsFileName				= "points.dat";
	public static final String	convolutionResultsFileName		= "points.dat";
	public static final String	percentileResultsFileName		= "points.percentile.dat";
	public static final String	probInIntervalResultsFileName	= "points.probininterval.dat";
	public static final String	probInStatesResultsFileName		= "results.dat";
	public static final String	momentResultsFileName			= "results.dat";
	public static final String	sspResultsFileName				= "results.dat";
	public static final String	frResultsFileName				= "results.dat";

	public static final String	ptdCoeffFileName				= "points.coeffs.dat";

	public static final String	sspPointsResultPattern			= "distribution";

	public static final String	frNumResultPattern				= "mean";
	public static final String	sspNumResultPattern				= "mean";
	public static final String	momentNumResultPattern			= "moment";

	public static final String	probInIntervalNumResultPattern	= "Interval Probability";
	public static final String	probInIntervalLowerProbPattern	= "lower bound";
	public static final String	probInIntervalUpperProbPattern	= "upper bound";

	public static final String	probInStatesNumResultPattern	= "transient";
	public static final String	percentileNumResultPattern		= "percentile";

}
