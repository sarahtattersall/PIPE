/*
 * TagArcEdit.java
 */
package pipe.historyActions;


import pipe.views.NormalArcView;

/**
 *
 * @author corveau
 */
public class TagArc
        extends HistoryItem
{
   
   private final NormalArcView arc;
   
   
   /** Creates a new instance of TagArcEdit
    * @param _arc*/
   public TagArc(NormalArcView _arc) {
      arc = _arc;
   }

   
   /** */
   public void undo() {
      arc.setTagged(!arc.isTagged());
   }

   
   /** */
   public void redo() {
      arc.setTagged(!arc.isTagged());
   }
   
}
