/*
 * SplitArcAction.java
 */
package pipe.actions.petrinet;

import pipe.controllers.ArcController;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

/**
 * This class is used to split an arc in two at the
 * point the user clicks the mouse button.
 *
 */
@SuppressWarnings("serial")
public class SplitArcAction
        extends javax.swing.AbstractAction {

    /**
     * Arc controller
     */
    private final ArcController<? extends Connectable, ? extends Connectable> arcController;

    /**
     * Point at which to split the arc
     */
    private final Point2D.Double point;


    /**
     *
     * @param arcController arc controller
     * @param mousePoint point at which to split the arc
     */
    public SplitArcAction(ArcController<? extends Connectable, ? extends Connectable> arcController, Point mousePoint) {
        this.arcController = arcController;
        point = new Point2D.Double(mousePoint.getX(), mousePoint.getY());
    }


    /**
     * Adds a new arc path point at the specified location
     * @param arg0 event 
     */
    @Override
    public void actionPerformed(ActionEvent arg0) {
        arcController.addPoint(point);
    }

}
