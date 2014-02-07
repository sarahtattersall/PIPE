package pipe.actions.gui;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;

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
    public void actionPerformed(ActionEvent e)
    {
        PetriNetController petriNetController =  pipeApplicationController.getActivePetriNetController();
        petriNetController.deleteSelection();
    }
}
