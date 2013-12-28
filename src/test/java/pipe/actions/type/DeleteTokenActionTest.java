package pipe.actions.type;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.controllers.PlaceController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.models.component.Place;
import pipe.models.component.Token;
import pipe.views.PipeApplicationView;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DeleteTokenActionTest {

    private DeleteTokenAction action;
    private PetriNetController mockPetriNetController;
    private PlaceController mockPlaceController;
    private Place place;
    private Token mockToken;
    private PipeApplicationView mockView;


    @Before
    public void setUp() {
        action = new DeleteTokenAction("Delete token", Constants.DELTOKEN, "Delete a token", "SUBTRACT");
        mockPetriNetController = mock(PetriNetController.class);
        mockPlaceController = mock(PlaceController.class);
        place = mock(Place.class);
        when(mockPetriNetController.getPlaceController(place)).thenReturn(mockPlaceController);

        mockToken = mock(Token.class);
        mockView = mock(PipeApplicationView.class);
        ApplicationSettings.register(mockView);


    }

    @Test
    public void deletesToken() {
        String tokenName = "Name";
        when(mockView.getSelectedTokenName()).thenReturn(tokenName);
        when(mockPetriNetController.getToken(tokenName)).thenReturn(mockToken);

        action.doConnectableAction(place, mockPetriNetController);

        verify(mockPlaceController).deleteTokenInPlace(mockToken);
    }
}
