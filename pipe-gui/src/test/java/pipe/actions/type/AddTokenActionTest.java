package pipe.actions.type;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.controllers.PlaceController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.views.PipeApplicationView;

import static org.mockito.Mockito.*;

public class AddTokenActionTest {
    private AddTokenAction action;
    private PetriNetController mockPetriNetController;
    private PlaceController mockPlaceController;
    private Place place;
    private Token mockToken;
    private PipeApplicationView mockView;


    @Before
    public void setUp() {
        action = new AddTokenAction();
        mockPetriNetController = mock(PetriNetController.class);
        mockPlaceController = mock(PlaceController.class);
        place = mock(Place.class);
        when(mockPetriNetController.getPlaceController(place)).thenReturn(mockPlaceController);

        mockToken = mock(Token.class);
        mockView = mock(PipeApplicationView.class);
        ApplicationSettings.register(mockView);


    }

    @Test
    public void addsToken() {
        when(mockPetriNetController.getSelectedToken()).thenReturn(mockToken);

        action.doConnectableAction(place, mockPetriNetController);

        verify(mockPlaceController, times(1)).addTokenToPlace(mockToken);
    }
}
