/*
 * AnnotationTextEdit.java
 */

package pipe.historyActions.annotation;

import pipe.models.component.annotation.Annotation;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Undoable Edit for changing an annotations text
 */
public class ChangeAnnotationText extends AbstractUndoableEdit {


    private final Annotation annotation;

    private final String oldText;

    private final String newText;

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
