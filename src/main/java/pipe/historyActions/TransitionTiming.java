/*
 * TransitionTimingEdit.java
 */
package pipe.historyActions;

import pipe.views.TransitionView;


/**
 *
 * @author corveau
 */
public class TransitionTiming
        extends HistoryItem
{
   
   private final TransitionView _transitionView;
   
   
   /** Creates a new instance of placeRateEdit
    * @param _transitionView*/
   public TransitionTiming(TransitionView _transitionView) {
      this._transitionView = _transitionView;
   }

   
   /** */
   public void undo() {
      _transitionView.setTimed(!_transitionView.isTimed());
   }

   
   /** */
   public void redo() {
      _transitionView.setTimed(!_transitionView.isTimed());
   }
   
}
