/*
 * TranslatePetriNetObjectEdit.java
 */

package pipe.historyActions.component;

import uk.ac.imperial.pipe.models.petrinet.PlaceablePetriNetComponent;

import javax.swing.undo.AbstractUndoableEdit;
import java.awt.geom.Point2D;


/**
 * Undo action for moving petri net objects on the canvas
 */
public class MovePetriNetObject
        extends AbstractUndoableEdit
{

    private final PlaceablePetriNetComponent component;

    private final Point2D starting;

    private final Point2D translated;

    public MovePetriNetObject(PlaceablePetriNetComponent component, Point2D starting, Point2D translated) {

        this.component = component;
        this.starting = starting;
        this.translated = translated;
    }

    @Override
    public void undo() {
        super.undo();
        component.setX((int) starting.getX());
        component.setY((int) starting.getY());
    }

    @Override
    public void redo() {
        super.redo();
        component.setX((int) translated.getX());
        component.setY((int) translated.getY());
    }
}
