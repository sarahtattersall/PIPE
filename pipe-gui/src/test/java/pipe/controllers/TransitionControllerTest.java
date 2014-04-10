package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.historyActions.*;
import pipe.models.component.transition.Transition;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransitionControllerTest {

    @Mock
    Transition transition;

    TransitionController controller;

    @Before
    public void setUp() {
        transition = mock(Transition.class);

        controller = new TransitionController(transition);
    }

    @Test
    public void setInfiniteServerCreatesHistoryItem() {
        boolean isInfinite = true;
        controller.setInfiniteServer(isInfinite);

        HistoryItem transitionInfiniteServer = new TransitionInfiniteServer(transition, isInfinite);
    }

    @Test
    public void setInfiniteServerModifiesTransition() {
        boolean isInfinite = true;
        controller.setInfiniteServer(isInfinite);
        verify(transition).setInfiniteServer(isInfinite);
    }

    @Test
    public void setTimedCreatesHistoryItem() {
        boolean isTimed = true;
        controller.setTimed(isTimed);

        HistoryItem transitionTimed = new TransitionTiming(transition, isTimed);
    }

    @Test
    public void setTimedModifiesTransition() {
        boolean isTimed = true;
        controller.setTimed(isTimed);
        verify(transition).setTimed(isTimed);
    }

    @Test
    public void setAngleCreatesHistoryItem() {
        int oldAngle = 45;
        int newAngle = 180;
        when(transition.getAngle()).thenReturn(oldAngle);
        controller.setAngle(newAngle);

        HistoryItem angleItem = new TransitionRotation(transition, oldAngle, newAngle);
    }

    @Test
    public void setAngleModifiesTransition() {
        int oldAngle = 45;
        int newAngle = 180;
        when(transition.getAngle()).thenReturn(oldAngle);
        controller.setAngle(newAngle);
        verify(transition).setAngle(newAngle);
    }

    @Test
    public void setPriorityCreatesHistoryItem() {
        int oldPriority = 1;
        int newPriority = 4;
        when(transition.getPriority()).thenReturn(oldPriority);
        controller.setPriority(newPriority);

        HistoryItem priorityItem = new TransitionPriority(transition, oldPriority, newPriority);
    }

    @Test
    public void setPriorityModifiesTransition() {
        int oldPriority = 1;
        int newPriority = 4;
        when(transition.getPriority()).thenReturn(oldPriority);
        controller.setPriority(newPriority);
        verify(transition).setPriority(newPriority);
    }

}
