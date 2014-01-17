/*
 * SplitArcAction.java
 */
package pipe.actions;

import pipe.controllers.ArcController;
import pipe.controllers.PetriNetController;
import pipe.gui.ZoomController;

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

    private final ArcController arcController;
    private final PetriNetController petriNetController;
    private final Point2D.Double point;


    public SplitArcAction(ArcController arcController, Point mousePoint, PetriNetController petriNetController) {
        this.arcController = arcController;
        this.petriNetController = petriNetController;
        point = new Point2D.Double(mousePoint.getX(), mousePoint.getY());
    }


    public void actionPerformed(ActionEvent arg0) {
        ZoomController zoomController = petriNetController.getZoomController();
        int percent = zoomController.getPercent();
        Point2D.Double unzoomedPoint = zoomController.getUnzoomedValue(point);
        arcController.addPoint(unzoomedPoint);
    }

}
