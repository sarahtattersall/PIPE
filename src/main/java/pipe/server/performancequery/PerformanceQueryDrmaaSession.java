package pipe.server.performancequery;

import java.util.logging.Level;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.JobTemplate;

import pipe.server.serverCommon.DrmaaSession;

public class PerformanceQueryDrmaaSession extends DrmaaSession implements ServerLoggingHandler
{

	public PerformanceQueryDrmaaSession() throws DrmaaException {
		super();
		ServerLoggingHandler.logger.log(Level.INFO, "DRMAA session initialised");
	}

	@Override
	public JobInfo submitJob(	final String scriptForTool,
								final String modFile,
								final String[] extraArgs,
								final String workPath) throws DrmaaException
	{
		String jobId;
		JobInfo info;
		String[] jobArgs;
		// Set job arguments equal the name of the input file, plus any
		// extra arguments if passed
		if (extraArgs == null)
		{
			jobArgs = new String[]{modFile};
		}
		else if (modFile != null)
		{
			jobArgs = new String[extraArgs.length + 1];
			jobArgs[0] = modFile;
			System.arraycopy(extraArgs, 0, jobArgs, 1, extraArgs.length);
		}
		else
		{
			jobArgs = extraArgs;
		}
		final JobTemplate jobTempl = this.createJobTemplate();
		jobTempl.setRemoteCommand(scriptForTool);
		jobTempl.setArgs(jobArgs);
		jobTempl.setWorkingDirectory(workPath);
		jobTempl.setJoinFiles(true); // Merges stdout and stderr to a
		// single file

		jobId = this.runJob(jobTempl);
		ServerLoggingHandler.logger.log(Level.INFO, "Job submitted to queue");

		info = this.wait(jobId);

		// Check job exit status
		if (info.wasAborted())
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "DRMAA job failed to exectue");
			ServerLoggingHandler.logger.log(Level.WARNING, "Job " + info.getJobId() + " was aborted");
		}
		else if (info.hasExited())
		{
			if (info.getExitStatus() == 0)
			{
				ServerLoggingHandler.logger.log(Level.FINE, "Job " + info.getJobId() + " finished regularly");
			}
			else
			{
				ServerLoggingHandler.logger.log(Level.WARNING, "Job " + info.getJobId() +
																" has exited with exit status " +
																info.getExitStatus());
			}
		}
		else if (info.hasSignaled())
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Job " + info.getJobId() +
															" finished due to signal " +
															info.getTerminatingSignal());

			if (info.hasCoreDump())
			{
				ServerLoggingHandler.logger.log(Level.INFO, "A core dump is available.");
			}
		}

		else
		{
			ServerLoggingHandler.logger.log(Level.WARNING, "Job " + info.getJobId() +
															" finished with unclear conditions");
		}

		// Cleanup after run
		this.deleteJobTemplate(jobTempl);
		return info;
	}
}
