package pipe.actions.gui.animate;

import pipe.actions.manager.AnimateActionManager;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.Animator;
import pipe.gui.ApplicationSettings;
import pipe.gui.model.PipeApplicationModel;

import java.awt.event.ActionEvent;

public class MultiRandomAnimateAction extends AnimateAction {
    private final AnimateActionManager animateActionManager;

    public MultiRandomAnimateAction(String name, String tooltip, String keystroke, AnimateActionManager animateActionManager) {
        super(name, tooltip, keystroke);
        this.animateActionManager = animateActionManager;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PipeApplicationController controller = ApplicationSettings.getApplicationController();

        PetriNetController petriNetController = controller.getActivePetriNetController();
        Animator animator = petriNetController.getAnimator();
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
