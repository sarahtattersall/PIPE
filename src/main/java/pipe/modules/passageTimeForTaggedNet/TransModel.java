package pipe.modules.passageTimeForTaggedNet;


import pipe.common.dataLayer.StateGroup;
import pipe.views.ArcView;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;
import pipe.views.TransitionView;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;




class TransModel {

	
	 
	  private final PlaceView[] _placeViews;
	  private final TransitionView[] _transitionViews;
	  private final PetriNetView _pnmldata;
	  private String modString = "";
	  private String perfString = "";
	  private final String tagPlace = "tagged_location";
    private final int UNTAGGED = 0;
	  private final int ORIGINAL = 1;
	  private final int CLONED = 2;
	  
	  private final ArrayList<StateGroup> sourceStateGrps;
    private final ArrayList<StateGroup> destStateGrps;
	  private final AnalysisSetting timePoints;

	  public TransModel(PetriNetView pnml, ArrayList<StateGroup> sourceState, ArrayList<StateGroup> destinationState, AnalysisSetting timeSettings){
		  
		  _pnmldata = pnml;
		  _placeViews = _pnmldata.places();
		  _transitionViews = _pnmldata.getTransitionViews();
		  sourceStateGrps=sourceState;
		  destStateGrps =destinationState;
		  timePoints =  timeSettings;
		  produceModel();
		  
	  }
	  
	  
	  private void produceModel(){
		  

		  System.out.println("\n Translate model");
		  
		  try{

			    FileWriter out1 = new FileWriter("current.mod");
			    
				
			    
//			    FileWriter fstream = new FileWriter("/homes/wl107/Desktop/current.txt");
			    
			    FileWriter fstream = new FileWriter("current.txt");
		
			    
			  	//FileWriter out1 = new FileWriter("c:\\current.mod");
			    //FileWriter fstream = new FileWriter("c:\\current.txt");
			    BufferedWriter out = new BufferedWriter(fstream);
			    
				
			    
			    generateMod();
			    
				  System.out.println("\n Generated model");		    
			    
			    out.write(modString);
			    out.write(perfString);
			    out.close();
			    out1.write(modString);
			    out1.write(perfString);
			    out1.close();
			    
			  }
		  catch (Exception e)
		  {
			    System.err.println("Error: " + e.getMessage());
		  }
 
			
	  }
	  
	  private void generateMod() {
			modString = "";
			model();
			method();
			//performance();
			passageTime();
		}
		
		private void model() {
			modString += "\\model{\n";
			stateVector();
			initial();
			transitions();
			
			modString += "}\n\n";		
		}
	  
		private void performance() {
			modString += "\\performance{\n";
			tokenDistribution();
			transitionMeasures();
			modString += "}\n";		
			
		}
		
		private void tokenDistribution() {
            for(PlaceView _placeView : _placeViews)
            {
                modString += "\t\\statemeasure{Mean tokens on place " + _placeView.getId() + "}{\n";
                modString += "\t\t\\estimator{mean variance distribution}\n";
                modString += "\t\t\\expression{" + _placeView.getId() + "}\n";
                modString += "\t}\n";
            }
		}
		
		private void transitionMeasures() {
			for(int i=0; i< _transitionViews.length; i++) {
				
				Iterator arcsTo = _transitionViews[i]. getConnectToIterator();
				boolean taggedArc = false;
				
			  	while(arcsTo.hasNext())			  		
			  		if(  ((ArcView)arcsTo.next()).isTagged() )
			  			taggedArc = true;
			  	
			  	transitionMeasuresDesc(i,false);
			  	if(taggedArc)transitionMeasuresDesc(i,true);
		
				
			}
		}
		
