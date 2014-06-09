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

    /**
     * Arc point
     */
    private final ArcPoint arcPoint;

    /**
     * Arc controller to add the arc point to
     */
    private final ArcController<? extends Connectable, ? extends Connectable> arcController;


    /**
     * Constructor
     * @param arcPoint new arc point
     * @param arcController arc controller to add the point to
     */
    public SplitArcPointAction(ArcPoint arcPoint,
                               ArcController<? extends Connectable, ? extends Connectable> arcController) {
        this.arcPoint = arcPoint;
        this.arcController = arcController;
    }

    /**
     * Adds the arc point as an intermediate point along the arc
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        arcController.splitArcPoint(arcPoint);
    }

}
