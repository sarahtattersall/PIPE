package pipe.controllers;

import pipe.historyActions.HistoryManager;
import pipe.historyActions.PlaceCapacity;
import pipe.historyActions.PlaceMarking;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;

import java.util.HashMap;
import java.util.Map;

public class PlaceController
        extends AbstractPetriNetComponentController<Place> {

    private final Place place;
    private final HistoryManager historyManager;

    public PlaceController(Place place,
                           HistoryManager historyManager) {
        super(place, historyManager);
        this.place = place;
        this.historyManager = historyManager;
    }

    public int getTokenCount(Token token) {
        return place.getTokenCount(token);
    }

    public void setTokenCounts(Map<Token, Integer> counts) {
        historyManager.newEdit();
        for (Map.Entry<Token, Integer> entry : counts.entrySet()) {
            Token token = entry.getKey();
            Integer newTokenCount = entry.getValue();
            int currentTokenCount = place.getTokenCount(token);

            PlaceMarking markingAction =
                    new PlaceMarking(place, token, currentTokenCount,
                            newTokenCount);
            markingAction.redo();

            historyManager.addEdit(markingAction);

        }

    }

    public void setCapacity(int capacity) {
        int oldCapacity = place.getCapacity();
        PlaceCapacity capacityAction =
                new PlaceCapacity(place, oldCapacity, capacity);
        capacityAction.redo();
        historyManager.addNewEdit(capacityAction);
    }

    public double getCapacity() {
        return place.getCapacity();
    }

    public String getName() {
        return place.getName();
    }

    public boolean hasCapacityRestriction() {
        return place.hasCapacityRestriction();
    }


    public void addTokenToPlace(Token token) {
        Map<Token, Integer> tokenCount = new HashMap<Token, Integer>();
        tokenCount.put(token, place.getTokenCount(token) + 1);
        this.setTokenCounts(tokenCount);
    }

    public void deleteTokenInPlace(Token token) {
        Map<Token, Integer> tokenCount = new HashMap<Token, Integer>();
        tokenCount.put(token, place.getTokenCount(token) - 1);
        this.setTokenCounts(tokenCount);
    }

}
