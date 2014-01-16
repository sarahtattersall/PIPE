/*
 * ArcPathPointTypeEdit.java
 */

package pipe.historyActions;

import pipe.models.component.arc.ArcPoint;


public class ArcPathPointType
        extends HistoryItem
{
   
   private final ArcPoint arcPoint;
  
   
   public ArcPathPointType(ArcPoint arcPoint) {
      this.arcPoint = arcPoint;
   }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ArcPathPointType that = (ArcPathPointType) o;

        if (!arcPoint.equals(that.arcPoint)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return arcPoint.hashCode();
    }

    @Override
   public void undo() {
      arcPoint.setCurved(!arcPoint.isCurved());
   }

   
   /** */
   @Override
   public void redo() {
       arcPoint.setCurved(!arcPoint.isCurved());
   }
}
