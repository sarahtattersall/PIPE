package pipe.handler;

import matchers.component.HasMultiple;
import matchers.component.HasTimed;
import matchers.component.HasXY;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.Grid;
import pipe.gui.PetriNetTab;
import pipe.handlers.MouseHandler;
import pipe.handlers.mouse.MouseUtilities;
import pipe.historyActions.AddPetriNetObject;
import pipe.historyActions.HistoryManager;
import pipe.models.*;
import pipe.views.PetriNetView;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

public class MouseHandlerTest {
    PetriNetController mockController;
    PetriNet mockNet;
    PetriNetTab mockTab;
    PetriNetView mockView;
    MouseUtilities mockUtilities;
    MouseHandler handler;
    HistoryManager mockHistory;

    PipeApplicationModel mockModel;
    MouseEvent mockEvent;

    @Before
    public void setup() {
        mockController = mock(PetriNetController.class);
        mockNet = mock(PetriNet.class);
        mockView = mock(PetriNetView.class);
        mockTab = mock(PetriNetTab.class);
        mockUtilities = mock(MouseUtilities.class);
        handler = new MouseHandler(mockUtilities, mockController, mockNet, mockTab, mockView);

        mockModel = mock(PipeApplicationModel.class);
        ApplicationSettings.register(mockModel);
        mockEvent = mock(MouseEvent.class);
        when(mockEvent.getPoint()).thenReturn(new Point(0,0));
        when(mockUtilities.isLeftMouse(mockEvent)).thenReturn(true);
        mockHistory = mock(HistoryManager.class);
        when(mockTab.getHistoryManager()).thenReturn(mockHistory);
    }

    @Test
    public void createsPlaceOnClick() {
        when(mockModel.getMode()).thenReturn(Constants.PLACE);
        handler.mousePressed(mockEvent);

        verify(mockNet).addPlace(argThat(
                new HasMultiple<Place>(
                        new HasXY(Grid.getModifiedX(0), Grid.getModifiedY(0))
                )
        ));
    }

    @Test
    public void notifyObserversOnPlaceCreation() {
        assertPetrinetNotifiesObservers(Constants.PLACE);
    }

    @Test
    public void createsPlaceHistoryOnClick() {
        when(mockModel.getMode()).thenReturn(Constants.PLACE);
        handler.mousePressed(mockEvent);

        verify(mockHistory).addNewEdit(any(AddPetriNetObject.class));
    }

    @Test
    public void createsImmediateTransitionOnClick() {
        when(mockModel.getMode()).thenReturn(Constants.IMMTRANS);
        handler.mousePressed(mockEvent);

        verify(mockNet).addTransition(argThat(
                new HasMultiple<Transition>(
                        new HasXY(Grid.getModifiedX(0),  Grid.getModifiedY(0)),
                        new HasTimed(false)))
        );
    }

    @Test
    public void createsTimedTransitionOnClick() {
        when(mockModel.getMode()).thenReturn(Constants.TIMEDTRANS);
        handler.mousePressed(mockEvent);

        verify(mockNet).addTransition(argThat(
                new HasMultiple<Transition>(
                        new HasXY(Grid.getModifiedX(0),  Grid.getModifiedY(0)),
                        new HasTimed(true)))
        );
    }

    @Test
    public void createsTimedTransitionHistoryOnClick() {
        when(mockModel.getMode()).thenReturn(Constants.TIMEDTRANS);
        handler.mousePressed(mockEvent);

        verify(mockHistory).addNewEdit(any(AddPetriNetObject.class));
    }

    @Test
    public void createsImmediateTransitionHistoryOnClick() {
        when(mockModel.getMode()).thenReturn(Constants.TIMEDTRANS);
        handler.mousePressed(mockEvent);

        verify(mockHistory).addNewEdit(any(AddPetriNetObject.class));
    }

    @Test
    public void notifyObserversOnImmediateTransitionCreation() {
        assertPetrinetNotifiesObservers(Constants.IMMTRANS);
    }

    @Test
    public void notifyObserversOnTimedTransitionCreation() {
        assertPetrinetNotifiesObservers(Constants.TIMEDTRANS);
    }

    @Test
    public void addsArcWithShiftDownPoint() {
       assertCreatesArcPoint(Constants.ARC, true);
    }

    @Test
    public void addsArcWithShiftUpPoint() {
        assertCreatesArcPoint(Constants.ARC, false);
    }

    @Test
    public void doesNotAddArcPointIfNotCreating() {
        assertDoesNotCreateArcPoint(Constants.ARC);
    }

    @Test
    public void addsInhibitionArcWithShiftUpPoint() {
        assertCreatesArcPoint(Constants.INHIBARC, false);
    }

    @Test
    public void addsInhibitionArcWithShiftDownPoint() {
        assertCreatesArcPoint(Constants.INHIBARC, true);
    }

    @Test
    public void doesNotAddInhibitionArcPointIfNotCreating() {
        assertDoesNotCreateArcPoint(Constants.INHIBARC);
    }

    private void assertPetrinetNotifiesObservers(int arcType)
    {
        when(mockModel.getMode()).thenReturn(arcType);
        handler.mousePressed(mockEvent);

        verify(mockNet).notifyObservers();
    }

    private void assertCreatesArcPoint(int arcType, boolean shiftDown)
    {
        when (mockModel.getMode()).thenReturn(arcType);
        when(mockController.isCurrentlyCreatingArc()).thenReturn(true);

        when (mockEvent.isShiftDown()).thenReturn(shiftDown);
        handler.mousePressed(mockEvent);

        verify(mockController).addArcPoint(Grid.getModifiedX(0), Grid.getModifiedY(0), shiftDown);
    }

    private void assertDoesNotCreateArcPoint(int arcType)
    {
        when (mockModel.getMode()).thenReturn(arcType);
        when(mockController.isCurrentlyCreatingArc()).thenReturn(false);

        handler.mousePressed(mockEvent);

        verify(mockController, never()).addArcPoint(anyInt(), anyInt(), anyBoolean());
    }

}
