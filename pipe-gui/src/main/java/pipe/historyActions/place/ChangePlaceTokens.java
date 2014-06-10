/*
 * PlaceMarkingEdit.java
 */

package pipe.historyActions.place;


import uk.ac.imperial.pipe.models.petrinet.Place;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * Responsible for changing the value of a single token in a place
 */
public final class ChangePlaceTokens extends AbstractUndoableEdit {

    /**
     * Place model
     */
    private final Place place;

    /**
     * Token id
     */
    private final String token;

    /**
     * Previous number of tokens stored in the place for the token id
     */
    private final int previousCount;

    /**
     * New number of tokens stored in the place for the token id
     */
    private final int newCount;

    /**
     * Constructor
     * @param place underlying place model
     * @param token token id
     * @param previousCount previous number of tokens stored in the place for the token id
     * @param newCount new number of tokens stored in the place for the token id
     */
    public ChangePlaceTokens(Place place, String token, int previousCount, int newCount) {
        this.place = place;
        this.token = token;
        this.previousCount = previousCount;
        this.newCount = newCount;
    }

    @Override
    public int hashCode() {
        int result = place != null ? place.hashCode() : 0;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + previousCount;
        result = 31 * result + newCount;
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final ChangePlaceTokens that = (ChangePlaceTokens) o;

        if (newCount != that.newCount) {
            return false;
        }
        if (previousCount != that.previousCount) {
            return false;
        }
        if (place != null ? !place.equals(that.place) : that.place != null) {
            return false;
        }
        if (token != null ? !token.equals(that.token) : that.token != null) {
            return false;
        }

        return true;
    }

    /**
     * Sets the token count for the token id to the previous count
     */
    @Override
    public void undo() {
        super.undo();
        place.setTokenCount(token, previousCount);

    }

    /**
     * Sets the token count for the token id to the new count
     */
    @Override
    public void redo() {
        super.redo();
        place.setTokenCount(token, newCount);

    }

}
