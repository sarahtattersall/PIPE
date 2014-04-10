package debug;

import javax.swing.*;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

class UndoableToggleEdit extends AbstractUndoableEdit {

    private JToggleButton button;

    private boolean selected;

    // Create a new edit for a JToggleButton that has just been toggled.
    public UndoableToggleEdit(JToggleButton button) {
        this.button = button;
        selected = button.isSelected();
    }

    // Return a reasonable name for this edit.
    @Override
    public String getPresentationName() {
        return "Toggle " + button.getText() + " " + (selected ? "on" : "off");
    }

    // Redo by setting the button state as it was initially.
    @Override
    public void redo() throws CannotRedoException {
        super.redo();
        button.setSelected(selected);
    }

    // Undo by setting the button state to the opposite value.
    @Override
    public void undo() throws CannotUndoException {
        super.undo();
        button.setSelected(!selected);
    }
}
