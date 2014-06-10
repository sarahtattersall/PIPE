package pipe.historyActions.token;


import uk.ac.imperial.pipe.models.petrinet.Token;

import javax.swing.undo.AbstractUndoableEdit;
import java.awt.Color;

/**
 * Changes a tokens colour to the old/new value respectively
 */
public class ChangeTokenColor extends AbstractUndoableEdit {

    /**
     * Token model
     */
    private final Token token;

    /**
     * Old color
     */
    private final Color oldColor;

    /**
     * New color
     */
    private final Color newColor;

    /**
     * Constructor
     * @param token underlying model
     * @param oldColor old color
     * @param newColor new color
     */
    public ChangeTokenColor(Token token, Color oldColor, Color newColor) {

        this.token = token;
        this.oldColor = oldColor;
        this.newColor = newColor;
    }

    /**
     * Sets the tokens color to the old color
     */
    @Override
    public void undo() {
        super.undo();
        token.setColor(oldColor);
    }

    /**
     * Sets the tokens color to the new color
     */
    @Override
    public void redo() {
        super.redo();
        token.setColor(newColor);
    }
}
