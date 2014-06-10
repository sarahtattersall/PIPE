package pipe.historyActions.arc;


import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Undo/Redo action for adding a point in an arcs path
 * @param <S>
 * @param <T>
 */
public class AddArcPathPoint<S extends Connectable, T extends Connectable> extends AbstractUndoableEdit {
    /**
     * Arc model to add/remove the point to/from
     */
    private final Arc<S, T> arc;

    /**
     * Arc point
     */
    private final ArcPoint point;

    /**
     * Constructor
     * @param arc arc the point belongs to
     * @param point arc point
     */
    public AddArcPathPoint(Arc<S, T> arc, ArcPoint point) {
        this.arc = arc;
        this.point = point;
    }

    @Override
    public int hashCode() {
        int result = arc.hashCode();
        result = 31 * result + point.hashCode();
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final AddArcPathPoint that = (AddArcPathPoint) o;

        if (!arc.equals(that.arc)) {
            return false;
        }
        if (!point.equals(that.point)) {
            return false;
        }

        return true;
    }

    /**
     * Remove the point from the arc
     */
    @Override
    public final void undo() {
        super.undo();
        arc.removeIntermediatePoint(point);
    }

    /**
     * Adds the point to the arc
     */
    @Override
    public final void redo() {
        super.redo();
        arc.addIntermediatePoint(point);
    }

}
