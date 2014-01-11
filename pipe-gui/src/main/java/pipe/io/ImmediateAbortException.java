/*
 * Created on 15-Jul-2005
 */
package pipe.io;


/**
 * @author Nadeem
 */
public class ImmediateAbortException
        extends Exception {
   
   
   public ImmediateAbortException() {
      super("Generate method could not carry out file io.");
   }
   
   
   public ImmediateAbortException(String reason) {
      super(reason);
   }
   
}
