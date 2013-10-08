/*
 * RateParameterValueEdit.java
 */

package pipe.historyActions;


import pipe.views.viewComponents.RateParameter;

/**
 *
 * @author corveau
 */
public class RateParameterValue
        extends HistoryItem
{
   
   private final RateParameter rateParameter;
   private final Double newValue;
   private final Double oldValue;
   
   
   /** Creates a new instance of placeCapacityEdit
    * @param _rateParameter
    * @param _oldValue
    * @param _newValue*/
   public RateParameterValue(RateParameter _rateParameter,
                             Double _oldValue, Double _newValue) {
      rateParameter = _rateParameter;
      oldValue = _oldValue;      
      newValue = _newValue;
   }

   
   /** */
   public void undo() {
      rateParameter.setValue(oldValue);
      rateParameter.update();
      rateParameter.updateBounds();
   }

   
   /** */
   public void redo() {
      rateParameter.setValue(newValue);
      rateParameter.update();
      rateParameter.updateBounds();
   }
   
}
