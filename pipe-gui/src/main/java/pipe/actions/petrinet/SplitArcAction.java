/*
 * SplitArcAction.java
 */
package pipe.actions.petrinet;

import pipe.controllers.ArcController;
import pipe.controllers.PetriNetController;
import pipe.gui.ZoomController;
import pipe.models.component.Connectable;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;

/**
 * This class is used to split an arc in two at the
 * point the user clicks the mouse button.
 *
 * @author Pere Bonet (after Nadeem)
 */
public class SplitArcAction
        extends javax.swing.AbstractAction {

    private final ArcController<? extends Connectable, ? extends Connectable> arcController;
    private final Point2D.Double point;


    public SplitArcAction(ArcController<? extends Connectable, ? extends Connectable> arcController, Point mousePoint) {
        this.arcController = arcController;
        point = new Point2D.Double(mousePoint.getX(), mousePoint.getY());
    }


    @Override
    public void actionPerformed(ActionEvent arg0) {
        arcController.addPoint(point);
    }

}
