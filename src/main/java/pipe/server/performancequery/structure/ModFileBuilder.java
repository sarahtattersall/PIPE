/*
 * Created on Jan 29, 2008
 * Created by darrenbrien
 */

package pipe.server.performancequery.structure;

import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;

class ModFileBuilder implements StructureLoggingHandler
{
	private final String	modExtension	= ".mod";

	private StringBuilder	currentFileContents;
	private StringBuilder	modelString		= null;

	private String			currentFile;
	private final String	path;
	private int				incarnation		= 0;

	public ModFileBuilder(final String path) {
		this.currentFile = "";
		this.path = path;
	}

	void addModelToFile(final ModFile m)
	{
		m.getModString().insert(0, this.modelString);
	}

	public ModFile addNewModfile(final OperationSubtree subtree)
	{
		final String nodeID = subtree.getNode().getID();
		this.currentFile = this.path + System.getProperty("file.separator") + nodeID + this.modExtension;
		ModFile modFile;
		modFile = new ModFile(subtree, this.currentFile);

		this.currentFileContents = modFile.getModString();
		return modFile;
	}

	void addToModel()
	{
		this.currentFileContents = this.modelString = new StringBuilder();
	}

	void addToSteadyStateMod(final OperationSubtree subtree)
	{
		this.currentFile = this.path + System.getProperty("file.separator") + "SteadyState" +
							this.incarnation + this.modExtension;
		final ModFile steadyStateModFile = ModFile.getSteadyStateModFile(subtree, this.currentFile);

		this.currentFileContents = steadyStateModFile.getModString();
	}

	/**
	 * @param line
     * @return allows for compound statements this.append.append.append etc
	 */
	ModFileBuilder append(final Object line)
	{
		this.currentFileContents.append(line.toString());
		return this;
	}

	private void create(final ModFile m) throws IOException
	{
		this.createFile(m);
	}

	private void createFile(final ModFile m) throws IOException
	{
		FileWriter modFileWriter;
		try
		{
			modFileWriter = new FileWriter(m.getFile());
			modFileWriter.write(m.getModString().toString());
			modFileWriter.close();
		}
		catch (final IOException e)
		{
			StructureLoggingHandler.logger.log(Level.WARNING, "Couldn't write/close file:" + m.getFilePath());
			throw e;
		}
	}

	public void finalise(final ModFile modFile) throws IOException
	{
		this.addModelToFile(modFile);

		this.create(modFile);
	}

	public void finaliseSteadyState() throws IOException
	{
		final String sSMeasureStartTag = "\\performance{\n";
		final String sSMeasureEndTag = "\n}\n\n";

		final ModFile m = ModFile.removeSteadyStateModFile();
		m.getModString().insert(0, sSMeasureStartTag);
		m.getModString().append(sSMeasureEndTag);
		this.addModelToFile(m);
		this.create(m);

		this.incarnation++;
	}

	public String getCurrentFile()
	{
		return this.currentFile;
	}

	public boolean hasModel()
	{
		return this.modelString != null;
	}
}
