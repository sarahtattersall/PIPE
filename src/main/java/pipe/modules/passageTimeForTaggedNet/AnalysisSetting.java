package pipe.modules.passageTimeForTaggedNet;

class AnalysisSetting {


	private static final long serialVersionUID = 1L;
	
	public final double startTime;
    public final double endTime;
    public final double timeStep;

	
	public AnalysisSetting(double start, double end, double step)
	{
		startTime = start;
		endTime = end;
		timeStep = step;

	}
}
