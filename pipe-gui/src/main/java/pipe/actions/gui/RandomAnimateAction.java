package pipe.actions.gui;

import pipe.controllers.GUIAnimator;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;

import java.awt.event.ActionEvent;

/**
 * Action responsible for randomly firing a single enabled transition
 */
@SuppressWarnings("serial")
public class RandomAnimateAction extends AnimateAction {

    /**
     * PIPE main application controller
     */
    private final PipeApplicationController applicationController;

    /**
     * Step forward action button
     */
    private final StepForwardAction stepForwardAction;

    /**
     * Step backward action button
     */
    private final StepBackwardAction stepBackwardAction;

    /**
     *
     * @param name image name
     * @param tooltip tooltip message
     * @param keystroke keyboard short cut
     * @param applicationController main application controller
     * @param stepForwardAction forward
     * @param stepBackwardAction backward 
     */
    public RandomAnimateAction(String name, String tooltip, String keystroke,
                               PipeApplicationController applicationController, StepForwardAction stepForwardAction,
                               StepBackwardAction stepBackwardAction) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
        this.stepForwardAction = stepForwardAction;
        this.stepBackwardAction = stepBackwardAction;
    }

    /**
     * Randomly fires one transition in Petri net that is currently in animation mode.
     *
     * Enables the step forwards and backwards buttons accordingly.
     * @param event event 
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();

        GUIAnimator animator = petriNetController.getAnimator();
        animator.doRandomFiring();

        stepForwardAction.setEnabled(animator.isStepForwardAllowed());
        stepBackwardAction.setEnabled(animator.isStepBackAllowed());
    }
}
