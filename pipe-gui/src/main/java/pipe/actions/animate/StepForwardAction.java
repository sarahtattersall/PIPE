package pipe.actions.animate;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.Animator;
import pipe.gui.ApplicationSettings;
import pipe.gui.model.PipeApplicationModel;

import java.awt.event.ActionEvent;

public class StepForwardAction extends AnimateAction {
    public StepForwardAction(final String name, final String tooltip, final String keystroke) {
        super(name, tooltip, keystroke);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        PetriNetController petriNetController = controller.getActivePetriNetController();

        Animator animator = petriNetController.getAnimator();
        animator.stepForward();

        //        applicationModel.stepforwardAction.setEnabled(animator.isStepForwardAllowed());
        //        applicationModel.stepbackwardAction.setEnabled(animator.isStepBackAllowed());
    }
}
