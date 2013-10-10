package pipe.modules.tagged;


import pipe.common.dataLayer.StateGroup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * This class translates pnml data is a valid mod file.
 * 
 * @author Nick Dingle (after Barry Kearns)
 * @date August 2007
 *
 */

class TransMod
{
	private final SimplePlaces places;
	private final SimpleTransitions transitions;
	private PerformanceMeasure performanceMeasures;
	private ArrayList<StateGroup> sourceStateGrps, destStateGrps;
	private AnalysisSetting timePoints;
	
	private int taggedPlaceIndex = -1;
	
	private String modString = "";
	private String perfString = "";
	private String file = "";
	private File modFile = null;
	private final String tagPlace = "tagged_location";
	
	private MessageDigest md = null;
	
	private final int UNTAGGED = 0;
	private final int ORIGINAL = 1;
	private final int CLONED = 2;
	
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
		
		for(int i=0; i<places.length; i++) {
			if(places.tagged[i])
				taggedPlaceIndex = i;
		}
		
//		String filename = "modFile" + clientNo + ".mod";
//		file = path + System.getProperty("file.separator") + filename;
		// This creates a file: path/modFile0.mod
	
		file = path;
		
		try{
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nSAE) {
			System.out.println("Hashing algorithm not found!");
			nSAE.printStackTrace();
		}		
		
