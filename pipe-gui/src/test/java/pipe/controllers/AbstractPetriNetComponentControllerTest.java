package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.historyActions.MultipleEdit;
import pipe.models.component.place.Place;
import pipe.utilities.transformers.Contains;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;

import java.util.Arrays;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class AbstractPetriNetComponentControllerTest {

    DummyController controller;

    @Mock
    Place place;

    @Mock
    UndoableEditListener listener;

    @Mock
    UndoableEdit undoableEdit1;

    @Mock
    UndoableEdit undoableEdit2;

    @Before
    public void setUp() {
        controller = new DummyController(place, listener);
    }

    @Test
    public void registeringEventCallsListener() {
        controller.addEdit(undoableEdit1);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(undoableEdit1)));
    }

    @Test
    public void multipleEditDoesNotCallListenerBeforeFinishing() {
        controller.startMultipleEdits();
        controller.addEdit(undoableEdit1);
        verify(listener, never()).undoableEditHappened(any(UndoableEditEvent.class));
    }

    @Test
    public void multipleEditCallsListenerOnFinish() {

        controller.startMultipleEdits();
        controller.addEdit(undoableEdit1);
        controller.finishMultipleEdits();

        UndoableEdit multipleEdit = new MultipleEdit(Arrays.asList(undoableEdit1));
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(multipleEdit)));
    }

    @Test
    public void multipleEditCallsListenerOnFinishWithMultipleItems() {

        controller.startMultipleEdits();
        controller.addEdit(undoableEdit1);
        controller.addEdit(undoableEdit2);
        controller.finishMultipleEdits();

        UndoableEdit multipleEdit = new MultipleEdit(Arrays.asList(undoableEdit1, undoableEdit2));
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(multipleEdit)));
    }

    @Test
    public void wontCallListenerOnFinishIfNoEventAdded() {
        controller.startMultipleEdits();
        controller.finishMultipleEdits();
        verify(listener, never()).undoableEditHappened(any(UndoableEditEvent.class));
    }

    public class DummyController extends AbstractPetriNetComponentController<Place> {

        protected DummyController(Place component, UndoableEditListener listener) {
            super(component, listener);
        }

        public void addEdit(UndoableEdit edit) {
            registerUndoableEdit(edit);
        }
    }
}
