/*
 * SplitArcAction.java
 */
package pipe.actions;

import pipe.controllers.ArcController;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.historyActions.HistoryManager;
import pipe.models.component.Arc;

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
    private final Point2D.Double point;


    public SplitArcAction(ArcController arcController, Point mousePoint) {
        this.arcController = arcController;
        point = new Point2D.Double(mousePoint.getX(), mousePoint.getY());
    }


    public void actionPerformed(ActionEvent arg0) {
        arcController.addPoint(point);
    }

}
