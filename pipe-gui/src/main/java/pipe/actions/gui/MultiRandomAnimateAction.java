package pipe.actions.gui;

import pipe.controllers.GUIAnimator;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;

import java.awt.event.ActionEvent;

public class MultiRandomAnimateAction extends AnimateAction {
    private final GuiAction stepbackwardAction;

    private final PipeApplicationController applicationController;

    public MultiRandomAnimateAction(String name, String tooltip, String keystroke, GuiAction stepbackwardAction,
                                    PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.stepbackwardAction = stepbackwardAction;
        this.applicationController = applicationController;
    }


    @Override
    public void actionPerformed(ActionEvent event) {

        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        GUIAnimator animator = petriNetController.getAnimator();
        if (animator.getNumberSequences() > 0) {
            // stop animation
            animator.setNumberSequences(0);
            setSelected(false);
        } else {
            stepbackwardAction.setEnabled(true);
            setSelected(true);
            animator.startRandomFiring();
        }
    }
}
