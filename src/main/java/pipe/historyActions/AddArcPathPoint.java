package pipe.historyActions;


import pipe.models.component.Arc;
import pipe.models.component.ArcPoint;
import pipe.models.component.Connectable;

public class AddArcPathPoint<S extends Connectable, T extends Connectable> extends HistoryItem {
    private final Arc<S,T> arc;
    private final ArcPoint point;

    public AddArcPathPoint(Arc<S,T> arc, ArcPoint point) {
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final AddArcPathPoint that = (AddArcPathPoint) o;

        if (!arc.equals(that.arc)) return false;
        if (!point.equals(that.point)) return false;

        return true;
    }

    @Override
    public void undo() {
        arc.removeIntermediatePoint(point);
    }

    @Override
    public void redo() {
        arc.addIntermediatePoint(point);
    }

}
