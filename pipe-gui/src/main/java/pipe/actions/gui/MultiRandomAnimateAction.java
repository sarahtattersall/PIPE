package pipe.actions.gui;

import pipe.controllers.GUIAnimator;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;

import java.awt.event.ActionEvent;

/**
 * This action is responsible for firing multiple random enabled
 * transitions when animation mode is on
 */
public class MultiRandomAnimateAction extends AnimateAction {
    /**
     * Step backward action, used to set its availability when a step forward has been performed.
     */
    private final GuiAction stepBackwardAction;

    /**
     * Main PIPE application controller
     */
    private final PipeApplicationController applicationController;

    /**
     * Constructor
     * @param name image name
     * @param tooltip tooltip message
     * @param keystroke shortcut keystroke
     * @param stepBackwardAction step backward action
     * @param applicationController main PIPE application controller
     */
    public MultiRandomAnimateAction(String name, String tooltip, String keystroke, GuiAction stepBackwardAction,
                                    PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.stepBackwardAction = stepBackwardAction;
        this.applicationController = applicationController;
    }


    /**
     * Fires the specified number of enabled transitions
     * @param event
     */
    @Override
    public void actionPerformed(ActionEvent event) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        GUIAnimator animator = petriNetController.getAnimator();
        if (animator.getNumberSequences() > 0) {
            // stop animation
            animator.setNumberSequences(0);
            setSelected(false);
        } else {
            stepBackwardAction.setEnabled(true);
            setSelected(true);
            animator.startRandomFiring();
        }
    }
}
