/**
 * 
 */
package pipe.modules.queryresult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

import pipe.common.PetriNetNode;

/**
 * @author dazz
 * 
 */
public class PointsResultWrapper extends TextFileResultWrapper
implements
	QueryResultLoggingHandler,
	Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= -1223493946656817497L;
	private final XYCoordinates	plotPoints;

	/**
	 * No output file, no value
	 * 
	 * @param pointsFileName
	 * @param resultsDir
	 * @param nodeID
	 * @param type
	 * @throws FileNotFoundException
	 */
	public PointsResultWrapper(	final String pointsFileName,
								final File resultsDir,
								final String nodeID,
								final PetriNetNode type) throws IOException {
		super(resultsDir, nodeID, type);
		this.plotPoints = this.getValues(this.getFileText(resultsDir, pointsFileName));
	}

	/**
	 * Has output file No Value
	 * 
	 * @param pointsFileName
	 * @param resultsDir
	 * @param outputFileName
	 * @param nodeID
	 * @param type
	 * @throws FileNotFoundException
	 */
	public PointsResultWrapper(	final String pointsFileName,
								final File resultsDir,
								final String outputFileName,
								final String nodeID,
								final PetriNetNode type) throws IOException {
		super(outputFileName, resultsDir, nodeID, type);
		this.plotPoints = this.getValues(this.getFileText(resultsDir, pointsFileName));
	}

	/**
	 * Has output file and value
	 * 
	 * @param pointsResult
	 * @param pointsFileName
     * @param resultsDir
	 * @param pattern
	 * @param outputFileName
	 * @param nodeID
	 * @param type
	 * @throws FileNotFoundException
	 */
    PointsResultWrapper(final String pointsFileName,
                        final File resultsDir,
                        final String pattern,
                        final String outputFileName,
                        final String nodeID,
                        final PetriNetNode type) throws IOException {
		super(outputFileName, resultsDir, pattern, nodeID, type);

		this.plotPoints = this.getValues(this.getFileText(resultsDir, pointsFileName));

	}

	/**
	 * @return the plotPoints
	 */
	public XYCoordinates getPoints()
	{
		return this.plotPoints;
	}

}
