/*
 * PlaceCapacityEdit.java
 */

package pipe.historyActions;

import pipe.views.PlaceView;

/**
 *
 * @author corveau
 */
public class PlaceCapacity
        extends HistoryItem
{
   
   private final PlaceView _placeView;
   private final Integer newCapacity;
   private final Integer oldCapacity;
   
   
   /**
    * Creates a new instance of PlaceCapacityEdit
    * @param _placeView
    * @param _oldCapacity
    * @param _newCapacity
    */
   public PlaceCapacity(PlaceView _placeView,
                        Integer _oldCapacity, Integer _newCapacity) {
      this._placeView = _placeView;
      oldCapacity = _oldCapacity;      
      newCapacity = _newCapacity;
   }

   
   /** */
   public void undo() {
      _placeView.setCapacity(oldCapacity);
   }
   

   /** */
   public void redo() {
      _placeView.setCapacity(newCapacity);
   }
   
}
