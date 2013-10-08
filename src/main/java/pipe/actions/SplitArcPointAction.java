/*
 * SplitArcPointAction.java
 *
 * Created on 21-Jun-2005
 */
package pipe.actions;

import java.awt.event.ActionEvent;

import pipe.gui.ApplicationSettings;
import pipe.views.viewComponents.ArcPathPoint;

/**
 * @author Nadeem
 *
 * This class is used to split a point on an arc into two to  allow the arc to 
 * be manipulated further.
 */
public class SplitArcPointAction 
        extends javax.swing.AbstractAction {
   
   private final ArcPathPoint arcPathPoint;
   
   
   public SplitArcPointAction(ArcPathPoint _arcPathPoint) {
      arcPathPoint = _arcPathPoint;
   }
   
   
   public void actionPerformed(ActionEvent e) {
       ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager().addNewEdit(
               arcPathPoint.splitPoint());
   }
   
}
