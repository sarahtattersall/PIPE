package pipe.modules.passage;

import java.io.IOException;

import pipe.modules.clientCommon.SocketIO;

class StatusListener
{
	private final SocketIO				server;
	private final ProgressBarHTMLPane	progress;
	private String								statusHTML	= "";

	public StatusListener(	final SocketIO server,
							final ProgressBarHTMLPane progressPane,
							final String currentStatus) {
		this.server = server;
		this.progress = progressPane;
		this.statusHTML = currentStatus;
	}

	public String listen() throws IOException
	{
		String status = null;
		String[] components;
		int statusCount = 0;
		boolean progressBar = false;

		status = this.server.receiveStatus();

		// Continue reading status until the socket returns null, or we have
		// completed analysis (finished or failed)
		while (!(status == null) && !(status.equals("Finished") || status.equals("Failed")))
		{

			components = status.split(" ");

			if (status.equalsIgnoreCase("Wait"))
			{
				if (statusCount == 0)
					status = "Queuing";

				else status = ".";

				this.statusHTML += status;
				this.progress.setText(this.statusHTML);
				sleep(500); // allow server to progress
			}

			// Check if the status is progress for the progress bar
			else if (components.length == 3 && components[1].equals("of"))
			{
				this.progress.setText(this.statusHTML + "<br>" + status + " paths explored");

				if (!progressBar)
				{
					this.progress.initProgressBar(	Integer.parseInt(components[0]),
													Integer.parseInt(components[2]));
					progressBar = true;
				}

				this.progress.updateProgressBar(Integer.parseInt(components[0]));

				if (components[0].equals(components[2]))
					this.statusHTML += "<br>" + status + " paths explored";

				sleep(10);
			}

			else if (statusCount > 0)
			{
				this.statusHTML += "<br>" + status;
				this.progress.setText(this.statusHTML);
				sleep(300); // smoother gui update
			}

			// Don't include a <br> if its the first message
			else
			{
				this.statusHTML += status;
				this.progress.setText(this.statusHTML);
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

	private void sleep(final int time_ms)
	{
		try
		{
			Thread.sleep(time_ms);
		}
		catch (InterruptedException e)
		{
		}
	}

}
