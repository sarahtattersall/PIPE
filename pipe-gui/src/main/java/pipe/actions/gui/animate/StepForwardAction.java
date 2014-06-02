package pipe.actions.gui.animate;

import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.GUIAnimator;

import java.awt.event.ActionEvent;

/**
 * Steps forward in the animation sequence, i.e. redoes the last transition fired when clicked
 */
public class StepForwardAction extends AnimateAction {
    private final PipeApplicationController applicationController;

    private StepBackwardAction stepBackwardAction;
    public StepForwardAction(String name, String tooltip, String keystroke,
                             PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
    }

    public void registerStepBack(StepBackwardAction stepBackwardAction) {
        this.stepBackwardAction = stepBackwardAction;
    }



    @Override
    public void actionPerformed(ActionEvent event) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();

        GUIAnimator animator = petriNetController.getAnimator();
        animator.stepForward();

        this.setEnabled(animator.isStepForwardAllowed());
        if (stepBackwardAction != null) {
            stepBackwardAction.setEnabled(animator.isStepBackAllowed());
        }
    }
}
