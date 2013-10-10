/*
 * PIPELineWithTextEdgePainter.java
 */

package pipe.extensions.jpowergraph;

import net.sourceforge.jpowergraph.SubGraphHighlighter;
import net.sourceforge.jpowergraph.defaults.TextEdge;
import net.sourceforge.jpowergraph.painters.edge.LineWithTextEdgePainter;
import net.sourceforge.jpowergraph.pane.JGraphPane;
import net.sourceforge.jpowergraph.swtswinginteraction.JPowerGraphGraphics;
import net.sourceforge.jpowergraph.swtswinginteraction.color.JPowerGraphColor;
import net.sourceforge.jpowergraph.swtswinginteraction.geometry.JPowerGraphPoint;

import java.awt.geom.Point2D;


//REMARK: this class extends a jpowergraph's class which is LGPL

/**
 * This class defines the edge painter for edges with text used in PIPE
 * Change:
 * - the arrow placement
 * @author Pere Bonet
 */
public class PIPELineWithTextEdgePainter <T extends TextEdge>
        extends LineWithTextEdgePainter <T> {
   
   
   public PIPELineWithTextEdgePainter(JPowerGraphColor dragging,
           JPowerGraphColor normal, boolean isDashedLine) {
      super(dragging, normal, isDashedLine);
   }
   
   
   public void paintEdge(JGraphPane graphPane, JPowerGraphGraphics g, T edge,
           SubGraphHighlighter theSubGraphHighlighter) {
      
      JPowerGraphPoint from = graphPane.getScreenPointForNode(edge.getFrom());
      JPowerGraphPoint to = graphPane.getScreenPointForNode(edge.getTo());
      JPowerGraphColor oldFGColor = g.getForeground();
      JPowerGraphColor oldBGColor = g.getBackground();
      
      g.setForeground(
              getEdgeColor(edge, graphPane, false, theSubGraphHighlighter));
      g.setBackground(
              getEdgeColor(edge, graphPane, false, theSubGraphHighlighter));
      paintArrow(g, from.x, from.y, to.x, to.y, false);
      g.setForeground(oldFGColor);
      g.setBackground(oldBGColor);
      
      String text = edge.getText();
      
      Point2D midpoint = new Point2D.Double((from.x + 2*to.x)/3,
              ((from.y + 2*to.y)/3));
      
      double slopeTop = from.y - to.y;
      double slopeBottom = from.x - to.x;
      double slope = Double.POSITIVE_INFINITY;
      if (slopeBottom != 0) {
         slope = slopeTop / slopeBottom;
      }
      
      int stringWidth = g.getStringWidth(text);
      
      if (slope > 2.0) {
         double xDiff = - (stringWidth + 10);
         double yDiff = -10;
         if (from.y < to.y) {
            yDiff = -5;
         }
         midpoint.setLocation(midpoint.getX() + xDiff, midpoint.getY() + yDiff);
      } else if (slope < -2.0) {
         double xDiff = 10;
         double yDiff = -10;
         if (from.y < to.y) {
            yDiff = -5;
         }
         midpoint.setLocation(midpoint.getX() + xDiff, midpoint.getY() + yDiff);
      } else if (slope > 0.4 || slope < -0.4) {
         double xDiff = 10;
         double yDiff = -10;
         if (from.x > to.x) {
            xDiff = -(stringWidth + 10);
         }
         if (from.y < to.y) {
            yDiff = -5;
         }
         midpoint.setLocation(midpoint.getX() + xDiff, midpoint.getY() + yDiff);
      } else {
         double xDiff = - stringWidth/2;
         double yDiff = -20;
         midpoint.setLocation(midpoint.getX() + xDiff, midpoint.getY() + yDiff);
      }
      
      g.setForeground(
              getEdgeColor(edge, graphPane, false, theSubGraphHighlighter));
      g.drawString(text, (int) midpoint.getX(), (int) midpoint.getY(), 1);
      g.setForeground(oldFGColor);
   }
   
   
   public static void paintArrow(JPowerGraphGraphics g, int x1, int y1, int x2,
                                  int y2, boolean isDashedLine) {
      double middleX = (x1 + 2*x2)/3;
      double middleY = (y1 + 2*y2)/3;
      
      double slope=Math.atan2(y2-y1,x2-x1);
      
      double pinnacleX = middleX + 
              SQUARE_ROOT_OF_3_OVER_2 * ARROW_BASE_LENGTH * Math.cos(slope);
      double pinnacleY = middleY + 
              SQUARE_ROOT_OF_3_OVER_2 * ARROW_BASE_LENGTH * Math.sin(slope);
      double backwardX = pinnacleX + 
              ARROW_BASE_LENGTH * Math.cos(slope + Math.PI + Math.PI/6.0);
      double backwardY = pinnacleY + 
              ARROW_BASE_LENGTH * Math.sin(slope + Math.PI + Math.PI/6.0);
      double forwardX = pinnacleX + 
              ARROW_BASE_LENGTH * Math.cos(slope + Math.PI - Math.PI/6.0);
      double forwardY = pinnacleY + 
              ARROW_BASE_LENGTH * Math.sin(slope + Math.PI - Math.PI/6.0);
      double baseX = (forwardX + backwardX)/2.0;
      double baseY =( forwardY + backwardY)/2.0;
      
      g.setLineDashed(isDashedLine);
      g.drawLine(x1, y1, (int)baseX, (int)baseY);
      g.drawLine((int)pinnacleX, (int)pinnacleY, x2, y2);
      g.setLineDashed(false);
      g.fillPolygon(new int[]{(int)backwardX, (int)backwardY, (int)pinnacleX, 
              (int)pinnacleY, (int)forwardX, (int)forwardY});
   }
   
}
