/*
 * PlaceCapacityEdit.java
 */

package pipe.historyActions.place;


import uk.ac.imperial.pipe.models.petrinet.Place;

import javax.swing.undo.AbstractUndoableEdit;

/**
 * @author corveau
 */
public class PlaceCapacity extends AbstractUndoableEdit {

    private final int newCapacity;

    private final int oldCapacity;

    private final Place place;

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        final PlaceCapacity capacity = (PlaceCapacity) o;

        if (Double.compare(capacity.newCapacity, newCapacity) != 0) {
            return false;
        }
        if (Double.compare(capacity.oldCapacity, oldCapacity) != 0) {
            return false;
        }
        if (!place.equals(capacity.place)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(newCapacity);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(oldCapacity);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + place.hashCode();
        return result;
    }

    public PlaceCapacity(Place place, int oldCapacity, int newCapacity) {

        this.place = place;
        this.oldCapacity = oldCapacity;
        this.newCapacity = newCapacity;
    }


    /** */
    @Override
    public void undo() {
        super.undo();
        place.setCapacity(oldCapacity);
    }


    /** */
    @Override
    public void redo() {
        super.redo();
        place.setCapacity(newCapacity);
    }

}
