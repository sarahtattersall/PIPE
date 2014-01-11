package pipe.actions.animate;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.AnimationHistoryView;
import pipe.gui.Animator;
import pipe.gui.ApplicationSettings;
import pipe.gui.model.PipeApplicationModel;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class MultiRandomAnimateAction extends AnimateAction {
    public MultiRandomAnimateAction(String name, String tooltip, String keystroke) {
        super(name, tooltip, keystroke);
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
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
//            applicationModel.stepbackwardAction.setEnabled(false);
//            applicationModel.stepforwardAction.setEnabled(false);
//            applicationModel.randomAction.setEnabled(false);
            setSelected(true);
            animator.startRandomFiring();
        }
    }
}
