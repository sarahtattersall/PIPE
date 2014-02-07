package pipe.actions.gui.animate;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.Animator;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class RandomAnimateAction extends AnimateAction {
    private final PipeApplicationView applicationView;

    private final PipeApplicationController applicationController;

    public RandomAnimateAction(String name, String tooltip, String keystroke, PipeApplicationView applicationView,
                               PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.applicationView = applicationView;
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();

        Animator animator = petriNetController.getAnimator();
        animator.doRandomFiring();

        applicationView.setStepForward(animator.isStepForwardAllowed());
        applicationView.setStepBackward(animator.isStepBackAllowed());
    }
}
