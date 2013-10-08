/**
 * SocketIO
 * 
 * This class implements the communication over a socket connection
 * to the server.
 * 
 * @author Barry Kearns
 * @date 16/01/08
 * 
 */

package pipe.modules.clientCommon;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

import pipe.common.AnalysisSettings;
import pipe.common.SimplePlaces;
import pipe.common.SimpleTransitions;
//import pipe.modules.queryeditor.QueryManager;

public class SocketIO
{

	private final String		server;
	private final int			portNo;
	private final Socket		socketConnection;

	private BufferedReader		socketReader;
	private ObjectOutputStream	socketWriter;

	public SocketIO(final String host, final int port) throws IOException {
		this.server = host;
		this.portNo = port;
		this.socketConnection = new Socket(this.server, this.portNo);

		this.socketReader = new BufferedReader(new InputStreamReader(this.socketConnection.getInputStream()));
		this.socketWriter = new ObjectOutputStream(this.socketConnection.getOutputStream());

	}

	public String getServer()
	{
		return this.server;
	}

	public int getPortNo()
	{
		return this.portNo;
	}

	public void send(final Object sendObj)
	{
		try
		{
			this.socketWriter.writeObject(sendObj);
			this.socketWriter.flush();
		}
		catch (Exception exp)
		{
			System.out.println("Error sending PNML data to " + this.server + ": " + exp);
			if (sendObj instanceof AnalysisSettings)
				printStatusMessage("Error sending analysis settings");
			else if (sendObj instanceof SimplePlaces)
				printStatusMessage("Error sending simple places");
			else if (sendObj instanceof SimpleTransitions)
				printStatusMessage("Error sending simple transitions");
			else printStatusMessage("Error sending " + sendObj.getClass());
		}
	}

	public String receiveStatus() throws IOException
	{
		String statusMsg = null;

		statusMsg = this.socketReader.readLine();

		return statusMsg;
	}

	public String receiveFileContent()
	{
		StringBuffer fileContent;
		fileContent = new StringBuffer();
		String fileLine = null;
		try
		{
			fileLine = this.socketReader.readLine();
			while (fileLine != null)
			{
                fileContent.append(fileLine).append(" <br>\n");
				fileLine = this.socketReader.readLine();
			}
		}
		catch (Exception exp)
		{
			printStatusMessage("Error reading results file from server: " + exp);
		}
		return fileContent.toString();
	}

	public void close() throws IOException
	{
		if (this.socketWriter != null)
		{
			this.socketWriter.close();
			this.socketWriter = null;
		}

		if (this.socketReader != null)
		{
			this.socketReader.close();
			this.socketReader = null;
		}
		this.socketConnection.close();
	}

	private void printStatusMessage(final String message)
	{
		//QueryManager.printStatusMessage(message);
	}

}
