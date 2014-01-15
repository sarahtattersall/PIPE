/*
 * DeleteArcPathPointEdit.java
 */

package pipe.historyActions;

import pipe.models.component.Arc;
import pipe.models.component.ArcPoint;
import pipe.models.component.Connectable;

/**
 * @author Pere Bonet
 */
public class DeleteArcPathPoint<S extends Connectable, T extends Connectable> extends HistoryItem {


    private final Arc<S, T> arc;

    private final ArcPoint point;

    public DeleteArcPathPoint(Arc<S, T> arc, ArcPoint component) {
        this.arc = arc;
        this.point = component;
    }

    @Override
    public void undo() {
        arc.addIntermediatePoint(point);
    }

    @Override
    public void redo() {
        arc.removeIntermediatePoint(point);
    }
}
