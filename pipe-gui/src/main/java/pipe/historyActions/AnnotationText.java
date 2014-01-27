/*
 * AnnotationTextEdit.java
 */

package pipe.historyActions;

import pipe.views.viewComponents.AnnotationView;

/**
 *
 * @author corveau
 */
public final class AnnotationText
        extends HistoryItem
{
   
   private final AnnotationView annotationView;
   private final String oldText;
   private final String newText;
   
   
   /** Creates a new instance of placeRateEdit
    * @param _annotationView
    * @param _oldText
    * @param _newText*/
   public AnnotationText(AnnotationView _annotationView,
                         String _oldText, String _newText) {
      annotationView = _annotationView;
      oldText = _oldText;
      newText = _newText;
   }

   
   /** */
   public void undo() {
      annotationView.setText(oldText);
   }

   
   /** */
   public void redo() {
      annotationView.setText(newText);
   }

   
   public String toString(){
      return super.toString() + " " + annotationView.getClass().getSimpleName() +
              "oldText: " + oldText + "newText: " + newText;
   }
      
}
