/*
 * PIPEInitialState.java
 */

package pipe.extensions.jpowergraph;

import net.sourceforge.jpowergraph.painters.node.ShapeNodePainter;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;


//REMARK: this class extends a jpowergraph's class which is LGPL

/**
 * The node that represents the initial marking in the reachability graph.
 * @author Pere Bonet
 */
public class PIPEInitialState 
        extends PIPEState {
   
   // light_red
   private static final JPowerGraphColor bgColor = new JPowerGraphColor(255, 102, 102);
   
   // black
   private static final JPowerGraphColor fgColor = new JPowerGraphColor(0, 0, 0);
   
   // a rectangle 
   private static final ShapeNodePainter shapeNodePainter = new ShapeNodePainter(
           ShapeNodePainter.RECTANGLE, bgColor, JPowerGraphColor.LIGHT_GRAY, 
           fgColor);   
   
   /**
    * Creates the initial state node.
    * @param label    the node id.
    * @param marking  the marking
    */   
   public PIPEInitialState(String label, String marking){
      super(label, marking);   
   }
   

   public static ShapeNodePainter getShapeNodePainter(){
      return shapeNodePainter;
   }
   
   
   public String getNodeType(){
      return "Initial State";
   }      
   
}
