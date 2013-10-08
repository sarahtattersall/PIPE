/**
 * 
 */
package pipe.modules.queryresult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pipe.common.PetriNetNode;
import pipe.exceptions.UnexpectedResultException;
import pipe.handlers.StringHelper;
import pipe.server.performancequery.nodeanalyser.NumNode;
import pipe.server.performancequery.nodeanalyser.ValueNodeAnalyser;

/**
 * @author dazz
 * 
 */
public class TextFileResultWrapper extends ResultWrapper implements Serializable, QueryResultLoggingHandler
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 4004434002146109095L;

	public static File getFile(final File directory, final String fileName) throws FileNotFoundException
	{
		String exceptionMsg = null;
		if (directory.isDirectory())
		{
			for (final File f : directory.listFiles())
			{
				if (f.getName().equalsIgnoreCase(fileName))
					return f;
			}
			exceptionMsg = fileName + " isn't contained in directory " + directory.getAbsolutePath();
		}
		throw new FileNotFoundException(exceptionMsg == null ? "File argument is not directory cannot find " +
																fileName : exceptionMsg);
	}

	private static double round(final double d)
	{
		return (int) (d * 1e6 + 0.5) / 1e6;
	}

	private final StringBuilder	fileString;
	private final File			resultsDir;

	private final String		regex	= "\\s+" + String.valueOf(StringHelper.SENTINEL) +
											"\\s+\\S+[[\r\n]\n\r]";

	private final Double		numResult;

	public TextFileResultWrapper(	final double numResult,
									final StringBuilder fileString,
									final String nodeID,
									final PetriNetNode type) {
		super(nodeID, type);
		this.fileString = fileString;
		this.numResult = numResult;
		this.resultsDir = null;
	}

	TextFileResultWrapper(final File resultsDir, final String nodeID, final PetriNetNode type) {
		super(nodeID, type);
		this.resultsDir = resultsDir;
		this.fileString = null;
		this.numResult = null;
	}

	TextFileResultWrapper(final String fileName,
                          final File resultsDir,
                          final String nodeID,
                          final PetriNetNode type) throws IOException {
		super(nodeID, type);
		this.fileString = this.findandRemoveSentinel(this.getFileText(resultsDir, fileName));
		this.resultsDir = resultsDir;
		this.numResult = null;
	}

	public TextFileResultWrapper(	final String fileName,
									final File resultsDir,
									final String pattern,
									final String nodeID,
									final PetriNetNode type) throws IOException {
		super(nodeID, type);
		this.fileString = this.findandRemoveSentinel(this.getFileText(resultsDir, fileName));

		this.numResult = StringHelper.getNumResult(pattern, this.fileString.toString());

		this.resultsDir = resultsDir;
	}

	private StringBuilder doGetFileText(final File f) throws IOException
	{
		final StringBuilder s = new StringBuilder();
		BufferedReader r = null;
		try
		{
			r = new BufferedReader(new FileReader(f));
			String line;
			while ((line = r.readLine()) != null)
			{
				s.append(line);
				s.append("\n");
			}
		}
		finally
		{
			if (r != null)
			{
				try
				{
					r.close();
				}
				catch (final IOException e)
				{
					QueryResultLoggingHandler.logger.log(Level.WARNING, "Couldn't close result file", e);
				}
			}
		}
		return s;
	}

	private StringBuilder findandRemoveSentinel(final StringBuilder pointsResult)
	{
		final Pattern p = Pattern.compile(this.regex, Pattern.CASE_INSENSITIVE);
		String s = pointsResult.toString();

		final Matcher m = p.matcher(s);

		while (m.find())
		{
			final String sentinelLine = m.group();
			final Scanner s1 = new Scanner(sentinelLine);

			// first double is sentinel value, second is value we use to scale
			// result of results
			s1.nextDouble();
			final double scaleFactor = s1.hasNextDouble() ? 1 - s1.nextDouble() : StringHelper.SENTINEL;

			final String start = s.substring(0, m.start());
			String rest = s.substring(m.end(), s.length());

			rest = this.scaleRest(rest, scaleFactor);

			s = start + rest;
		}
		return new StringBuilder(s);
	}

	/**
	 * @return the pdfPoints
	 */
	public StringBuilder getFileString()
	{
		return this.fileString;
	}

	StringBuilder getFileText(final File directory, final String fileName) throws
            IOException
	{
		return this.doGetFileText(TextFileResultWrapper.getFile(directory, fileName));
	}

	/**
	 * @return the numResult
	 */
	public double getNumResult()
	{
		if (this.numResult == null)
		{
			QueryResultLoggingHandler.logger.warning("numResult is null, does this result type return a number result? You're about to get a null pointer exception!");
		}
		return this.numResult;
	}

	public String getPlotName()
	{
		try
		{
			switch (this.getOrginalType())
			{
				case PASSAGETIMEDENSITY :
					return "Passage Time Results";

				case DISTRIBUTION :
				case PERCENTILE :
				case PROBININTERVAL :
					return "Passage Time Results (CDF)";
				case PROBINSTATES :
					return "Transient Distribution";
				case STEADYSTATEPROB :
					return "Steady State Probability Distribution";
				default :
					throw new UnexpectedResultException(this.getOrginalType() +
														" isn't implemented to use a plot and so doesn't have a default name");
			}
		}
		catch (final UnexpectedResultException e)
		{
			QueryResultLoggingHandler.logger.log(Level.WARNING, "Returning empty string", e);
		}
		return "";
	}

	@Override
	public ValueNodeAnalyser getResult()
	{
		return new NumNode(this.numResult);
	}

	/**
	 * @return the resultsDir
	 */
	public File getResultsDir()
	{
		return this.resultsDir;
	}

	XYCoordinates getValues(final StringBuilder s)
	{
		final XYCoordinates data = new XYCoordinates();
		if (s != null)
		{
			final Scanner s1 = new Scanner(s.toString());
			// get each line
			final String delim = "[[\r\n]\n\r]";
			s1.useDelimiter(delim);

			// use p2 to check for double <space> double pairs
			final Pattern p2 = Pattern.compile("\\s*\\S+\\s+\\S+");

			int i = 0;
			try
			{
				while (s1.hasNext())
				{
					final String temp = s1.next();

					if (p2.matcher(temp).matches())
					{
						// get both vals on each line
						final Scanner s2 = new Scanner(temp);

						final double x = s2.hasNextDouble() ? s2.nextDouble() : StringHelper.SENTINEL;
						final double y = s2.hasNextDouble() ? s2.nextDouble() : StringHelper.SENTINEL;

						if (s2.hasNext() || x == StringHelper.SENTINEL || x == StringHelper.SENTINEL)
						{
							final String msg = "line:" + i + " elements not pair of doubles";
							throw new UnexpectedResultException(msg);
						}
						data.add(new XYCoordinate(x, y));
					}
					i++;
				}
			}
			catch (final UnexpectedResultException e)
			{
				QueryResultLoggingHandler.logger.log(Level.WARNING, "Results file received is bad..", e);
			}
		}
		return data;
	}

	private String scaleRest(final String rest, final double scaleFactor)
	{
		final Scanner s1 = new Scanner(rest);
		// get each line
		final String delim = "[[\r\n]\n\r]";
		final Pattern whiteSpace = Pattern.compile("\\s+");

		s1.useDelimiter(delim);

		final StringBuilder replacement = new StringBuilder();

		int i = 0;
		try
		{
			while (s1.skip(whiteSpace).hasNext())
			{
				final String temp = s1.next();

				// get both vals on each line
				final Scanner s2 = new Scanner(temp);

				final double x = s2.hasNextDouble() ? s2.nextDouble() : StringHelper.SENTINEL;
				double y = StringHelper.SENTINEL;
				if (s2.hasNextDouble())
				{
					y = scaleFactor != 0 ? TextFileResultWrapper.round(s2.nextDouble() / scaleFactor) : 0;
				}

				if (s2.hasNext() || x == StringHelper.SENTINEL || y == StringHelper.SENTINEL)
				{
					final String msg = "line:" + i + " elements not pair of doubles";
					throw new UnexpectedResultException(msg);
				}

                replacement.append("\n\t\t").append(String.valueOf(x)).append("\t").append(String.valueOf(y));
				i++;
			}
		}
		catch (final UnexpectedResultException e)
		{
			QueryResultLoggingHandler.logger.log(Level.WARNING, "Results file received is bad..\n" + rest, e);
		}
		return replacement.toString();
	}
}
