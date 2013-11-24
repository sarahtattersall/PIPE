/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 *
 */
package pipe.actions;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.views.viewComponents.ArcPathPoint;

import java.awt.event.ActionEvent;


public class ToggleArcPointAction 
        extends javax.swing.AbstractAction {

   private final ArcPathPoint arcPathPoint;

   
   public ToggleArcPointAction(ArcPathPoint _arcPathPoint) {
      arcPathPoint = _arcPathPoint;
   }

   
   /* (non-Javadoc)
    * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
    */
   public void actionPerformed(ActionEvent e) {

       PipeApplicationController controller = ApplicationSettings.getApplicationController();
       PetriNetController petriNetController = controller.getActivePetriNetController();
       petriNetController.getHistoryManager().addNewEdit(arcPathPoint.togglePointType());
   }

}
