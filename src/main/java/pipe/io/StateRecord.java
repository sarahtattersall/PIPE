/*
 * Created on 21-Jul-2005
 */
package pipe.io;

import java.io.IOException;
import java.io.RandomAccessFile;

import pipe.calculations.MarkingState;


/**
 * @author Nadeem
 * @author Matthew Worthington/Edwin Chung - modifications made both to the read
 * and write methods to enable reachability call to generate method in 
 * StateSpaceGenerator to save type of state - i.e. tangible or not. Also added 
 * new helper methods.  
 * This class is used to record states in a file.
 */
public class StateRecord {
   
   private int stateid;
   private int[] state = null;
   private boolean isTangible;
   
   
   public StateRecord(){
   }
   
   
   public StateRecord(MarkingState newstate){
      stateid = newstate.getIDNum();
      state = new int[newstate.getState().length];
      System.arraycopy(
               newstate.getState(), 0, state, 0, newstate.getState().length);
      isTangible = newstate.getIsTangible();
   }
   
   
   public void write(RandomAccessFile opfile) throws IOException{
      if (state == null) {
         return;
      }
      opfile.writeInt(stateid);
       for(int aState : state)
       {
           opfile.writeInt(aState);
       }
   }
   
   
   public void write(RandomAccessFile opfile, boolean Tangible) 
           throws IOException{
      write(opfile);
      opfile.writeBoolean(Tangible);
   }   
   
   
   public void read(int statesize, RandomAccessFile ipfile) throws IOException{
      state = new int[statesize];
      stateid = ipfile.readInt();
      for (int index = 0; index < state.length; index++) {
         state[index] = ipfile.readInt();
      }
   }
   
   
   public void read1(int statesize, RandomAccessFile ipfile) throws IOException{
      read(statesize, ipfile);
      isTangible = ipfile.readBoolean();
   }   
   
   
   public int[] getState(){
      return state;
   }
   
   
   public int getID(){
      return stateid;
   }
   
   
   public boolean getTangible(){
      return isTangible;
   }   
   
   
   public String toString(){
      String s = String.valueOf(stateid) + " - ";

       for(int aState : state)
       {
           s += aState;
       }
      
      return s + " [tangible? " + isTangible + "]\n";
   }

   public String getMarkingString() {
      String s = "{";
      
      for (int i = 0; i < state.length - 1; i++) {
         if (state[i] == -1) {
            s += "\u03C9, "; //\u03C9: Unicode Character 'GREEK SMALL LETTER OMEGA'
         } else {
            s += state[i] + ", ";
         }
      }
      if (state[state.length - 1] == -1) {
            s += "\u03C9";
      } else {
         s += state[state.length- 1];
      }      
      s += "}";
      
      return s;
   }
}
