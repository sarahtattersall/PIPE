/**
 * 
 */
package pipe.modules.queryresult;

import java.io.Serializable;
import java.util.Comparator;

/**
 * @author dazz
 * 
 */
public class XYCoordinate implements Serializable, Comparable<XYCoordinate>
{
	public static class YComponentComparator implements Comparator<XYCoordinate>
	{
		public int compare(final XYCoordinate coord1, final XYCoordinate coord2)
		{
			return (int) ((coord1.getY() - coord2.getY()) * XYCoordinate.valueGranularity);
		}
	}

	/**
	 * 
	 */
	private static final long	serialVersionUID	= -6908177385271106413L;

	private static final int	valueGranularity	= 1000000;

	private final double		x, y;

	public XYCoordinate(final double x, final double y) {
		this.x = x;
		this.y = y;
	}

	public int compareTo(final XYCoordinate coord)
	{
		return (int) ((this.x - coord.x) * XYCoordinate.valueGranularity);
	}

	/**
	 * @return the x
	 */
	public double getX()
	{
		return this.x;
	}

	/**
	 * @return the y
	 */
	public double getY()
	{
		return this.y;
	}
}
