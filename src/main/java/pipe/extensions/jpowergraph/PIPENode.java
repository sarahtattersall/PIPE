/*
 * PIPENode.java
 */

package pipe.extensions.jpowergraph;

import net.sourceforge.jpowergraph.defaults.DefaultNode;
import net.sourceforge.jpowergraph.painters.node.ShapeNodePainter;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;


/**
 * This class defines the default node for PIPE
 * @author Pere Bonet
 */
public abstract class PIPENode 
        extends DefaultNode {
   
   // the state id, used in the graph's legend
   private String label = "";
   // the node id, used in the graph's legend
   private String marking = "";
   
   // gray
   private static final JPowerGraphColor bgColor = new JPowerGraphColor(128, 128, 128);
   // black
   static final JPowerGraphColor fgColor = new JPowerGraphColor(0, 0, 0);
   
   // the ShapeNodePainter for this node
   private static final ShapeNodePainter shapeNodePainter = new ShapeNodePainter(
           ShapeNodePainter.ELLIPSE, bgColor, bgColor, fgColor);
   
   /**
    * Creates a new node instance.
    * @param _label    the node id.
    * @param _marking  the marking
    */
   PIPENode(String _label, String _marking){
      this.label = _label;
      this.marking = _marking;
   }
   
   
   public String getLabel() {
      return label;
   }
   
   
   public String getNodeType(){
      return "PIPENode";
   }
   
   
   public String getMarking(){
      return marking;
   }
   
   
   public static ShapeNodePainter getShapeNodePainter(){
      return shapeNodePainter;
   }
   
}
