package pipe.handlers;

import pipe.gui.ApplicationSettings;
import pipe.views.GroupTransitionView;
import pipe.views.TransitionView;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.LinkedList;

/**
 * This class handles mouse clicks by the user. 
 * 
 * @author unknown 
 * @author David Patterson
 * 
 * Change by David Patterson was to fire the selected 
 * transition in the PetriNet, and then record the firing
 * in the animator.
 * 
 * @author Pere Bonet reverted the above change.
 */
public class AnimationHandler 
        extends javax.swing.event.MouseInputAdapter {
   
	// This linked list stores the previously fired groupTransition.
	// Once the user clicks on a grouped transition on the GUI, this is not null
	// Once the user fires a transition within the group this is used to
	// recover the group transition.
	private final LinkedList<GroupTransitionView> _lastGroupTransitionView = new LinkedList<GroupTransitionView>();
   
   public void mouseClicked(MouseEvent e){      
      if (e.getComponent() instanceof TransitionView) {
         TransitionView transitionView = (TransitionView)e.getComponent();
         
         if (SwingUtilities.isLeftMouseButton(e)
                 && (transitionView.isEnabled(true))) {
             ApplicationSettings.getApplicationView().getAnimationHistory().clearStepsForward();
             ApplicationSettings.getApplicationView().getAnimator().fireTransition(transitionView);
             ApplicationSettings.getApplicationView().setRandomAnimationMode(false);
            if(!_lastGroupTransitionView.isEmpty()){
            	for(GroupTransitionView groupTransitionView : _lastGroupTransitionView){
	        		for (TransitionView t : groupTransitionView.getTransitions()) {
	        			t.hideFromCanvas();
	        			t.hideAssociatedArcs();
	        		}
	        		groupTransitionView.getNameLabel().setVisible(true);
	        		groupTransitionView.setVisible(true);
	        		groupTransitionView.showAssociatedArcs();
	            }
            }
         }
      }
      else if (e.getComponent() instanceof GroupTransitionView) {
          GroupTransitionView groupTransitionView = (GroupTransitionView)e.getComponent();

          if (SwingUtilities.isLeftMouseButton(e)
        		  && (groupTransitionView.isEnabled(true)) ) {
      		for(TransitionView t: groupTransitionView.getTransitions()){
    			t.unhideFromCanvas();
    			t.showAssociatedArcs();
    		}
    		groupTransitionView.hideAssociatedArcs();
    		groupTransitionView.setVisible(false);
    		groupTransitionView.getNameLabel().setVisible(false);
    		
    		_lastGroupTransitionView.add(groupTransitionView);
      		/*PetriNet model = Pipe.getCurrentPetriNetView();
      		model.removePetriNetObject(groupTransition);
      		PetriNetTab view = Pipe.getCurrentTab();
      		view.remove(groupTransition);
	        */     /*Pipe.getAnimationHistory().clearStepsForward();
	             Pipe.getAnimator().fireTransition(enabledTransitions.get(transitionToFirePos));
	             Pipe.getApplicationView().setRandomAnimationMode(false);*/
          }
       }
   }
   
}
