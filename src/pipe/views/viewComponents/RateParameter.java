package pipe.views.viewComponents;

import pipe.gui.ApplicationSettings;
import pipe.gui.Grid;
import pipe.gui.widgets.EscapableDialog;
import pipe.gui.widgets.ParameterPanel;
import pipe.historyActions.HistoryItem;
import pipe.historyActions.HistoryManager;
import pipe.historyActions.RateParameterValue;
import pipe.views.PetriNetView;
import pipe.views.TransitionView;

import java.util.HashSet;
import java.util.Iterator;


/**
 * This class defines a marking parameter (an double value > 0)
 * @author Pere Bonet
 */
public class RateParameter extends Parameter
{

   // the value of the parameter
   private Double value;    
   
   // the set of transitions that use this parameter
   private final HashSet<TransitionView> _transitionsHashSet;
   
      
   public RateParameter(String _name, Double _value, int x, int y){
      super (x, y);
      name = _name;
      value = _value;
      _transitionsHashSet = new HashSet();
      update();
   }
   
   
   public void enableEditMode(){
      // Build interface
       EscapableDialog guiDialog =
              new EscapableDialog(ApplicationSettings.getApplicationView(),"PIPE2",true);
      guiDialog.add(new ParameterPanel(guiDialog.getRootPane(), this));

      // Make window fit contents' preferred size
      guiDialog.pack();
      
      // Move window to the middle of the screen
      guiDialog.setLocationRelativeTo(null);
      
      guiDialog.setResizable(false);
      guiDialog.setVisible(true);
      
      guiDialog.dispose();      
   }   
   

   public Double getValue() {
      return value;
   }

   
   public HistoryItem setValue(Double _value) {
      double oldValue = value;
      value = _value;
      valueChanged = true;
      return new RateParameterValue(this, oldValue, value);
   }
   

   /** 
    * Adds a transition to this placesHashSet
    *
    * @param transitionView The transition to be removed
    * @return true if transitionHashSet did not already contain transition
    */
   public void add(TransitionView transitionView){
      _transitionsHashSet.add(transitionView);
   }
   
   
   /**
    * Removes a transition from transitionsHashSet
    *
    * @param transitionView The transition to be removed
    * @return true if placesHashSet contained place
    */   
   public void remove(TransitionView transitionView){
      _transitionsHashSet.remove(transitionView);
   }   
   
   
   // updates each transition in transitionsHashSet to current parameter value
   public void update() {
      if (valueChanged){
         valueChanged = false;
         Iterator<TransitionView> iterator = _transitionsHashSet.iterator();
         while (iterator.hasNext()){
            TransitionView t = iterator.next();
            t.setRate(value);
            t.update();
         }
      }      
      this.setText(/*"[R]" + */name + "=" + value);
      this.setSize(this.getMinimumSize());
   }   

   
   public Parameter copy() {
      return new RateParameter(name, value, this.getX(), this.getY());
   }

   
   public Parameter paste(double x, double y, boolean fromAnotherView,PetriNetView model) {
      return new RateParameter(name, value,
                               this.getX() + Grid.getModifiedX(x),
                               this.getY() + Grid.getModifiedY(y));
   }

   
   public void delete(){
      Object[] transitions = _transitionsHashSet.toArray();
      if (transitions.length > 0) {
          HistoryManager historyManager = ApplicationSettings.getApplicationView().getCurrentTab().getHistoryManager();
          for(Object transition : transitions)
          {
              historyManager.addEdit(
                      ((TransitionView) transition).clearRateParameter());
          }
      }
      super.delete();      
   }

   
   // returns the array of transitions that are using this parameter
   public Object[] getTransitions() {
      return _transitionsHashSet.toArray();
   }
   
}
