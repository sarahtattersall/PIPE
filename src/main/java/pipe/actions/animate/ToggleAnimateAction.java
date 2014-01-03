package pipe.actions.animate;

import pipe.controllers.PetriNetController;
import pipe.gui.Animator;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.models.PetriNet;
import pipe.models.PipeApplicationModel;
import pipe.views.PetriNetViewComponent;
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
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
        try
        {
            boolean isTabAnimated = currentTab.isInAnimationMode();
            pipeApplicationView.setAnimationMode(!isTabAnimated);
            PetriNetController controller = currentTab.getPetriNetController();
            if(!isTabAnimated)
            {
                applicationModel.restoreMode();
                PetriNetViewComponent.ignoreSelection(false);
            }
            else
            {
                PetriNetViewComponent.ignoreSelection(true);
                Animator animator = controller.getAnimator();
                animator.clear();
                // Do we keep the selection??
                currentTab.getSelectionObject().clearSelection();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(pipeApplicationView, e.toString(),
                    "Animation Mode Error", JOptionPane.ERROR_MESSAGE);
            applicationModel.startAction.setSelected(false);
            currentTab.changeAnimationMode(false);
        }
        applicationModel.stepforwardAction.setEnabled(false);
        applicationModel.stepbackwardAction.setEnabled(false);
    }
}
