/**
 * 
 */
package pipe.modules.queryresult;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;


/**
 * @author dazz
 * 
 */
public class XYCoordinates implements Serializable, QueryResultLoggingHandler
{
	/**
	 * 
	 */
	private static final long				serialVersionUID	= 3044855272952988357L;
	private final ArrayList<XYCoordinate>	points;
	private final ArrayList<XYCoordinate>	pointsOrderedByY;
	private boolean							sorted;

	public XYCoordinates() {
		this.sorted = false;
		this.points = new ArrayList<XYCoordinate>();
		this.pointsOrderedByY = new ArrayList<XYCoordinate>();
	}

	public void add(final XYCoordinate coord)
	{
		this.sorted = false;
		this.points.add(coord);
		this.pointsOrderedByY.add(coord);
	}

	private int adjustIndex(final int index)
	{
		return index < 0 ? Math.abs(index + 1) : index;
	}

	private XYCoordinate doGetCoordinateAtX(final double x)
	{
		return this.points.get(this.adjustIndex(Collections.binarySearch(this.points, new XYCoordinate(x, 0))));
	}

	private XYCoordinate doGetCoordinateAtY(final double y)
	{
		return this.pointsOrderedByY.get(this.adjustIndex(Collections.binarySearch(	this.pointsOrderedByY,
																					new XYCoordinate(0, y),
																					new XYCoordinate.YComponentComparator())));
	}

	private List<XYCoordinate> doGetFromXToX(final double start, final double end)
	{
		final ArrayList<XYCoordinate> subList = new ArrayList<XYCoordinate>();

		final int startIndex = this.adjustIndex(Collections.binarySearch(	this.points,
																			new XYCoordinate(start, 0)));
		final int endIndex = this.adjustIndex(Collections.binarySearch(this.points, new XYCoordinate(end, 0)));
		for (int index = startIndex; index < endIndex; index++)
		{
			subList.add(this.points.get(index));
		}
		return Collections.unmodifiableList(subList);
	}

	private List<XYCoordinate> doGetFromYToY(final double start, final double end)
	{
		final ArrayList<XYCoordinate> subList = new ArrayList<XYCoordinate>();
		final int startIndex = this.adjustIndex(Collections.binarySearch(	this.pointsOrderedByY,
																			new XYCoordinate(0, start),
																			new XYCoordinate.YComponentComparator()));
		final int endIndex = this.adjustIndex(Collections.binarySearch(	this.pointsOrderedByY,
																		new XYCoordinate(0, end),
																		new XYCoordinate.YComponentComparator()));
		for (int index = startIndex; index < endIndex; index++)
		{
			subList.add(this.pointsOrderedByY.get(index));
		}
		return Collections.unmodifiableList(subList);
	}

	private XYCoordinate doGetMaxX()
	{
		try
		{
			return this.points.get(this.points.size() - 1);
		}
		catch (final IndexOutOfBoundsException e)
		{
			QueryResultLoggingHandler.logger.log(Level.WARNING, "XYCoordinates list is empty");
			throw e;
		}
	}

	private XYCoordinate doGetMaxY()
	{
		try
		{
			return this.pointsOrderedByY.get(this.pointsOrderedByY.size() - 1);
		}
		catch (final IndexOutOfBoundsException e)
		{
			QueryResultLoggingHandler.logger.log(Level.WARNING, "XYCoordinates list is empty");
			throw e;
		}
	}

	private XYCoordinate doGetMinX()
	{
		return this.points.get(0);
	}

	private XYCoordinate doGetMinY()
	{
		return this.pointsOrderedByY.get(0);
	}

	public XYCoordinate getCoordinateAtX(final double x)
	{
		this.sort();
		return this.doGetCoordinateAtX(x);
	}

	public XYCoordinate getCoordinateAtY(final double y)
	{
		this.sort();
		return this.doGetCoordinateAtY(y);
	}

	public List<XYCoordinate> getFromXToX(final double start, final double end)
	{
		this.sort();
		return this.doGetFromXToX(start, end);
	}

	public List<XYCoordinate> getFromYToY(final double start, final double end)
	{
		this.sort();
		return this.doGetFromYToY(start, end);
	}

	public int getItemCount()
	{
		return this.points.size();
	}

	public XYCoordinate getMaxX()
	{
		this.sort();
		return this.doGetMaxX();
	}

	public XYCoordinate getMaxY()
	{
		this.sort();
		return this.doGetMaxY();
	}

	public XYCoordinate getMinX()
	{
		this.sort();
		return this.doGetMinX();
	}

	public XYCoordinate getMinY()
	{
		this.sort();
		return this.doGetMinY();
	}

	public List<XYCoordinate> getPoints()
	{
		return Collections.unmodifiableList(this.points);
	}

	private void sort()
	{
		if (!this.sorted)
		{
			Collections.sort(this.points);
			Collections.sort(this.pointsOrderedByY, new XYCoordinate.YComponentComparator());
			this.sorted = true;
		}
	}
}
