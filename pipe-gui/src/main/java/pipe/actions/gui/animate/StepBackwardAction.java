package pipe.actions.gui.animate;

import pipe.actions.manager.AnimateActionManager;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.Animator;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

/**
 * Steps back in the animation sequence, i.e. undoes the last transition fired when clicked
 */
public class StepBackwardAction extends AnimateAction {

    private final PipeApplicationController applicationController;

    private final AnimateActionManager animateActionManager;

    public StepBackwardAction(String name, String tooltip, String keystroke,
                              PipeApplicationController applicationController,
                              AnimateActionManager animateActionManager) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
        this.animateActionManager = animateActionManager;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        Animator animator = petriNetController.getAnimator();
        animator.stepBack();

        animateActionManager.setStepForward(animator.isStepForwardAllowed());
        animateActionManager.setStepBackward(animator.isStepBackAllowed());
    }
}
