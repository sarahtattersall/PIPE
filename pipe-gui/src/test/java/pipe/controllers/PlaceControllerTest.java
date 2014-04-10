package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.historyActions.ChangePlaceTokens;
import pipe.historyActions.MultipleEdit;
import pipe.historyActions.PetriNetObjectName;
import pipe.historyActions.PlaceCapacity;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.utilities.transformers.Contains;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.awt.Color;
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
        Map<Token, Integer> tokenCounts = new HashMap<>();
        Token defaultToken = new Token("Default", true, 0, Color.BLACK);
        int oldCount = 7;
        int newCount = 5;

        when(place.getTokenCount(defaultToken)).thenReturn(oldCount);
        tokenCounts.put(defaultToken, newCount);
        placeController.setTokenCounts(tokenCounts);

        verify(place).setTokenCount(defaultToken, newCount);
    }

    @Test
    public void setTokenCountCreatesUndoItem() {
        Map<Token, Integer> tokenCounts = new HashMap<>();
        Token defaultToken = new Token("Default", true, 0, Color.BLACK);
        int oldCount = 7;
        int newCount = 5;

        when(place.getTokenCount(defaultToken)).thenReturn(oldCount);
        tokenCounts.put(defaultToken, newCount);
        placeController.setTokenCounts(tokenCounts);

        UndoableEdit changed = new ChangePlaceTokens(place, defaultToken, oldCount, newCount);
        MultipleEdit edit = new MultipleEdit(Arrays.asList(changed));
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(edit)));
    }

    @Test
    public void incrementsPlaceCounter() {
        int count = 1;
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        when(place.getTokenCount(token)).thenReturn(count);
        placeController.addTokenToPlace(token);
        verify(place).setTokenCount(token, count + 1);
    }

    @Test
    public void incrementPlaceCounterCreatesHistoryItem() {
        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        int oldCount = 7;

        when(place.getTokenCount(defaultToken)).thenReturn(oldCount);
        placeController.addTokenToPlace(defaultToken);

        UndoableEdit changed = new ChangePlaceTokens(place, defaultToken, oldCount, oldCount + 1);
        MultipleEdit edit = new MultipleEdit(Arrays.asList(changed));
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(edit)));
    }

    @Test
    public void decrementsPlaceCounter() {
        int count = 1;
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        when(place.getTokenCount(token)).thenReturn(count);
        placeController.deleteTokenInPlace(token);
        verify(place).setTokenCount(token, count - 1);
    }

    @Test
    public void decrementPlaceCounterCreatesHistoryItem() {
        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        int oldCount = 7;

        when(place.getTokenCount(defaultToken)).thenReturn(oldCount);
        placeController.deleteTokenInPlace(defaultToken);

        UndoableEdit changed = new ChangePlaceTokens(place, defaultToken, oldCount, oldCount - 1);
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

        UndoableEdit nameEdit = new PetriNetObjectName(place, oldName, newName);
        verify(listener).undoableEditHappened(argThat(Contains.thisAction(nameEdit)));
    }

}
