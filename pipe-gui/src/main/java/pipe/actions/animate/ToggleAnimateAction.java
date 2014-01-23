package pipe.actions.animate;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.Animator;
import pipe.gui.PetriNetTab;
import pipe.views.AbstractPetriNetViewComponent;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

/**
 * This action is responsible for toggling the petri net in and out of animation mode
 */
public class ToggleAnimateAction extends AnimateAction {
    private final PipeApplicationView applicationView;

    private final PipeApplicationController applicationController;

    public ToggleAnimateAction(String name, String tooltip, String keystroke, PipeApplicationView applicationView,
                               PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.applicationView = applicationView;
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PetriNetTab currentTab = applicationView.getCurrentTab();

        boolean isTabAnimated = currentTab.isInAnimationMode();
        applicationView.setAnimationMode(!isTabAnimated);

        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        Animator animator = petriNetController.getAnimator();
        if (!isTabAnimated) {
            animator.startAnimation();
            AbstractPetriNetViewComponent.ignoreSelection(false);
        } else {
            animator.finish();
            AbstractPetriNetViewComponent.ignoreSelection(true);
        }
    }
}
