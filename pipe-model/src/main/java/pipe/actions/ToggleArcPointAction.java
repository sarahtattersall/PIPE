/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 *
 */
package pipe.actions;

import pipe.controllers.ArcController;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.models.component.ArcPoint;

import java.awt.event.ActionEvent;


public class ToggleArcPointAction
        extends javax.swing.AbstractAction {

    private final ArcPoint arcPoint;
    private final ArcController arcController;


    public ToggleArcPointAction(ArcPoint _arcPathPoint, ArcController arcController) {
        arcPoint = _arcPathPoint;
        this.arcController = arcController;
    }

    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {

        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        PetriNetController petriNetController = controller.getActivePetriNetController();
        arcController.toggleArcPointType(arcPoint);
//        petriNetController.getHistoryManager().addNewEdit(arcPoint.togglePointType());
    }

}
