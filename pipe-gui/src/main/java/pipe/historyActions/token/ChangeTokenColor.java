package pipe.historyActions.token;

import pipe.models.component.token.Token;

import javax.swing.undo.AbstractUndoableEdit;
import java.awt.Color;

/**
 * Changes a tokens colour to the old/new value respectively
 */
public class ChangeTokenColor extends AbstractUndoableEdit {

    private final Token token;

    private final Color oldColor;

    private final Color newColor;

    public ChangeTokenColor(Token token, Color oldColor, Color newColor) {

        this.token = token;
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    @Override
    public void undo() {
        super.undo();
        token.setColor(oldColor);
    }

    @Override
    public void redo() {
        super.redo();
        token.setColor(newColor);
    }
}
