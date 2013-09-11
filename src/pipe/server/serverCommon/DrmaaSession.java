package pipe.server.serverCommon;

import java.io.IOException;

import org.ggf.drmaa.DrmaaException;
import org.ggf.drmaa.JobInfo;
import org.ggf.drmaa.JobTemplate;
import org.ggf.drmaa.Session;
import org.ggf.drmaa.SessionFactory;

public abstract class DrmaaSession
{
	// singleton session
	private static Session	drmaaSession	= null;

	private synchronized static void getSession() throws DrmaaException
	{
		if (DrmaaSession.drmaaSession == null)
		{
			DrmaaSession.drmaaSession = SessionFactory.getFactory().getSession();
			DrmaaSession.drmaaSession.init("");
		}
	}

	protected String	statusPath;

	protected DrmaaSession() throws DrmaaException {
		DrmaaSession.getSession();
	}

	protected DrmaaSession(final String statusPath) throws DrmaaException {
		this();
		this.statusPath = statusPath;
	}

	protected JobTemplate createJobTemplate() throws DrmaaException
	{
		return DrmaaSession.drmaaSession.createJobTemplate();

	}

	protected void deleteJobTemplate(final JobTemplate jobTempl) throws DrmaaException
	{
		DrmaaSession.drmaaSession.deleteJobTemplate(jobTempl);
	}

	protected String runJob(final JobTemplate jobTempl) throws DrmaaException
	{
		return DrmaaSession.drmaaSession.runJob(jobTempl);

	}

	public abstract JobInfo submitJob(	final String executable,
										final String inFile,
										final String[] extraArgs,
										final String workPath) throws DrmaaException;

	protected JobInfo wait(final String jobId) throws DrmaaException
	{
		return DrmaaSession.drmaaSession.wait(jobId, Session.TIMEOUT_WAIT_FOREVER);
	}

// public void close() throws DrmaaException
// {
// System.out.println("Ending DRMAA session");
// DrmaaSession.drmaaSession.exit();
// }
}
