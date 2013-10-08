/*
 * Created on 01-Jul-2005
 */
package pipe.calculations;

import java.util.LinkedList;

import pipe.gui.ApplicationSettings;
import pipe.views.MarkingView;


/**
 * @author Nadeem
 * This class is used to hold basic details of a state which in the case of a 
 * Petri Net is it's marking.
 */
public class State {
   
   private int[] state;
private LinkedList<MarkingView>[] placeMarking;
   
   
   public State(int[] newState){
      setState(newState);
   }
   
   
   State(State newState){
      setState(newState.getState());
   }
   
   
   void setState(int[] newState){
      state = new int[newState.length];
      System.arraycopy(newState, 0, state, 0, newState.length);
   }
   
   
   public int[] getState(){
      return state;
   }
   
   
   /**
    * equals()
    * Overloads the Object.equals() method. Tests the supplied state object 
    * parameter to see if it represents exactly the same state as this object. 
    * Returns true if and only if the arrays containing the states are the same
    * length and contain the same values at the same element indices.
    * @param test   The state object to be compared to this one
    * @return
    */
   public boolean equals(State test){
      int[] teststate = test.getState();
      
      if (teststate.length != state.length) {
         return false;
      }
      
      for (int index = 0; index < state.length; index++) {
         if (state[index] != teststate[index]) {
            return false;
         }
      }
      return true;
   }
   
   
   /**
    * hashCode()
    * This overrides the Object.hashCode() method.
    */
    public int hashCode(){
      int total = 0;
      
      for (int offset = 0; offset < state.length; offset++) {
         total = (2 * total);
         for (int index = 0; index < (state.length - offset); index++) {
            total += state[index];
         }
         // If we have overflowed then wrap round to zero.
         if (total < 0) {
            total = Integer.MAX_VALUE + total;    
         }
      }
      if (total < 0) {
         total = Integer.MAX_VALUE + total;    
      }
      return total;
   }

   
   /**
    * hashCode2()
    * This is an extra hashing function used for collision resolution. If both 
    * the original hashcode and this hashcode match that of another state 
    * object, then they are very probably the same state.
    * @return
    */
   public int hashCode2(){
      int total = 0;
      
      for (int offset = 0; offset < state.length; offset++) {
         total = 2 * total;
         for (int index = offset; index < state.length; index++) {
            total += state[index];
         }
      }
      return total;
   }
   
   
   public String toString(){
      int length = state.length;
      String output = state[0] + ", ";
      
      for (int i = 1; i < length-1; i++) {
         output += state[i] + ", ";
      }
      output += state[length-1];
      
      return output;
   }


public void setPlaceMarking(LinkedList<MarkingView>[] currentMarkingVector) {
	this.placeMarking = currentMarkingVector;
	
}

public LinkedList<MarkingView>[] getPlaceMarking(){
	if(placeMarking==null){
		return ApplicationSettings.getApplicationView().getCurrentPetriNetView().getCurrentMarkingVector();
	}else{
		return placeMarking;
	}
}
   
}
