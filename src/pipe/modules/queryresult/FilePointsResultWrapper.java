package pipe.modules.queryresult;

import pipe.common.PetriNetNode;
import pipe.handlers.StringHelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;

public class FilePointsResultWrapper extends TextFileResultWrapper implements Serializable
{
	private static final long	serialVersionUID	= 8777420664944453420L;
	private XYCoordinates				points;

	public FilePointsResultWrapper(	final String fileName,
									final File resultsDir,
									final String numPattern,
									final String pointsPattern,
									final String nodeID,
									final PetriNetNode type) throws IOException {
		super(fileName, resultsDir, numPattern, nodeID, type);

		this.points = this.getValues(StringHelper.findSubStringPoints(this.getFileString(), pointsPattern));
	}

	public XYCoordinates getPoints()
	{
		return this.points;
	}

}
