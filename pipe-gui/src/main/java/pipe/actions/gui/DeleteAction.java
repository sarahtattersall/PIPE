package pipe.actions.gui;

import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import pipe.historyActions.MultipleEdit;
import pipe.utilities.gui.GuiUtils;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class DeleteAction extends GuiAction
{

    private final PipeApplicationController pipeApplicationController;

    public DeleteAction(PipeApplicationController pipeApplicationController)
    {
        super("Delete", "Delete selection (delete)", KeyEvent.VK_DELETE, 0);
        this.pipeApplicationController = pipeApplicationController;
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        PetriNetController petriNetController =  pipeApplicationController.getActivePetriNetController();
        try {
            registerUndoEvent(new MultipleEdit(petriNetController.deleteSelection()));
        } catch (PetriNetComponentException e) {
            GuiUtils.displayErrorMessage(null, e.getMessage());
        }
    }
}
