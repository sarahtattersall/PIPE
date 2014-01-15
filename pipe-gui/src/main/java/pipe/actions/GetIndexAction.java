/*
 * Created on 18-Jul-2005
 */
package pipe.actions;

import pipe.views.viewComponents.ArcPathPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * @author Nadeem
 */
class GetIndexAction
        extends AbstractAction {
   
   private final ArcPathPoint selected;
   private final Point mp;
   
   
   public GetIndexAction(ArcPathPoint component, Point mousepos) {
      selected = component;
      mp = mousepos;
   }
   
   
   public void actionPerformed(ActionEvent arg0) {
      System.out.println("Index is: " + selected.getIndex());
      System.out.println("At position: " + selected.getPoint().getX() + ", " +
                         selected.getPoint().getY());
      System.out.println("Mousepos: " + mp.x + ", " + mp.y);
   }
   
}
