/*
 * Created on 15-Jul-2005
 */
package pipe.io;

import java.io.IOException;
import java.io.RandomAccessFile;


/**
 * @author Nadeem
 * This class is used to read/write a fileheader for a Reachability Graph file.
 */
public class ReachabilityGraphFileHeader {
   
   private int signature; // Will be used to check the correct file type
   private int numStates;
   private int stateArraySize;
   private int numTransitions;
   private int transitionRecordSize;
   private long offsetToTransitions;
	

   /**
    * Sets up a Reachability Graph File Header Object ready for writing.
    * @param ns
    * @param ss
    * @param nt
    * @param trs
    * @param offset
    */
   public ReachabilityGraphFileHeader(int ns, int ss, int nt, int trs, long offset) {
      signature = 8271; // ASCII code for 'R' 'G'
      numStates = ns;
      stateArraySize = ss;
      numTransitions = nt;
      transitionRecordSize = trs;
      offsetToTransitions = offset;
   }

   
   public ReachabilityGraphFileHeader(RandomAccessFile input)
           throws IOException{
      this(0, 0, 0, 0, 0);
      read(input);
   }

   
   /**
    * Sets up a blank Reachability Graph File Header Object
    */
   public ReachabilityGraphFileHeader(){
      this(0, 0, 0, 0, 0);
   }

   
   public void write(RandomAccessFile outputfile) throws IOException{
      outputfile.writeInt(signature);
      outputfile.writeInt(numStates);
      outputfile.writeInt(stateArraySize);
      outputfile.writeInt(numTransitions);
      outputfile.writeInt(transitionRecordSize);
      outputfile.writeLong(offsetToTransitions);
   }
   
   
   public void read(RandomAccessFile inputfile) 
           throws IOException
   {
      signature = inputfile.readInt();
      
      // Check the specified file is an RG File
      if (signature != 8271) {
         throw new IncorrectFileFormatException("RG File");
      }
      
      numStates = inputfile.readInt();
      stateArraySize = inputfile.readInt();
      numTransitions = inputfile.readInt();
      transitionRecordSize = inputfile.readInt();
      offsetToTransitions = inputfile.readLong();
   }

   
   public int getNumStates(){
      return numStates;
   }
   
   
   public int getStateArraySize(){
      return stateArraySize;
   }

   
   public int getNumTransitions(){
      return numTransitions;
   }
   
   
   public int getTransitionRecordSize(){
      return transitionRecordSize;
   }
   
   
   public long getOffsetToTransitions(){
      return offsetToTransitions;
   }
   
}
