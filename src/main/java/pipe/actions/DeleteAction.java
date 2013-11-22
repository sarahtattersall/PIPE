package pipe.actions;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;
import pipe.gui.PetriNetTab;
import pipe.historyActions.HistoryManager;
import pipe.views.PipeApplicationView;

import java.awt.event.ActionEvent;

public class DeleteAction extends GuiAction
{

    public DeleteAction(String name, String tooltip, String keystroke)
    {
        super(name, tooltip, keystroke);
    }

    public void actionPerformed(ActionEvent e)
    {
        PipeApplicationView pipeApplicationView = ApplicationSettings.getApplicationView();
        PetriNetTab appView = pipeApplicationView.getCurrentTab();

        //TODO: Ensure this works
        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        PetriNetController petriNetController =  controller.getActivePetriNetController();
        HistoryManager historyManager = petriNetController.getHistoryManager();
//        petriNetController.getHistoryManager().newEdit();
        petriNetController.getHistoryManager().deleteSelection(appView.getSelectionObject().getSelection());
        appView.getSelectionObject().deleteSelection();
    }
}
