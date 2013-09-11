/*
 * PIPELoopWithTextEdgePainter.java
 */
package pipe.extensions.jpowergraph;

import java.awt.geom.Point2D;

import net.sourceforge.jpowergraph.SubGraphHighlighter;
import net.sourceforge.jpowergraph.defaults.LoopEdge;
import net.sourceforge.jpowergraph.painters.edge.LoopEdgePainter;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphRectangle;


//REMARK: this class extends a jpowergraph's class which is LGPL


/**
 * This class defines the edge painter for edges with text used in PIPE.
 * @author Pere Bonet
 */
public class PIPELoopWithTextEdgePainter <T extends LoopEdge> 
        extends LoopEdgePainter <T> {
   
   /** Square root of 3 over 6. */
   protected static final double SQUARE_ROOT_OF_3_OVER_2=0.866;
   /** The width of the base of the arrow. */
   protected static final double ARROW_BASE_LENGTH=11.0;
   private static final int CIRCULAR = 0;
   public static final int RECTANGULAR = 1;

    private int widthpad = 15;
   private int heightpad = 15;

   
   /**
    * Creates a new instance of PIPETangibleState
    * @param _label    the node id.
    * @param _marking  the marking
    * @param dragging
    * @param normal
    * @param theShape
    */   
   public PIPELoopWithTextEdgePainter(JPowerGraphColor dragging, 
           JPowerGraphColor normal, int theShape) {
      super(new JPowerGraphColor(197, 197, 197), dragging, normal, theShape);
   }

   
   // 
   public void paintEdge(JGraphPane graphPane, JPowerGraphGraphics g, T edge, 
           SubGraphHighlighter theSubGraphHighlighter) {     
      
      JPowerGraphRectangle r = new JPowerGraphRectangle(0, 0, 0, 0);
      getEdgeScreenBounds(graphPane, edge, r);
      
      JPowerGraphColor oldFGColor = g.getForeground();
      JPowerGraphColor oldBGColor = g.getBackground();
      g.setForeground(getEdgeColor(edge,graphPane, false, theSubGraphHighlighter));
      g.setBackground(getEdgeColor(edge,graphPane, false, theSubGraphHighlighter));
       int shape = CIRCULAR;
       paintArrow(g, r.x, r.y, r.width, r.height, shape);

      String text = "" ;
      if (edge instanceof PIPELoopWithTextEdge) {
         text = ((PIPELoopWithTextEdge)edge).getText();
      }
      
      Point2D midpoint = new Point2D.Double((r.x + r.width)/2,
              ((r.y + r.height)/2));
      
      midpoint = new Point2D.Double(r.x,  r.y);
      
      double slopeTop = r.y - r.height;
      double slopeBottom = r.x - r.width;
      double slope = Double.POSITIVE_INFINITY;
      if (slopeBottom != 0) {
         slope = slopeTop / slopeBottom;
      }
      
      int stringWidth = g.getStringWidth(text);
      
      if (slope > 2.0) {
         double xDiff = - (stringWidth + 5); //+ 10
         double yDiff = -5; //-10
         if (r.y < r.height) {
            yDiff = -2; //-5
         }
         midpoint.setLocation(midpoint.getX() + xDiff, midpoint.getY() + yDiff);
      } else if (slope < -2.0) {
         double xDiff = 5; //+10
         double yDiff = -5; // -10
         if (r.y < r.height) {
            yDiff = -2; // -5
         }
         midpoint.setLocation(midpoint.getX() + xDiff, midpoint.getY() + yDiff);
      } else if (slope > 0.4 || slope < -0.4) {
         double xDiff = 5; // + 10
         double yDiff = -5; // -10
         if (r.x > r.width) {
            xDiff = -(stringWidth + 5); // + 10
         }
         if (r.y < r.height) {
            yDiff = -2; // -5
         }
         midpoint.setLocation(midpoint.getX() + xDiff, midpoint.getY() + yDiff);
      } else{
         double xDiff = - stringWidth/3; // /2
         double yDiff = -20; //-20
         midpoint.setLocation(midpoint.getX() + xDiff, midpoint.getY() + yDiff);
      }
      
      g.drawString(text, (int) midpoint.getX(), (int) midpoint.getY(), 1);
      g.setForeground(oldFGColor);
      g.setBackground(oldBGColor);
   }
           
}
