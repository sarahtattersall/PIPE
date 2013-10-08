package pipe.modules.tagged;

import java.io.Serializable;

/**
 * 
 * @author Barry Kearns
 * @date September 2007
 *
 */
public class AnalysisSetting implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	public final double startTime;
    public final double endTime;
    public final double timeStep;

    public AnalysisSetting(double start, double end, double step, String method, int processors)
	{
		startTime = start;
		endTime = end;
		timeStep = step;
	}

}
