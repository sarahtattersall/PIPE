/*
 * PIPELoopWithTextEdge.java
 */

package pipe.extensions.jpowergraph;

import net.sourceforge.jpowergraph.Node;
import net.sourceforge.jpowergraph.defaults.LoopEdge;


//REMARK: this class extends a jpowergraph's class which is LGPL

/**
 * This class defines the loop edges with text used in PIPE
 * @author Pere Bonet
 */
public class PIPELoopWithTextEdge
        extends LoopEdge {
   
   private final String text;
   
   
   /**
    * Creates a new instance of PIPELoopWithTextEdge
    * @param theNode
    * @param _text
    */
   public PIPELoopWithTextEdge(Node theNode, String _text) {
      super(theNode);
      text = _text;
   }
   
   public String getText(){
      return text;
   }
   
}
