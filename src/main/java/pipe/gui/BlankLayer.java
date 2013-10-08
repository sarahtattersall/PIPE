/*
 * BlankLayer.java
 */

package pipe.gui;

import javax.swing.*;
import java.awt.*;

/**
 * This class must be removed after zoom functionality is improved!!!
 */
public class BlankLayer extends JComponent {
   
   private static final Color gridColor = new Color(255,255,255);

   
   public BlankLayer(){
   }
   
   
   public void paint(Graphics g) {
      super.paint(g);
      Graphics2D g2d = (Graphics2D) g;
      g2d.setPaint(gridColor);
      g2d.fillRect(0,0, 1000, 1000);      
      ApplicationSettings.getApplicationView().hideNet(false);
   }
   
}
