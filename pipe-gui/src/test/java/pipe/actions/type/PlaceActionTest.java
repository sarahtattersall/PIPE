package pipe.actions.type;

import matchers.component.HasMultiple;
import matchers.component.HasXY;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.actions.gui.create.PlaceAction;
import pipe.controllers.PetriNetController;
import pipe.models.component.place.Place;
import pipe.models.petrinet.PetriNet;

import java.awt.Point;
import java.awt.event.MouseEvent;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PlaceActionTest {

    @Mock
    private PetriNetController mockController;

    @Mock
    private PetriNet mockNet;

    private PlaceAction action;

    @Before
    public void setUp() {
        action = new PlaceAction();
        when(mockController.getPetriNet()).thenReturn(mockNet);
    }

    @Test
    public void createsPlaceOnClick() {

        Point point = new Point(10, 20);
        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getClickCount()).thenReturn(1);
        when(mockEvent.getPoint()).thenReturn(point);

        action.doAction(mockEvent, mockController);

        verify(mockNet).addPlace(argThat(new HasMultiple<Place>(new HasXY(point.getX(), point.getY()))));
    }

    @Test
    public void createsUndoOnClickAction() {
        Point point = new Point(10, 20);
        MouseEvent mockEvent = mock(MouseEvent.class);
        when(mockEvent.getClickCount()).thenReturn(1);
        when(mockEvent.getPoint()).thenReturn(point);

        action.doAction(mockEvent, mockController);

        //        verify(mockHistory).addNewEdit(any(AddPetriNetObject.class));
    }
}
