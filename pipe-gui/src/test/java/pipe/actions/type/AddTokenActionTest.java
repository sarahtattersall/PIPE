package pipe.actions.type;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import javax.swing.event.UndoableEditListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pipe.actions.gui.AddTokenAction;
import pipe.actions.gui.PipeApplicationModel;
import pipe.controllers.PetriNetController;
import pipe.controllers.PlaceController;
import pipe.views.PipeApplicationView;
import uk.ac.imperial.pipe.models.petrinet.Place;

@RunWith(MockitoJUnitRunner.class)
public class AddTokenActionTest {
    private AddTokenAction action;

    @Mock
    private PetriNetController mockPetriNetController;

    @Mock
    private PlaceController mockPlaceController;

    @Mock
    private Place place;

    private static final String TOKEN_ID = "Default";

    @Mock
    private PipeApplicationView mockView;

    @Mock
    UndoableEditListener listener;

    @Mock
    PipeApplicationModel applicationModel;

    @Before
    public void setUp() {
        action = new AddTokenAction(applicationModel);
        action.addUndoableEditListener(listener);
        when(mockPetriNetController.getPlaceController(place)).thenReturn(mockPlaceController);
        when(mockPlaceController.getTokenCount(TOKEN_ID)).thenReturn(1);
    }

    @Test
    public void addsToken() {
        when(mockPetriNetController.getSelectedToken()).thenReturn(TOKEN_ID);

        action.doConnectableAction(place, mockPetriNetController);

        Map<String, Integer> counts = new HashMap<>();
        counts.put(TOKEN_ID, 2);
        verify(mockPlaceController).setTokenCounts(counts);
    }

}
