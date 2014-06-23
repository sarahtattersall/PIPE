package pipe.historyActions;

import uk.ac.imperial.pipe.models.petrinet.*;

import javax.swing.undo.AbstractUndoableEdit;
import java.awt.Point;
import java.util.Map;

/**
 * Event to undo/redo the auto-layout of a Petri net
 */
public class LayoutPetriNetEvent extends AbstractUndoableEdit {
    /**
     * Petri net whose components have changed layout
     */
    private final PetriNet petriNet;

    /**
     * Previous x, y locations of components
     */
    private final Map<String, Point> previousPoints;

    public LayoutPetriNetEvent(PetriNet petriNet, Map<String, Point> previousPoints, Map<String, Point> newPoints) {
        this.petriNet = petriNet;
        this.previousPoints = previousPoints;
        this.newPoints = newPoints;
    }

    /**
     * New x, y locations of components
     */
    private final Map<String, Point> newPoints;

    @Override
    public void undo() {
        apply(previousPoints);
    }

    @Override
    public void redo() {
       apply(newPoints);
    }

    /**
     *
     * @param points points for the components
     */
    private void apply(Map<String, Point> points) {
        for (Place place : petriNet.getPlaces()) {
            Point point = points.get(place.getId());
            applyPoint(place, point);
        }

        for (Transition transition : petriNet.getTransitions()) {
            Point point = points.get(transition.getId());
            applyPoint(transition, point);
        }
    }

    private void applyPoint(PlaceablePetriNetComponent connectable, Point point) {
        connectable.setX((int)Math.round(point.getX()));
        connectable.setY((int)Math.round(point.getY()));

    }
}
