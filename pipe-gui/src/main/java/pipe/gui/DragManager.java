package pipe.gui;

import pipe.controllers.PetriNetController;

import java.awt.*;
import java.awt.geom.Point2D;

/**
 * Handles dragging of objects around when selected
 */
public class DragManager {

    private PetriNetController petriNetController;

    private Point dragStart = new Point(0, 0);

    public DragManager(PetriNetController petriNetController) {
        this.petriNetController = petriNetController;
    }

    public void setDragStart(Point dragStart) {
        this.dragStart = dragStart;
    }

    /**
     * Drag items to location
     *
     * @param location location of mouse to drag items to
     */
    public void drag(Point location) {
        int x = (int) (location.getX() - dragStart.getX());
        int y = (int) (location.getY() - dragStart.getY());
        dragStart = location;
        petriNetController.translateSelected(new Point2D.Double(x, y));
    }
}
