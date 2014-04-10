package pipe.actions.type;

import matchers.component.HasMultiple;
import matchers.component.HasTimed;
import matchers.component.HasXY;
import org.junit.Before;
import org.junit.Test;
import pipe.actions.gui.create.ImmediateTransitionAction;
import pipe.actions.gui.create.TransitionAction;
import pipe.controllers.PetriNetController;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.awt.Point;
import java.awt.event.MouseEvent;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class ImmediateTransitionActionTest {

    private TransitionAction action;

    private PetriNetController mockController;

    private PetriNet mockNet;

    @Before
    public void setUp() {
        action = new ImmediateTransitionAction();
        mockController = mock(PetriNetController.class);
        mockNet = mock(PetriNet.class);
        when(mockController.getPetriNet()).thenReturn(mockNet);
    }


    @Test
    public void createsTimedTransitionOnClick() {
        Point point = new Point(10, 20);
        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getClickCount()).thenReturn(1);
        when(mockEvent.getPoint()).thenReturn(point);

        action.doAction(mockEvent, mockController);

        verify(mockNet).addTransition(
                argThat(new HasMultiple<Transition>(new HasXY(point.getX(), point.getY()), new HasTimed(false))));
    }

    @Test
    public void createsUndoActionOnClick() {
        Point point = new Point(10, 20);
        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getClickCount()).thenReturn(1);
        when(mockEvent.getPoint()).thenReturn(point);

        action.doAction(mockEvent, mockController);

        //        verify(mockHistory).addNewEdit(any(AddPetriNetObject.class));
    }
}
