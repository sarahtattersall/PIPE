/*
 * PIPEInitialVanishingState.java
 */

package pipe.extensions.jpowergraph;

import net.sourceforge.jpowergraph.painters.node.ShapeNodePainter;


//REMARK: this class extends a jpowergraph's class which is LGPL

/**
 * The node that represents the initial state when it is a vanishing state in 
 * the reachability graph.
 * @author Pere Bonet
 */
public class PIPEInitialVanishingState 
        extends PIPEVanishingState {
    
   private static final ShapeNodePainter shapeNodePainter = new ShapeNodePainter(
           ShapeNodePainter.RECTANGLE, bgColor, bgColor, fgColor);   
   
   
   public PIPEInitialVanishingState(String label, String marking) {
      super(label, marking);   
   }

   
   public static ShapeNodePainter getShapeNodePainter() {
      return shapeNodePainter;
    }
   
   
   public String getNodeType() {
      return "Vanishing State (Initial State)";
    }      
   
}
