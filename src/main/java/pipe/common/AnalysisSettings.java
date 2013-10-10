/**
 * AnalysisSettings
 * 
 * This class encompasses all relevant server- and analysis process-related
 * preferences.
 * 
 * @author Barry Kearns
 * @date September 2007
 *
 */

package pipe.common;

import java.io.Serializable;
import java.util.logging.Level;

public class AnalysisSettings implements Serializable
{
	private static final long	serialVersionUID	= 1L;

	public final String			serverAddress;
	public final int			serverPort;
	public final int			numProcessors;
	public final boolean		clearCache;
	public final boolean		autoTimeRange;
	public final Level			clientLoggingLevel, serverLoggingLevel;

	public final double			startTime, endTime, timeStep;
	public final String			inversionMethod;

	public AnalysisSettings(final double start,
							final double end,
							final double step,
							final String method,
							final int processors) {
		this.startTime = start;
		this.endTime = end;
		this.timeStep = step;
		this.inversionMethod = method;
		this.numProcessors = processors;
		this.serverAddress = null;
		this.serverPort = 0;
		this.clearCache = false;
		this.autoTimeRange = false;
		this.clientLoggingLevel = Level.OFF;
		this.serverLoggingLevel = Level.OFF;
	}

	public AnalysisSettings(final String serverAddress,
							final int serverPort,
							final boolean clearCache,
							final boolean autoTimeRange,
							final int processors,
							final double start,
							final double end,
							final double step,
							final String method,
							final Level usingServerLogging,
							final Level usingClientLogging) {
		this.startTime = start;
		this.endTime = end;
		this.timeStep = step;
		this.inversionMethod = method;
		this.numProcessors = processors;
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
		this.clearCache = clearCache;
		this.autoTimeRange = autoTimeRange;
		this.clientLoggingLevel = usingClientLogging;
		this.serverLoggingLevel = usingServerLogging;
	}

}
