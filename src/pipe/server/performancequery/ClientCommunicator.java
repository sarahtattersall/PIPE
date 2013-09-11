/**
 * ClientCommunicator
 * 
 * This thread checks for the status of the analysis process. 
 * 
 * @author Tamas Suto
 * @date 31/01/08
 * 
 */

package pipe.server.performancequery;

import java.io.File;
import java.io.IOException;

import pipe.common.AnalysisInstruction;
import pipe.modules.interfaces.Cleanable;
import pipe.modules.interfaces.QueryConstants;
import pipe.server.CommunicationsManager;

public class ClientCommunicator implements ServerLoggingHandler, StatusIndicatorUpdater
{
	private final ClientUpdater clientUpdater = CommunicationsManager.clientUpdater;

	/**
	 * Constructor that checks the log file, as well as sends results over to
	 * the client
     * @param cleaner
     */
	public ClientCommunicator(final Cleanable cleaner)
    {
		// Initialise updater with client objectOutputStream
		clientUpdater.setCleaner(cleaner);
	}

	public void cleanUp()
	{
		this.clientUpdater.cleanUp();
	}

	void deleteDirectory(final String dirPath)
	{

		final File clearDir = new File(dirPath);
		final String children[] = clearDir.list();
		for (final String element : children)
		{
			final File tmp = new File(clearDir, element);
			if (tmp.isDirectory())
			{
				this.deleteDirectory(tmp.getAbsolutePath());
			}
			else
			{
				tmp.delete();
			}
		}
		clearDir.delete();

	}

	public Runnable getClientListener()
	{
		return this.clientUpdater;
	}

	public void sendLine(final String ln)
	{
		this.clientUpdater.sendLine(ln);
	}

	public void updateNodeStatus(final String status, final String nodeID)
	{

		if (status.equalsIgnoreCase(QueryConstants.EVALNOTSUPPORTED) ||
			status.equalsIgnoreCase(QueryConstants.EVALCOMPLETE) ||
			status.equalsIgnoreCase(QueryConstants.EVALINPROGRESS) ||
			status.equalsIgnoreCase(QueryConstants.EVALNOTSTARTED) ||
			status.equalsIgnoreCase(QueryConstants.EVALFAILED))
		{
			this.clientUpdater.sendStatusUpdate(AnalysisInstruction.UPDATE.toString(), nodeID, status);
		}

	}

	public void waitForConnection()
	{
		try {
			this.clientUpdater.waitForConnection();
		} catch (IOException e) {
			ServerLoggingHandler.logger.severe("IOException in ClientCommunicator.waitForConnection()");
		}
	}

}
