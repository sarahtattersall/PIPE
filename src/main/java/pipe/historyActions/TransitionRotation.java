/*
 * transitionPriorityEdit.java
 */
package pipe.historyActions;

import pipe.views.TransitionView;


/**
 *
 * @author corveau
 */
public class TransitionRotation
        extends HistoryItem
{
   
   private final TransitionView _transitionView;
   private final Integer angle;
   
   
   /** Creates a new instance of placePriorityEdit
    * @param _transitionView
    * @param _angle*/
   public TransitionRotation(TransitionView _transitionView, Integer _angle) {
      this._transitionView = _transitionView;
      angle = _angle;
   }

   
   /** */
   public void undo() {
      _transitionView.rotate(-angle);
   }

   
   /** */
   public void redo() {
      _transitionView.rotate(angle);
   }
   
}
