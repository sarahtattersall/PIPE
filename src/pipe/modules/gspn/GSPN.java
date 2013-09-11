/*
 * Created on 29-Jun-2005
 */
package pipe.modules.gspn;

import pipe.calculations.StateList;
import pipe.gui.widgets.PetriNetChooserPanel;
import pipe.gui.widgets.ResultsHTMLPane;
import pipe.modules.classification.Classification;
import pipe.views.PetriNetView;
import pipe.views.PlaceView;

import java.io.BufferedWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * @author Nadeem
 * @author minor changes Will Master 02/2007
 * This class is used by all the GSPN analysis modules. It contains data and
 * functions required by all of them.
 */
public class GSPN 
        extends Classification {
   
   PetriNetChooserPanel sourceFilePanel;
   ResultsHTMLPane results;
   

   /**Qualitative analysis - see if the supplied GSPN is an EFC-GSPN.  
    * This is a necessary precondition for quantitative analysis.
    * @param DataLayer
    * @param pnmlData
    * @return
    */
   private boolean isEFCdGSPN(PetriNetView pnmlData) {
      return extendedFreeChoiceNet(pnmlData);
   }


   //Format StateList data nicely.
   String renderStateSpaceLinked(PetriNetView pnmlData, StateList data) {
      if ((data.size() == 0) || (data.get(0).length == 0)) {
         return "n/a";
      }
      int markSize = data.get(0).length;
      ArrayList result=new ArrayList();
      // add headers to table
      result.add("");
      
      PlaceView[] placeViews = pnmlData.places();//pendent++
      for (int i = 0; i < markSize; i++) {
         result.add(placeViews[i].getName());
         //result.add(_pnmlData.getPlace(i).getName());
         //result.add("<A NAME= 'M" + i + "'></A>");
      }
      
      for (int i = 0; i < data.size(); i++) {
         result.add(data.getID(i)+ "<A NAME= 'M" + i + "'></A>");
         for (int j = 0; j < markSize; j++) {
            result.add(Integer.toString(data.get(i)[j]));
         }
      }
      return ResultsHTMLPane.makeTable(result.toArray(), markSize + 1, false,
              true, true, true);
   }

   //Format StateList data nicely.
   protected String renderStateSpaceLinked(PetriNetView pnmlData, StateList data, BufferedWriter writer) {
      if ((data.size() == 0) || (data.get(0).length == 0)) {
         return "n/a";
      }
      int markSize = data.get(0).length;
      ArrayList result=new ArrayList();
      // add headers to table
      result.add("");
      
      PlaceView[] placeViews = pnmlData.places();
      for (int i = 0; i < markSize; i++) {
         result.add(placeViews[i].getName());
         //result.add("<A NAME= 'M" + i + "'></A>");
      }
      
      for (int i = 0; i < data.size(); i++) {
         result.add(data.getID(i)+ "<A NAME= 'M" + i + "'></A>");
         for (int j = 0; j < markSize; j++) {
            result.add(Integer.toString(data.get(i)[j]));
         }
      }
      return ResultsHTMLPane.makeTable(result.toArray(), markSize + 1, false,
              true, true, true);
   }   
   
   
   //Format lists of doubles nicely
   String renderLists(double[] data, StateList list) {
      if (list.size() == 0) {
         return "n/a";
      }
      int rows = list.size();
      
      ArrayList result = new ArrayList();
      // add headers to table
      result.add("Marking");
      result.add("Value");
      
      DecimalFormat f=new DecimalFormat();
      f.setMaximumFractionDigits(5);
      for (int i = 0; i < rows; i++) {
         result.add("<A HREF='#" + list.getID(i) + "'>" +
                            list.getID(i).toUpperCase() + "</A>");
         result.add(f.format(data[i]));
      }
      return ResultsHTMLPane.makeTable(
              result.toArray(), 2, false, true, true, true);
   }

   
   //Format lists of doubles nicely
   String renderLists(double[] data, PlaceView[] placeViews, String[] headings){
      int rows = data.length;
      
      ArrayList result=new ArrayList();
      // add headers to table
      result.add(headings[0]);
      result.add(headings[1]);
      
      DecimalFormat f=new DecimalFormat();
      f.setMaximumFractionDigits(5);
      for (int i = 0; i < rows; i++) {
         result.add(placeViews[i].getName());
         result.add(f.format(data[i]));
      }
      return ResultsHTMLPane.makeTable(
              result.toArray(), 2, false, true, true, true);
   }   
   
   
   /**Work out if a specified marking describes a tangible state.
    * A state is either tangible (all enabled transitions are timed)
    * or vanishing (there exists at least one enabled state that is transient, 
    * i.e. untimed).
    * If an immediate transition exists, it will automatically fire before a 
    * timed transition.
    * @param DataLayer - the net to be tested
    * @param int[]     - the marking of the net to be tested
    * @return boolean  - is it tangible or not
    * /
   protected boolean isTangibleState(PetriNet _pnmlData, int[] marking) {
      Transition[] trans = _pnmlData.getTransitions();
      int numTrans = trans.length;
      boolean hasTimed = false;
      boolean hasImmediate = false;
      
      for (int i = 0; i < numTrans; i++ ){
         if (isTransitionEnabled(_pnmlData, marking, i) == true){
            if (trans[i].isTime()== true) {
               //If any immediate transtions exist, the state is vanishing
               //as they will fire immediately
               hasTimed = true;
            } else if (trans[i].isTime() != true) {
               hasImmediate = true;
            }
         }
      }
      return (hasTimed == true && hasImmediate == false);
   }      
   
   */   
   
/******************************************************************************
 * Old code                                                                   *
 ******************************************************************************/

   /**Get the initial marking of the supplied net
    * @param pnmlData
    * @return
    */ /*
   private int[] marking(PetriNet _pnmlData){
      int places = _pnmlData.numberOfPlaces();
      int[] marking = new int[places];
      
      for (int i = 0; i < places; i++) {
         marking[i] = _pnmlData.getPlace(i).getInitialMarking();
      }
      return marking;
   } */
   
   
   
   
   /**Generate the reachability set using myTree function
    * Add each marking to an arraylist, testing to see if the marking is 
    * already present before adding.
    *
    * @param DataLayer
    * @return
    */ /*
   protected StateList getReachabilitySet(PetriNet _pnmlData)
           throws TreeTooBigException {
      PNMatrix plus = new PNMatrix(_pnmlData.getForwardsIncidenceMatrix());
      PNMatrix minus = new PNMatrix(_pnmlData.getBackwardsIncidenceMatrix());
      
      int[] marking = _pnmlData.getCurrentMarkingVector();
      StateList reachSetArray = new StateList();
      myTree reachSet = 
              new myTree(marking, plus, minus, reachSetArray, _pnmlData);
      return reachSetArray;
   } */   

   
   
   // This method is never called   
   /**Test for condition Equal Conflict.  I.E., for all t1, t2
    * in the set of transitions, where t1<>t2, that share the same
    * input place, either t1, t2 are both in the set of timed transitions (T1)
    * or t1, t2 are both in the set of immediate transitions (T2).
    *
    * @param DataLayer
    * @return boolean
    */ /*
   protected boolean testEqualConflict(PetriNet _pnmlData) {
      Place[] places = _pnmlData.places();
      Arc[] arcs = _pnmlData.arcs();
      int arcsCount = arcs.length;
      int placesCount = places.length;
      
      for (int i = 0; i < placesCount ; i++) {
         boolean hasTimed = false;
         boolean hasUntimed = false;
         //get arcs with places[i] as source
         for (int j = 0; j < arcsCount; j++) {
            if (arcs[j].getSource()==places[i]){
               Connectable targ = arcs[j].getTarget();
               if (((Transition)targ).isTime() == true) {
                  hasTimed = true;
               } else {
                  hasUntimed = true;
               }
            }
            if (hasTimed == true && hasUntimed == true) {
               return false;
            }
         }
      }
      return true;
   }*/

   
   // This method is never called   
   /**
    * See if the supplied net has any timed transitions.
    * @param DataLayer
    * @return boolean
    * @author Matthew
    *
    */ /*
   protected boolean hasImmediateTransitions(PetriNet _pnmlData){
      Transition[] transitions = _pnmlData.getTransitions();
      int transCount = transitions.length;
      
      for (int i = 0; i< transCount; i++) {
         if (transitions[i].isTime() == false){
            return true;
         }
      }
      return false;
   } */
   
   
   // This method is never called   
   /**This function performs a Gaussian reduction on a given Matrix, returning 
    * an array of values representing the solution.
    * @param Matrix - the matrix of coefficients to be solved
    * @return double[] - the array of solutions
    */ /*
   protected double[] reduction(Matrix input) {
      int row = input.getRowDimension();
      int col = input.getColumnDimension();
      double[] result = new double[col-1];
      
      //initialise results to 0 - have n-1 unknowns in n equations, so result 
      //can be 1 less than size of input matrix.
      for (int i = 0; i<col-1; i++){
         result[i] = 0;
      }
      //***********************************************************
      //First stage - reduce matrix of coefficients by substitution
      //***********************************************************
      boolean reducedThisRow = false;
      
      //Start - first row should have 1 as each coefficient.  
      //Test if second row has 0 as coeffiecient - if so, move on and swap.
      for (int i = 0; i < row - 1; i++) {
         for (int j = i + 1; j <row; j++) {
            if ((input.get(j, i) == 0.0) && (reducedThisRow == false)) { 
               //if the element is 0 and we haven't already reduced a row, 
               // search down the list till we find one, then swap it into the 
               // current position
               int k = j;
               while ((input.get(k,i) == 0.0) && (k < row - 1 )) {
                  k++;
               }
               if (k < row) {
                  swapRows(input, j, k);
               } else {
                  throw new ArithmeticException(
                          "Not enough parameters to calculate result");
               }
            } else if (input.get(j,i) != 0.0) {					
               //reduce the row coeffecients by arithmetic substitution.
               double factor = ((input.get(i, i))/(input.get(j, i)));
//               System.out.println(factor + " Factor");
//               System.out.println(input.get(i,i) +" input.get(i,i)");
//               System.out.println(input.get(j,i) +" input.get(i,j)");
               multiplyRow(input,j,factor);
               subtractRow(input,i,j);
//               input.print(8,5);      //if the coefficient is 0 and we've already performed a reduction in this pass
               reducedThisRow = true;	//take no action and move onto the next
            }
         }
         reducedThisRow = false;
      }
      //************************************
      //next stage - backwards substitution.
      //************************************
      for (int i = row - 2; i >= 0; i--) {
         double backSub = 0;
         for (int j = i+1; j < row-1; j++) {
            backSub = backSub + (result[j]*input.get(i,j));
         }
         result[i] = (input.get(i,row - 1) - backSub)/input.get(i,i);
      }
      return result;
   }*/
   
   
   /*
   //Helper function for reduction function
   private void swadpRows(Matrix input, int row1, int row2) {
      int col = input.getColumnDimension();
      double temp;
      
      for (int i = 0; i < col; i++) {
         temp = input.get(row1,i);
         input.set(row1,i,input.get(row2,i));
         input.set(row2,i,temp);
      }
   }*/
   
   
   // This method is never called   
   /*
   //Helper function for reduction function
   private void multiplyRow(Matrix input, int row, double factor) {
      int col = input.getColumnDimension();
      double newVal;
      
      for (int i = 0; i <col ; i++){
         newVal = (input.get(row, i)) * factor;
         input.set(row, i, newVal);
      }
   }*/
   
   
   // This method is never called   
   /*
   //subtract the values of row1 from the values of row2
   private void subtractRow(Matrix input, int row1, int row2) {
      int col = input.getColumnDimension();
      double r1, r2;
      
      for (int i = 0; i < col; i++) {
         r1 = input.get(row1, i);
         r2 = input.get(row2, i);
         //System.out.println(r1 +" r1 " + r2 + " r2 " + i + " i ");
         input.set(row2, i, (r2 - r1));
      }
   }*/
   
   
   // This method is never called   
   /*
   private void printMatrix(double[][] input) {
      int rows = input.length;
      int cols = input[0].length;
      
      System.out.println("Printing a matrix of " + rows + " rows and " +
              cols +" columns.");
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < cols; j++) {
            System.out.print(input[i][j] + " ");
         }
         System.out.println("");
      }
   }*/
   
   
   // This method is never called   
   /*
   private void printMatrix(int[][] input) {
      int rows = input.length;
      int cols = input[0].length;
      
      System.out.println("Printing a matrix of " + rows + " rows and " +
              cols + " columns.");
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < cols; j++) {
            System.out.print(input[i][j] + " ");
         }
         System.out.println("");
      }
   }*/
   
   
   // This method is never called   
   /*
   private void printMarking(int[] marking) {
      int rows = marking.length;
      
      System.out.print("Marking as follows: ");
      for (int i = 0; i < rows; i++) {
         System.out.print(marking[i] + " ");
      }
      System.out.println();
   }*/
   
   
   // This method is never called   
   /*
   //Format StateList data nicely.
   protected String renderStateSpace(PetriNet _pnmlData, StateList data) {
      if ((data.size() == 0) || (data.get(0).length == 0)) {
         return "n/a";
      }
      int markSize = data.get(0).length;
      ArrayList result=new ArrayList();
      // add headers to table
      result.add("");

      Place[] places = _pnmlData.places();
      for (int i = 0; i < markSize; i++) {
         result.add(places[i].getName());
         //result.add("<A NAME= 'M" + i + "'></A>");
      }
      
      for (int i = 0; i < data.size(); i++) {
         result.add(data.getID(i));
         for (int j = 0; j < markSize; j++) {
            result.add(Integer.toString(data.get(i)[j]));
         }
      }
      return ResultsHTMLPane.makeTable(result.toArray(), markSize + 1, false,
              true, true, true);
   }*/
      
   
   // This method is never called   
   /*
   //Format throughput data nicely.
   protected String renderThroughput(PetriNet _pnmlData, double[] data) {
      if ((data.length) == 0) {
         return "n/a";
      }
      int transCount = data.length;
      ArrayList result=new ArrayList();
      // add headers to table
      result.add("Transition");
      result.add("Throughput");
      DecimalFormat f = new DecimalFormat();
      f.setMaximumFractionDigits(5);
      Transition[] transitions = _pnmlData.getTransitions();
      for (int i = 0; i < transCount; i++) {
         result.add(transitions[i].getName());
         result.add(f.format(data[i]));
      }
      return ResultsHTMLPane.makeTable(
              result.toArray(), 2, false, true, true, true);
   }*/
   
   /*
   //Format probability matrices nicely
   protected String renderProbabilities (double[][] probabilities, 
           StateList list1, StateList list2) {
      if ((list1.size() == 0) || (list2.get(0).length == 0)) {
         return "n/a";
      }
      int rows = list1.size();
      int cols = list2.size();
      ArrayList result = new ArrayList();
      // add headers to table
      result.add("");
      for (int i = 0; i < cols; i++) {
         result.add("<A HREF='#M" + i + "'>" + list2.getID(i)+ "</A>");
      }
      
      DecimalFormat f = new DecimalFormat();
      f.setMaximumFractionDigits(5);
      for (int i = 0; i < rows; i++) {
         result.add("<A HREF='#M" + i + "'>" + list1.getID(i)+ "</A>");
         for (int j = 0; j < cols; j++) {
            result.add(f.format(probabilities[i][j]));
         }
      }
      return ResultsHTMLPane.makeTable(
              result.toArray(),cols + 1, false, true, true, true);
   }*/

   
}
