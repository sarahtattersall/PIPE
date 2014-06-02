/*
 * Created on 10-Feb-2004
 */
package pipe.gui;

import pipe.constants.GUIConstants;

import java.awt.*;
import java.awt.geom.GeneralPath;

/**
 * This class represents a grid that will draw itself on an object
 */
public class Grid {
   
   private float gridSpacing = GUIConstants.PLACE_TRANSITION_HEIGHT/2;
   private final Color gridColor = new Color(240,240,255);
   private GeneralPath gridDisplay;
   private boolean enabled = true;
   private int gridHeight, gridWidth;
   private int gridCount = 1;
   
   
   private void createGrid() {
      gridDisplay = new GeneralPath();
      
      for (float i = gridSpacing; i <= gridWidth; i += gridSpacing) {
         gridDisplay.moveTo(i,2);
         gridDisplay.lineTo(i,gridHeight);
      }
      for (float i = gridSpacing; i <= gridHeight; i += gridSpacing) {
         gridDisplay.moveTo(2,i);
         gridDisplay.lineTo(gridWidth,i);
      }
   }
   
   
   public void enableGrid() {
      enabled = true;
   }
   
   
   public void increment() {
      gridCount++;
      gridCount %= 4;
      
      if (gridCount == 3) {
         disableGrid();
      } else {
         enableGrid();
         setGridSpacing(Math.pow(2, gridCount - 2));
      }
   }
   
   
   private void setGridSpacing(double spacing) {
      gridSpacing = (float)(spacing * GUIConstants.PLACE_TRANSITION_HEIGHT);
   }
   
   
   public void disableGrid() {
      if (enabled) {
         enabled = false;
      }
   }
   
   
   public boolean isEnabled() {
      return enabled;
   }
   
   
   public void updateSize(Container parent) {
      if (enabled) {
         gridHeight = parent.getHeight();
         gridWidth = parent.getWidth();
         createGrid();
      }
   }
   
   
   public void drawGrid(Graphics g) {
      Graphics2D g2d = (Graphics2D) g;

      g2d.setPaint(gridColor);
      g2d.draw(gridDisplay);
   }
   
   
   public int getModifiedValue(double value) {
      if (!enabled) {
         return (int)value;
      }
      return (int)(Math.round(value / gridSpacing) * gridSpacing);
      
   }
}
