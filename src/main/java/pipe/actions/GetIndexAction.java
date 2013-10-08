/*
 * Created on 18-Jul-2005
 */
package pipe.actions;

import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import pipe.views.viewComponents.ArcPathPoint;


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
      System.out.println("At position: " + selected.getPoint().x + ", " +
                         selected.getPoint().y);
      System.out.println("Mousepos: " + mp.x + ", " + mp.y);
   }
   
}
