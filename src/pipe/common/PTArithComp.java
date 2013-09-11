/*
 * Created on Jan 21, 2008
 * Created by darrenbrien
 */
package pipe.common;

public enum PTArithComp
{
	LESS, LEQ, EQ, GEQ, GREATER;

	public static PTArithComp fromString(final String operation)
	{
		if (operation.equalsIgnoreCase("lt"))
		{
			return LESS;
		}
		else if (operation.equalsIgnoreCase("leq"))
		{
			return LEQ;
		}
		else if (operation.equalsIgnoreCase("eq"))
		{
			return EQ;
		}
		else if (operation.equalsIgnoreCase("geq"))
		{
			return GEQ;
		}
		else
		{
			return GREATER;
		}
	}
}