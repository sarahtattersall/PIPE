package pipe.actions.animate;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.Animator;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.models.PetriNet;
import pipe.gui.model.PipeApplicationModel;
import pipe.views.AbstractPetriNetViewComponent;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class ToggleAnimateAction extends AnimateAction {
    public ToggleAnimateAction(final String name, final String tooltip, final String keystroke) {
        super(name, tooltip, keystroke);
    }

    @Override
    public void actionPerformed(ActionEvent event) {

        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
        PetriNetTab currentTab = pipeApplicationView.getCurrentTab();
        PipeApplicationController applicationController = ApplicationSettings.getApplicationController();
        try
        {
            boolean isTabAnimated = currentTab.isInAnimationMode();
            pipeApplicationView.setAnimationMode(!isTabAnimated);
            PetriNetController petriNetController = applicationController.getActivePetriNetController();
            if(!isTabAnimated)
            {
                pipeApplicationView.restoreMode();
                AbstractPetriNetViewComponent.ignoreSelection(false);
                PetriNet petriNet = petriNetController.getPetriNet();
                petriNet.markEnabledTransitions();
            }
            else
            {
                AbstractPetriNetViewComponent.ignoreSelection(true);
                Animator animator = petriNetController.getAnimator();
                animator.clear();
                // Do we keep the selection??
//                currentTab.getSelectionObject().clearSelection();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(pipeApplicationView, e.toString(),
                    "Animation Mode Error", JOptionPane.ERROR_MESSAGE);
//            applicationModel.startAction.setSelected(false);
            currentTab.changeAnimationMode(false);
        }
//        applicationModel.stepforwardAction.setEnabled(false);
//        applicationModel.stepbackwardAction.setEnabled(false);
    }
}
