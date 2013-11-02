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
import pipe.models.PetriNet;
import pipe.models.PipeApplicationModel;
import pipe.models.Place;
import pipe.views.PetriNetView;

import java.awt.*;
import java.awt.event.MouseEvent;

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

        verify(mockNet).addPlace(argThat(new PlaceHasXY(Grid.getModifiedX(0), Grid.getModifiedY(0))));
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


    /**
     * Matcher that asserts the x and y values are the same
     */
    private class PlaceHasXY extends ArgumentMatcher<Place> {
        double x;
        double y;

        public PlaceHasXY(double x, double y)
        {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean matches(Object item) {
            Place place = (Place) item;
            return place.getX() == x &&
                   place.getY() == y;
        }
    }
}
