/*
 * Created on 25-Jul-2005
 */
package pipe.exceptions;

import java.io.IOException;


/**
 * @author Nadeem
 */
public class IncorrectFileFormatException 
        extends IOException {
   
   
   public IncorrectFileFormatException() {
      super("The specified file is not an " + "RG File" + ".");
   }
   
}
