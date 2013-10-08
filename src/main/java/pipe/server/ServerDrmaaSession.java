/**
 * 
 */
package pipe.server;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.JobTemplate;

import pipe.server.serverCommon.DrmaaSession;

/**
 * @author dazz
 * 
 */
public class ServerDrmaaSession extends DrmaaSession
{
	private String	myLogFile;

	public ServerDrmaaSession(final String statusPath) throws DrmaaException {
		super(statusPath);
	}

	public void setMyLogFile(final String myLogFile)
	{
		this.myLogFile = myLogFile;
		writeToLog("DRMAA session initialised");
	}

	@Override
	public JobInfo submitJob(	final String executable,
								final String inFile,
								final String[] extraArgs,
								final String workPath) throws DrmaaException
	{
		String jobId;
		String[] jobArgs;

		// Set job arguments equal the name of the input file, plus any extra
		// arguments if passed
		if (extraArgs == null)
			jobArgs = new String[]{inFile};
		else
		{
			jobArgs = new String[extraArgs.length + 1];
			jobArgs[0] = inFile;
			System.arraycopy(extraArgs, 0, jobArgs, 1, extraArgs.length);
		}

		JobTemplate jobTempl = createJobTemplate();
		jobTempl.setRemoteCommand(executable);
		jobTempl.setArgs(jobArgs);
		jobTempl.setWorkingDirectory(workPath);
		jobTempl.setJoinFiles(true); // Merges stdout and stderr to a single
		// file

		jobId = runJob(jobTempl);
		writeToLog("Job submitted to queue");

		JobInfo info = wait(jobId);

		// Check job exit status
		if (info.wasAborted())
		{
			setFail("DRMAA job failed to exectue");
			writeToLog("Job " + info.getJobId() + " never ran");
		}
		else if (info.hasExited())
		{
			writeToLog("Job " + info.getJobId() + " finished regularly with exit status " +
						info.getExitStatus());
		}
		else if (info.hasSignaled())
		{
			writeToLog("Job " + info.getJobId() + " finished due to signal " + info.getTerminatingSignal());

			if (info.hasCoreDump())
				writeToLog("A core dump is available.");
		}

		else
		{
			writeToLog("Job " + info.getJobId() + " finished with unclear conditions");
		}

		// Cleanup after run
		deleteJobTemplate(jobTempl);

		return info;
	}

	public void setFail(final String error)
	{
		try
		{
			// Inform status file that DRMAA failed to execute file
			PrintWriter errorOut = new PrintWriter(new FileWriter(this.statusPath));

			if (error != null)
				errorOut.println(error);

			errorOut.println("Failed");
			errorOut.close();
		}
		catch (Exception e)
		{
		}
	}

	private void writeToLog(final String logEntry)
	{
		try
		{
			if (this.myLogFile != null)
			{
				BufferedWriter file = new BufferedWriter(new FileWriter(this.myLogFile, true));
				file.write(logEntry + System.getProperty("line.separator"));
				file.close();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
