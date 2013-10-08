package pipe.common;

public enum AnalysisInstruction
{
	START(-300, "Start"),
	UPDATE(-200, "Update"),
	STOP(-100, "Stop"),
	FINISHED(100, "Finished"),
	TIMEOUT(200, "Timeout"),
	FAILED(300, "Failed"),
	NAN(Integer.MAX_VALUE, "NotAnInstruction");

	public static AnalysisInstruction getFromName(final String name)
	{
		for (final AnalysisInstruction a : AnalysisInstruction.values())
		{
			if (a.toString().equals(name))
			{
				return a;
			}
		}
		return NAN;
	}

	private final String	name;
	private final int		value;

	private AnalysisInstruction(final int value, final String name) {
		this.value = value;
		this.name = name;
	}

	public Integer intValue()
	{
		return this.value;
	}

	@Override
	public String toString()
	{
		return this.name;
	}
}
