package net.sourceforge.jeval;

/**
 * Contains constants used by classes in this package.
 */
public class EvaluationConstants {

	/**
	 * The single quote character.
	 */
	public static final char SINGLE_QUOTE = '\'';

	/**
	 * The double quote character.
	 */
	public static final char DOUBLE_QUOTE = '"';

	/**
	 * The open brace character.
	 */
	public static final char OPEN_BRACE = '{';

	/**
	 * The closed brace character.
	 */
	public static final char CLOSED_BRACE = '}';

	/**
	 * The pound sign character.
	 */
	public static final char POUND_SIGN = '#';

	/**
	 * The open variable string.
	 */
	public static final String OPEN_VARIABLE = String.valueOf(POUND_SIGN)
			+ String.valueOf(OPEN_BRACE);

	/**
	 * The closed brace string.
	 */
	public static final String CLOSED_VARIABLE = String.valueOf(CLOSED_BRACE);

	/**
	 * The true value for a Boolean string.
	 */
	public static final String BOOLEAN_STRING_TRUE = "1.0";

	/**
	 * The false value for a Boolean string.
	 */
	public static final String BOOLEAN_STRING_FALSE = "0.0";
	
	/**
	 * The comma character.
	 */
	public static final char COMMA = ',';
	
	/**
	 * The function argument separator.
	 */
	public static final char FUNCTION_ARGUMENT_SEPARATOR = COMMA;
}
