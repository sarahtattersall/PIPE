/*
 * AnnotationTextEdit.java
 */

package pipe.historyActions;

import pipe.views.viewComponents.AnnotationNote;

/**
 *
 * @author corveau
 */
public final class AnnotationText
        extends HistoryItem
{
   
   private final AnnotationNote annotationNote;
   private final String oldText;
   private final String newText;
   
   
   /** Creates a new instance of placeRateEdit
    * @param _annotationNote
    * @param _oldText
    * @param _newText*/
   public AnnotationText(AnnotationNote _annotationNote,
                         String _oldText, String _newText) {
      annotationNote = _annotationNote;
      oldText = _oldText;
      newText = _newText;
   }

   
   /** */
   public void undo() {
      annotationNote.setText(oldText);
   }

   
   /** */
   public void redo() {
      annotationNote.setText(newText);
   }

   
   public String toString(){
      return super.toString() + " " + annotationNote.getClass().getSimpleName() +
              "oldText: " + oldText + "newText: " + newText;
   }
      
}
