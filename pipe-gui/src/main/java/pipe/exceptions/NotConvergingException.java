package pipe.exceptions;

/**
 * Exception called when a Gauss-Seidel algorithm is called, but doesn't converge
 * 
 * @author Oliver Haggarty - August 2007
 *
 */
public class NotConvergingException extends Exception {
	public NotConvergingException() {
		super("The Gauss-Seidel method is not converging");
	}
}
