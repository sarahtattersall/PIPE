/*
 * MyNode.java
 *
 * Created on September 28, 2007, 10:37 AM
 */

package pipe.extensions.jpowergraph;

import net.sourceforge.jpowergraph.painters.node.ShapeNodePainter;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;

/**
 * This class defines a node used in the reachability graph to represent a 
 * Vanishing state
 * @author Pere Bonet
 */
public class PIPEVanishingState 
        extends PIPENode{
   
   // light_blue
   static final JPowerGraphColor bgColor = new JPowerGraphColor(182, 220, 255);
   
   private static final ShapeNodePainter shapeNodePainter = new ShapeNodePainter(
           ShapeNodePainter.ELLIPSE, bgColor, JPowerGraphColor.LIGHT_GRAY,
           fgColor);
   
   /**
    * Creates a new instance of PIPEVanishingState
    * @param _label    the node id.
    * @param _marking  the marking
    * @param label
    * @param marking
    */   
   public PIPEVanishingState(String label, String marking){
      super(label, marking);
   }
   
   
   public static ShapeNodePainter getShapeNodePainter(){
      return shapeNodePainter;
   }
   
   
   public String getNodeType(){
      return "Vanishing State";
   }
   
}
