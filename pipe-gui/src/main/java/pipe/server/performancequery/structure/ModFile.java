/**
 * 
 */
package pipe.server.performancequery.structure;

import java.io.File;

/**
 * @author dazz
 * 
 */
public class ModFile implements StructureLoggingHandler
{

	private static ModFile	steadyStates;

	public static ModFile getSteadyStateModFile(final OperationSubtree subtree, final String filePath)
	{
		if (ModFile.steadyStates == null)
		{
			ModFile.steadyStates = new ModFile(subtree, filePath);
		}
		return ModFile.steadyStates;
	}

	public static ModFile removeSteadyStateModFile()
	{
		try
		{
			return ModFile.steadyStates;
		}
		finally
		{
			ModFile.steadyStates = null;
		}

	}

	private final File				file;

	private final StringBuilder		modString;

	private final String			filePath;

	private final OperationSubtree	subtree;

	public ModFile(final OperationSubtree subtree, final String filePath) {
		this.subtree = subtree;
		this.filePath = filePath;
		this.modString = new StringBuilder();
		this.file = new File(filePath);
		this.getSubtree().setModFile(this.file);
	}

	/**
	 * @return the file
	 */
	public File getFile()
	{
		return this.file;
	}

	/**
	 * @return the filePath
	 */
	public String getFilePath()
	{
		return this.filePath;
	}

	/**
	 * @return the modString
	 */
	public StringBuilder getModString()
	{
		return this.modString;
	}

	/**
	 * @return the subtree
	 */
    private OperationSubtree getSubtree()
	{
		return this.subtree;
	}
}