		private void transitionMeasuresDesc(int i, boolean clone){
			
			String id = _transitionViews[i].getId();
			if(clone) id += "_tagged";
			
			modString += "\t\\statemeasure{Enabled probability for transition " + id + "}{\n";
			modString += "\t\t\\estimator{mean}\n";
			modString += "\t\t\\expression{(" + getTransitionConditions(i) + ") ? 1 : 0}\n";
			modString += "\t}\n";
			
			modString += "\t\\countmeasure{Throughput for transition " + id + "}{\n";
			modString += "\t\t\\estimator{mean}\n";
			modString += "\t\t\\precondition{1}\n";
			modString += "\t\t\\postcondition{1}\n";
			modString += "\t\t\\transition{" + id + "}\n";
			modString += "\t}\n";
			
		}
		
		private void method()
		{
			modString += "\\solution{\n\t\\method{sor}\n\n}";
		}
		
		private void stateVector() 
		{	
			modString += "\t\\statevector{\n";
			modString += "\t\t\\type{short}{";
			
			modString += _placeViews[0].getId();
			
			for(int i=1; i< _placeViews.length; i++) {
				modString += ", "+ _placeViews[i].getId();
			}

			modString += ", " + tagPlace;		
				
			modString += "}\n";
			modString += "\t}\n\n";
		}
	  
		private void initial() {
			modString += "\t\\initial{\n";			
			modString += "\t\t";
            for(PlaceView _placeView : _placeViews)
            {
                modString += _placeView.getId() + " = " + _placeView.getCurrentMarkingView() + "; ";
            }
            int taggedPlaceIndex = -1;
            modString += tagPlace + " = " + taggedPlaceIndex + ";";
			modString += "\n\t}\n";					
		}
		
