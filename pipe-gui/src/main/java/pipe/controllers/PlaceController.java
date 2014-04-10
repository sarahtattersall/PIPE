package pipe.controllers;

import pipe.historyActions.ChangePlaceTokens;
import pipe.historyActions.MultipleEdit;
import pipe.historyActions.PlaceCapacity;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlaceController extends AbstractPetriNetComponentController<Place> {

    private final Place place;


    public PlaceController(Place place, UndoableEditListener listener) {
        super(place, listener);
        this.place = place;
    }

    /**
     * @param token
     * @return number of tokens of type token in the corresponding place
     */
    public int getTokenCount(Token token) {
        return place.getTokenCount(token);
    }

    /**
     * Sets the place token counts to those in counts and registers an undo event for it
     *
     * @param counts a map of token to its new count for the place
     */
    public void setTokenCounts(Map<Token, Integer> counts) {
        List<UndoableEdit> undoableEditList = new LinkedList<>();
        for (Map.Entry<Token, Integer> entry : counts.entrySet()) {
            Token token = entry.getKey();
            Integer newTokenCount = entry.getValue();
            int currentTokenCount = place.getTokenCount(token);
            UndoableEdit markingAction = new ChangePlaceTokens(place, token, currentTokenCount, newTokenCount);
            place.setTokenCount(token, newTokenCount);
            undoableEditList.add(markingAction);

        }
        registerUndoableEdit(new MultipleEdit(undoableEditList));
    }

    /**
     * Updates the place's capacity and regisers an UndoableEdit
     *
     * @param capacity new capacity for place
     */
    public void setCapacity(int capacity) {
        int oldCapacity = place.getCapacity();
        place.setCapacity(capacity);
        UndoableEdit capacityAction = new PlaceCapacity(place, oldCapacity, capacity);
        registerUndoableEdit(capacityAction);
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
        Map<Token, Integer> tokenCount = new HashMap<>();
        tokenCount.put(token, place.getTokenCount(token) + 1);
        setTokenCounts(tokenCount);
    }

    public void deleteTokenInPlace(Token token) {
        Map<Token, Integer> tokenCount = new HashMap<>();
        tokenCount.put(token, place.getTokenCount(token) - 1);
        setTokenCounts(tokenCount);
    }

}
