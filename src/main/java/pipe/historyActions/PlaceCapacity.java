/*
 * PlaceCapacityEdit.java
 */

package pipe.historyActions;

import pipe.models.PetriNet;
import pipe.models.Place;
import pipe.views.PlaceView;

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
    private final PetriNet petriNet;


    public PlaceCapacity(Place place, PetriNet petriNet, double oldCapacity, double newCapacity) {

        this.place = place;
        this.petriNet = petriNet;
        this.oldCapacity = oldCapacity;
        this.newCapacity = newCapacity;
    }


    /** */
   public void undo() {
      place.setCapacity(oldCapacity);
       petriNet.notifyObservers();
   }
   

   /** */
   public void redo() {
      place.setCapacity(newCapacity);
       petriNet.notifyObservers();
   }
   
}