		private void transitions()
		{
			
			for(int i=0; i< _transitionViews.length; i++)
			{
			  	boolean taggedArc = false;
				int numInputArc=0;
			  	Iterator arcsTo = _transitionViews[i]. getConnectToIterator();
			  	
			  	/*since the net has been validated, if the transition has a tagged input arc
			  	 * it must have corresponding tagged output, hence need to check for tagged input
			  	 */ 
			  	while(arcsTo.hasNext()){
			  		
			  		if(  ((ArcView)arcsTo.next()).isTagged() )
			  		{
			  			taggedArc = true;
			  			numInputArc++;
			  		}
			  		
			  	}
			  	
			  	if(taggedArc) 
			  	{
				  	writeTransition(ORIGINAL, i, numInputArc);
			  		writeTransition(CLONED, i,  numInputArc);
			  	}
			  	else 
			  	{
				  	writeTransition(UNTAGGED, i, numInputArc);
			  	}
			  		
			}//end for
		}//end transition
		
		
		private void writeTransition(int type, int i, int numInputArc) 
		{
			Iterator arcToTransitions = _transitionViews[i]. getConnectToIterator();
		  	Iterator arcFromTransitions = _transitionViews[i]. getConnectFromIterator();
			
			int[] tagInputPlaceIndex = new int[numInputArc];
			int taggedInput=0;
			int tagOutputPlaceIndex = -1;

		  	
		  	/* if type is CLONED or ORIGINAL, 
		  	 * need to find tagged input places and tagged output place
		  	 */
		  	if(type != UNTAGGED) 
		  	{
		  		
		  		while(arcToTransitions.hasNext())
		  		{
			  		
		  			/*
		  			 * if the arc is tagged, 
		  			 * there's possibility that the place attach to it
		  			 * may contain a tagged token
		  			 */
		  			final ArcView arcView = ((ArcView)arcToTransitions.next());
			  		if( arcView.isTagged()  )
			  		{
			  			tagInputPlaceIndex[taggedInput] = getPlaceIndex(arcView.getSource().getId());
			  			taggedInput++;
			  		}
			  		
			  	}
		  		
		  		while(arcFromTransitions.hasNext())
		  		{
		  			/*
		  			 * obtain tagged outputPlace index
		  			 */
		  			final ArcView arcView = ((ArcView)arcFromTransitions.next());
			  		if( arcView.isTagged()  )
			  		{
			  			tagOutputPlaceIndex = getPlaceIndex(arcView.getTarget().getId());
			  			break;
			  		}
		  			
		  		}
		  		
		  	}//if !untagged

		  		/*
		  		 * 1) write transition id
		  		 */
		  		if(type == ORIGINAL || type == UNTAGGED) 
					modString += "\t\\transition{"+ _transitionViews[i].getId() +"}{\n";
				
				else if(type == CLONED)	
					modString += "\t\\transition{"+ _transitionViews[i].getId() +"_tagged}{\n";
				
		  		
		  		/*
		  		 * 2) write enabling condition 
		  		 */
		  		if(type == UNTAGGED)
					modString += "\t\t\\condition{" + getTransitionConditions(i) + "}\n";
				
		  		/*
		  		 * this is transition in mode ut'
		  		 * fire when the input places doesn't contain tagged token
		  		 * if it contains tagged token, then it is enable if the marking on this
		  		 * place is greater than 1
		  		 */
				else if(type == ORIGINAL) 
				{
					
				  	modString += "\t\t\\condition{(" + getTransitionConditions(i) + 
			  		" && tagged_location != " + tagInputPlaceIndex[0];
				  	
				  	for(int x=1;x<taggedInput;x++)
				  		modString += " && tagged_location!=" + tagInputPlaceIndex[x];
				  	
				  	modString+= ") || (" + getTaggedTransitionConditions(i, tagInputPlaceIndex)+" )}\n";	
				}
		  		/*
		  		 * transition in mode t' can fire when there's 
		  		 * correct marking and tagged_location must be
		  		 * one of the input places
		  		 */
				else if(type == CLONED) 
				{
				  	modString += "\t\t\\condition{(" + getTransitionConditions(i) + 
				  		") && (tagged_location==" + tagInputPlaceIndex[0];
				  	
				  	for(int x=1;x<taggedInput;x++)
				  		modString += " || tagged_location==" + tagInputPlaceIndex[x];
	
				  	modString += ")}\n";
				}
		  		
		  		/*
		  		 * 3) write action
		  		 */
		  		modString += "\t\t\\action{\n";
		  		arcToTransitions = _transitionViews[i]. getConnectToIterator();
			  	arcFromTransitions = _transitionViews[i]. getConnectFromIterator();
			  	
			  	int[][] incidenceMatrix = _pnmldata.getActiveTokenView().simpleMatrix();
			  	
			  	while (arcToTransitions.hasNext()) 
			  	{
			  		final ArcView arcView = ((ArcView)arcToTransitions.next());
			  		String currentId = arcView.getSource().getId();
			  		int placeNo = this.getPlaceIndex(currentId);
			  		if( incidenceMatrix!=null && incidenceMatrix[placeNo][i]<0  )
			  		{
			  			modString += "\t\t\tnext->"+currentId;
			  			modString += " = "+currentId+" - " + arcView.getWeight() +  ";\n";
			  		}
			  	}
			  	while (arcFromTransitions.hasNext()) 
			  	{
			  		final ArcView arcView = ((ArcView)arcFromTransitions.next());
			  		String currentId = arcView.getTarget().getId();
			  		int placeNo = this.getPlaceIndex(currentId);
			  		if( incidenceMatrix!=null && incidenceMatrix[placeNo][i]>0  )
			  		{
			  			modString += "\t\t\tnext->"+currentId;
			  			modString += " = "+currentId+" + " + arcView.getWeight() +  ";\n";
			  		}
			  	}

			  	if(type==CLONED)
					modString += "\t\t\tnext->tagged_location=" + tagOutputPlaceIndex + ";\n"; 
				
				
				modString += "\t\t}\n";
		  		
				/*
				 * 4) rate and weight
				 */
				if(type == UNTAGGED)
				{
					if (_transitionViews[i].isTimed())
				  		modString += "\t\t\\rate{" + _transitionViews[i].getRate();
					else 
				  		modString += "\t\t\\weight{" + _transitionViews[i].getRate();
					
					if(_transitionViews[i].isInfiniteServer())
					{
						String tagged_place = "(";
						
						arcToTransitions = _transitionViews[i]. getConnectToIterator();
						if(arcToTransitions.hasNext()) 
					  	{
					  		tagged_place += ((ArcView)arcToTransitions.next()).getSource().getId();
                          }
						while(arcToTransitions.hasNext()) 
					  	{
					  		final ArcView arcView = ((ArcView)arcToTransitions.next());
					  		String currentId = arcView.getSource().getId();
					  		tagged_place += "+" + currentId;
					  	}
							
						tagged_place += ")";
						
						modString += "*" + tagged_place + "}\n";	  	
					}
					else
					{
						modString += "}\n";
					}
					
				  	
				}
				else if(type == ORIGINAL)
				{
					double rate = _transitionViews[i].getRate();
					
					if (_transitionViews[i].isTimed())
				  		modString += "\t\t\\rate{";
				  	
				  	else // not timed transition
				  		modString += "\t\t\\weight{";
					
						
					if(_transitionViews[i].isInfiniteServer())
					{
						
						String tagged_place = "(";
						
						arcToTransitions = _transitionViews[i]. getConnectToIterator();
						if(arcToTransitions.hasNext()) 
					  	{
					  		tagged_place += ((ArcView)arcToTransitions.next()).getSource().getId();
                          }
						while(arcToTransitions.hasNext()) 
					  	{
					  		final ArcView arcView = ((ArcView)arcToTransitions.next());
					  		String currentId = arcView.getSource().getId();
					  		tagged_place += "+" + currentId;
					  	}
							
						tagged_place += ")";
				  	
							
							modString += " tagged_location== " + tagInputPlaceIndex[0] + 
				  			"? ( (double)" + rate + " -  ((double)"+ getArcWeightFromPlace(_placeViews[tagInputPlaceIndex[0]].getId(), i )
				  			+ "/(double)" + _placeViews[tagInputPlaceIndex[0]].getId() +") * " + rate + ") * " + tagged_place + " : ";
				  		
							int index=1;
				  		
							while(index<taggedInput)
							{
								modString += " tagged_location== " + tagInputPlaceIndex[index] + 
								"? ( (double)" + rate + " -  ((double)"+ getArcWeightFromPlace(_placeViews[tagInputPlaceIndex[index]].getId(), i )
								+ "/(double)" + _placeViews[tagInputPlaceIndex[index]].getId() +") * " + rate + ") * " + tagged_place + " : ";
				  		
								index++;
							}
				  		
				  		
							modString += rate + "*" + tagged_place +"}\n";
	
					}
						
					else //not infiniteServer
					{
							modString += " tagged_location== " + tagInputPlaceIndex[0] + 
				  			"? ( (double)" + rate + " -  ((double)"+ getArcWeightFromPlace(_placeViews[tagInputPlaceIndex[0]].getId(), i )
				  			+ "/(double)" + _placeViews[tagInputPlaceIndex[0]].getId() +") * " + rate + ") : ";
				  		
							int index=1;
				  		
							while(index<taggedInput)
							{
								modString += " tagged_location== " + tagInputPlaceIndex[index] + 
								"? ( (double)" + rate + " -  ((double)"+ getArcWeightFromPlace(_placeViews[tagInputPlaceIndex[index]].getId(), i )
								+ "/(double)" + _placeViews[tagInputPlaceIndex[index]].getId() +") * " + rate + ") : ";
				  		
								index++;
							}
							modString += rate +"}\n";	
					}

				}// end else if original
				else if(type == CLONED)
				{
					double rate = _transitionViews[i].getRate();
					
				  	if (_transitionViews[i].isTimed())
				  		modString += "\t\t\\rate{";
				  	
				  	else // not timed transition
				  		modString += "\t\t\\weight{";
				  	
				  	if(_transitionViews[i].isInfiniteServer())
				  	{
				  		
				  		String tagged_place = "(";
						
						arcToTransitions = _transitionViews[i]. getConnectToIterator();
						if(arcToTransitions.hasNext()) 
					  	{
					  		tagged_place += ((ArcView)arcToTransitions.next()).getSource().getId();
                          }
						while(arcToTransitions.hasNext()) 
					  	{
					  		final ArcView arcView = ((ArcView)arcToTransitions.next());
					  		String currentId = arcView.getSource().getId();
					  		tagged_place += "+" + currentId;
					  	}
							
						tagged_place += ")";
							
				  			modString += "tagged_location== " + tagInputPlaceIndex[0] + 
				  			"? ((double)"+ getArcWeightFromPlace(_placeViews[tagInputPlaceIndex[0]].getId(), i )
				  			+ "/(double)" + _placeViews[tagInputPlaceIndex[0]].getId() +") * " + rate + "*" + tagged_place + " : ";
				  		
				  			int index=1;
				  		
				  			while(index<taggedInput)
				  			{
				  				modString += " tagged_location== " + tagInputPlaceIndex[index] + 
				  				"? ((double)"+ getArcWeightFromPlace(_placeViews[tagInputPlaceIndex[index]].getId(), i )
				  				+ "/(double)" + _placeViews[tagInputPlaceIndex[index]].getId() +") * " + rate + "*" + tagged_place + " : ";
				  		
				  				index++;
				  			}
				  		
				  		
				  			modString += rate + "*" + tagged_place +"}\n";
				  			
				  	}
				  		
				  	else // not InfiniteServer
				  	{
				  			
				  			modString += "tagged_location== " + tagInputPlaceIndex[0] + 
				  			"? ((double)"+ getArcWeightFromPlace(_placeViews[tagInputPlaceIndex[0]].getId(), i )
				  			+ "/(double)" + _placeViews[tagInputPlaceIndex[0]].getId() +") * " + rate + " : ";
				  		
				  			int index=1;
				  		
				  			while(index<taggedInput)
				  			{
				  				modString += " tagged_location== " + tagInputPlaceIndex[index] + 
				  				"? ((double)"+ getArcWeightFromPlace(_placeViews[tagInputPlaceIndex[index]].getId(), i )
				  				+ "/(double)" + _placeViews[tagInputPlaceIndex[index]].getId() +") * " + rate + " : ";
				  		
				  				index++;
				  			}
				  		
				  			modString += rate +"}\n";
		
				  	}

				}//end else if cloned
				  	
				modString += "\t}\n";
				
			
		}//end writeTransition
		

