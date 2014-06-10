package pipe.controllers;

import pipe.historyActions.annotation.ChangeAnnotationText;
import uk.ac.imperial.pipe.models.petrinet.Annotation;

import javax.swing.event.UndoableEditListener;

/**
 * Controller for the annotation model
 */
public final class AnnotationController extends AbstractPetriNetComponentController<Annotation> {
    /**
     * Constructor
     * @param component an annotation to control
     * @param listener listener for undo events
     */
    public AnnotationController(Annotation component, UndoableEditListener listener) {
        super(component, listener);
    }

    /**
     *
     * @return annotation text
     */
    public String getText() {
        return component.getText();
    }

    /**
     * Changes the annotations text.
     * Creates an undoable event
     * @param text new text for the annotation
     */
    public void setText(String text) {
        String oldText = component.getText();
        component.setText(text);
        registerUndoableEdit(new ChangeAnnotationText(component, oldText, text));
    }
}
