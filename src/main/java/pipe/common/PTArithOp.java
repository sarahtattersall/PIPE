/*
 * Created on Jan 21, 2008
 * Created by darrenbrien
 */
package pipe.common;

public enum PTArithOp
{
	PLUS, MINUS, DIVIDE, MULTIPLY, POWER;

	public static PTArithOp fromString(final String operation)
	{
		if (operation.equalsIgnoreCase("plus"))
		{
			return PLUS;
		}
		else if (operation.equalsIgnoreCase("minus"))
		{
			return MINUS;
		}
		else if (operation.equalsIgnoreCase("times"))
		{
			return MULTIPLY;
		}
		else if (operation.equalsIgnoreCase("div"))
		{
			return DIVIDE;
		}
		else
		{
			return POWER;
		}
	}

}
