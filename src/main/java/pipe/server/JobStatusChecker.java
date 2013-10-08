/**
 * ResultSender
 * 
 * This thread checks for the status of a job.
 * 
 * @author Barry Kearns
 * @author Tamas Suto
 * @date 16/01/08
 * 
 */

package pipe.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import pipe.server.serverCommon.ServerAction;

public class JobStatusChecker implements Runnable
{

	private Thread				statusChecker;

	private final String		logFilePath;
	private final String		resultsFilePath;
	private final JobStatusUpdater	clientUpdater;
	private final Socket		clientConnection;
	private final String		myLogPath;

	/**
	 * Constructor that checks the log file, as well as sends results over to
	 * the client
	 * 
	 * @param clientSock
     * @param logPath
     * @param resultsPath
     * @param workingDirectoryPath
     * @param myLogPath
	 *            TODO
	 */
	public JobStatusChecker(final Socket clientSock,
							final String logPath,
							final String resultsPath,
							final String workingDirectoryPath,
							final String myLogPath) {
		this.clientConnection = clientSock;
		this.logFilePath = logPath;
		this.resultsFilePath = resultsPath;
		this.myLogPath = myLogPath;
		// Initialise updater with client objectOutputStream
		this.clientUpdater = new JobStatusUpdater(this.clientConnection, this.logFilePath, myLogPath);
	}

	public void run()
	{
		int count;
		boolean finished = false;
		String logEntryWithDate;

		try
		{
			this.statusChecker = Thread.currentThread();

			// need to be able to do interrupts
			Thread.sleep(10);

			File statusFile = new File(this.logFilePath);
			if (!statusFile.exists())
				statusFile.createNewFile();
			InputStreamReader in = new InputStreamReader(new FileInputStream(statusFile));
			BufferedReader bufreader = new BufferedReader(in);
			writeToLog("Waiting for results!");
			// keep on checking the file continuously until finished
			while (!finished)
			{
				count = 0;
				while (!bufreader.ready())
				{

					// Poll the file every second to check for updates
					count++;

					// Inform client
					if (count % 7 == 0)
					{
						try
						{
							this.clientUpdater.send("Wait");
						}
						catch (Exception exp)
						{
							writeToLog("Error updating client");
						}
					}

					// Sleep to allow DRMAA job to progress (arbitrarily 500ms)
					Thread.sleep(500);
				}

				logEntryWithDate = bufreader.readLine();
				logEntryWithDate.trim();
				String logEntry;
				int indexOfDash = logEntryWithDate.indexOf("-");
				if (indexOfDash != -1)
				{
					int startOfActualStatus = indexOfDash + 2;
					logEntry = logEntryWithDate.substring(startOfActualStatus);
				}
				else logEntry = logEntryWithDate;

				if (logEntry.equalsIgnoreCase("Finished") || logEntry.equalsIgnoreCase("Failed"))
					finished = true;

				this.clientUpdater.send(logEntry);
			}

			// send result data to result file
			if (this.resultsFilePath != null)
				this.clientUpdater.sendFileContents(this.resultsFilePath);

			writeToLog("Done with results ending...");
			// close log file
			bufreader.close();
			in.close();

			// Close connection to client
			this.clientConnection.close();

		}
		catch (InterruptedException e)
		{
			writeToLog("Stopping log checker thread");
			// Thread.currentThread().interrupt()
        }
		catch (Exception e)
		{
			writeToLog("Error" + e);
			writeToLog(ServerAction.getStackTrace(e));

		}
	}

	public void stopThread()
	{
		this.statusChecker.interrupt();
	}

	void deleteDirectory(final String dirPath)
	{
		try
		{
			File clearDir = new File(dirPath);
			String children[] = clearDir.list();
            for(String aChildren : children)
            {
                File tmp = new File(clearDir, aChildren);
                if(tmp.isDirectory())
                    deleteDirectory(tmp.getAbsolutePath());
                else tmp.delete();
            }
			clearDir.delete();
		}
		catch (Exception e)
		{
			writeToLog(ServerAction.getStackTrace(e));
		}
	}

	void writeToLog(final String logEntry)
	{
		final String newline = System.getProperty("line.separator");
		try
		{
			BufferedWriter file = new BufferedWriter(new FileWriter(this.myLogPath, true));
			file.write(getCurrentDateAndTime() + " - " + logEntry + newline);
			file.close();
		}
		catch (IOException e)
		{
			try
			{
				// create new file to indicate ioerror
				File error = new File(this.myLogPath + "error");
				error.createNewFile();
			}
			catch (IOException e2)
			{
				// this doesn't help us but there is an io prob
				e.printStackTrace();
			}
		}
	}

	private String getCurrentDateAndTime()
	{
		Calendar cal = Calendar.getInstance();
		final String DATE_FORMAT_NOW = "dd/MM/yyyy HH:mm:ss";
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}

}
