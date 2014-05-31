/*
 * SplitArcPointAction.java
 *
 * Created on 21-Jun-2005
 */
package pipe.actions.petrinet;

import pipe.controllers.ArcController;
import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;

import java.awt.event.ActionEvent;

/**
 * This class is used to split a point on an arc into two to  allow the arc to
 * be manipulated further.
 */
public class SplitArcPointAction extends javax.swing.AbstractAction {

    private final ArcPoint arcPoint;

    private final ArcController<? extends Connectable, ? extends Connectable> arcController;


    public SplitArcPointAction(ArcPoint arcPoint,
                               ArcController<? extends Connectable, ? extends Connectable> arcController) {
        this.arcPoint = arcPoint;
        this.arcController = arcController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        arcController.splitArcPoint(arcPoint);
    }

}
