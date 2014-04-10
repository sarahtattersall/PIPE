package pipe.historyActions;

import javax.swing.undo.AbstractUndoableEdit;
import java.util.List;

public class MultipleEdit extends AbstractUndoableEdit {

    private final List<AbstractUndoableEdit> multipleEdits;


    public MultipleEdit(List<AbstractUndoableEdit> multipleEdits) {
        this.multipleEdits = multipleEdits;
    }


    /** */
    @Override
    public void undo() {
        super.undo();
        for (AbstractUndoableEdit edit : multipleEdits) {
            edit.undo();
        }
    }


    /** */
    @Override
    public void redo() {
        super.redo();
        for (AbstractUndoableEdit edit : multipleEdits) {
            edit.redo();
        }
    }

}
