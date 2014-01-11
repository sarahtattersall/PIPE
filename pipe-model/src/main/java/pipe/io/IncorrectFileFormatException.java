/*
 * Created on 25-Jul-2005
 */
package pipe.io;

import java.io.IOException;


/**
 * @author Nadeem
 */
public class IncorrectFileFormatException 
        extends IOException {
   
   
   public IncorrectFileFormatException(String format) {
      super("The specified file is not an " + format + ".");
   }
   
}
