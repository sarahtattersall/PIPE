package pipe.actions.animate;

import pipe.actions.AnimateAction;
import pipe.gui.AnimationHistory;
import pipe.gui.Animator;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.models.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class AnimationAction extends AnimateAction {
    public AnimationAction(final String name, final int typeID, final String tooltip, final String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
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
