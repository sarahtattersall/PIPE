/*
 * DeleteArcPathPointEdit.java
 */

package pipe.historyActions.arc;


import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Deletes a point from an arcs path
 */
public class DeleteArcPathPoint<S extends Connectable, T extends Connectable> extends AbstractUndoableEdit {


    /**
     * Arc model
     */
    private final Arc<S, T> arc;

    /**
     * Arc point to delete from the arc
     */
    private final ArcPoint point;

    /**
     * Constructor
     *
     * @param arc       model
     * @param arcPoint arc point to delete from the model
     */
    public DeleteArcPathPoint(Arc<S, T> arc, ArcPoint arcPoint) {
        this.arc = arc;
        this.point = arcPoint;
    }

    /**
     * Adds the point back to the arc
     */
    @Override
    public void undo() {
        super.undo();
        arc.addIntermediatePoint(point);
    }

    /**
     * Deletes the point from the arc
     */
    @Override
    public void redo() {
        super.redo();
        arc.removeIntermediatePoint(point);
    }
}
