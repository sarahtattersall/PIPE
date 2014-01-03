package pipe.actions.animate;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.AnimationHistoryView;
import pipe.gui.Animator;
import pipe.gui.ApplicationSettings;
import pipe.models.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class RandomAnimateAction extends AnimateAction {
    public RandomAnimateAction(final String name, final String tooltip, final String keystroke) {
        super(name, tooltip, keystroke);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        PetriNetController petriNetController = controller.getActivePetriNetController();
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();

        Animator animator = petriNetController.getAnimator();
        animator.doRandomFiring();
        applicationModel.stepforwardAction.setEnabled(animator.isStepForwardAllowed());
        applicationModel.stepbackwardAction.setEnabled(animator.isStepBackAllowed());
//        pipeApplicationView.getAnimator().updateArcAndTran();
    }
}
