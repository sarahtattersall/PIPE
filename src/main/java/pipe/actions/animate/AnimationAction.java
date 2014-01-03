package pipe.actions.animate;

import pipe.gui.AnimationHistory;
import pipe.gui.Animator;
import pipe.gui.ApplicationSettings;
import pipe.models.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class AnimationAction extends AnimateAction {
    public AnimationAction(String name, String tooltip, String keystroke) {
        super(name, tooltip, keystroke);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
        AnimationHistory animationHistory = pipeApplicationView.getAnimationHistory();
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();


        Animator a = pipeApplicationView.getAnimator();
        if(a.getNumberSequences() > 0)
        {
            a.setNumberSequences(0); // stop animation
            setSelected(false);
        }
        else
        {
            applicationModel.stepbackwardAction.setEnabled(false);
            applicationModel.stepforwardAction.setEnabled(false);
            applicationModel.randomAction.setEnabled(false);
            setSelected(true);
            animationHistory.clearStepsForward();
            pipeApplicationView.getAnimator().startRandomFiring();
            pipeApplicationView.getAnimator().updateArcAndTran();
        }
    }
}
