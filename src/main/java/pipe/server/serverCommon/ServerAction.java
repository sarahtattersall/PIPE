/**
 * ServerAction
 * 
 * This is invoked by the listening server and is responsible for 
 * implementing the required functionality dealing with requests 
 * to the server
 * 
 * @author Barry Kearns
 * @author Nicholas Dingle
 * @author Harini Kulatunga
 * @author Tamas Suto
 * 
 * @date 07/07
 * @date 01/08
 */

package pipe.server.serverCommon;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StreamCorruptedException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.ggf.drmaa.DrmaaException;

import pipe.common.SimplePlaces;
import pipe.common.SimpleTransitions;
import pipe.common.dataLayer.StateGroup;
import pipe.common.*;
import pipe.server.JobStatusChecker;
import pipe.server.ServerDrmaaSession;
import pipe.server.TransMod;

public class ServerAction implements Runnable
{
	public static String getStackTrace(final Exception e)
	{
		final OutputStream buf = new ByteArrayOutputStream();
		final PrintStream p = new PrintStream(buf);
		e.printStackTrace(p);
		return buf.toString();
	}

    private Socket									clientConnection	= null;

	private int									clientNo			= 0;
	protected String								workPath;
	protected final int									maxProcessors;

    private File									analysisLogFile;

	protected File									resultsDir;
	// data obtained for performance queries
	protected SimplePlaces							places;
	protected SimpleTransitions transitions;
	protected ArrayList<StateGroup>					stateGroups;
	protected HashMap<String, ArrayList<String>>	stateLabels;

	protected AnalysisSettings						settings;

    protected final PathsWrapper					paths;

	private final HashMap<InetAddress, Integer>		clients;

	protected ObjectInputStream						in;

	private AnalysisType							analysisRequested;
	private final String									newline				= System.getProperty("line.separator");

	private final String							slash				= System.getProperty("file.separator");

