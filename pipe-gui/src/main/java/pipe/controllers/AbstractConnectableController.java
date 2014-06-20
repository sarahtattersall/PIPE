package pipe.controllers;

import uk.ac.imperial.pipe.models.petrinet.Connectable;

import javax.swing.event.UndoableEditListener;
import java.awt.Point;

public abstract class AbstractConnectableController<T extends Connectable>
        extends AbstractPetriNetComponentController<T> {
    /**
     * Constructor
     *
     * @param component underlying Petri net controller
     * @param listener  undo listener
     */
    protected AbstractConnectableController(T component, UndoableEditListener listener) {
        super(component, listener);
    }

    public void moveNameLabel(Point point) {
        int xOffset = (int) point.getX() - component.getX();
        int yOffset = (int) point.getY() - component.getY();
        component.setNameXOffset(xOffset);
        component.setNameYOffset(yOffset);
    }
}
