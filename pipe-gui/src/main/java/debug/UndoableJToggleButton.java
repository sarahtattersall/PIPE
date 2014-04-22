package debug;

import javax.swing.*;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import java.awt.event.ActionEvent;

class UndoableJToggleButton extends JToggleButton {
    private UndoableEditListener listener;

    // For this example, we'll just provide one constructor . . .
    public UndoableJToggleButton(String txt) {
        super(txt);
    }

    // Set the UndoableEditListener.
    public void addUndoableEditListener(UndoableEditListener l) {
        listener = l; // Should ideally throw an exception if listener != null
    }

    // Remove the UndoableEditListener.
    public void removeUndoableEditListener(UndoableEditListener l) {
        listener = null;
    }

    // We override this method to call the super implementation first (to fire
    // the
    // action event) and then fire a new UndoableEditEvent to our listener.
    @Override
    protected void fireActionPerformed(ActionEvent ev) {

        // Fire the ActionEvent as usual.
        super.fireActionPerformed(ev);

        if (listener != null) {
            listener.undoableEditHappened(new UndoableEditEvent(this,
                    new UndoableToggleEdit(this)));
        }
    }
}
