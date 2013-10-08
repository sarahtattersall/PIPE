package pipe.server;

import pipe.common.SimpleArc;
import pipe.common.SimplePlaces;
import pipe.common.SimpleTransitions;
import pipe.common.dataLayer.StateGroup;
import pipe.common.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * This class translates pnml data is a valid mod file.
 * 
 * @author Barry Kearns
 * @date August 2007
 *
 */

public class TransMod 
{
	private final SimplePlaces places;
	private final SimpleTransitions transitions;
	private PerformanceMeasure performanceMeasures;
	private ArrayList<StateGroup> sourceStateGrps, destStateGrps;
	private AnalysisSettings timePoints;
	
	private final StringBuilder modString = new StringBuilder();
	private String file = "";
	private File modFile = null;
	
	/**
	 * This method generates a mod file at the location specified by the path.
	 * The type of mod file produced is aimed toward Steady State Analysis as it 
	 * allows the specification of performance measures. 
	 * 
	 * @param splaces Set of places in the PNML data
	 * @param stransitions Set of transitions in the PNML data
	 * @param performanceMeasures Optional set performance measures
	 * @param path Path to directory to store resulting mod file
	 * @param clientNo the unique client number
	 */
	public TransMod(SimplePlaces splaces, SimpleTransitions stransitions, PerformanceMeasure performanceMeasures, String path, int clientNo)
	{
		places = splaces;
		transitions = stransitions;
		this.performanceMeasures = performanceMeasures;
		
		String filename = "modFile" + clientNo + ".mod";
		file = path + System.getProperty("file.separator") + filename;
		// This creates a file: path/modFile0.mod
		
		try
		{			
			// Create output mod file
			modFile = new File(file);
			FileWriter modFileWriter = new FileWriter(modFile); 
			
			System.out.print("Creating mod file " + file + ".. ");
			
			if (places.length > 0)
			{
				model();
				performance();
			}
			
			//System.out.println(modString);
			
			// Write buffer to file
			modFileWriter.write(modString.toString());
			modFileWriter.close();
			System.out.println("Done");			
		}
		
		catch (IOException e3)
		{
			System.out.println("Could not write to file! " + file);
			e3.printStackTrace();
		}	
				
	}
	
	/**
	 * This method generates a mod file at the location specified by the path.
	 * The type of mod file produced is aimed toward Passage Time Analysis as it 
	 * allows the specification of passage time parameters.  
	 * 
	 * @param splaces Set of places in the PNML data
	 * @param stransitions Set of transitions in the PNML data
	 * @param sourceStateGroups The conditions for valid source states
	 * @param destinationStateGroups The conditions for valid target states
	 * @param timeSettings The start /end / time step for the passage
	 * @param path Path to directory to store resulting mod file
	 * @param clientNo the unique client number
	 */
	public TransMod(SimplePlaces splaces, SimpleTransitions stransitions, ArrayList<StateGroup> sourceStateGroups, ArrayList<StateGroup> destinationStateGroups, AnalysisSettings timeSettings, String path, int clientNo)
	{
		places = splaces;
		transitions = stransitions;
		sourceStateGrps = sourceStateGroups;
		destStateGrps = destinationStateGroups;
		timePoints = timeSettings;

		
		String filename = "modFile" + clientNo + ".mod";
		file = path + System.getProperty("file.separator") + filename;

		
		try
		{			
			// Create output mod file
			modFile = new File(file);
			FileWriter modFileWriter = new FileWriter(modFile); 
			
			System.out.print("Creating mod file " + file + ".. ");
			
			if (places.length > 0)
			{
				model();
				passageTime();
			}
						
			// Write buffer to file
			modFileWriter.write(modString.toString());
			modFileWriter.close();
			System.out.println("Done");			
		}
		
		catch (IOException e3)
		{
			System.out.println("Could not write to file! " + file);
			e3.printStackTrace();
		}	
				
	}
	
	private void model()
	{
		modString.append("\\model{\n");			
		stateVector();
		initial();
		transitions();
		modString.append("}\n\n");
		
		maxStates();
	}
	
	private void stateVector() {
		modString.append("\t\\statevector{\n");
		modString.append("\t\t\\type{short}{");
		
		modString.append(places.ids[0]);
		
		for(int i=1; i<places.length; i++)
            modString.append(", ").append(places.ids[i]);
				
		modString.append("}\n");
		modString.append("\t}\n\n");
	}
	
	private void initial()
	{
		modString.append("\t\\initial{\n");
		modString.append("\t\t");
		
		for(int i=0; i<places.length; i++)
			modString.append(places.ids[i]).append(" = ").append(places.marking[i]).append("; ");
				
		modString.append("\n\t}\n");		
	}
	
