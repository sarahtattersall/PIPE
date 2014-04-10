package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.historyActions.*;
import pipe.models.component.transition.Transition;
import pipe.utilities.transformers.Contains;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class TransitionControllerTest {

    @Mock
    Transition transition;

    @Mock
    UndoableEditListener listener;


    TransitionController controller;


    @Before
    public void setUp() {
        controller = new TransitionController(transition, listener);
    }

    @Test
    public void setInfiniteServerCreatesHistoryItem() {
        boolean isInfinite = true;
        controller.setInfiniteServer(isInfinite);
        UndoableEdit transitionInfiniteServer = new TransitionInfiniteServer(transition, isInfinite);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(transitionInfiniteServer)));
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

        UndoableEdit transitionTimed = new TransitionTiming(transition, isTimed);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(transitionTimed)));
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

        UndoableEdit angleItem = new TransitionRotation(transition, oldAngle, newAngle);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(angleItem)));
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

        UndoableEdit priorityItem = new TransitionPriority(transition, oldPriority, newPriority);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(priorityItem)));
    }

    @Test
    public void setPriorityModifiesTransition() {
        int oldPriority = 1;
        int newPriority = 4;
        when(transition.getPriority()).thenReturn(oldPriority);
        controller.setPriority(newPriority);
        verify(transition).setPriority(newPriority);
    }

    @Test
    public void setNameChangesName() {
        String newName = "newName";
        controller.setName(newName);
        verify(transition).setId(newName);
        verify(transition).setName(newName);
    }


    @Test
    public void setNameCreatesUndoItem() {
        String oldName = "oldName";
        String newName = "newName";
        when(transition.getId()).thenReturn(oldName);
        controller.setName(newName);

        UndoableEdit nameEdit = new PetriNetObjectName(transition, oldName, newName);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(nameEdit)));
    }


}
