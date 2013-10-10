/*
 * TransitionRecord.java
 *
 * Created on 15-Jul-2005
 */
package pipe.io;

import java.io.IOException;
import java.io.RandomAccessFile;

import pipe.calculations.MarkingState;


/**
 * @author Nadeem
 * @author Matthew Worthington/Edwin Chung - modifications made both to the read
 * and write methods to enable reachability call to generate method in 
 * StateSpaceGenerator to save particular transition numbers. 
 * This class is used for storing a transition from one GSPN state to another 
 * in a file. The file would typically be used to describe a reachability graph.
 */
public class TransitionRecord {
   
   // Rather than storing the entire state object, these hold a copies of the 
   // state arrays
   private int fromState;
   private int toState;
   
   // A record of the transition rate between the two states.
   private double rate;
   private int transition;
   
   
   /**
    * Sets up the record ready for writing to a file.
    * @param from
    * @param to
    * @param r
    */
   public TransitionRecord(MarkingState from, MarkingState to, double r) {
      fromState = from.getIDNum();
      toState = to.getIDNum();
      rate = r;
   }
   
   
   public TransitionRecord(int from, int to, double r) {
      fromState = from;
      toState = to;
      rate = r;
   }
   
   
   public TransitionRecord(int from, int to, double r, int t) {
      fromState = from;
      toState = to;
      rate = r;
      transition = t;
   }
   
   
   // Sets up a blank record ready for reading in from a file
   public TransitionRecord(){
      fromState = 0;
      toState = 0;
      rate = 0.0;
   }
   
   /**
    * write()
    * Writes a TransitionRecord to the specified file.
    * @param outputfile		The file to write data to
    * @throws IOException
    */
   public void write(RandomAccessFile outputfile) throws IOException{
      outputfile.writeInt(fromState);
      outputfile.writeInt(toState);
      outputfile.writeDouble(rate);
   }
   
   
   public void write1(RandomAccessFile outputfile) throws IOException{
      write(outputfile);
      outputfile.writeInt(transition);
   }   
   
   
   /**
    * read()
    * Reads a TransitionRecord from the specified input file.
    *
    *
    * @param inputfile   The file to read data from
    * @throws IOException
    */
   public void read(RandomAccessFile inputfile) throws IOException{
      fromState = inputfile.readInt();
      toState = inputfile.readInt();
      rate = inputfile.readDouble();
   }
   
   
   public void read1(RandomAccessFile inputfile) throws IOException{
      read(inputfile);
      transition = inputfile.readInt();
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
      return fromState;
   }
   
   
   public int getToState(){
      return toState;
   }
   
   
   public double getRate(){
      return rate;
   }
   
   
   public int getTransitionNo() {
      return transition;
   }
   
   
   public int getRecordSize(){
      // Calculated as follows:
      // Ints are 4bytes, doubles are 8bytes
      // There are 3 int's plus one double
      return 3*4 + 8; //abans era 2*4 + 8 perque no hi havia la transicio
   }
   
   
   /**
    * equals()
    * Overrides the Object.equals method. Returns true if and only if both 
    * records have identical elements in their fromstatearrays and also in their 
    * twostatearrays.
    * @param test		The record to be compared with
    * @return
    */
   public boolean equals(TransitionRecord test){
      return (fromState == test.getFromState()) && (toState == test.getToState());
   }
   
}
