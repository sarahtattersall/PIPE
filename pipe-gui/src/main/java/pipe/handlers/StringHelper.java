/**
 * 
 */
package pipe.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author dazz
 * 
 */
public class StringHelper
{
	/**
	 * A value used to identify invalid results or results to be adjusted
	 */
	public static final double	SENTINEL	= -9.999e-99;

	public static StringBuilder findSubStringPoints(final StringBuilder s, String scanTo)
	{
		final String points = s.toString();
		scanTo = scanTo + "[\n\r]";
		final Pattern p1 = Pattern.compile(scanTo, Pattern.CASE_INSENSITIVE);
		final Matcher m = p1.matcher(points);
		String result = null;
		while (m.find())
		{
			result = points.substring(m.end(), points.length());
		}
		return new StringBuilder(result);
	}

	public static double getNumResult(final String pattern, final String fileText)
	{
		final String wholePattern = ".*?" + pattern + ".*?";
		final Pattern p = Pattern.compile(wholePattern, Pattern.CASE_INSENSITIVE);
		double result = StringHelper.SENTINEL;
		String line = null;
		final Scanner s1 = new Scanner(fileText);
		s1.useDelimiter("\n|\r");
		while (s1.hasNext())
		{
			line = s1.next();
			if (p.matcher(line).matches())
			{
				for (final Scanner s2 = new Scanner(line); s2.hasNext();)
				{
					if (s2.hasNextDouble())
					{
						result = s2.nextDouble();
					}
					else
					{
						s2.next();
					}
				}
			}
		}
		return result;
	}

	/**
	 * Appends correct delimeter to a number th, st, rd returning the number and
	 * delimeter appended
	 * 
	 * @param numVal
	 * @return
	 */
	public static String getStringTH(final double numVal)
	{
		final double remainder = numVal % 10;
		String numth = String.valueOf(numVal);
		if (remainder == 1)
		{
			numth += "st";
		}
		else if (remainder == 2)
		{
			numth += "nd";
		}
		else if (remainder == 3)
		{
			numth += "rd";
		}
		else
		{
			numth += "th";
		}
		return numth;
	}

	/**
	 * 
	 * @param s -
	 *            string to check for validity and replace labels for
	 * @param replacement -
	 *            StringBuilder to be populated with replacement string
	 *            containing place ids obtained from contained place names
	 * @param nameToId -
	 *            HashMap of place name to place id maps
	 * @return
	 */
	public static boolean hasSpecifiedLabels(	final String s,
												final StringBuilder replacement,
												final HashMap<String, String> nameToId)
	{
		return StringHelper.hasSpecifiedLabels(s, replacement, nameToId, false);
	}

	/**
	 * 
	 * @param s -
	 *            string to check for validity and replace labels for
	 * @param replacement -
	 *            Out variable. StringBuilder to be populated with replacement
	 *            string containing place ids obtained from contained place
	 *            names
	 * @param nameToId -
	 *            HashMap of place name to place id maps
	 * @param replaceNames
     * @param replaceName -
	 *            should the place names be replaced with the place ids
	 * @return does string have all specified names that map to place ids in the
	 * model @
	 */
	private static boolean hasSpecifiedLabels(	final String s,
												final StringBuilder replacement,
												final HashMap<String, String> nameToId,
												final boolean replaceNames)
	{
		final String regex = "#\\([\\w\\s]+\\)";
		final Pattern pattern = Pattern.compile(regex);

		Matcher m = pattern.matcher(s);
		boolean retval = true;
		final ArrayList<String> labels = new ArrayList<String>();
		while (m.find())
		{
			// get each state name used
			final String label = m.group().replaceAll("[#\\(\\)]", "").trim();
			retval &= nameToId.containsKey(label);

			// get corresponing place id for this label
			if (retval)
			{
				labels.add(nameToId.get(label));
			}
		}

		if (retval && replaceNames)
		{
			// if input ok replace all #(*) occurances with their place id
			// use order of labels arraylist built above to do this
			replacement.delete(0, replacement.length());
			replacement.append(s);
			for (final String label : labels)
			{
				m = pattern.matcher(replacement.toString());
				replacement.delete(0, replacement.length());
				replacement.append(m.replaceFirst(label));
			}
		}
		return retval;
	}

	/**
	 * 
	 * @param s -
	 *            string to check for validity and replace labels for
	 * @param replacement -
	 *            StringBuilder to be populated with replacement string
	 *            containing place ids obtained from contained place names
	 * @param nameToId -
	 *            HashMap of place name to place id maps
	 * @return
	 */
	public static boolean hasSpecifiedLabelsAndReplace(	final String s,
														final StringBuilder replacement,
														final HashMap<String, String> nameToId)
	{
		return StringHelper.hasSpecifiedLabels(s, replacement, nameToId, true);
	}
}
