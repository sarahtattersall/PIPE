/*
 * SplitInverseArcEdit.java
 */
package pipe.historyActions;


import pipe.views.NormalArcView;

/**
 *
 * @author corveau
 */
public class SplitInverseArc
        extends HistoryItem
{
   
   private final NormalArcView arc;
   
   
   /** Creates a new instance of placeRateEdit
    * @param _arc*/
   public SplitInverseArc(NormalArcView _arc) {
      arc = _arc;
   }

   
   /** */
   public void undo() {
      arc.join();
   }

   
   /** */
   public void redo() {
      arc.split();
   }
   
}
