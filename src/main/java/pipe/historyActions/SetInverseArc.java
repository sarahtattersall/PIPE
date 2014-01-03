/*
 * SetInverseArcEdit.java
 */
package pipe.historyActions;


import pipe.views.ArcView;

/**
 *
 * @author Pere Bonet
 */
public class SetInverseArc
        extends HistoryItem
{
   
   private final ArcView arc;
   private final ArcView inverse;
   private final boolean junts;
   
   
   /** Creates a new instance of placeRateEdit
    * @param _arc
    * @param _inverse
    * @param _junts*/
   public SetInverseArc(ArcView _arc, ArcView _inverse, boolean _junts){
      arc = _arc;
      inverse = _inverse;
      junts = _junts;
   }

   
   /** */
   public void undo() {
//      arc.clearInverse();
   }

   
   /** */
   public void redo() {
//      inverse.setInverse(arc, junts);
   }
   
}
