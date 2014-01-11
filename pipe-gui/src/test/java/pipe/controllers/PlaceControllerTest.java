package pipe.controllers;

import org.junit.Before;
import org.junit.Test;
import pipe.historyActions.HistoryItem;
import pipe.historyActions.HistoryManager;
import pipe.historyActions.PlaceCapacity;
import pipe.historyActions.PlaceMarking;
import pipe.models.component.Place;
import pipe.models.component.Token;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

public class PlaceControllerTest {
    Place place;
    HistoryManager historyManager;
    PlaceController placeController;

    @Before
    public void setUp() {
        place = mock(Place.class);
        historyManager = mock(HistoryManager.class);
        placeController = new PlaceController(place, historyManager);
    }

    @Test
    public void setCapacityCreatesNewHistoryItem() {
        double oldCapacity = 5;
        double newCapacity = 10;
        when(place.getCapacity()).thenReturn(oldCapacity);

        placeController.setCapacity(newCapacity);

        HistoryItem capacityItem =
                new PlaceCapacity(place, oldCapacity, newCapacity);
        verify(historyManager).addNewEdit(capacityItem);
    }

    @Test
    public void setCapacityModifiesPlaceCapacity() {
        double oldCapacity = 5;
        double newCapacity = 10;
        when(place.getCapacity()).thenReturn(oldCapacity);

        placeController.setCapacity(newCapacity);
        verify(place).setCapacity(newCapacity);
    }

    @Test
    public void setTokenCountsCreatesHistoryItem() {
        Map<Token, Integer> tokenCounts = new HashMap<Token, Integer>();
        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        int oldCount = 7;
        int newCount = 5;

        when(place.getTokenCount(defaultToken)).thenReturn(oldCount);
        tokenCounts.put(defaultToken, newCount);
        placeController.setTokenCounts(tokenCounts);

        verify(historyManager).newEdit();

        HistoryItem placeMarking =
                new PlaceMarking(place, defaultToken, oldCount, newCount);
        verify(historyManager).addEdit(placeMarking);
    }

    @Test
    public void setTokenCountModifiesPlace() {
        Map<Token, Integer> tokenCounts = new HashMap<Token, Integer>();
        Token defaultToken = new Token("Default", true, 0, new Color(0, 0, 0));
        int oldCount = 7;
        int newCount = 5;

        when(place.getTokenCount(defaultToken)).thenReturn(oldCount);
        tokenCounts.put(defaultToken, newCount);
        placeController.setTokenCounts(tokenCounts);

        verify(place).setTokenCount(defaultToken, newCount);
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

        verify(historyManager).newEdit();
        HistoryItem placeMarking =
                new PlaceMarking(place, defaultToken, oldCount, oldCount + 1);
        verify(historyManager).addEdit(placeMarking);
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

        verify(historyManager).newEdit();
        HistoryItem placeMarking =
                new PlaceMarking(place, defaultToken, oldCount, oldCount - 1);
        verify(historyManager).addEdit(placeMarking);
    }

}
