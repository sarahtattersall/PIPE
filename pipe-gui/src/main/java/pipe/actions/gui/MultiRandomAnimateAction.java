package pipe.actions.gui;

import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.controllers.GUIAnimator;

import java.awt.event.ActionEvent;

public class MultiRandomAnimateAction extends AnimateAction {
    private final GuiAction stepbackwardAction;

    private final GuiAction stepforwardAction;

    private final PipeApplicationController applicationController;

    public MultiRandomAnimateAction(String name, String tooltip, String keystroke,
                                    GuiAction stepbackwardAction,
                                    GuiAction stepforwardAction,
                                    PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.stepbackwardAction = stepbackwardAction;
        this.stepforwardAction = stepforwardAction;
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
            stepbackwardAction.setEnabled(true);
            setSelected(true);
            animator.startRandomFiring();
        }
    }
}
