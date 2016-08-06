package pipe.handlers;

import pipe.controllers.PetriNetController;
import pipe.controllers.GUIAnimator;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * TransitionAnimationHandler fires the transition when clicked
 * if in animation mode
 */
public class TransitionAnimationHandler extends javax.swing.event.MouseInputAdapter {

    /**
     * Underlying component
     */
    private final Transition transition;

    /**
     * Petri net controller the transition belongs in
     */
    private final PetriNetController petriNetController;

    /**
     *
     * @param transition transition model
     * @param petriNetController Petri net controller for the Petri net the transition belongs in
     */
    public TransitionAnimationHandler(Transition transition, PetriNetController petriNetController) {
        this.transition = transition;
        this.petriNetController = petriNetController;
    }

    /**
     * When clicked this fires the transition if it is enabled in animation mode
     * @param e mouse event 
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (petriNetController.isInAnimationMode() && SwingUtilities.isLeftMouseButton(e) && transition.isEnabled()) {
            GUIAnimator animator = petriNetController.getAnimator();
            animator.fireTransition(transition);

            //TODO: STEP FORWARD AND BACK
//            PipeApplicationView applicationView = petriNetController.getPetriNetTab().getApplicationView();
//            applicationView.getAnimateActionManager().setStepForward(animator.isStepForwardAllowed());
//            applicationView.getAnimateActionManager().setStepBackward(animator.isStepBackAllowed());
        }
    }

}
