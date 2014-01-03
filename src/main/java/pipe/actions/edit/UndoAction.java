package pipe.actions.edit;

import pipe.actions.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.ApplicationSettings;

import java.awt.event.ActionEvent;

public class UndoAction extends GuiAction {

    public UndoAction() {
        super("Undo", "Undo (Ctrl-Z)", "ctrl Z");
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        PipeApplicationController applicationController = ApplicationSettings
                .getApplicationController();
        PetriNetController controller = applicationController.getActivePetriNetController();
        controller.getHistoryManager().doUndo();
    }
}