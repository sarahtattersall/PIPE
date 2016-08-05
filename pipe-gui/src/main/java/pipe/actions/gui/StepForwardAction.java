package pipe.actions.gui;

import pipe.controllers.GUIAnimator;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;

import java.awt.event.ActionEvent;

/**
 * Steps forward in the animation sequence, i.e. redoes the last transition fired when clicked
 */
@SuppressWarnings("serial")
public class StepForwardAction extends AnimateAction {
    /**
     * Main PIPE application controller
     */
    private final PipeApplicationController applicationController;

    /**
     * Step backward action
     */
    private StepBackwardAction stepBackwardAction;

    /**
     *
     * @param name icon name
     * @param tooltip tooltip message
     * @param keystroke keyboard shortcut
     * @param applicationController main PIPE application controller
     */
    public StepForwardAction(String name, String tooltip, String keystroke,
                             PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
    }

    /**
     * Register a step back action. When a redo is performed this action will be enabled
     * @param stepBackwardAction action
     */
    public void registerStepBack(StepBackwardAction stepBackwardAction) {
        this.stepBackwardAction = stepBackwardAction;
    }


    /**
     * Performs an redo and enables the step back action if it is set
     * @param event event 
     */
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
