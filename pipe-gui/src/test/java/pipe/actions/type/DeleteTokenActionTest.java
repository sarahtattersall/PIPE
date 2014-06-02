package pipe.actions.type;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.actions.gui.DeleteTokenAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PlaceController;
import pipe.actions.gui.PipeApplicationModel;
import uk.ac.imperial.pipe.models.petrinet.Place;

import javax.swing.event.UndoableEditListener;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteTokenActionTest {

    @Mock
    UndoableEditListener listener;

    @Mock
    PipeApplicationModel applicationModel;

    private DeleteTokenAction action;

    @Mock
    private PetriNetController mockPetriNetController;

    @Mock
    private PlaceController mockPlaceController;

    @Mock
    private Place place;

    private String tokenId = "Default";

    @Before
    public void setUp() {
        action = new DeleteTokenAction(applicationModel);
        action.addUndoableEditListener(listener);
        when(mockPetriNetController.getPlaceController(place)).thenReturn(mockPlaceController);
        when(mockPlaceController.getTokenCount(tokenId)).thenReturn(1);
    }

    @Test
    public void deletesToken() {
        when(mockPetriNetController.getSelectedToken()).thenReturn(tokenId);

        action.doConnectableAction(place, mockPetriNetController);

        Map<String, Integer> counts = new HashMap<>();
        counts.put(tokenId, 0);
        verify(mockPlaceController).setTokenCounts(counts);
    }

}
