/*
 * PlaceCapacityEdit.java
 */

package pipe.historyActions;

import pipe.models.PetriNet;
import pipe.models.component.Place;

/**
 *
 * @author corveau
 */
public class PlaceCapacity
        extends HistoryItem
{

    private final double newCapacity;
    private final double oldCapacity;
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

    public PlaceCapacity(Place place, double oldCapacity, double newCapacity) {

        this.place = place;
        this.oldCapacity = oldCapacity;
        this.newCapacity = newCapacity;
    }


    /** */
   public void undo() {
      place.setCapacity(oldCapacity);
   }
   

   /** */
   public void redo() {
      place.setCapacity(newCapacity);
   }
   
}
