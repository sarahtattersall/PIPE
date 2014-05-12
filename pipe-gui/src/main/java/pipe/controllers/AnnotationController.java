package pipe.controllers;

import pipe.historyActions.annotation.ChangeAnnotationText;
import uk.ac.imperial.pipe.models.component.annotation.Annotation;

import javax.swing.event.UndoableEditListener;

public class AnnotationController extends AbstractPetriNetComponentController<Annotation> {
    protected AnnotationController(Annotation component, UndoableEditListener listener) {
        super(component, listener);
    }

    public String getText() {
        return component.getText();
    }

    public void setText(String text) {
        String oldText = component.getText();
        component.setText(text);
        registerUndoableEdit(new ChangeAnnotationText(component, oldText, text));
    }
}
