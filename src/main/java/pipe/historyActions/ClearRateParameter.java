/*
 * ClearRateParameterEdit.java
 */

package pipe.historyActions;

import pipe.views.TransitionView;
import pipe.views.viewComponents.RateParameter;

/**
 *
 * @author corveau
 */
public class ClearRateParameter
        extends HistoryItem
{
   
   private final TransitionView _transitionView;
   private final RateParameter oldRateParameter;
   
   
   /** Creates a new instance of placeCapacityEdit
    * @param _transitionView
    * @param _oldRateParameter*/
   public ClearRateParameter(TransitionView _transitionView,
                             RateParameter _oldRateParameter) {
      this._transitionView = _transitionView;
      oldRateParameter = _oldRateParameter;
   }

   
   /** */
   public void undo() {
      _transitionView.setRateParameter(oldRateParameter);
   }

   
   /** */
   public void redo() {
      _transitionView.clearRateParameter();
   }
   
}
