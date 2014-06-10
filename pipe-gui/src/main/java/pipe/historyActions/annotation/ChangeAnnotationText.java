/*
 * AnnotationTextEdit.java
 */

package pipe.historyActions.annotation;


import uk.ac.imperial.pipe.models.petrinet.Annotation;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Undoable Edit for changing an annotations text
 */
public final class ChangeAnnotationText extends AbstractUndoableEdit {

    /**
     * Underlying annotation model
     */
    private final Annotation annotation;

    /**
     * Old annotation text
     */
    private final String oldText;

    /**
     * new annotation text
     */
    private final String newText;

    /**
     * Constructor
     * @param annotation underlying model
     * @param oldText old annotaton text
     * @param newText new annotation text
     */
    public ChangeAnnotationText(Annotation annotation, String oldText, String newText) {

        this.annotation = annotation;
        this.oldText = oldText;
        this.newText = newText;
    }


    /**
     * Sets annotation text to previous value
     */
    @Override
    public void undo() {
        super.undo();
        annotation.setText(oldText);
    }


    /**
     * Sets annotation text to new value
     */
    @Override
    public void redo() {
        super.redo();
        annotation.setText(newText);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ChangeAnnotationText)) {
            return false;
        }

        ChangeAnnotationText that = (ChangeAnnotationText) o;

        if (!annotation.equals(that.annotation)) {
            return false;
        }
        if (!newText.equals(that.newText)) {
            return false;
        }
        if (!oldText.equals(that.oldText)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = annotation.hashCode();
        result = 31 * result + oldText.hashCode();
        result = 31 * result + newText.hashCode();
        return result;
    }
}
