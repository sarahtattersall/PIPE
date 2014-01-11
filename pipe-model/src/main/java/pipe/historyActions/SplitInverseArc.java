/*
 * SplitInverseArcEdit.java
 */
package pipe.historyActions;


import pipe.views.ArcView;

/**
 *
 * @author corveau
 */
public class SplitInverseArc
        extends HistoryItem
{
   
   private final ArcView arc;
   
   
   /** Creates a new instance of placeRateEdit
    * @param _arc*/
   public SplitInverseArc(ArcView _arc) {
      arc = _arc;
   }

   
   /** */
   public void undo() {
//      arc.join();
   }

   
   /** */
   public void redo() {
//      arc.split();
   }
   
}
