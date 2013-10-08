/*
 * ArcPathPointTypeEdit.java
 */

package pipe.historyActions;

import pipe.views.viewComponents.ArcPathPoint;

/**
 *
 * @author corveau
 */
public class ArcPathPointType
        extends HistoryItem
{
   
   private final ArcPathPoint arcPathPoint;
  
   
   /** Creates a new instance of placeWeightEdit
    * @param _arcPathPoint*/
   public ArcPathPointType(ArcPathPoint _arcPathPoint) {
      arcPathPoint = _arcPathPoint;
   }
   
   
   /** */
   public void undo() {
      arcPathPoint.togglePointType();
   }

   
   /** */
   public void redo() {
      arcPathPoint.togglePointType();
   }

   
   
   public String toString(){
      return super.toString() + " " + arcPathPoint.getName();
   }
      
}
