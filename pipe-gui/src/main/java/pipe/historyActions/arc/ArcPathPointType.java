/*
 * ArcPathPointTypeEdit.java
 */

package pipe.historyActions.arc;

import pipe.models.component.arc.ArcPoint;

import javax.swing.undo.AbstractUndoableEdit;


public class ArcPathPointType extends AbstractUndoableEdit {

    private final ArcPoint arcPoint;


    public ArcPathPointType(ArcPoint arcPoint) {
        this.arcPoint = arcPoint;
    }

    @Override
    public int hashCode() {
        return arcPoint.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ArcPathPointType that = (ArcPathPointType) o;

        if (!arcPoint.equals(that.arcPoint)) {
            return false;
        }

        return true;
    }

    @Override
    public void undo() {
        super.undo();
        arcPoint.setCurved(!arcPoint.isCurved());
    }


    /** */
    @Override
    public void redo() {
        super.redo();
        arcPoint.setCurved(!arcPoint.isCurved());
    }
}
