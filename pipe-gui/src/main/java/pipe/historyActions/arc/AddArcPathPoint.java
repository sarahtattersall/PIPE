package pipe.historyActions.arc;


import pipe.models.component.Connectable;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;

import javax.swing.undo.AbstractUndoableEdit;

public class AddArcPathPoint<S extends Connectable, T extends Connectable> extends AbstractUndoableEdit {
    private final Arc<S, T> arc;

    private final ArcPoint point;

    private int index;

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

    @Override
    public void undo() {
        super.undo();
        arc.removeIntermediatePoint(point);
    }

    @Override
    public void redo() {
        super.redo();
        arc.addIntermediatePoint(point);
    }

}
