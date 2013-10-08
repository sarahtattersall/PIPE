/*
 * AnnotationBorderEdit.java
 */

package pipe.historyActions;

import pipe.views.viewComponents.Note;

/**
 *
 * @author corveau
 */
public class AnnotationBorder
        extends HistoryItem
{
   
   private final Note note;
   
   
   /** Creates a new instance of placeRateEdit
    * @param _note*/
   public AnnotationBorder(Note _note) {
      note = _note;
   }
   
   
   /** */
   public void undo() {
      note.showBorder(!note.isShowingBorder());
   }

   
   /** */
   public void redo() {
      note.showBorder(!note.isShowingBorder());
   }
   
   
   public String toString(){
      return super.toString() + " " + note.getClass().getSimpleName();
   }
   
}
