package pipe.actions.edit;

import pipe.actions.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;

import java.awt.event.ActionEvent;

public class CopyAction extends GuiAction{
    private final PipeApplicationController applicationController;

    public CopyAction(final String name, final String tooltip,
                      final String keystroke, PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        petriNetController.copySelection();
    }
}
