/*
 * Created on Jan 21, 2008
 * Created by darrenbrien
 */
package pipe.common;

public enum PTDisCon
{
	DISJUNCTION, CONJUNCTION;

	public static PTDisCon fromString(final String operation)
	{
		if (operation.equalsIgnoreCase("or"))
			return DISJUNCTION;
		return CONJUNCTION;
	}
}
