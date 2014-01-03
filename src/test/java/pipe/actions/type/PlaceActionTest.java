package pipe.actions.type;

import matchers.component.HasId;
import matchers.component.HasMultiple;
import matchers.component.HasXY;
import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.gui.Grid;
import pipe.historyActions.AddPetriNetObject;
import pipe.historyActions.HistoryManager;
import pipe.models.PetriNet;
import pipe.models.component.Place;

import java.awt.*;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PlaceActionTest {

    private PetriNetController mockController;
    private HistoryManager mockHistory;
    private PetriNet mockNet;
    private PlaceAction action;


    @Before
    public void setUp() {
        action = new PlaceAction("Place", Constants.PLACE, "place", "p");
        mockController = mock(PetriNetController.class);
        mockNet = mock(PetriNet.class);
        when(mockController.getPetriNet()).thenReturn(mockNet);

        mockHistory = mock(HistoryManager.class);
        when(mockController.getHistoryManager()).thenReturn(mockHistory);
    }


    @Test
    public void createsPlace() {

        Point point = new Point(10, 20);

        action.doAction(point, mockController);

        verify(mockNet).addPlace(argThat(
                new HasMultiple<Place>(
                        new HasXY(Grid.getModifiedX(point.getX()), Grid.getModifiedY(point.getY()))
                )
        ));
    }

    @Test
    public void createsUndoAction() {
        Point point = new Point(10, 20);

        action.doAction(point, mockController);

        verify(mockHistory).addNewEdit(any(AddPetriNetObject.class));
    }
}
