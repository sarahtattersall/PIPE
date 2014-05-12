package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.historyActions.MultipleEdit;
import pipe.historyActions.component.ChangePetriNetComponentName;
import pipe.historyActions.place.ChangePlaceTokens;
import pipe.historyActions.place.PlaceCapacity;
import pipe.utilities.transformers.Contains;
import uk.ac.imperial.pipe.models.component.place.Place;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlaceControllerTest {
    @Mock
    Place place;

    @Mock
    UndoableEditListener listener;

    PlaceController placeController;

    private final static String DEFAULT_TOKEN_ID = "Default";

    @Before
    public void setUp() {
        placeController = new PlaceController(place, listener);
    }

    @Test
    public void setCapacityCreatesNewHistoryItem() {
        int oldCapacity = 5;
        int newCapacity = 10;
        when(place.getCapacity()).thenReturn(oldCapacity);

        placeController.setCapacity(newCapacity);

        UndoableEdit capacityItem = new PlaceCapacity(place, oldCapacity, newCapacity);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(capacityItem)));
    }

    @Test
    public void setCapacityModifiesPlaceCapacity() {
        int oldCapacity = 5;
        int newCapacity = 10;
        when(place.getCapacity()).thenReturn(oldCapacity);

        placeController.setCapacity(newCapacity);
        verify(place).setCapacity(newCapacity);
    }


    @Test
    public void setTokenCountModifiesPlace() {
        Map<String, Integer> tokenCounts = new HashMap<>();
        int oldCount = 7;
        int newCount = 5;

        when(place.getTokenCount(DEFAULT_TOKEN_ID)).thenReturn(oldCount);
        tokenCounts.put(DEFAULT_TOKEN_ID, newCount);
        placeController.setTokenCounts(tokenCounts);

        verify(place).setTokenCount(DEFAULT_TOKEN_ID, newCount);
    }

    @Test
    public void setTokenCountCreatesUndoItem() {
        Map<String, Integer> tokenCounts = new HashMap<>();
        int oldCount = 7;
        int newCount = 5;

        when(place.getTokenCount(DEFAULT_TOKEN_ID)).thenReturn(oldCount);
        tokenCounts.put(DEFAULT_TOKEN_ID, newCount);
        placeController.setTokenCounts(tokenCounts);

        UndoableEdit changed = new ChangePlaceTokens(place, DEFAULT_TOKEN_ID, oldCount, newCount);
        MultipleEdit edit = new MultipleEdit(Arrays.asList(changed));
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(edit)));
    }

    @Test
    public void incrementsPlaceCounter() {
        int count = 1;
        when(place.getTokenCount(DEFAULT_TOKEN_ID)).thenReturn(count);
        placeController.addTokenToPlace(DEFAULT_TOKEN_ID);
        verify(place).setTokenCount(DEFAULT_TOKEN_ID, count + 1);
    }

    @Test
    public void incrementPlaceCounterCreatesHistoryItem() {
        int oldCount = 7;

        when(place.getTokenCount(DEFAULT_TOKEN_ID)).thenReturn(oldCount);
        placeController.addTokenToPlace(DEFAULT_TOKEN_ID);

        UndoableEdit changed = new ChangePlaceTokens(place, DEFAULT_TOKEN_ID, oldCount, oldCount + 1);
        MultipleEdit edit = new MultipleEdit(Arrays.asList(changed));
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(edit)));
    }

    @Test
    public void decrementsPlaceCounter() {
        int count = 1;
        when(place.getTokenCount(DEFAULT_TOKEN_ID)).thenReturn(count);
        placeController.deleteTokenInPlace(DEFAULT_TOKEN_ID);
        verify(place).setTokenCount(DEFAULT_TOKEN_ID, count - 1);
    }

    @Test
    public void decrementPlaceCounterCreatesHistoryItem() {
        int oldCount = 7;

        when(place.getTokenCount(DEFAULT_TOKEN_ID)).thenReturn(oldCount);
        placeController.deleteTokenInPlace(DEFAULT_TOKEN_ID);

        UndoableEdit changed = new ChangePlaceTokens(place, DEFAULT_TOKEN_ID, oldCount, oldCount - 1);
        MultipleEdit edit = new MultipleEdit(Arrays.asList(changed));
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(edit)));
    }

    @Test
    public void setNameChangesName() {
        String newName = "newName";
        placeController.setName(newName);
        verify(place).setId(newName);
        verify(place).setName(newName);
    }


    @Test
    public void setNameCreatesUndoItem() {
        String oldName = "oldName";
        String newName = "newName";
        when(place.getId()).thenReturn(oldName);
        placeController.setName(newName);

        UndoableEdit nameEdit = new ChangePetriNetComponentName(place, oldName, newName);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(nameEdit)));
    }

}
