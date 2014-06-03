package pipe.historyActions;

import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.UndoableEdit;
import java.util.Collection;
import java.util.LinkedList;

public class MultipleEdit extends AbstractUndoableEdit {

    private final Collection<UndoableEdit> multipleEdits = new LinkedList<>();


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

    /** */
    @Override
    public void undo() {
        super.undo();

        for (UndoableEdit edit : multipleEdits) {
            edit.undo();
        }
    }


    /** */
    @Override
    public void redo() {
        super.redo();
        for (UndoableEdit edit : multipleEdits) {
            edit.redo();
        }
    }

}
