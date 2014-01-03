/*
 * SplitArcPointAction.java
 *
 * Created on 21-Jun-2005
 */
package pipe.actions;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.views.viewComponents.ArcPathPoint;

import java.awt.event.ActionEvent;

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
       PipeApplicationController controller = ApplicationSettings.getApplicationController();
       PetriNetController petriNetController = controller.getActivePetriNetController();
       petriNetController.getHistoryManager().addNewEdit(arcPathPoint.splitPoint());
   }
   
}
