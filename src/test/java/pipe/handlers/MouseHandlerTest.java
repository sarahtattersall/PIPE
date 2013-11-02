package pipe.handlers;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.Grid;
import pipe.gui.PetriNetTab;
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
        when(mockModel.getMode()).thenReturn(Constants.PLACE);
        handler.mousePressed(mockEvent);

        verify(mockNet).notifyObservers();
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
        when(mockModel.getMode()).thenReturn(Constants.TIMEDTRANS);
        handler.mousePressed(mockEvent);

        verify(mockNet).notifyObservers();
    }

    @Test
    public void notifyObserversOnTimedTransitionCreation() {
        when(mockModel.getMode()).thenReturn(Constants.IMMTRANS);
        handler.mousePressed(mockEvent);

        verify(mockNet).notifyObservers();
    }



    /**
     * Interface for Connectable mactchers
     */
    private interface Has<T> {
        public boolean matches(T component);
    }

    /**
     * Checks if connectable has given x and y
     */
    private class HasXY<T extends Connectable> implements Has<T> {

        double x;
        double y;
        HasXY(double x, double y) {
            this.x = x;
            this.y =y ;
        }

        @Override
        public boolean matches(T component) {
            return component.getX() == x && component.getY() == y;
        }
    }

    /**
     * Matcher that asserts the x and y values are the same
     */
    private class HasMultiple<T extends Connectable> extends ArgumentMatcher<T> {
        List<Has> has_items = new LinkedList<Has>();

        public HasMultiple(Has... items)
        {
            for (Has has : items) {
                has_items.add(has);
            }
        }

        @Override
        public boolean matches(Object item) {
            T connectable = (T) item;
            for (Has has : has_items)
            {
                if (!has.matches(connectable))
                {
                    return false;
                }
            }
            return true;
        }
    }

    private class HasTimed implements Has<Transition> {
        boolean timed;
        public HasTimed(boolean timed) {
            this.timed = timed;

        }

        @Override
        public boolean matches(Transition component) {
            return component.isTimed() == timed;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }
}
