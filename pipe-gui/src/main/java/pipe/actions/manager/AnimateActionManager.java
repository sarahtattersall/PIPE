package pipe.actions.manager;

import pipe.actions.gui.GuiAction;
import pipe.actions.gui.animate.*;
import pipe.controllers.PipeApplicationController;
import pipe.gui.model.PipeApplicationModel;

import java.util.Arrays;

public class AnimateActionManager implements ActionManager {
    private final GuiAction toggleAnimationAction;

    private final GuiAction stepbackwardAction;

    private final GuiAction stepforwardAction;

    private final GuiAction randomAction;

    private final AnimateAction multipleRandomAction =
            new MultiRandomAnimateAction("Animate", "Randomly fire a number of transitions", "7", this);

    public AnimateActionManager(PipeApplicationModel applicationModel, PipeApplicationController applicationController) {
        toggleAnimationAction = new ToggleAnimateAction("Animation mode", "Toggle Animation Mode", "Ctrl A",
                applicationModel, applicationController);
        stepforwardAction = new StepForwardAction("Forward", "Step forward a firing", "6", applicationController, this);
        randomAction =
                new RandomAnimateAction("Random", "Randomly fire a transition", "5", applicationController, this);
        stepbackwardAction = new StepBackwardAction("Back", "Step backward a firing", "4", applicationController, this);
    }

    @Override
    public Iterable<GuiAction> getActions() {
        return Arrays.asList(toggleAnimationAction, stepbackwardAction, stepforwardAction, randomAction, multipleRandomAction);
    }

    @Override
    public void enableActions() {
        randomAction.setEnabled(true);
        multipleRandomAction.setEnabled(true);
        stepbackwardAction.setEnabled(false);
        stepforwardAction.setEnabled(false);

    }

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

    public void setStepForward(boolean stepForwardAllowed) {
        stepforwardAction.setEnabled(stepForwardAllowed);
    }

    public void setStepBackward(boolean stepBackwardAllowed) {
        stepbackwardAction.setEnabled(stepBackwardAllowed);
    }
}