		private int getArcWeightFromPlace(String placeId, int i)
		{
			Iterator arcsTo = _transitionViews[i]. getConnectToIterator();
			
			//System.out.println("\n in get arc weight " + placeId);
			while(arcsTo.hasNext())
			{
				final ArcView arcView = (ArcView)arcsTo.next();
				//System.out.println("\n"+arc.getSource().getId());
				if(arcView.getSource().getId().equals(placeId)){
					//System.out.println("\nfound match");
					return arcView.getWeight().getFirst().getCurrentMarking();
				}
			}
			
			return -1;
		}
		
		/*
		 * for each of the place connected to input tagged arc,
		 * check if it equals to tagged_location, if yes then, it must
		 * have marking one more greater than the backward incidence function
		 * otherwise, everything is normal
		 */
		private String getTaggedTransitionConditions(int transitionNum, int[] tagInputPlace) {
			
			String condition = "";
		  	Iterator arcsTo = _transitionViews[transitionNum]. getConnectToIterator();
		  	if (arcsTo.hasNext()){
		  		final ArcView arcView = (ArcView)arcsTo.next();
		  		if(arcView.isTagged())
		  		{
		  			condition += "((tagged_location== "+getPlaceIndex(arcView.getSource().getId())
		  				+ " && " + arcView.getSource().getId()+" > "+ (arcView.getWeight().getFirst().getCurrentMarking()-1+1)
		  				+ ") || ( tagged_location!="+getPlaceIndex(arcView.getSource().getId())
		  				+ " && " + arcView.getSource().getId()+" > "+ (arcView.getWeight().getFirst().getCurrentMarking()-1)
		  				+ "))";
		  		}
		  		else condition += arcView.getSource().getId()+" > "+ (arcView.getWeight().getFirst().getCurrentMarking() - 1);
		  	}
		  		
		  	while (arcsTo.hasNext())
		  	{
		  		
		  		final ArcView arcView = (ArcView)arcsTo.next();
		  		if(arcView.isTagged())
		  		{
		  			condition += " && ((tagged_location== "+getPlaceIndex(arcView.getSource().getId())
		  				+ " && " + arcView.getSource().getId()+" > "+ (arcView.getWeight().getFirst().getCurrentMarking()-1+1)
		  				+ ") || ( tagged_location!="+getPlaceIndex(arcView.getSource().getId())
		  				+ " && " + arcView.getSource().getId()+" > "+ (arcView.getWeight().getFirst().getCurrentMarking()-1)
		  				+ "))";
		  		}
		  		else condition += " && "+ arcView.getSource().getId()+" > "+ (arcView.getWeight().getFirst().getCurrentMarking() - 1);
	  		
		  	}
		  	
		  	return condition;

		}
		
