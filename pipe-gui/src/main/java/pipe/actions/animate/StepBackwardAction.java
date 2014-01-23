package pipe.actions.animate;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.Animator;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

/**
 * Steps back in the animation sequence, i.e. undoes the last transition fired when clicked
 */
public class StepBackwardAction extends AnimateAction {
    private final PipeApplicationView applicationView;

    private final PipeApplicationController applicationController;

    public StepBackwardAction(String name, String tooltip, String keystroke, PipeApplicationView applicationView,
                              PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.applicationView = applicationView;
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        Animator animator = petriNetController.getAnimator();
        animator.stepBack();

        applicationView.setStepForward(animator.isStepForwardAllowed());
        applicationView.setStepBackward(animator.isStepBackAllowed());
    }
}
