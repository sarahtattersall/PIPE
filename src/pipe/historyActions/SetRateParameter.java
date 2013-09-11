/*
 * SetRateParameterEdit.java
 */
package pipe.historyActions;


import pipe.views.TransitionView;
import pipe.views.viewComponents.RateParameter;


/**
 *
 * @author corveau
 */
public class SetRateParameter
        extends HistoryItem
{
   
   private final TransitionView _transitionView;
   private final Double oldRate;
   private final RateParameter newRateParameter;
   
   
   /** Creates a new instance of placeCapacityEdit
    * @param _transitionView
    * @param _oldRate
    * @param _newRateParameter*/
   public SetRateParameter(TransitionView _transitionView,
                           Double _oldRate,
                           RateParameter _newRateParameter) {
      this._transitionView = _transitionView;
      oldRate = _oldRate;
      newRateParameter = _newRateParameter;
   }

   
   /** */
   public void undo() {
      _transitionView.clearRateParameter();
      _transitionView.setRate(oldRate);
   }

   
   /** */
   public void redo() {
      _transitionView.setRateParameter(newRateParameter);
   }
   
}