	private void transitions()
	{
		
		for(int i=0; i<transitions.length; i++)
		{
			modString.append("\t\\transition{").append( transitions.ids[i] ).append("}{\n");
			// Add the transition condition, i.e. all inputs to the transition hold a marking
		  	modString.append("\t\t\\condition{").append( getTransitionConditions(i)).append("}\n");
		  	modString.append("\t\t\\action{\n");
		  	
		  	LinkedList<SimpleArc> arcToTransitions = transitions.arcsTo.get(i);
		  	LinkedList<SimpleArc> arcFromTransitions = transitions.arcsFrom.get(i);
		  	
		  
		  	// Consider when a place is both an input and an output to the transition, potentially of different weights
		  	// We assume that each place can have a maximum of one arc to and/or from the transition
		  	arcToLoop: for (SimpleArc currArc : arcToTransitions)
		  	{
		  		// Check if the current transition has the place as both an output and an input,
		  		// if so compact to a single action of the correct weight
		  		for (SimpleArc compareArc : arcFromTransitions)
		  		{
		  			if(currArc.placeId.equals(compareArc.placeId))
		  			{
		  				if(currArc.weight > compareArc.weight)
		  				{
		  					// Adjust source place's weight and remove the target place
		  					currArc.weight-= compareArc.weight;
		  					arcFromTransitions.remove(compareArc);
		  					break;
		  				}
		  				else if (currArc.weight < compareArc.weight)
		  				{
		  					// Adjust the weight of the target place and skip writing to the source place
		  					compareArc.weight -= currArc.weight;
		  					continue arcToLoop;
		  				}
		  				else //Equal weights cancel each other so print nothing
		  				{
		  					arcFromTransitions.remove(compareArc);
		  					continue arcToLoop;
		  				}
		  			}
		  		}
		  		
		  		modString.append("\t\t\tnext->").append( currArc.placeId);
		  		modString.append(" = ").append(currArc.placeId).append(" - ").append(currArc.weight).append(";\n");
		  	}
		  	
		  	
		  	// Add a marking to all outputs of the transition
		  	for (SimpleArc currArc : transitions.arcsFrom.get(i))
		  	{	
		  		modString.append("\t\t\tnext->").append(currArc.placeId);
		  		modString.append(" = ").append(currArc.placeId).append(" + ").append(currArc.weight).append(";\n");
		  	}
		  	
		  	modString.append("\t\t}\n");
		  	
		  	
		  	
		  	// Specify the rate / weight of the transition
		  	if (transitions.timed[i])
		  		modString.append("\t\t\\rate{").append(transitions.rate[i]).append("}\n");
		  	else
		  		modString.append("\t\t\\weight{").append(transitions.rate[i]).append("}\n");
		  		
		  	
		  	modString.append("\t}\n");
		  	
		}
	}

	private String getTransitionConditions(int transitionNum) {
		String condition = "";
	  	
		Iterator<SimpleArc> arcsTo = transitions.arcsTo.get(transitionNum).iterator();
		SimpleArc currArc;
		
	  	if (arcsTo.hasNext())
	  	{
	  		currArc = arcsTo.next();
	  		condition += currArc.placeId + " > " + (currArc.weight - 1);
	  	}
	  		
	  		
	  	while (arcsTo.hasNext())
	  	{
	  		currArc = arcsTo.next();
	  		condition += " && " + currArc.placeId + " > " + (currArc.weight - 1);
	  	}
	  	
	  	return condition; 	
	}
	
	/**
	 * This method is to limit the number of states that are generated
	 * to a maximum of 2 million. This ensures infinite state models terminate 
	 */
	private void maxStates()
	{
		modString.append("\\generation{\n\t\\maxstates{2000000}\n}\n");
	}
	
	private void performance()
	{
		if (performanceMeasures.states.size() > 0 || performanceMeasures.counts.size() > 0)
		{
			modString.append("\\performance{\n");
		
			stateMeasure();
			countMeasure();
			modString.append("\n}\n\n");
		}
	}
	
	private void stateMeasure()
	{
		for(int i=0; i< performanceMeasures.states.size(); i++)
		 {
			// Set the "State <state id>" as the identifier
			modString.append("\t\\statemeasure{").append("State ").append(performanceMeasures.states.get(i)).append("}{\n");			
			
			// Add the estimators
			modString.append("\t\t\\estimator{");         

			for(int j=0; j<performanceMeasures.stateEstimators.size(); j++)
                modString.append(performanceMeasures.stateEstimators.get(j)).append(" ");
			
			modString.append("}\n");
			
			// set the state we wish to measure
			modString.append("\t\t\\expression{").append( performanceMeasures.states.get(i)).append("}\n");
			
			modString.append("\t}\n");        
		 }
	}
	
	private void countMeasure()
	{
		for(int i=0; i< performanceMeasures.counts.size(); i++)
		 {
			// Set the "Transition <state id>" as the identifier
			modString.append("\t\\countmeasure{").append("Transition ").append(performanceMeasures.counts.get(i)).append("}{\n");			
			
			// Add the estimators
			modString.append("\t\t\\estimator{mean}\n");         
			
			// set the transition we wish to count
			modString.append("\t\t\\transition{").append(performanceMeasures.counts.get(i)).append("}\n");
			
			modString.append("\t}\n");        
		 }
	}
	
	private void passageTime()
	{
		// Set solution type to 'sor'
		modString.append("\\solution{\n\t\\method{sor}\n}");
			
		
		modString.append("\n\\passage{\n");
		
		// Add source conditions
		modString.append("\t\\sourcecondition{");
		stateGroups(sourceStateGrps);
		modString.append("}\n");
		
		// Add target conditions
		modString.append("\t\\targetcondition{");
		stateGroups(destStateGrps);
		modString.append("}\n");
		
		// Add time parameters
        modString.append("\t\\t_start{").append(timePoints.startTime).append("}\n");
        modString.append("\t\\t_stop{").append(timePoints.endTime).append("}\n");
        modString.append("\t\\t_step{").append(timePoints.timeStep).append("}\n");
		
		modString.append("}\n");

	}
	
	private void stateGroups(ArrayList<StateGroup> stateGroups)
	{
		String[] currentCondition;
		int groupCount =0;
		
		for(StateGroup curStateGroup : stateGroups)
		{
			currentCondition = curStateGroup.getConditions();
			
			// for any group after the first add the OR 
			if (groupCount > 0)
				modString.append(" || ");
			
			
			modString .append( "(" ).append(currentCondition[0]);
			
			for(int i=1; i< currentCondition.length;i++)
				modString.append(" && ").append(currentCondition[i]);
			
			modString.append( ")" );
			
			groupCount++;
		}
	}
	
	public void destroy()
	{
		modFile.delete();
	}
	
	public String getFilePath()
	{
		return file;
	}	
	
}

