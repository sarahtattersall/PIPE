package pipe.server.serverCommon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import pipe.common.LoggingHelper;

/**
 * 
 */

/**
 * @author dazz
 * 
 */
public class PathsWrapper
{

	private static final Logger	logger	= Logger.getLogger("pipe.server.serverCommon");

	private String			workPath, dnamacaPath, smartaPath, momaPath, hydraPath, convoPath,
	probInIntervalPath, percentilePath;
	private Integer			maxProcessors;

	/**
	 * This method reads the path settings from the file paths.config. This file
	 * is in the form: workingDir /vol/grail/users/grail_service/workingDir ...
	 */
	public PathsWrapper() {
		File pathFile = null;
		String currentLine;
		String[] components;

		try
		{
			final String pathConfigFilePath = "pipe" + System.getProperty("file.separator") + "server" +
												System.getProperty("file.separator") + "serverCommon" +
												System.getProperty("file.separator") + "paths.config";
			pathFile = new File(pathConfigFilePath);
			final BufferedReader pathSettings = new BufferedReader(new FileReader(pathFile));
			while ((currentLine = pathSettings.readLine()) != null)
			{
				components = currentLine.split("\\s+");
				if (components.length == 2)
				{
					if (components[0].equalsIgnoreCase("workingDir"))
					{
						this.workPath = components[1];
						PathsWrapper.logger.log(Level.INFO, "Working directory: " + this.workPath);
					}
					else if (components[0].equalsIgnoreCase("dnamaca"))
					{
						this.dnamacaPath = components[1];
						PathsWrapper.logger.log(Level.INFO, "DNAmaca path: " + this.dnamacaPath);
					}
					else if (components[0].equalsIgnoreCase("smarta"))
					{
						this.smartaPath = components[1];
						PathsWrapper.logger.log(Level.INFO, "SMARTA path: " + this.smartaPath);
					}
					else if (components[0].equalsIgnoreCase("moma"))
					{
						this.momaPath = components[1];
						PathsWrapper.logger.log(Level.INFO, "MOMA path: " + this.momaPath);
					}
					else if (components[0].equalsIgnoreCase("convo"))
					{
						this.convoPath = components[1];
						PathsWrapper.logger.log(Level.INFO, "convo path: " + this.convoPath);
					}
					else if (components[0].equalsIgnoreCase("probininterval"))
					{
						this.probInIntervalPath = components[1];
						PathsWrapper.logger.log(Level.INFO, "probininterval path: " + this.probInIntervalPath);
					}
					else if (components[0].equalsIgnoreCase("percentile"))
					{
						this.percentilePath = components[1];
						PathsWrapper.logger.log(Level.INFO, "percentile path: " + this.percentilePath);
					}
					else if (components[0].equalsIgnoreCase("hydra"))
					{
						this.hydraPath = components[1];
						PathsWrapper.logger.log(Level.INFO, "HYDRA path: " + this.hydraPath);
					}
					else if (components[0].equalsIgnoreCase("maxprocessors"))
					{
						// Test it is a valid integer before assigning to String
						final Integer value = Integer.parseInt(components[1]);
						this.maxProcessors = value.intValue();
						PathsWrapper.logger.log(Level.INFO, "Maximum processors set to : " +
															this.maxProcessors);
					}
				}
			}
		}
		catch (final FileNotFoundException e)
		{
			PathsWrapper.logger.log(Level.WARNING, LoggingHelper.getStackTrace(e));
			throw new Error("Error finding paths.config file " + pathFile.getAbsolutePath());
		}
		catch (final NumberFormatException e)
		{
			PathsWrapper.logger.log(Level.WARNING, LoggingHelper.getStackTrace(e));
			throw new Error("The max number of processors specified is invalid: " +
							pathFile.getAbsolutePath());
		}
		catch (final IOException e)
		{
			PathsWrapper.logger.log(Level.WARNING, LoggingHelper.getStackTrace(e));
			throw new Error("Error reading paths.config file " + pathFile.getAbsolutePath());
		}
	}

	/**
	 * @return the convoPath
	 */
	public String getConvoPath()
	{
		return this.convoPath;
	}

	/**
	 * @return the dnamacaPath
	 */
	public String getDnamacaPath()
	{
		return this.dnamacaPath;
	}

	/**
	 * @return the hydraPath
	 */
	public String getHydraPath()
	{
		return this.hydraPath;
	}

	/**
	 * @return the maxProcessors
	 */
	public Integer getMaxProcessors()
	{
		return this.maxProcessors;
	}

	/**
	 * @return the momaPath
	 */
	public String getMomaPath()
	{
		return this.momaPath;
	}

	/**
	 * @return the percentilePath
	 */
	public String getPercentilePath()
	{
		return this.percentilePath;
	}

	/**
	 * @return the probInIntervalPath
	 */
	public String getProbInIntervalPath()
	{
		return this.probInIntervalPath;
	}

	/**
	 * @return the smartaPath
	 */
	public String getSmartaPath()
	{
		return this.smartaPath;
	}

	/**
	 * @return the workPath
	 */
	public String getWorkPath()
	{
		return this.workPath;
	}
}
