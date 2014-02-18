package pipe.handlers;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.Animator;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.models.component.transition.Transition;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.MouseEvent;

/**
 * TransitionAnimationHandler fires the transition when clicked
 * if in animation mode
 */
public class TransitionAnimationHandler extends javax.swing.event.MouseInputAdapter {

    private final Transition transition;

    private final PetriNetTab petriNetTab;

    public TransitionAnimationHandler(Transition transition, PetriNetTab petriNetTab) {
        this.transition = transition;
        this.petriNetTab = petriNetTab;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (petriNetTab.isInAnimationMode() && SwingUtilities.isLeftMouseButton(e) && (transition.isEnabled())) {
            PipeApplicationController controller = ApplicationSettings.getApplicationController();
            PetriNetController petriNetController = controller.getActivePetriNetController();
            Animator animator = petriNetController.getAnimator();
            animator.fireTransition(transition);

            PipeApplicationView applicationView = ApplicationSettings.getApplicationView();
            applicationView.setStepForward(animator.isStepForwardAllowed());
            applicationView.setStepBackward(animator.isStepBackAllowed());
        }
    }

}
