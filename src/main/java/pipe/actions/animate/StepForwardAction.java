package pipe.actions.animate;

import pipe.gui.AnimationHistory;
import pipe.gui.ApplicationSettings;
import pipe.models.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class StepForwardAction extends AnimateAction {
    public StepForwardAction(final String name, final String tooltip, final String keystroke) {
        super(name, tooltip, keystroke);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
        AnimationHistory animationHistory = pipeApplicationView.getAnimationHistory();
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();

        animationHistory.stepForward();
        pipeApplicationView.getAnimator().stepForward();

        applicationModel.stepforwardAction.setEnabled(animationHistory.isStepForwardAllowed());
        applicationModel.stepbackwardAction.setEnabled(animationHistory.isStepBackAllowed());
        pipeApplicationView.getAnimator().updateArcAndTran();
    }
}
