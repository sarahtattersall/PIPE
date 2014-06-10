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

    /**
     * Petri net component that can be moved
     */
    private final PlaceablePetriNetComponent component;

    /**
     * Initial point
     */
    private final Point2D starting;

    /**
     * Translated point
     */
    private final Point2D translated;

    /**
     * Constructor
     * @param component moveable Petri net component
     * @param starting initial location
     * @param translated translated location
     */
    public MovePetriNetObject(PlaceablePetriNetComponent component, Point2D starting, Point2D translated) {

        this.component = component;
        this.starting = starting;
        this.translated = translated;
    }

    /**
     * Sets the components location to its initial location
     */
    @Override
    public void undo() {
        super.undo();
        component.setX((int) starting.getX());
        component.setY((int) starting.getY());
    }

    /**
     * Sets the component location to its translated location
     */
    @Override
    public void redo() {
        super.redo();
        component.setX((int) translated.getX());
        component.setY((int) translated.getY());
    }
}
