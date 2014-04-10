/*
 * PlaceMarkingEdit.java
 */

package pipe.historyActions;

import pipe.models.component.place.Place;
import pipe.models.component.token.Token;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * 
 * @author corveau
 */
public class ChangePlaceTokens extends AbstractUndoableEdit
{

    private final Place place;
    private final Token token;
    private final int previousCount;
    private final int newCount;

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

    @Override
    public int hashCode() {
        int result = place != null ? place.hashCode() : 0;
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + previousCount;
        result = 31 * result + newCount;
        return result;
    }

    public ChangePlaceTokens(Place place, Token token, int previousCount, int newCount) {
		this.place = place;
        this.token = token;
        this.previousCount = previousCount;
        this.newCount = newCount;
    }

	@Override
    public void undo() {
        super.undo();
        place.setTokenCount(token, previousCount);

	}

	@Override
    public void redo() {
        super.redo();
        place.setTokenCount(token, newCount);

	}

}
