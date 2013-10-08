package pipe.modules.iai;

/**
 * This class is used to calculate exponentially distributed
 * random variables.
 * 
 * The algorithm is based on the basic formulas to be found in
 * any math book on the subject. 
 * 
 * @author dpatterson
 *
 */
class ExponentialDistribution
{

	private final double mean;
	
	public ExponentialDistribution( double rate )
	{
		mean = 1.0d / rate;
	}
	
	public double inverse( double rval )
	{
		return - mean * Math.log(rval );
	}
}
