package pipe.actions.gui;

import pipe.controllers.GUIAnimator;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;

import java.awt.event.ActionEvent;

public class RandomAnimateAction extends AnimateAction {

    private final PipeApplicationController applicationController;

    private final StepForwardAction stepForwardAction;

    private final StepBackwardAction stepBackwardAction;

    public RandomAnimateAction(String name, String tooltip, String keystroke,
                               PipeApplicationController applicationController, StepForwardAction stepForwardAction,
                               StepBackwardAction stepBackwardAction) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
        this.stepForwardAction = stepForwardAction;
        this.stepBackwardAction = stepBackwardAction;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();

        GUIAnimator animator = petriNetController.getAnimator();
        animator.doRandomFiring();

        stepForwardAction.setEnabled(animator.isStepForwardAllowed());
        stepBackwardAction.setEnabled(animator.isStepBackAllowed());
    }
}
