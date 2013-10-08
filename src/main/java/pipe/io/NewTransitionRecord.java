package pipe.io;

import java.io.IOException;
import java.nio.MappedByteBuffer;

import pipe.calculations.MarkingState;

/**
 * Implementation of TransitionRecord using java.nio.* classes
 *
 * @author Oliver Haggarty - 08/2007 - (Ideas taken from Nadeem Akharware)
 *
 */
public class NewTransitionRecord {
   
   // Rather than storing the entire state object,
   // these hold a copies of the state arrays
   private int fromstate;
   private int tostate;
   
   // A record of the transition rate between the two states.
   private double rate;
   private int transition;
   private char isFromTangible;
   
   
   /**
    * Sets up the record ready for writing to a file.
    * @param from
    * @param to
    * @param r
    * @param t
    * @param isFTan
    */
   public NewTransitionRecord(int from, int to, double r, int t, boolean isFTan) {
      fromstate = from;
      tostate = to;
      rate = r;
      transition = t;
      if (isFTan) {
         isFromTangible = 'T';
      } else {
         isFromTangible = 'V';
      }
   }
   
   
   /**
    * Sets up record ready for writing to a file
    * @param from
    * @param to
    * @param r
    */
   public NewTransitionRecord(MarkingState from, MarkingState to, double r) {
      fromstate = from.getIDNum();
      tostate = to.getIDNum();
      rate = r;
   }
   
   
   /**
    * Sets up record ready for writing to a file
    * @param from
    * @param to
    * @param r
    */
   public NewTransitionRecord(int from, int to, double r) {
      fromstate = from;
      tostate = to;
      rate = r;
   }
   
   
   /**
    * Sets up record ready for writing to a file
    * @param from
    * @param to
    * @param r
    * @param t
    */
   public NewTransitionRecord(int from, int to, double r, int t) {
      fromstate = from;
      tostate = to;
      rate = r;
      transition = t;
   }
   
   
   /**
    * Sets up blank record
    * @param from
    * @param to
    * @param r
    */
   public NewTransitionRecord(){
      fromstate = 0;
      tostate = 0;
      rate = 0.0;
   }
   
   
   /**
    * write()
    * Writes a TransitionRecord to the specified file.
    *
    * @param outputfile		The file to write data to
    * @throws IOException
    * @param outputBuf
    */
   public void write(MappedByteBuffer outputBuf)
   {
      outputBuf.putInt(fromstate);
      outputBuf.putInt(tostate);
      outputBuf.putDouble(rate);
      outputBuf.putInt(transition);
      outputBuf.putChar(isFromTangible);
   }
   
   
   /**
    * read()
    * Reads a TransitionRecord from the specified input file.
    *
    *
    * @param inputBuf		The file to read data from
    * @throws IOException
    */
   public void read(MappedByteBuffer inputBuf)
   {
      fromstate = inputBuf.getInt();
      tostate = inputBuf.getInt();
      rate = inputBuf.getDouble();
      transition = inputBuf.getInt();
      isFromTangible = inputBuf.getChar();
   }
   
   
   /**
    * updateRate()
    * When recording a transition from one state to another, it is possible that
    * there will be multiple paths between them through vanishing states. If 
    * this happens, the rates of transition between all those paths should just
    * be combined into one effective rate by multiplying the rates.
    * @param r
    */
   public void updateRate(double r){
      rate *= r;
   }
   
   
   public int getFromState(){
      return fromstate;
   }
   
   
   public int getTransitionNo() {
      return transition;
   }
   
   
   public int getToState(){
      return tostate;
   }
   
   
   public double getRate(){
      return rate;
   }
   
   
   public boolean getIsFromTan() {
       return isFromTangible == 'T';
   }
   
   
   /**
    * Returns size of record
    * @return
    */
   public int getRecordSize(){
      // Calculated as follows:
      // Ints are 4bytes, doubles are 8bytes, chars are 2bytes
      // There are 2 int's plus one double and one char
      return 3*4 + 8 + 2;
   }
   
   
   /**
    * equals()
    * Overrides the Object.equals method. Returns true if and only if both 
    * records have identical elements in their fromstatearrays and also in their 
    * twostatearrays.
    *
    * @param test		The record to be compared with
    * @return
    */
   public boolean equals(TransitionRecord test){
      return (fromstate == test.getFromState() && 
              tostate == test.getToState());
   }
   
}
