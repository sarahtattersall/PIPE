package pipe.actions.gui.animate;

import pipe.actions.gui.create.NoopAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.Animator;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.views.AbstractPetriNetViewComponent;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This action is responsible for toggling the petri net in and out of animation mode
 */
public class ToggleAnimateAction extends AnimateAction {
    private final PipeApplicationController applicationController;

    /**
     * Noop action to be used for toggling in and out of animation
     * Removes any spurious creates happening in animation mode
     */
    private final ActionListener noopAction = new NoopAction();

    public ToggleAnimateAction(String name, String tooltip, String keystroke,
                               PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PetriNetTab currentTab = applicationController.getActivePetriNetController().getPetriNetTab();
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        boolean animated = petriNetController.toggleAnimation();


        //TODO: Remove call to Application settings
        PipeApplicationView applicationView = ApplicationSettings.getApplicationView();
        applicationView.setAnimationMode(animated);

        noopAction.actionPerformed(null);

        Animator animator = petriNetController.getAnimator();
        if (!animated) {
            animator.startAnimation();
            AbstractPetriNetViewComponent.ignoreSelection(false);
        } else {
            animator.finish();
            AbstractPetriNetViewComponent.ignoreSelection(true);
        }
    }
}
