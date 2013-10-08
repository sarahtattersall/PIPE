/*
 * DeleteArcPathPointEdit.java
 */

package pipe.historyActions;

import pipe.views.ArcView;
import pipe.views.viewComponents.ArcPath;
import pipe.views.viewComponents.ArcPathPoint;

/**
 *
 * @author Pere Bonet
 */
public class DeleteArcPathPoint
        extends HistoryItem
{
   
   private final ArcPath arcPath;
   private final ArcPathPoint point;
   private final Integer index;

   /** Creates a new instance of placeWeightEdit
    * @param _arc
    * @param _point
    * @param _index*/
   public DeleteArcPathPoint(ArcView _arc, ArcPathPoint _point, Integer _index) {
      arcPath = _arc.getArcPath();
      point = _point;
      index = _index;
   }

   
   /** */
   public void undo() {
      arcPath.insertPoint(index, point);
      arcPath.updateArc();      
   }

   
   /** */
   public void redo() {
      point.delete();
   }
   
}
