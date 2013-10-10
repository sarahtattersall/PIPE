/*
 * ChangeRateParameterEdit.java
 */

package pipe.historyActions;

import pipe.views.TransitionView;
import pipe.views.viewComponents.RateParameter;

/**
 *
 * @author corveau
 */
public class ChangeRateParameter
        extends HistoryItem
{
   
   private final TransitionView _transitionView;
   private final RateParameter oldRateParameter;
   private final RateParameter newRateParameter;
   
   
   /** Creates a new instance of placeCapacityEdit
    * @param _transitionView
    * @param _oldRateParameter
    * @param _newRateParameter*/
   public ChangeRateParameter(TransitionView _transitionView,
                              RateParameter _oldRateParameter,
                              RateParameter _newRateParameter) {
      this._transitionView = _transitionView;
      oldRateParameter = _oldRateParameter;
      newRateParameter = _newRateParameter;
   }

   
   /** */
   public void undo() {
      _transitionView.changeRateParameter(oldRateParameter);
   }

   
   /** */
   public void redo() {
      _transitionView.changeRateParameter(newRateParameter);
   }
   
}
