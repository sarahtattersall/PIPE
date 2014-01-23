package pipe.actions.animate;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.Animator;
import pipe.gui.ApplicationSettings;
import pipe.gui.model.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

/**
 * Steps forward in the animation sequence, i.e. redoes the last transition fired when clicked
 */
public class StepForwardAction extends AnimateAction {
    private final PipeApplicationView applicationView;

    private final PipeApplicationController applicationController;

    public StepForwardAction(String name, String tooltip, String keystroke, PipeApplicationView applicationView,
                             PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.applicationView = applicationView;
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();

        Animator animator = petriNetController.getAnimator();
        animator.stepForward();

        applicationView.setStepForward(animator.isStepForwardAllowed());
        applicationView.setStepBackward(animator.isStepBackAllowed());
    }
}
