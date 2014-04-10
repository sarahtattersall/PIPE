package pipe.controllers;

import pipe.historyActions.PlaceCapacity;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;

import java.util.Map;

public class PlaceController extends AbstractPetriNetComponentController<Place> {

    private final Place place;

    public PlaceController(Place place) {
        super(place);
        this.place = place;
    }

    public int getTokenCount(Token token) {
        return place.getTokenCount(token);
    }

    public void setTokenCounts(Map<Token, Integer> counts) {
        //        historyManager.newEdit();
        //        for (Map.Entry<Token, Integer> entry : counts.entrySet()) {
        //            Token token = entry.getKey();
        //            Integer newTokenCount = entry.getValue();
        //            int currentTokenCount = place.getTokenCount(token);
        //
        //            ChangePlaceTokens markingAction =
        //                    new ChangePlaceTokens(place, token, currentTokenCount,
        //                            newTokenCount);
        //            markingAction.redo();
        //
        //            historyManager.addEdit(markingAction);
        //
        //        }

    }

    public void setCapacity(int capacity) {
        int oldCapacity = place.getCapacity();
        PlaceCapacity capacityAction = new PlaceCapacity(place, oldCapacity, capacity);
        capacityAction.redo();
        //        historyManager.addNewEdit(capacityAction);
    }

    public int getCapacity() {
        return place.getCapacity();
    }

    public String getName() {
        return place.getName();
    }

    public boolean hasCapacityRestriction() {
        return place.hasCapacityRestriction();
    }


    public void addTokenToPlace(Token token) {
        //        Map<Token, Integer> tokenCount = new HashMap<>();
        //        tokenCount.put(token, place.getTokenCount(token) + 1);
        //        setTokenCounts(tokenCount);
    }

    public void deleteTokenInPlace(Token token) {
        //        Map<Token, Integer> tokenCount = new HashMap<>();
        //        tokenCount.put(token, place.getTokenCount(token) - 1);
        //        setTokenCounts(tokenCount);
    }

}
