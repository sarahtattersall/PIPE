/*
 * Created on 25-Jul-2005
 */
package pipe.calculations;

import pipe.io.ImmediateAbortException;
import pipe.io.ReachabilityGraphFileHeader;
import pipe.io.TransitionRecord;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * @author Nadeem
 *
 * This class is always used as a static class. It is used to determine the 
 * steady state of a general stochastic petri net. The only public method is 
 * solve() which calculates the steady state.
 */
public class SteadyStateSolver {

   
   public static double[] solve(File rgfile) throws ImmediateAbortException {
      // A sparse matrix containing the transition probabilities between
      // tangible states.
      LinkedList[] Qprime = createSparseMatrix(rgfile);
      return gaussSiedel(Qprime);
   }

   
   /**
    * createSparseMatrix()
    * Read's the state to state transitions from a reachability graph file and 
    * the associated rates and creates the matrix Q. However, as it is a sparse
    * matrix (lots of zeroes) it creates it in a more efficient form than an 
    * n by n array. Instead it returns an array of LinkedLists. Each ROW of the
    * array represents a COLUMN of the matrix. Each object in the linked list 
    * represents an element of the matrix as a record <row, rate>. This way, 
    * only non-zero elements are stored. It is stored column wise as later 
    * calculations use the transpose of this matrix.
    *
    * @param rgfile   The reachability graph file
    * @return         The sparse matrix Q
    * @throws         ImmediateAbortException
    */
   private static LinkedList[] createSparseMatrix(File rgfile) throws 
           ImmediateAbortException {
      ReachabilityGraphFileHeader rgheader = new ReachabilityGraphFileHeader();
      RandomAccessFile inputfile = null;
      
      // A sparse matrix containing the transition probabilities between 
      // tangible states.
      LinkedList[] Qprime = null;
      double[] rowsum = null;
      
      TransitionRecord current = new TransitionRecord();
      
      DecimalFormat f = new DecimalFormat();
      f.setMaximumFractionDigits(1);
      
      // Open the reachability graph file for reading
      try {
         inputfile = new RandomAccessFile(rgfile, "r");
         rgheader.read(inputfile);
      } catch (IOException e) {
         System.out.println("IO error!");
         throw new ImmediateAbortException("IO error");
      }
      
      // Populate the sparse matrix
      int numcolumns = rgheader.getNumStates();
      int numtransitions = rgheader.getNumTransitions();
      Qprime = new LinkedList[numcolumns];
      rowsum = new double[numcolumns];
      for (int row = 0; row < numcolumns; row++) {
         rowsum[row] = 0.0;
      }
      for (int index=0; index < numcolumns; index++) {
         Qprime[index] = new LinkedList();
      }
      System.out.println("Creating sparse matrix...");
      try{
         // Populate the sparse matrix
         // We don't need to insert elements in a column in row order as each 
         // element tells you which row it's in. We just need to get the column 
         // right.
         inputfile.seek(rgheader.getOffsetToTransitions());
         for (int record = 0; record < numtransitions; record++) {
            current.read(inputfile);
            if (current.getFromState() != current.getToState()) {
               int row = current.getFromState();
               int column = current.getToState();
            //   System.out.println("from state" +current.getFromState() + " to state "+current.getFromState()+ " rate is "+ current.getRate());
               Qprime[column].add(new MatrixElement(row, current.getRate()));
               rowsum[row] += current.getRate();
            }
            //System.out.print(f.format(((double)record/numtransitions)*100) +
            //        "% complete.  \r");
         }
         
         // Now add in the diagonal elements. Each diagonal element is the 
         // negative sum of the off diagonals on that ROW.
         for (int column = 0; column < numcolumns; column++) {
            Qprime[column].add(new MatrixElement(column, 0.0 - rowsum[column]));
         }
         System.out.println("100.0% complete.  ");
      } catch (IOException e) {
         throw new ImmediateAbortException("IO Error!");
      } catch (OutOfMemoryError e) {
         System.out.println("There was insufficient memory to"
                  + " hold the infinitesimal generator matrix.");
         throw new ImmediateAbortException("There was insufficient memory to"
                  + " hold the infinitesimal generator matrix.");
      } catch (Exception e) {
         System.out.println("Unknown exception!");
         throw new ImmediateAbortException("Unknown exception!");
      }
      
      try {
         inputfile.close();
      } catch (IOException e) {
         throw new ImmediateAbortException("Could not close rgfile.");
      }
      
      
      //debug
//      for(int i=0; i < numcolumns; i++){
//         System.out.println("Col: " + i + " " + Qprime[i]);
//      }

      return Qprime;
   }
   
   
   /**
    * gaussSiedel()
    * Solves matrix equations of the form Ax = b for x using the Gauss-Siedel 
    * iterative method. In this case, A is a sparse matrix, b=0 and the sum of
    * all xi = 1
    * @param A   a sparse matrix
    * @return
    */
   private static double[] gaussSiedel(LinkedList[] A){
      int numrows = A.length;
      double[] x = new double[numrows];
      
      // Used to decide whether a particular x value has converged or not
      boolean[] converged = new boolean[numrows];
      boolean plausible = false;
      double xprevious;
      double residual;
      double epsilon = 0.00001;
      
      DecimalFormat f=new DecimalFormat();
      f.setMaximumFractionDigits(1);
      
      // Set initial guess of x values to 1, and converged
      // array to false
      for (int row = 0; row < numrows; row++) {
         x[row] = 1.0;
         converged[row] = false;
      }
      
      // Now solve for x using Gauss-Siedel.
      Iterator iterator;
      MatrixElement current;
      double sum;
      double aii = 1.0;
      System.out.println("Solving steady state distribution...");
      System.out.println("Please wait, it could take some time...");
      while (!plausible) {
         for (int row = 0; row < numrows; row++) {
            iterator = A[row].iterator();
            sum = 0;
            while (iterator.hasNext()) {
               current = (MatrixElement)iterator.next();
               if (current.getRow()!= row) {
                  sum += current.getRate() * x[current.getRow()];
               } else {
                  aii = current.getRate();
               }
            }
            if (aii != 0.0) {
               x[row] = (0.0 - sum) / aii;
            }
         }
         // Now we've been through an iteration on all the rows, check the 
         // residuals to see how accurate our answer is
         for (int row = 0; row < numrows; row++) {
        	 iterator = A[row].iterator();
            residual = 0;
            while (iterator.hasNext()) {
               current = (MatrixElement)iterator.next();
               residual += current.getRate()*x[current.getRow()];
            }
            if (residual < 0) {
               residual = 0-residual;
            }
             converged[row] = residual < epsilon;
         }
         // If we think all the rows have converged, we should check whether 
         // the steady state values are plausible by checking none of the values 
         // are < 0
         if (allConverged(converged)) {
            plausible = true;
             for(double aX : x)
             {
                 if(aX < 0)
                 {
                     plausible = false;
                 }
             }
         }
      }
      System.out.println("The steady state solution has been calculated.");
      // Now normalise the steady state vector
      sum = 0;
       for(double aX : x)
       {
           sum += aX;
       }
      for (int i = 0; i < x.length; i++) {
         x[i] /= sum;
      }
      return x;
   }
   
   
   private static boolean allConverged(boolean[] c){
       for(boolean aC : c)
       {
           if(!aC)
           {
               return false;
           }
       }
      return true;
   }
   
}


/**
 * @author Nadeem
 * This class is used to create a sparse matrix. It represents each element in 
 * the matrix as a pair of values <row, rate>.
 */
class MatrixElement{
   
   private int row;
   private final double rate;
   
   
   public MatrixElement(int rw, double rt){
      row = rw;
      if (rt == -0.0) {
         rate = 0.0;
      } else {
         rate = rt;
      }
   }
   
   public void setRow(int row){
	   this.row=row;
   }
   
   
   public int getRow(){
      return row;
   }
   
   
   public double getRate(){
      return rate;
   }
   
   
   public String toString(){
      return "Row: " + row + " Rate: " + rate;
   }
   
}