	public ServerAction(final ObjectInputStream receiver,
						final AnalysisType analType,
						final Socket connection,
						final int id,
						final PathsWrapper paths,
						final HashMap<InetAddress, Integer> clients) {
		this(receiver, connection, id, paths, clients);
		this.analysisRequested = analType;
		// set up log files
		try
		{
			final String analysisLogFilePath = this.workPath + this.slash + "analysis.log";
			this.analysisLogFile = new File(analysisLogFilePath);
			if (!this.analysisLogFile.exists())
			{
				this.analysisLogFile.createNewFile();
			}
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

	protected ServerAction(	final ObjectInputStream receiver,
							final Socket connection,
							int id,
							final PathsWrapper paths,
							final HashMap<InetAddress, Integer> clients) {
		this.in = receiver;
		this.clients = clients;
		this.paths = paths;

		this.clientConnection = connection;
		this.clientNo = id;

		this.maxProcessors = paths.getMaxProcessors();

		final DateFormat f1 = new SimpleDateFormat("dd-MM-yy");
		final DateFormat f2 = new SimpleDateFormat("HH-mm-ss");

		final String s = f1.format(new java.util.Date()) + this.slash + f2.format(new java.util.Date());

		this.workPath = paths.getWorkPath() + this.slash + s;

		// directory to separate client's temporary data
        File workDir = new File(this.workPath);

		// if the directory exists, increment id until a free userID directory
		// is available.
		// Note: under normal execution, userID should be free as the id is
		// incremented when a new client connects
		while (workDir.isDirectory())
		{
			this.workPath += ++id;
			workDir = new File(this.workPath);
		}
		if (!workDir.mkdirs())
		{
			// in case the directory creation fails, fall back to basic working
			// directory
			this.workPath = paths.getWorkPath();
		}
	}

	protected void decrementConnections()
	{
		final InetAddress address = this.clientConnection.getInetAddress();
		int noConnections = this.clients.get(address);
		this.clients.put(address, --noConnections);
	}

	private String getCurrentDateAndTime()
	{
		final Calendar cal = Calendar.getInstance();
		final String DATE_FORMAT_NOW = "dd/MM/yyyy HH:mm:ss";
		final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());
	}

	@SuppressWarnings("unchecked")
	public void run()
	{
		try
		{
			// need in order to be able to interrupt thread
			Thread.sleep(10);

            Thread serverAction = Thread.currentThread();
			this.writeToLog(this.analysisRequested + " analysis to be performed");

			// check what kind of analysis the user wishes to perform

			switch (this.analysisRequested)
			{
				case STEADYSTATE :
				{
					this.writeToLog("Performing steady-state analysis");
					this.writeToLog("Recieving Data");
					this.places = (SimplePlaces) this.in.readObject();
					this.transitions = (SimpleTransitions) this.in.readObject();
					final PerformanceMeasure performanceMeasure = (PerformanceMeasure) this.in.readObject();

					// Generate .mod file
					final TransMod genMod = new TransMod(	this.places,
															this.transitions,
															performanceMeasure,
															this.workPath,
															this.clientNo);

					// Path to log file that will record job progress; mod
					// file's
					// name with .log extension
					final String statusFilePath = genMod.getFilePath() + ".log";

					// Path to results file results.dat located in the user's
					// working directory
					final String resultsFilePath = this.workPath + System.getProperty("file.separator") +
													"results.dat";

					// Create a thread to read updates to job log and sends them
					// to the client
					final Thread status = new Thread(new JobStatusChecker(	this.clientConnection,
																			statusFilePath,
																			resultsFilePath,
																			this.workPath,
																			this.analysisLogFile.getPath()));
					status.start();

					// Create DRMAA session and start dnamaca with created .mod
					// file
					final ServerDrmaaSession drmSession = new ServerDrmaaSession(statusFilePath);
					drmSession.setMyLogFile(this.analysisLogFile.getPath());
					drmSession.submitJob(	this.paths.getDnamacaPath(),
											genMod.getFilePath(),
											null,
											this.workPath);

					break;
				}
				case PASSAGETIME :
				{
					this.writeToLog("Performing passage time analysis");
					this.writeToLog("Recieving Data");
					this.places = (SimplePlaces) this.in.readObject();
					this.transitions = (SimpleTransitions) this.in.readObject();
					final ArrayList<StateGroup> sourceStateGrps = (ArrayList<StateGroup>) this.in.readObject();
					final ArrayList<StateGroup> destStateGrps = (ArrayList<StateGroup>) this.in.readObject();
					final AnalysisSettings analysisSettings = (AnalysisSettings) this.in.readObject();
                    Boolean clearCache = (Boolean) this.in.readObject();

					// Generate .mod file
					final TransMod genMod = new TransMod(	this.places,
															this.transitions,
															sourceStateGrps,
															destStateGrps,
															analysisSettings,
															this.workPath,
															this.clientNo);

					// Path to log file that will record job progress; mod
					// file's
					// name with .log extension
					final String statusFilePath = genMod.getFilePath() + ".log";
					this.writeToLog(statusFilePath);

					// Path to results file points.dat located in the user's
					// working
					// directory
					final String resultsFilePath = this.workPath + System.getProperty("file.separator") +
													"points.dat";
					this.writeToLog(resultsFilePath);

					// Create a thread to read updates to job log and sends them
					// them to the client
					final Thread status = new Thread(new JobStatusChecker(	this.clientConnection,
																			statusFilePath,
																			resultsFilePath,
																			this.workPath,
																			this.analysisLogFile.getPath()));
					status.start();

					// Create DRMAA session and start dnamaca with created .mod
					// file
					final ServerDrmaaSession drmSession = new ServerDrmaaSession(statusFilePath);
					drmSession.setMyLogFile(this.analysisLogFile.getPath());
					this.writeToLog("starting new drmaa session");
					if (analysisSettings.numProcessors > this.maxProcessors)
					{
						final String msg = "The number of processors specified exceeds the server limit";
						drmSession.setFail(msg);
						this.writeToLog(msg);
					}
					else
					{
						// Convert number of processors int to string
						final String numProc = Integer.toString(analysisSettings.numProcessors);
						this.writeToLog("Running job with " + numProc + " processors");
						// Submit the job passing the execuable, input file,
						// number
						// of processors + temp directory, working directory
						final String[] extraParams = new String[]{numProc,
								analysisSettings.inversionMethod,
								this.workPath,
								clearCache.toString()};
						this.writeToLog(this.paths.getSmartaPath());
						this.writeToLog(genMod.getFilePath());
						for (final String s : extraParams)
						{
							this.writeToLog(s);
						}
						this.writeToLog(this.workPath);
						drmSession.submitJob(	this.paths.getSmartaPath(),
												genMod.getFilePath(),
												extraParams,
												this.workPath);
					}
					this.writeToLog("drmaa session completed");
					break;
				}
				default :
					throw new UnsupportedOperationException(this.analysisRequested +
															"not supported by ServerAction");
			}
		}
		catch (final InterruptedException e)
		{
			this.writeToLog("Stopping server-side processing thread");
        }
		catch (final StreamCorruptedException sce)
		{
			this.writeToLog("Stream Corrupted Exception" + sce.getMessage());
			this.writeToLog(ServerAction.getStackTrace(sce));
		}
		catch (final DrmaaException drmaaExp)
		{
			this.writeToLog("DRMAA error: " + drmaaExp.getMessage());
			this.writeToLog(ServerAction.getStackTrace(drmaaExp));

		}
		catch (final ClassCastException cce)
		{
			this.writeToLog("Received invalid PNML data from client: " + cce.getMessage());
			this.writeToLog(ServerAction.getStackTrace(cce));

		}
		catch (final IOException ioe)
		{
			this.writeToLog("I/O exception creating stream: " + ioe.getMessage());
			this.writeToLog(ServerAction.getStackTrace(ioe));

		}
		catch (final Exception expc)
		{
			this.writeToLog("An exeception occured: " + expc.getMessage());
			this.writeToLog(ServerAction.getStackTrace(expc));
		}
		finally
		{
			this.decrementConnections();
		}
	}

	private void writeToLog(final String logEntry)
	{
		try
		{
			final BufferedWriter file = new BufferedWriter(new FileWriter(	this.analysisLogFile.getPath(),
																			true));
			file.write(this.getCurrentDateAndTime() + " - " + logEntry + this.newline);
			file.close();
		}
		catch (final IOException e)
		{
			e.printStackTrace();
		}
	}

}
