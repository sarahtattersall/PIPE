/*
 * placeRateEdit.java
 */
package pipe.historyActions;

import pipe.views.TransitionView;


/**
 *
 * @author corveau
 * @author yufeiwang(added functional rate features)
 */
public class TransitionRate
        extends HistoryItem
{
   
   private final TransitionView _transitionView;
   private final Double newRate;
   private final Double oldRate;
   
   private final String newFunctionalRate;
   private final String oldFunctionalRate;
   
   /** Creates a new instance of placeRateEdit
    * @param _transitionView
    * @param _oldRate
    * @param _newRate*/
   public TransitionRate(
           TransitionView _transitionView, Double _oldRate, Double _newRate) {
      this._transitionView = _transitionView;
      oldRate = _oldRate;      
      newRate = _newRate;
      newFunctionalRate="";
      oldFunctionalRate="";
   }
   
   public TransitionRate(
		   TransitionView _transitionView, String _oldRate, String _newRate){
	      this._transitionView = _transitionView;
	      oldFunctionalRate = _oldRate;      
	      newFunctionalRate = _newRate;
	      oldRate = 1.0;      
	      newRate = 1.0;
   }
   
   /** */
   public void undo() {
      _transitionView.setRate(oldRate);
      _transitionView.setRate(oldFunctionalRate);
   }

   
   /** */
   public void redo() {
      _transitionView.setRate(newRate);
      _transitionView.setRate(newFunctionalRate);
   }
   
}
