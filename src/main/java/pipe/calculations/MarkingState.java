/*
 * Created on Mar 2, 2004
 */
package pipe.calculations;


/**
 * @author unespecified
 *
 * @author Nadeem
 * This class modified to make use of the new State class (01/07/2005)
 *
 * @author Matthew Worthington/Edwin Chung added a new attribute to include the 
 * information whether the state is tangible or vanishing. Appropriate 
 * constructors and methods are added/modified to check the equivalance of 
 * two markings.
 */
public class MarkingState 
        extends State {
   
   private int idnum;
   public static boolean isTangible;
   
   
   public MarkingState(State markingInput, int idInput) {
      super(markingInput);
      idnum = idInput;
   }
   
   public MarkingState(State markingInput, int idInput, boolean Tangible){
      super(markingInput);
      idnum = idInput;
      isTangible = Tangible;
   }
   
   
   public MarkingState(int[] markingInput, int idInput){
      super(markingInput);
      idnum = idInput;
   }
   
   
   public MarkingState(int[] markingInput, String idInput) {
      super(markingInput);
   }
   
   
   public int[] getMarking(){
      return getState();
   }
   
   
   public String getID(){
      return "M" + idnum;
   }
   
   
   public int getIDNum(){
      return idnum;
   }
   

   public boolean getIsTangible(){
      return isTangible;
   }
   
   
   public boolean equals (MarkingState m1){
      return (this.equals((State)m1) && (isTangible == isTangible));
   }   
   
}
