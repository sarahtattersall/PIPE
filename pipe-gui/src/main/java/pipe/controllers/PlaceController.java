package pipe.controllers;

import pipe.historyActions.MultipleEdit;
import pipe.historyActions.place.ChangePlaceTokens;
import pipe.historyActions.place.PlaceCapacity;
import uk.ac.imperial.pipe.models.petrinet.Place;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Controller responsible for interacting with a place Petri net component
 */
public class PlaceController extends AbstractConnectableController<Place> {

    /**
     * Underlying model
     */
    private final Place place;

    /**
     * Constructor
     * @param place underling model
     * @param listener listener for undo event creation
     */
    public PlaceController(Place place, UndoableEditListener listener) {
        super(place, listener);
        this.place = place;
    }

    /**
     * @param token token id
     * @return number of tokens of type token in the corresponding place
     */
    public int getTokenCount(String token) {
        return place.getTokenCount(token);
    }

    /**
     * Sets the place token counts to those in counts and registers an undo event for it
     *
     * @param counts a map of token id to its new count for the place
     */
    public void setTokenCounts(Map<String, Integer> counts) {
        List<UndoableEdit> undoableEditList = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            String token = entry.getKey();
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


    /**
     *
     * @return the place capacity
     */
    public int getCapacity() {
        return place.getCapacity();
    }

    /**
     *
     * @return the place name
     */
    public String getName() {
        return place.getId();
    }

    /**
     *
     * @return if the place has a capacity restriction
     */
    public boolean hasCapacityRestriction() {
        return place.hasCapacityRestriction();
    }


    /**
     *
     * Increments the value of the specified token in a place
     *
     * @param token token id
     */
    public void addTokenToPlace(String token) {
        Map<String, Integer> tokenCount = new HashMap<>();
        tokenCount.put(token, place.getTokenCount(token) + 1);
        setTokenCounts(tokenCount);
    }

    /**
     * Deletes a single token of the type of token specified from
     * the underlying place.
     *
     * For example if the place has 3 red tokens and 2 black tokens
     * and we call this with "red" the place will now have 2 red and two
     * black tokens.
     *
     * @param token token id to decrement the count of
     */
    //TODO: If it had 0?
    public void deleteTokenInPlace(String token) {
        Map<String, Integer> tokenCount = new HashMap<>();
        tokenCount.put(token, place.getTokenCount(token) - 1);
        setTokenCounts(tokenCount);
    }

}
