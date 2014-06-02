package pipe.actions.gui.animate;

import pipe.actions.manager.AnimateActionManager;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.gui.GUIAnimator;

import java.awt.event.ActionEvent;

public class MultiRandomAnimateAction extends AnimateAction {
    private final AnimateActionManager animateActionManager;

    private final PipeApplicationController applicationController;

    public MultiRandomAnimateAction(String name, String tooltip, String keystroke,
                                    AnimateActionManager animateActionManager,
                                    PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.animateActionManager = animateActionManager;
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        GUIAnimator animator = petriNetController.getAnimator();
        if(animator.getNumberSequences() > 0)
        {
            animator.setNumberSequences(0); // stop animation
            setSelected(false);
        }
        else
        {
            animateActionManager.setStepBackward(true);
            animateActionManager.setStepBackward(animator.isStepBackAllowed());
            setSelected(true);
            animator.startRandomFiring();
        }
    }
}
