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

    private final PetriNetController petriNetController;

    public TransitionAnimationHandler(Transition transition, PetriNetController petriNetController) {
        this.transition = transition;
        this.petriNetController = petriNetController;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (petriNetController.isInAnimationMode() && SwingUtilities.isLeftMouseButton(e) && (transition.isEnabled())) {
            PipeApplicationController controller = ApplicationSettings.getApplicationController();
            PetriNetController petriNetController = controller.getActivePetriNetController();
            Animator animator = petriNetController.getAnimator();
            animator.fireTransition(transition);

            //TODO: REMOVE APPLICATION SETTINGS.....
            PipeApplicationView applicationView = ApplicationSettings.getApplicationView();
            applicationView.getAnimateActionManager().setStepForward(animator.isStepForwardAllowed());
            applicationView.getAnimateActionManager().setStepBackward(animator.isStepBackAllowed());
        }
    }

}
