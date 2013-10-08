package pipe.modules.steadyStateCloud;

import java.io.IOException;

import pipe.modules.clientCommon.HTMLPane;
import pipe.modules.clientCommon.SocketIO;

public class StatusListener
{
	private final SocketIO	server;
	private final HTMLPane	progress;
	private String					statusHTML	= "";

	public StatusListener(final SocketIO server, final HTMLPane progress, final String currentStatus) {
		this.server = server;
		this.progress = progress;
		this.statusHTML = currentStatus;
	}

	public String listen() throws IOException
	{
		String status = null;
		int statusCount = 0;

		status = this.server.receiveStatus();

		// Continue reading status until the socket returns null, or we have
		// completed analysis (finished or failed)
		while (!(status == null) && !(status.equals("Finished") || status.equals("Failed")))
		{
			if (status.equalsIgnoreCase("Wait"))
			{
				if (statusCount == 0)
				{
					status = "Queuing";
					statusCount++;

				}
				else status = ".";

				this.statusHTML += status;
			}

			else if (statusCount > 0)
				this.statusHTML += "<br>" + status;

			// Don't include a <br> if its the first message
			else this.statusHTML += status;

			this.progress.setText(this.statusHTML);

			// Sleep for a half second to allow smooth GUI progress
			try
			{
				Thread.sleep(500);
			}
			catch (InterruptedException e)
			{
			}

			status = this.server.receiveStatus();
			statusCount++;
		}

		if (status != null && status.equals("Finished"))
			this.statusHTML += "<br>" + "Job finished successfully";

		else if (status != null && status.equals("Failed"))
			this.statusHTML += "<br>" + "Job failed to complete";

		this.statusHTML += "<br>" + "Transferring results..";

		return this.statusHTML + "<br>";
	}

}
