/*
 * ClearInverseArcEdit.java
 */
package pipe.historyActions.arc;


import pipe.historyActions.HistoryItem;
import pipe.views.ArcView;

/**
 *
 * @author corveau
 */
public class ClearInverseArc
        extends HistoryItem
{
   
   private final ArcView arc;
   private final ArcView inverse;
   private final boolean junts;
   
   
   /** Creates a new instance of placeRateEdit
    * @param _arc
    * @param _inverse
    * @param _junts*/
   public ClearInverseArc(ArcView _arc, ArcView _inverse, boolean _junts){
      arc = _arc;
      inverse = _inverse;
      junts = _junts;
   }

   
   /** */
   public void undo() {
//      inverse.setInverse(arc, junts);
   }

   
   /** */
   public void redo() {
//      arc.clearInverse();
   }
   
}
