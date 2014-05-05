package pipe.actions.gui.animate;

import pipe.actions.manager.AnimateActionManager;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.GUIAnimator;

import java.awt.event.ActionEvent;

public class RandomAnimateAction extends AnimateAction {

    private final PipeApplicationController applicationController;

    private final AnimateActionManager animateActionManager;

    public RandomAnimateAction(String name, String tooltip, String keystroke,
                               PipeApplicationController applicationController,
                               AnimateActionManager animateActionManager) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
        this.animateActionManager = animateActionManager;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();

        GUIAnimator animator = petriNetController.getAnimator();
        animator.doRandomFiring();

        animateActionManager.setStepForward(animator.isStepForwardAllowed());
        animateActionManager.setStepBackward(animator.isStepBackAllowed());
    }
}
