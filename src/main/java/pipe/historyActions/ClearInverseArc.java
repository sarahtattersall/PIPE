/*
 * ClearInverseArcEdit.java
 */
package pipe.historyActions;


import pipe.views.NormalArcView;

/**
 *
 * @author corveau
 */
public class ClearInverseArc
        extends HistoryItem
{
   
   private final NormalArcView arc;
   private final NormalArcView inverse;
   private final boolean junts;
   
   
   /** Creates a new instance of placeRateEdit
    * @param _arc
    * @param _inverse
    * @param _junts*/
   public ClearInverseArc(NormalArcView _arc, NormalArcView _inverse, boolean _junts){
      arc = _arc;
      inverse = _inverse;
      junts = _junts;
   }

   
   /** */
   public void undo() {
      inverse.setInverse(arc, junts);
   }

   
   /** */
   public void redo() {
      arc.clearInverse();
   }
   
}
