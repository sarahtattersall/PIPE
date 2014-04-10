package pipe.actions.type;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.actions.gui.create.AddTokenAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PlaceController;
import pipe.historyActions.ChangePlaceTokens;
import pipe.historyActions.MultipleEdit;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.utilities.transformers.Contains;
import pipe.views.PipeApplicationView;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AddTokenActionTest {
    private AddTokenAction action;

    @Mock
    private PetriNetController mockPetriNetController;

    @Mock
    private PlaceController mockPlaceController;

    @Mock
    private Place place;

    @Mock
    private Token mockToken;

    @Mock
    private PipeApplicationView mockView;

    @Mock
    UndoableEditListener listener;


    @Before
    public void setUp() {
        action = new AddTokenAction();
        action.addUndoableEditListener(listener);
        when(mockPetriNetController.getPlaceController(place)).thenReturn(mockPlaceController);
        when(mockPlaceController.getTokenCount(mockToken)).thenReturn(1);
    }

    @Test
    public void addsToken() {
        when(mockPetriNetController.getSelectedToken()).thenReturn(mockToken);

        action.doConnectableAction(place, mockPetriNetController);

        Map<Token, Integer> counts = new HashMap<>();
        counts.put(mockToken, 2);
        verify(mockPlaceController).setTokenCounts(counts);
    }

}
