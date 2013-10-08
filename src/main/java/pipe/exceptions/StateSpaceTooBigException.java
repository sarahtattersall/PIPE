/*
 * Created on 26-Jul-2005
 */
package pipe.exceptions;


/**
 * @author Nadeem
 */
public class StateSpaceTooBigException 
        extends Exception {

   
   public StateSpaceTooBigException() {
      super("The entire state space is too large to be held in memory.");
   }
   
   
   public StateSpaceTooBigException(int n) {
      super("There are greater than " + n +
               "tangible states. There is insufficient memory to hold them all.");
   }
   
}
