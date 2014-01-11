package pipe.modules.tagged;

import java.io.Serializable;
import java.util.ArrayList;

class PerformanceMeasure implements Serializable
{
	private static final long serialVersionUID = 1L;
	public ArrayList<String> states = null;
	public ArrayList<String> counts = null;
	public ArrayList<String> stateEstimators = null;
	
	public PerformanceMeasure()
	{
		states = new ArrayList<String>();
		counts = new ArrayList<String>();
		stateEstimators = new ArrayList<String>();
	}

	
	public void addState(String id)
	{
		states.add(id);
	}
	
	public void addCount(String id)
	{
		counts.add(id);
	}
	
	public void addStateEstimator(String newEstimator)
	{
		stateEstimators.add(newEstimator);
	}
	
	public int getStatesSize()
	{
		return states.size();
	}
	
	public int getCountsSize()
	{
		return counts.size();
	}
	
	public int getEstimatorsSize()
	{
		return stateEstimators.size();
	}

}
