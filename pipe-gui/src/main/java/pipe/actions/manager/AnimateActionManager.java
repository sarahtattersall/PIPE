package pipe.actions.manager;

import pipe.actions.gui.*;
import pipe.controllers.application.PipeApplicationController;

import java.util.Arrays;

/**
 * Manager to store all animate actions displayed on the tool bar
 */
public final class AnimateActionManager implements ActionManager {
    /**
     * Flag for toggling animation mode
     */
    private final GuiAction toggleAnimationAction;

    /**
     * Step backwards action to step back in the animation history
     */
    private final StepBackwardAction stepbackwardAction;

    /**
     * Step forwards action to step forward in the animation history
     */
    private final StepForwardAction stepforwardAction;

    /**
     * Fires a random transition
     */
    private final GuiAction randomAction;

    /**
     * Fires multiple random transitions
     */
    private final AnimateAction multipleRandomAction;

    /**
     * Constructor
     * @param applicationModel main PIPE application model
     * @param applicationController main PIPE application controller
     */
    public AnimateActionManager(PipeApplicationModel applicationModel, PipeApplicationController applicationController) {
        toggleAnimationAction = new ToggleAnimateAction("Animation mode", "Toggle Animation Mode", "Ctrl A",
                applicationModel, applicationController);
        stepforwardAction = new StepForwardAction("Forward", "Step forward a firing", "6", applicationController);
        stepbackwardAction = new StepBackwardAction("Back", "Step backward a firing", "4", applicationController, stepforwardAction);
        stepforwardAction.registerStepBack(stepbackwardAction);
        randomAction =
                new RandomAnimateAction("Random", "Randomly fire a transition", "5", applicationController, stepforwardAction, stepbackwardAction);
        multipleRandomAction = new MultiRandomAnimateAction("Animate", "Randomly fire a number of transitions", "7", stepbackwardAction,
                applicationController);
    }

    /**
     *
     * @return all actions stored
     */
    @Override
    public Iterable<GuiAction> getActions() {
        return Arrays.asList(toggleAnimationAction, stepbackwardAction, stepforwardAction, randomAction, multipleRandomAction);
    }

    /**
     * Enables the actions when changing to animation mode
     */
    @Override
    public void enableActions() {
        randomAction.setEnabled(true);
        multipleRandomAction.setEnabled(true);
        stepbackwardAction.setEnabled(false);
        stepforwardAction.setEnabled(false);

    }

    /**
     * Disables the actions for changing to edit mode
     */
    @Override
    public void disableActions() {
        for (GuiAction action : getAnimateActions()) {
            action.setEnabled(false);
        }
    }

    public Iterable<GuiAction> getEditActions() {
        return Arrays.asList(toggleAnimationAction);
    }

    public Iterable<GuiAction> getAnimateActions() {
        return Arrays.asList(stepbackwardAction, stepforwardAction, randomAction, multipleRandomAction);
    }
}
