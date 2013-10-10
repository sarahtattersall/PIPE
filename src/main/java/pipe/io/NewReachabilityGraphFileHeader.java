package pipe.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;

/**
 * Version of ReachabilityGraphFileHeader implemented using the faster java.nio.* classes
 *
 * @author Oliver Haggarty - 08/2007 (Ideas taken from code by Nadeem Akharware)
 *
 */
public class NewReachabilityGraphFileHeader
        extends ReachabilityGraphFileHeader {
   
   private int signature; // Will be used to check the correct file type
   private int numstates;
   private int statearraysize;
   private int numtransitions;
   private int transitionrecordsize;
   private long offsettotransitions;
   
   
   /**
    * Sets up a Reachability Graph File Header Object ready
    * for writing.
    * @param ns
    * @param ss
    * @param nt
    * @param trs
    * @param offset
    */
   public NewReachabilityGraphFileHeader(int ns, int ss, int nt, int trs, long offset) {
      signature = 8271; // ASCII code for 'R' 'G'
      numstates = ns;
      statearraysize = ss;
      numtransitions = nt;
      transitionrecordsize = trs;
      offsettotransitions = offset;
   }
   
   
   /**
    * Creates blank reachabiliyt graph file header, everything set to zero
    * @param input
    * @throws IncorrectFileFormatException
    * @throws IOException
    */
   public NewReachabilityGraphFileHeader(RandomAccessFile input) throws
           IOException {
      this(0,0,0,0,0);
      read(input);
   }
   
   
   /**
    * Sets up a blank Reachability Graph File Header Object
    */
   public NewReachabilityGraphFileHeader(){
      this(0,0,0,0,0);
   }
   
   
   /**
    * writes the contents of the reachability graph header to a specified buffer
    * @param outputBuf
    * @throws IOException
    */
   public void write(MappedByteBuffer outputBuf)
   {
      outputBuf.putInt(signature);
      outputBuf.putInt(numstates);
      outputBuf.putInt(statearraysize);
      outputBuf.putInt(numtransitions);
      outputBuf.putInt(transitionrecordsize);
      outputBuf.putLong(offsettotransitions);
   }
   
   
   /**
    * read in the contents of the reachabiliyt graph header from a specified buffer
    * @param inputBuf
    * @throws IOException
    * @throws IncorrectFileFormatException
    */
   public void read(MappedByteBuffer inputBuf) throws
           IOException
   {
      signature = inputBuf.getInt();
      
      // Check the specified file is an RG File
      if(signature != 8271)
         throw new IncorrectFileFormatException("RG File");
      
      numstates = inputBuf.getInt();
      statearraysize = inputBuf.getInt();
      numtransitions = inputBuf.getInt();
      transitionrecordsize = inputBuf.getInt();
      offsettotransitions = inputBuf.getLong();
   }
   
   
   public int getNumStates(){
      return numstates;
   }
   
   
   public int getStateArraySize(){
      return statearraysize;
   }
   
   
   public int getNumTransitions(){
      return numtransitions;
   }
   
   
   public int getTransitionRecordSize(){
      return transitionrecordsize;
   }
   
   
   public long getOffsetToTransitions(){
      return offsettotransitions;
   }
   
}
