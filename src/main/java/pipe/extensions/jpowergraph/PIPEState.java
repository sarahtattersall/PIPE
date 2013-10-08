/*
 * PIPEState.java
 */

package pipe.extensions.jpowergraph;

import net.sourceforge.jpowergraph.painters.node.ShapeNodePainter;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;


//REMARK: this class extends a jpowergraph's class which is LGPL


/**
 * This class defines the node used when coverability graph is generated
 * @author Pere Bonet
 */
public class PIPEState 
        extends PIPENode {
   
   // light_red
   private static final JPowerGraphColor bgColor = new JPowerGraphColor(255, 102, 102);
   // black
   private static final JPowerGraphColor fgColor = JPowerGraphColor.BLACK;
    
   private static final ShapeNodePainter shapeNodePainter = new ShapeNodePainter(
           ShapeNodePainter.ELLIPSE, bgColor, JPowerGraphColor.LIGHT_GRAY,
           fgColor);   

   /**
    * Creates the initial state node.
    * @param label    the node id.
    * @param marking  the marking
    */   
   public PIPEState(String label, String marking){
      super(label, marking);
   }

   
   public static ShapeNodePainter getShapeNodePainter(){
      return shapeNodePainter;
   }
   
   
   public String getNodeType(){
      return "State";
   }   

}
