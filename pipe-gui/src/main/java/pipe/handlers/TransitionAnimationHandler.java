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

    private final Transition transition;

    private final PetriNetController petriNetController;

    public TransitionAnimationHandler(Transition transition, PetriNetController petriNetController) {
        this.transition = transition;
        this.petriNetController = petriNetController;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (petriNetController.isInAnimationMode() && SwingUtilities.isLeftMouseButton(e) && (transition.isEnabled())) {
            GUIAnimator animator = petriNetController.getAnimator();
            animator.fireTransition(transition);

            //TODO: STEP FORWARD AND BACK
//            PipeApplicationView applicationView = petriNetController.getPetriNetTab().getApplicationView();
//            applicationView.getAnimateActionManager().setStepForward(animator.isStepForwardAllowed());
//            applicationView.getAnimateActionManager().setStepBackward(animator.isStepBackAllowed());
        }
    }

}
