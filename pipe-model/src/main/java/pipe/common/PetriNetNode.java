/*
 * Created on Jan 21, 2008
 * Created by darrenbrien
 */

package pipe.common;

import java.io.Serializable;

public enum PetriNetNode implements Serializable
{
	RESULT, SEQUENTIAL,

	DISTRIBUTION, CONVOLUTION,

	PASSAGETIMEDENSITY, PROBINSTATES, PROBININTERVAL, MOMENT, FIRINGRATE, STEADYSTATEPROB, PERCENTILE,

	STEADYSTATESTATES, STATESATTIME, SUBSET,

	ININTERVAL, DISCON, ARITHCOMP, ARITHOP, NEGATION,

	MACRO, ARGUMENT,

	ACTIONS, STATES, STATEFUNCTION,

	BOOL, NUM, RANGE;

	public boolean isOpNode()
	{
		switch (this)
		{
			// TODO case RANGE: needs to be an OPNODE really!
			case RESULT :
			case SEQUENTIAL :
			case PASSAGETIMEDENSITY :
			case DISTRIBUTION :
			case CONVOLUTION :
			case PROBININTERVAL :
			case PROBINSTATES :
			case MOMENT :
			case FIRINGRATE :
			case STEADYSTATEPROB :
			case PERCENTILE :
			case STEADYSTATESTATES :
			case STATESATTIME :
			case ININTERVAL :
			case DISCON :
			case ARITHCOMP :
			case ARITHOP :
			case NEGATION :
			case SUBSET :
				return true;
			default :
				return false;
		}
	}

	public boolean isSteadyState()
	{
		switch (this)
		{
			case STEADYSTATEPROB :
			case FIRINGRATE :
				return true;
			default :
				return false;
		}
	}

	public boolean isValueNode()
	{
		return !this.isOpNode();
	}

	@Override
	public String toString()
	{
		switch (this)
		{
			case RESULT :
				return "Result";
			case SEQUENTIAL :
				return "Sequential";
			case PASSAGETIMEDENSITY :
				return "PassageTimeDensity";
			case DISTRIBUTION :
				return "Distribution";
			case CONVOLUTION :
				return "Convolution";
			case PROBININTERVAL :
				return "ProbInInterval";
			case PROBINSTATES :
				return "ProbInStates";
			case MOMENT :
				return "Moment";
			case FIRINGRATE :
				return "FiringRate";
			case STEADYSTATEPROB :
				return "SteadyStateProb";
			case PERCENTILE :
				return "Percentile";
			case STEADYSTATESTATES :
				return "SteadyStateStates";
			case STATESATTIME :
				return "StatesAtTime";
			case ININTERVAL :
				return "InInterval";
			case DISCON :
				return "Discon";
			case ARITHCOMP :
				return "ArithComp";
			case ARITHOP :
				return "ArithOp";
			case NEGATION :
				return "Negation";
			case SUBSET :
				return "Subset";
			case ACTIONS :
				return "Actions";
			case ARGUMENT :
				return "Argument";
			case BOOL :
				return "Bool";
			case MACRO :
				return "Macro";
			case NUM :
				return "Num";
			case RANGE :
				return "Range";
			case STATEFUNCTION :
				return "StateFunction";
			case STATES :
				return "States";
			default :
				return "You must add a case to the toString() method for this operator";
		}
	}

	public boolean usesModFile()
	{
		switch (this)
		{
			case PASSAGETIMEDENSITY :
			case STEADYSTATEPROB :
			case FIRINGRATE :
			case STATESATTIME :
			case STEADYSTATESTATES :

				return true;
			default :
				return false;
		}
	}

	public boolean usesNodeAnalyser()
	{
		switch (this)
		{
			case ININTERVAL :
			case DISCON :
			case ARITHCOMP :
			case ARITHOP :
			case NEGATION :
			case NUM :
			case BOOL :
			case RANGE :
				return true;
			default :
				return false;
		}
	}
}
