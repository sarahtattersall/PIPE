/*
 * Created on 04-Mar-2004
 * Author is Michael Camacho
 *
 */
package pipe.actions.petrinet;

import pipe.controllers.ArcController;
import uk.ac.imperial.pipe.models.component.Connectable;
import uk.ac.imperial.pipe.models.component.arc.ArcPoint;

import java.awt.event.ActionEvent;


public class ToggleArcPointAction
        extends javax.swing.AbstractAction {

    private final ArcPoint arcPoint;
    private final ArcController<? extends Connectable, ? extends Connectable> arcController;


    public ToggleArcPointAction(ArcPoint _arcPathPoint, ArcController<? extends Connectable, ? extends Connectable> arcController) {
        arcPoint = _arcPathPoint;
        this.arcController = arcController;
    }

    /**
     *
     * Toggles the point type
     *
     * @param e event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        arcController.toggleArcPointType(arcPoint);
    }

}
