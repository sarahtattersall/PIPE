/**
 * ClientUpdater
 * 
 * This class is responsible for updating the client by sending the 
 * Strings through the socket connection that it has received from
 * ResultSender, which are in essence the job status messages.
 * 
 * @author Barry Kearns
 * @author Tamas Suto
 * @date 16/01/08
 */

package pipe.server;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;

class JobStatusUpdater
{

	private PrintWriter	clientWriter;
	private final Socket		clientConnection;
    private final String		myLogPath;

	public JobStatusUpdater(final Socket clientSocket, final String logFilePath, final String myLogPath) {
		this.clientConnection = clientSocket;
		this.myLogPath = myLogPath;
		try
		{
			this.clientWriter = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
		}
		catch (Exception exp)
		{
			writeToLog("Error opening socket to client: " + exp);
		}
	}

	public void send(final String status)
	{
		this.clientWriter.println(status);
	}

	public void sendFileContents(final String filePath)
	{
		DataOutputStream dataOut;
		File sourceFile = new File(filePath);

		writeToLog("Sending file " + filePath + " to client");
		try
		{
			dataOut = new DataOutputStream(this.clientConnection.getOutputStream());
			InputStream fileInput = new FileInputStream(sourceFile);
			byte[] buffer = new byte[1024];
			for (int count; (count = fileInput.read(buffer)) >= 0;)
			{
				dataOut.write(buffer, 0, count);
			}
			dataOut.flush();
			dataOut.close();
			fileInput.close();
		}
		catch (Exception exp)
		{
			writeToLog("Error opening socket to write to client ( " + exp + ")");
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
			e.printStackTrace();
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
