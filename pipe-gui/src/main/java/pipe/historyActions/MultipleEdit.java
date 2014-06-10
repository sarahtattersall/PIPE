package pipe.historyActions;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import java.util.Collection;
import java.util.LinkedList;

/**
 * A class that allows for many undoable edit items to be undone/redone at once
 *
 * Very useful when making multiple changes in one action
 */
public class MultipleEdit extends AbstractUndoableEdit {

    /**
     * Multiple undoable actions to be undone/redone in one undo/redo action
     */
    private final Collection<UndoableEdit> multipleEdits = new LinkedList<>();


    /**
     *
     * @param multipleEdits  actions to be undone/redone in one undo/redo action
     */
    public MultipleEdit(Collection<UndoableEdit> multipleEdits) {
        this.multipleEdits.addAll(multipleEdits);
    }

    @Override
    public int hashCode() {
        return multipleEdits.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof MultipleEdit)) {
            return false;
        }

        MultipleEdit that = (MultipleEdit) o;

        if (!multipleEdits.equals(that.multipleEdits)) {
            return false;
        }

        return true;
    }

    /**
     * Undoes every action in the multiple edits
     */
    @Override
    public void undo() {
        super.undo();

        for (UndoableEdit edit : multipleEdits) {
            edit.undo();
        }
    }


    /**
     * Redoes every action in the multiple edits
     */
    @Override
    public void redo() {
        super.redo();
        for (UndoableEdit edit : multipleEdits) {
            edit.redo();
        }
    }

}
