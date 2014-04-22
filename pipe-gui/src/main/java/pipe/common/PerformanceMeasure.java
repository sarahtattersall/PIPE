package pipe.common;

import java.io.Serializable;
import java.util.ArrayList;

public class PerformanceMeasure implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	public ArrayList<String>	states				= null;
	public ArrayList<String>	counts				= null;
	public ArrayList<String>	stateEstimators		= null;

	public PerformanceMeasure() {
		this.states = new ArrayList<String>();
		this.counts = new ArrayList<String>();
		this.stateEstimators = new ArrayList<String>();
	}

	public void addCount(String id)
	{
		this.counts.add(id);
	}

	public void addState(String id)
	{
		this.states.add(id);
	}

	public void addStateEstimator(String newEstimator)
	{
		this.stateEstimators.add(newEstimator);
	}

	public int getCountsSize()
	{
		return this.counts.size();
	}

	public int getEstimatorsSize()
	{
		return this.stateEstimators.size();
	}

	public int getStatesSize()
	{
		return this.states.size();
	}

}
