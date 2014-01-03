package pipe.actions.animate;

import pipe.actions.AnimateAction;
import pipe.gui.AnimationHistory;
import pipe.gui.ApplicationSettings;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.models.PipeApplicationModel;
import pipe.views.PetriNetViewComponent;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class StartAnimateAction extends AnimateAction {
    public StartAnimateAction(final String name, final int typeID, final String tooltip, final String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }

    @Override
    public void actionPerformed(final ActionEvent event) {

        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
        PetriNetTab currentTab = pipeApplicationView.getCurrentTab();
        PipeApplicationModel applicationModel = ApplicationSettings.getApplicationModel();
        try
        {
            pipeApplicationView.setAnimationMode(!currentTab.isInAnimationMode());
            if(!currentTab.isInAnimationMode())
            {
                applicationModel.restoreMode();
                PetriNetViewComponent.ignoreSelection(false);
            }
            else
            {
                applicationModel.setMode(Constants.START);
                currentTab.getPetriNetController().getPetriNet().markEnabledTransitions();
                PetriNetViewComponent.ignoreSelection(true);
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
