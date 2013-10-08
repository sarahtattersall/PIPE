package pipe.exceptions;

/**
 * Exception called when a start/target state logical expression entered by the user
 * is syntactically incorrect, or doesn't correspond to an existing state
 * 
 * @author Oliver Haggarty - August 2007
 *
 */
public class NotValidExpressionException extends Exception {
	public NotValidExpressionException() {
		super("The expression being parsed was not valid");
	}
	
	public NotValidExpressionException(String message) {
		super(message);
	}
}