		try
		{			
			// Create output mod file
			modFile = new File(file);
			FileWriter modFileWriter = new FileWriter(modFile); 
			
			System.out.print("Creating mod file " + file + ".. ");
			
			if (places.length > 0)
			{
				model();
				modString += computeHash(modString);
//				performance();
				perfString += computeHash(perfString);
			}
			
			//System.out.println(modString);
			
			// Write buffer to file
			modFileWriter.write(modString);
			modFileWriter.write(perfString);
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
	public TransMod(SimplePlaces splaces, SimpleTransitions stransitions, ArrayList<StateGroup> sourceStateGroups, ArrayList<StateGroup> destinationStateGroups, AnalysisSetting timeSettings, String path, int clientNo) throws IOException
    {
		places = splaces;
		transitions = stransitions;
		sourceStateGrps = sourceStateGroups;
		destStateGrps = destinationStateGroups;
		timePoints = timeSettings;

		for(int i=0; i<places.length; i++) {
			if(places.tagged[i])
				taggedPlaceIndex = i;
		}
		
		//String filename = "modFile" + clientNo + ".mod";
		//file = path + System.getProperty("file.separator") + filename;
		file = path;
		
		try{
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException nSAE) {
			System.out.println("Hashing algorithm not found!");
			nSAE.printStackTrace();
		}
        // Create output mod file
        modFile = new File(file);
        FileWriter modFileWriter = new FileWriter(modFile);

        System.out.print("Creating mod file " + file + ".. ");

        if (places.length > 0)
        {
            model();
            modString += computeHash(modString);
        }

        // Write buffer to file
        modFileWriter.write(modString);
        modFileWriter.write(perfString);
        modFileWriter.close();
        System.out.println("Done");
				
	}
	
	private void model()
	{
		modString += "\\model{\n";			
		stateVector();
		initial();
		transitions();
		modString += "}\n\n";
		
//		maxStates();
		method();
	}
	
	private void stateVector() 
	{	
		modString += "\t\\statevector{\n";
		modString += "\t\t\\type{short}{";
		
		modString += places.ids[0];
		
		for(int i=1; i<places.length; i++) {
			modString += ", "+places.ids[i];
		}

		modString += ", " + tagPlace;		
			
		modString += "}\n";
		modString += "\t}\n\n";
	}
	
	private void initial()
	{	
		modString += "\t\\initial{\n";
		modString += "\t\t";
		
		for(int i=0; i<places.length; i++) {
			modString += places.ids[i] + " = " + places.marking[i] + "; ";
		}

		modString += tagPlace + " = " + taggedPlaceIndex + ";";
		
		modString += "\n\t}\n";		
	}

	private void transitions()
	{
		
		for(int i=0; i<transitions.length; i++)
		{
		  	boolean taggedArc = false;
			
		  	LinkedList<SimpleArc> arcToTransitions = transitions.arcsTo.get(i);
		  	LinkedList<SimpleArc> arcFromTransitions = transitions.arcsFrom.get(i);

		  	
		  	//int maxLength = (arcToTransitions.size() > arcFromTransitions.size() ? arcToTransitions.size() : arcFromTransitions.size() );

            for(SimpleArc arcToTransition : arcToTransitions)
            {
                if(arcToTransition.tagged)
                {
                    taggedArc = true;
                    break;
                }
            }
            for(SimpleArc arcFromTransition : arcFromTransitions)
            {
                if(arcFromTransition.tagged)
                {
                    taggedArc = true;
                    break;
                }
            }
		  	
		  	if(taggedArc) {
			  	writeTransition(ORIGINAL, i, arcToTransitions, arcFromTransitions);
		  		writeTransition(CLONED, i, arcToTransitions, arcFromTransitions);
		  	}
		  	else {
			  	writeTransition(UNTAGGED, i, arcToTransitions, arcFromTransitions);
		  	}
		  		
		}
	}
	
	private void writeTransition(int type, int i, LinkedList<SimpleArc> arcToTransitions, LinkedList<SimpleArc>arcFromTransitions) {
		
		int tagInputPlaceIndex = -1;
		int tagOutputPlaceIndex = -1;

	  	String tagInputPlace = null;
	  	String tagOutputPlace = null;
	  	
	  	
	  	if(type != UNTAGGED) {
              for(SimpleArc arcToTransition : arcToTransitions)
              {
                  if(arcToTransition.tagged)
                  {
                      tagInputPlaceIndex = getPlaceIndex(arcToTransition.placeId);
                      break;
                  }
              }
              for(SimpleArc arcFromTransition : arcFromTransitions)
              {
                  if(arcFromTransition.tagged)
                  {
                      tagOutputPlaceIndex = getPlaceIndex(arcFromTransition.placeId);
                      break;
                  }
              }

	  		tagInputPlace = places.ids[tagInputPlaceIndex];
			tagOutputPlace = places.ids[tagOutputPlaceIndex];
	  	}
		
		
		if(type == ORIGINAL || type == UNTAGGED) {
			modString += "\t\\transition{"+ transitions.ids[i] +"}{\n";
		}
		else if(type == CLONED)	{
			modString += "\t\\transition{"+ transitions.ids[i] +"_tagged}{\n";
		}
		
		
		if(type == UNTAGGED){
			modString += "\t\t\\condition{" + getTransitionConditions(i) + "}\n";
		}
		else if(type == ORIGINAL) {
		  	modString += "\t\t\\condition{(" + getTransitionConditions(i) + 
	  		" && tagged_location != " + tagInputPlaceIndex + ") || (" + getTaggedTransitionConditions(i, tagInputPlace) 
	  		+ " && tagged_location == " + tagInputPlaceIndex + ")}\n";	
		}
		else if(type == CLONED) {
		  	modString += "\t\t\\condition{(" + getTransitionConditions(i) + 
		  		") && (tagged_location==" + tagInputPlaceIndex + ")}\n";
		}
		
		
		modString += "\t\t\\action{\n";
		
		// Consider when a place is both an input and an output to the transition, potentially of different weight
		// We assume that each place can have a maximum of one arc to and/or from the transition
		arcToLoop: for (SimpleArc currArc : arcToTransitions)
		{
			// Check if the current transition has the place as both an output and an input,
		  	// if so compact to a single action of the correct weight
		  	for (SimpleArc compareArc : arcFromTransitions){
		  		if(currArc.placeId.equals(compareArc.placeId)){
		  			if(currArc.weight > compareArc.weight){
		  				// Adjust source place's weight and remove the target place
		  				currArc.weight-= compareArc.weight;
		  				arcFromTransitions.remove(compareArc);
		  				break;
		  			}
		  			else if (currArc.weight < compareArc.weight){
		  				// Adjust the weight of the target place and skip writing to the source place
		  				compareArc.weight -= currArc.weight;
		  				continue arcToLoop;
		  			}
		  			else{
		  				// Equal weights cancel each other so print nothing
		  				arcFromTransitions.remove(compareArc);
		  				continue arcToLoop;
		  			}
		  		}
		  	}
		  		
		  	modString += "\t\t\tnext->" + currArc.placeId;
		  	modString += " = "+ currArc.placeId + " - " + currArc.weight +";\n";
		}
		
	  	// Add a marking to all outputs of the transition
	  	for (SimpleArc currArc : transitions.arcsFrom.get(i))
	  	{	
	  		modString += "\t\t\tnext->" + currArc.placeId;
	  		modString += " = "+ currArc.placeId + " + " + currArc.weight + ";\n";
	  	}
		
		if(type==CLONED){
			modString += "\t\t\tnext->tagged_location=" + tagOutputPlaceIndex + ";\n"; 
		}
		
		modString += "\t\t}\n";
		  	
		
		//NEED TO IMPLEMENT WEIGHT CHANGE CORRECTLY....
		if(type == UNTAGGED) {
			if (transitions.timed[i])
		  		modString += "\t\t\\rate{" + transitions.rate[i] + "}\n";
		  	else
		  		modString += "\t\t\\weight{" + transitions.rate[i] + "}\n";			
		}
		else if(type == ORIGINAL){
			double rate = transitions.rate[i];
			
			//= (1.0*((double)(P0-1)/P0));
			
			
			if (transitions.timed[i])
		  		modString += "\t\t\\rate{tagged_location==" + tagInputPlaceIndex + 
		  			" ? ("+ rate + "*((double)(" + tagInputPlace +"-1)/" + tagInputPlace + 
		  			")) : " + rate + "}\n";
		  	else
		  		modString += "\t\t\\weight{" + transitions.rate[i] + "}\n";
		}else if(type == CLONED){
			double rate = transitions.rate[i];
		  		
		  	if (transitions.timed[i])
		  		modString += "\t\t\\rate{" + tagInputPlace + 
		  			">1 ? ((double)"+ rate + "/(double)" + tagInputPlace +") : "+ rate +"}\n";
		  	else
		  		modString += "\t\t\\weight{" + rate + "}\n";		  		
		}
		  	
		modString += "\t}\n";
				
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

	private String getTaggedTransitionConditions(int transitionNum, String tagInputPlace) {
		String condition = "";
	  	
		Iterator<SimpleArc> arcsTo = transitions.arcsTo.get(transitionNum).iterator();
		SimpleArc currArc;
		
	  	if (arcsTo.hasNext())
	  	{
	  		currArc = arcsTo.next();
	  		if(currArc.placeId.equals(tagInputPlace))
		  		condition += currArc.placeId + " > " + ((currArc.weight-1)+1);
	  		else
	  			condition += currArc.placeId + " > " + (currArc.weight - 1);
	  	}
	  		
	  		
	  	while (arcsTo.hasNext())
	  	{
	  		currArc = arcsTo.next();
	  		if(currArc.placeId.equals(tagInputPlace))
		  		condition += " && " + currArc.placeId + " > " + ((currArc.weight-1)+1);
	  		else	  		
	  			condition += " && " + currArc.placeId + " > " + (currArc.weight - 1);
	  	}
	  	
	  	return condition; 	
	}


	/**
	 * This method is to limit the number of states that are generated
	 * to a maximum of 2 million.This ensures infintie state models terminate
	 */
	private void maxStates()
	{
		modString += "\\generation{\n\t\\maxstates{20000000}\n\n}";
	}
	
	private void method()
	{
		modString += "\\solution{\n\t\\method{sor}\n\n}";
	}
	
	
	private void performance()
	{
		if (performanceMeasures != null && (performanceMeasures.states.size() > 0 || performanceMeasures.counts.size() > 0))
		{
			perfString += "\\performance{\n";
		
			stateMeasure();
			countMeasure();
			perfString += "\n}\n\n";
		}
	}
	
	private void stateMeasure()
	{
		for(int i=0; i< performanceMeasures.states.size(); i++)
		 {
			// Set the "State <state id>" as the identifier
			perfString += "\t\\statemeasure{" + "State " + performanceMeasures.states.get(i) + "}{\n";			
			
			// Add the estimators
			perfString += "\t\t\\estimator{";         

			for(int j=0; j<performanceMeasures.stateEstimators.size(); j++)
				perfString += performanceMeasures.stateEstimators.get(j) + " ";
			
			perfString += "}\n";
			
			// set the state we wish to measure
			perfString += "\t\t\\expression{" + performanceMeasures.states.get(i) + "}\n";
			
			perfString += "\t}\n";        
		 }
	}

	private void countMeasure()
	{
		for(int i=0; i< performanceMeasures.counts.size(); i++)
		 {
			// Set the "Transition <state id>" as the identifier
			perfString += "\t\\countmeasure{" + "Transition " + performanceMeasures.counts.get(i) + "}{\n";			
			
			// Add the estimators
			perfString += "\t\t\\estimator{mean}\n";         
			
			// set the transition we wish to count
			perfString += "\t\t\\transition{" + performanceMeasures.counts.get(i) + "}\n";
			
			perfString += "\t}\n";        
		 }
	}
	
	private void passageTime()
	{
		// Set solution type to 'sor'
		perfString += "\n\\solution{\n\t\\method{sor}\n\n}";
			
		
		perfString += "\n\\passage{\n";
		
		if(sourceStateGrps != null && destStateGrps != null && timePoints != null) {
			// Add source conditions
			perfString += "\t\\sourcecondition{";
			stateGroups(sourceStateGrps);
			perfString += "}\n";
		
			//  Add target conditions
			perfString += "\t\\targetcondition{";
			stateGroups(destStateGrps);
			perfString += "}\n";
		
			// Add time parameters
			perfString += "\t\\t_start{" + timePoints.startTime + "}\n";
			perfString += "\t\\t_stop{" + timePoints.endTime + "}\n";
			perfString += "\t\\t_step{" + timePoints.timeStep + "}\n";
			
			perfString += "}\n";
		}

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
				perfString += " || ";
			
			
			perfString += "(" + currentCondition[0];
			
			for(int i=1; i< currentCondition.length;i++)
				perfString += " && " + currentCondition[i];
			
			perfString += ")";
			
			groupCount++;
		}
	}

	private int getPlaceIndex(String placeName){
		
		int index = -1;
		
//		System.out.println("Searching for " + placeName);
		
		for(int i=0; i<places.length; i++) {		
//			System.out.println("Comparing input " + placeName + " with " + places.ids[i]);
			if(places.ids[i].equals(placeName)){
//				System.out.println("Comparison is true");
				index = i;
				break;
			}
		}
		
//		System.out.println("Returning " + index);
		
		return index;
	}
	
	public void destroy()
	{
		modFile.delete();
	}
	
	public String getFilePath()
	{
		return file;
	}	
	
	private String computeHash(String inputString) {

		return "";
		
	/*	//This code works, but is commented out as it messes up the input file
		try {
			byte[] input = inputString.getBytes();
			md.update(input);
			MessageDigest mdClone = (MessageDigest) md.clone();
			byte[] hash = mdClone.digest();
//			byte[] hash = md.digest();
			BigInteger bIHash = new BigInteger(hash);
				
			//print as a string of hex characters
			String sHash = bIHash.toString(16);
			
			if (sHash.length() % 2 != 0) {
			        // Pad with 0
			        sHash = "0"+sHash;
			}
			
			return sHash;
			
			//return new String(hash);
			
		} catch (CloneNotSupportedException cNSE) {
			System.out.println("Could not clone MessageDigest!");
			cNSE.printStackTrace();	
		}
		
		return "% No hash computed due to error\n\n";
		*/
	}	
	

}

