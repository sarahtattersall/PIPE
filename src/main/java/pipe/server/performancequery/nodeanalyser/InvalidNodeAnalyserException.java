/**
 * 
 */
package pipe.server.performancequery.nodeanalyser;

import java.lang.instrument.IllegalClassFormatException;

/**
 * @author dazz
 * 
 */
public class InvalidNodeAnalyserException extends IllegalClassFormatException
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 3777419691549918175L;

	public InvalidNodeAnalyserException(final String msg) {
		super(msg);
	}

}