		private int getPlaceIndex(String placeName){		
			int index = -1;
			for(int i=0; i< _placeViews.length; i++) {
				if(_placeViews[i].getId().equals(placeName))
				{
					index = i;
					break;
				}
			}
//			System.out.println("Returning " + index);
			
			return index;
		}
		
		private String getTransitionConditions(int transitionNum) {
			
			String condition = "";
		  	Iterator arcsTo = _transitionViews[transitionNum]. getConnectToIterator();
		  	if (arcsTo.hasNext()){
		  		final ArcView arcView = (ArcView)arcsTo.next();
		  		condition += arcView.getSource().getId()+" > "+ (arcView.getWeight().getFirst().getCurrentMarking()-1);
		  	}
		  		
		  	while (arcsTo.hasNext())
		  	{
		  		final ArcView arcView = (ArcView)arcsTo.next();
		  		condition += " && "+ arcView.getSource().getId()+" > "+ (arcView.getWeight().getFirst().getCurrentMarking()-1);
		  	}
		  	return condition;
			
		}
		
		
		private void passageTime()
		{
			
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
			int tag_count =0;
			for(StateGroup curStateGroup : stateGroups)
			{
				boolean first_con = false;
				boolean and_con = false;
				currentCondition = curStateGroup.getConditions();
				String[] tag = new String[currentCondition.length];
				tag_count=0;
				// for any group after the first add the OR 
				if (groupCount > 0)
					perfString += " || ";
				
				
				perfString += "(";
				if(currentCondition[0].indexOf("tagged_location")>=0)
				tag[tag_count++]=currentCondition[0];
				else {
					perfString += currentCondition[0];
					first_con = true;
				}
				
				for(int i=1; i< currentCondition.length;i++)
					if(currentCondition[i].indexOf("tagged_location")>=0)
						tag[tag_count++]=currentCondition[i];
					else {
						if(first_con){
							perfString += " && ";
							
						}
						
						first_con = true;
						
						perfString += currentCondition[i];
					}
						
				for(int i=0; i<tag_count; i++){
					
					if(i==0){
						if(first_con)perfString+= " &&";
						perfString+=" (";
					}
					
					perfString+=tag[i];
					
					if(i<tag_count-1)perfString+="||";
					if(i==tag_count-1) perfString+=")";
					
				}
					
				
				perfString += ")";
				
				groupCount++;
			}
		}
		
	
}
