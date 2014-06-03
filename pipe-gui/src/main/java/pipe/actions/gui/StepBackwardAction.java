package pipe.actions.gui;

import pipe.controllers.GUIAnimator;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;

import java.awt.event.ActionEvent;

/**
 * Steps back in the animation sequence, i.e. undoes the last transition fired when clicked
 */
public class StepBackwardAction extends AnimateAction {

    private final PipeApplicationController applicationController;

    private final StepForwardAction stepForwardAction;

    public StepBackwardAction(String name, String tooltip, String keystroke,
                              PipeApplicationController applicationController, StepForwardAction stepForwardAction) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
        this.stepForwardAction = stepForwardAction;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        GUIAnimator animator = petriNetController.getAnimator();
        animator.stepBack();

        stepForwardAction.setEnabled(animator.isStepForwardAllowed());
        this.setEnabled(animator.isStepBackAllowed());
    }
}
