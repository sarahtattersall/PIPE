/*
 * UndoManager.java
 */
package pipe.historyActions;

import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.models.PetriNet;
import pipe.gui.model.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * Class to handle undo & redo functionality
 *
 * @author pere
 */
public class HistoryManager {
    private static final int UNDO_BUFFER_CAPACITY = Constants.DEFAULT_BUFFER_SIZE;

    private int freePosition = 0; //index for new edits
    private int fillCount = 0; //
    private int startOfBuffer = 0; // index of the eldest element
    private int undoneEdits = 0;

    private final ArrayList<List<HistoryItem>> edits = new ArrayList<List<HistoryItem>>(UNDO_BUFFER_CAPACITY);

    private final PipeApplicationView applicationView;


    /**
     * Creates a new instance of HistoryManager
     */
    public HistoryManager(PipeApplicationView applicationView) {
        this.applicationView = applicationView;
        applicationView.setUndoActionEnabled(false);
        applicationView.setRedoActionEnabled(false);
        for (int i = 0; i < UNDO_BUFFER_CAPACITY; i++) {
            edits.add(null);
        }
    }


    public void doRedo() {

        if (undoneEdits > 0) {
            checkMode();

            // The currentEdit to redo
            Iterator<HistoryItem> currentEdit = edits.get(freePosition).iterator();
            while (currentEdit.hasNext()) {
                currentEdit.next().redo();
            }
            freePosition = (freePosition + 1) % UNDO_BUFFER_CAPACITY;
            fillCount++;
            undoneEdits--;
            if (undoneEdits == 0) {
                applicationView.setRedoActionEnabled(false);
            }
            applicationView.setUndoActionEnabled(true);
        }
    }


    public void doUndo() {

        if (fillCount > 0) {
            checkMode();

            if (--freePosition < 0) {
                freePosition += UNDO_BUFFER_CAPACITY;
            }
            fillCount--;
            undoneEdits++;

            // The currentEdit to undo (reverse order)
            List<HistoryItem> currentEdit = edits.get(freePosition);
            for (int i = currentEdit.size() - 1; i >= 0; i--) {
                currentEdit.get(i).undo();
            }

            if (fillCount == 0) {
                applicationView.setUndoActionEnabled(false);
            }
            applicationView.setRedoActionEnabled(true);
        }
    }


    public void clear() {
        freePosition = 0;
        fillCount = 0;
        startOfBuffer = 0;
        undoneEdits = 0;
        applicationView.setUndoActionEnabled(false);
        applicationView.setRedoActionEnabled(false);
    }


    public void newEdit() {
        List<HistoryItem> lastEdit = edits.get(currentIndex());
        if ((lastEdit != null) && (lastEdit.isEmpty())) {
            return;
        }

        undoneEdits = 0;
        applicationView.setUndoActionEnabled(true);
        applicationView.setRedoActionEnabled(false);

        List<HistoryItem> compoundEdit = new ArrayList<HistoryItem>();
        edits.set(freePosition, compoundEdit);
        freePosition = (freePosition + 1) % UNDO_BUFFER_CAPACITY;
        if (fillCount < UNDO_BUFFER_CAPACITY) {
            fillCount++;
        } else {
            startOfBuffer = (startOfBuffer + 1) % UNDO_BUFFER_CAPACITY;
        }
    }


    public void addEdit(HistoryItem historyItem) {  //FIXME can throw if edits list is not yet full of compoundEdits (still has nulls)

        List<HistoryItem> compoundEdit = edits.get(currentIndex());
        compoundEdit.add(historyItem);

    }


    public void addNewEdit(HistoryItem historyItem) {
        newEdit(); // mark for a new "transtaction""
        addEdit(historyItem);
    }

    private int currentIndex() {
        int lastAdd = freePosition - 1;
        if (lastAdd < 0) {
            lastAdd += UNDO_BUFFER_CAPACITY;
        }
        return lastAdd;
    }

    private void checkMode() {
//        if ((app.getMode() == Constants.FAST_PLACE) ||
//                (app.getMode() == Constants.FAST_TRANSITION)) {
//            app.resetMode();
//        }
    }
}
