/*
 * PIPEInitialTangibleState.java
 */

package pipe.extensions.jpowergraph;

import net.sourceforge.jpowergraph.painters.node.ShapeNodePainter;


//REMARK: this class extends a jpowergraph's class which is LGPL

/**
 * The node that represents the initial state when it is a tangible state in 
 * the reachability graph.
 * @author Pere Bonet
 */
public class PIPEInitialTangibleState 
        extends PIPETangibleState {
   
    
   private static final ShapeNodePainter shapeNodePainter = new ShapeNodePainter(
           ShapeNodePainter.RECTANGLE, bgColor, bgColor, fgColor);   
   
   
   public PIPEInitialTangibleState(String label, String marking){
      super(label, marking);
   }

   
   public static ShapeNodePainter getShapeNodePainter(){
      return shapeNodePainter;
   }
   
   
   public String getNodeType(){
      return "Tangible State (Initial State)";
   }   

}
