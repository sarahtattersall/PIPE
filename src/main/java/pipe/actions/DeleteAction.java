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
        PipeApplicationController controller = ApplicationSettings.getApplicationController();
        PetriNetController petriNetController =  controller.getActivePetriNetController();
        petriNetController.deleteSelection();
    }
}
