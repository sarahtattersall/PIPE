/*
 * GraphPanel.java
 *
 * Created on 05-Mar-2004
 */
package pipe.gui.widgets;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.border.BevelBorder;


/**
 * @author pk903
 */
public class GraphPanel 
        extends JPanel {
   
   private ArrayList xValues = new ArrayList();
   private ArrayList yValues = new ArrayList();
   private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
   private final static double GraphLeft = 0.1;
   private final static double GraphRight = 0.9;
   private final static double GraphTop = 0.1;
   private final static double GraphBottom = 0.9;
   private final static double GraphPointSize = 1;
   
   
   public GraphPanel(ArrayList x, ArrayList y) {
      this();
      xValues = x;
      yValues = y;
   }
   
   
   public GraphPanel() {
      super(new BorderLayout());
      setBackground(Color.white);
      setBorder(new BevelBorder(BevelBorder.LOWERED));
   }
   
   
   public void setValues(ArrayList x, ArrayList y) {
      xValues = x;
      yValues = y;
      repaint();
   }
   
   
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      Graphics2D g2 = (Graphics2D) g;
      updateGraph(g2);
   }
   
   
   private void updateGraph(Graphics2D g2) {
      calculateRange();
      drawAxis(g2);
      drawPoints(g2);
   }
   
   
   private void calculateRange() {
      xMax = ((Double)xValues.get(0)).doubleValue();
      yMax = ((Double)yValues.get(0)).doubleValue();
      xMin = 0;
      yMin = 0;
      
      Iterator valueIterator = xValues.iterator();
      while (valueIterator.hasNext()) {
         double currentX = ((Double)valueIterator.next()).doubleValue();
         if (xMin > currentX) {
            xMin = currentX;
         }
         if (xMax < currentX) {
            xMax = currentX;
         }
      }
      
      valueIterator = yValues.iterator();
      while (valueIterator.hasNext()) {
         double currentY = ((Double)valueIterator.next()).doubleValue();
         if (yMin > currentY) {
            yMin = currentY;
         }
         if (yMax < currentY) {
            yMax = currentY;
         }
      }
   }
   
   
   private void drawAxis(Graphics2D g2) {
      Line2D.Double xAxis = 
              new Line2D.Double(getTranslated(xMin,0), getTranslated(xMax,0));
      Line2D.Double yAxis = 
              new Line2D.Double(getTranslated(0,yMin), getTranslated(0,yMax));
      g2.draw(xAxis);
      g2.draw(yAxis);
      g2.drawString("" + xMax, (float)getTranslated(xMax, 0).getX(),
                    (float)getTranslated(xMax, 0).getY());
      g2.drawString("" + yMax, (float)getTranslated(0, yMax).getX(), 
                    (float)getTranslated(0,yMax).getY());
      
   }
   
   
   private void drawPoints(Graphics2D g2) {
      Point2D.Double currentPoint = new Point2D.Double();
      for (int i=0; i < xValues.size(); i++) {
         currentPoint.setLocation(getTranslated(
                  ((Double)xValues.get(i)).doubleValue(),
                  ((Double)yValues.get(i)).doubleValue()));
         g2.draw(getPointPlot(currentPoint));
      }
   }
   
   
   private Point2D getTranslated(double x, double y) {
      Dimension windowDimension = this.getSize();
      double leftOut = windowDimension.width * GraphLeft;
      double rightOut = windowDimension.width * GraphRight;
      double topOut = windowDimension.height * GraphTop;
      double bottomOut = windowDimension.height * GraphBottom;
      double xRange = xMax - xMin;
      double yRange = yMax - yMin;
      double xOut = ((x - xMin)/xRange) * (rightOut - leftOut) + leftOut;
      double yOut = bottomOut - ((y - yMin) / yRange) * (bottomOut - topOut);
      return new Point2D.Double(xOut, yOut);
   }
   
   
   private Shape getPointPlot(Point2D.Double point) {
      return new Rectangle2D.Double(point.x - GraphPointSize, 
                                    point.y - GraphPointSize,
                                    GraphPointSize * 2, 
                                    GraphPointSize * 2);
   }
   
}
