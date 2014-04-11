package pipe.actions.type;

import matchers.component.HasMultiple;
import matchers.component.HasTimed;
import matchers.component.HasXY;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.actions.gui.create.TimedTransitionAction;
import pipe.actions.gui.create.TransitionAction;
import pipe.controllers.PetriNetController;
import pipe.historyActions.component.AddPetriNetObject;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.utilities.transformers.Contains;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.awt.Point;
import java.awt.event.MouseEvent;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TimedTransactionActionTest {

    private TransitionAction action;

    @Mock
    private PetriNetController mockController;

    @Mock
    private PetriNet mockNet;

    @Mock
    UndoableEditListener listener;

    @Before
    public void setUp() {
        action = new TimedTransitionAction();
        action.addUndoableEditListener(listener);
        when(mockController.getPetriNet()).thenReturn(mockNet);
        when(mockController.getUniqueTransitionName()).thenReturn("T0");
    }


    @Test
    public void createsTimedTransitionOnClick() {
        Point point = new Point(10, 20);
        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getClickCount()).thenReturn(1);
        when(mockEvent.getPoint()).thenReturn(point);

        action.doAction(mockEvent, mockController);

        verify(mockNet).addTransition(
                argThat(new HasMultiple<>(new HasXY(point.getX(), point.getY()), new HasTimed(true))));
    }

    @Test
    public void createsUndoActionOnClick() {
        Point point = new Point(10, 20);
        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getClickCount()).thenReturn(1);
        when(mockEvent.getPoint()).thenReturn(point);

        Transition transition = new Transition("T0", "T0");
        transition.setX(10);
        transition.setY(20);
        transition.setTimed(true);

        action.doAction(mockEvent, mockController);

        UndoableEdit addAction = new AddPetriNetObject(transition, mockNet);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(addAction)));
    }
}
