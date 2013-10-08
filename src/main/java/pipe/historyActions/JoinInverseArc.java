/*
 * JoinInverseArcEdit.java
 */
package pipe.historyActions;
import pipe.views.NormalArcView;

/**
 *
 * @author corveau
 */
public class JoinInverseArc
        extends HistoryItem
{
   
   private final NormalArcView arc;
   
   
   /** Creates a new instance of placeRateEdit
    * @param _arc*/
   public JoinInverseArc(NormalArcView _arc) {
      arc = _arc;
   }

   
   /** */
   public void undo() {
      arc.split();
   }

   
   /** */
   public void redo() {
      arc.join();
   }
   
}
