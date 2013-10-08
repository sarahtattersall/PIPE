/*
 * Created on Feb 12, 2004
 */
package pipe.calculations;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import pipe.exceptions.StateSpaceTooBigException;
import pipe.io.ReachabilityGraphFileHeader;
import pipe.io.StateRecord;


/**
 * @author Matthew
 */
public class StateList {
   
   private final ArrayList list;
   
   
   public StateList() {
      list = new ArrayList();
   }
   
   
   /**
    * StateList()
    * @param rgfile - A reachability graph file containing a list of tangible 
    *                 states.
    * @param readTangible
    * @throws IOException
    * @throws StateSpaceTooBigException
    *
    * @author Edwin Chung Mar 2007 - modified the constructor so it can allow
    * the reading of the type of state i.e. whether it is tangible or not  
    */
   public StateList(File rgfile, boolean readTangible) throws 
           IOException, StateSpaceTooBigException
   {
      list = new ArrayList();
      RandomAccessFile input = new RandomAccessFile(rgfile, "r");
      ReachabilityGraphFileHeader rgheader = new ReachabilityGraphFileHeader(input);
      StateRecord currentrecord = new StateRecord();
      
      int numtangiblestates = rgheader.getNumStates();
      int state = 0;
      try{
         for (state = 0; state < numtangiblestates; state++){
            if (readTangible) {
               currentrecord.read1(rgheader.getStateArraySize(), input);
            } else {
               currentrecord.read(rgheader.getStateArraySize(), input);
            }
            fastadd(currentrecord.getState(), currentrecord.getID());
         }
         input.close();
      } catch (OutOfMemoryError e){
         throw new StateSpaceTooBigException(state);
      }
      
   }
   
   
   /**
    * fastadd()
    * The original add() methods carry out checks for duplicate entries.
    * If the constructor StateList(RandomAccessFile rgfile) has been used, then 
    * this method can be called as the file should contain no duplicate entries.
    * This makes the code more efficient and should execute faster as there 
    * could potentially be a few hundred thousand states.
    * @param m   The marking/state to be added
    * @param id	 An integer used to create an id for the marking
    * @author Nadeem 30/06/2005
    * @throws OutOfMemoryError
    */
   private void fastadd(int[] m, int id) throws OutOfMemoryError{
      MarkingState marking = new MarkingState(m, id);
      list.add(marking);
   }

   
   public void add(int[] m) {
      list.trimToSize();
      int size = list.size();
      //int[] candidate = m.marking();
      
      if (size == 0) {
         MarkingState marking = new MarkingState(m, 0);
         list.add(marking);
         //id = "M0";
         //print(m);
         return;
      } else if (size > 0 ) {
          for(Object aList : list)
          {
              MarkingState comparator = (MarkingState) aList;
              int[] compareInts = comparator.getMarking();
              if(compareMarking(m, compareInts))
              {
                  return;
              }
          }
      }
      
      MarkingState marking = new MarkingState(m, size);
      list.add(marking);
   }
   
   
   //This is for adding states with an existing ID to another StateList
   public void add(int[] marking, int idInput) {
      MarkingState m = new MarkingState(marking, idInput);
      list.add(m);
   }
   
   
   public String getID(int index) {
      MarkingState record = (MarkingState)list.get(index);
      return record.getID();
   }
   
   
   public int getIDNum(int index) {
      MarkingState record = (MarkingState)list.get(index);
      return record.getIDNum();
   }
   
   
   void output(int i) {
      MarkingState mark = (MarkingState)list.get(i);
      int[] row = mark.getMarking();
      int size = row.length;
      
      for (int j = 0; j < size; j++) {
         System.out.print(row[j] + " ");
      }
   }
   
   
   public int[] get(int index) {
      MarkingState record = (MarkingState)list.get(index);
      return record.getMarking();
   }
   
   
   public int size() {
      return list.size();
   }
      
   
   boolean compareMarking(int[] mark1, int[] mark2) {
      int m1 = mark1.length;
      int m2 = mark2.length;
      //System.out.println("m1 = " + m1 + " m2 = " + m2);
      if (m1 != m2) {
         return false;
      } else {
         for (int i = 0; i < m1; i++) {
            if (mark1[i] != mark2[i]) {
               //System.out.println (mark1[i] +" ");
               return false;
            }
         }
      }
      return true;
   }
   
   
   public boolean isEmpty() {
      return list.isEmpty();
   }
   
   
   public void print() {
      int size = list.size();
      for (int i = 0; i < size; i++) {
         output(i);
         System.out.println();
      }
   }
   
   
   public void print(int[] marking) {
      int length = marking.length;
      for (int i = 0; i < length; i++) {
         System.out.print(marking [i] + " ");
      }
      System.out.println("");
   }
   
}
