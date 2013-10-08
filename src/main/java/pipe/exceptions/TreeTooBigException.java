/*
 * Created on Mar 11, 2004
 *
 */
package pipe.exceptions;

import pipe.gui.Constants;


/**
 * @author Matthew
 */
public class TreeTooBigException 
        extends Exception {
   
   
   public TreeTooBigException() {
      //super("The state-space tree for this net has more than " + Constants.MAX_NODES +
      //       " nodes DNAMACA might be a more appropriate tool for this analysis");
      super("The state-space tree for this net is too big, DNAMACA might be " +
              "a more appropriate tool for this analysis");      
   }
   
}
