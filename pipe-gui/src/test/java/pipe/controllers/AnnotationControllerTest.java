package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.historyActions.ChangeAnnotationText;
import pipe.models.component.annotation.Annotation;
import pipe.utilities.transformers.Contains;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AnnotationControllerTest {
    AnnotationController controller;

    @Mock
    Annotation annotation;

    @Mock
    UndoableEditListener listener;

    @Before
    public void setUp() {
        controller = new AnnotationController(annotation, listener);
    }

    @Test
    public void changesAnnotationText() {
        String changedText = "This text has changed";
        controller.setText(changedText);
        verify(annotation).setText(changedText);
    }

    @Test
    public void createsAnUndoItem() {
        String existingText = "This is the existing text";
        String changedText = "This text has changed";
        when(annotation.getText()).thenReturn(existingText);
        controller.setText(changedText);

        UndoableEdit changeTextEdit = new ChangeAnnotationText(annotation, existingText, changedText);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(changeTextEdit)));
    }
}
