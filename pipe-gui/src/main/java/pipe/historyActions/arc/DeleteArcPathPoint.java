/*
 * DeleteArcPathPointEdit.java
 */

package pipe.historyActions.arc;

import pipe.models.component.Connectable;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * @author Pere Bonet
 */
public class DeleteArcPathPoint<S extends Connectable, T extends Connectable> extends AbstractUndoableEdit {


    private final Arc<S, T> arc;

    private final ArcPoint point;

    public DeleteArcPathPoint(Arc<S, T> arc, ArcPoint component) {
        this.arc = arc;
        this.point = component;
    }

    @Override
    public void undo() {
        super.undo();
        arc.addIntermediatePoint(point);
    }

    @Override
    public void redo() {
        super.redo();
        arc.removeIntermediatePoint(point);
    }
}
