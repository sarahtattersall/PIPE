package pipe.actions.edit;

import pipe.actions.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;

import java.awt.event.ActionEvent;

public class PasteAction extends GuiAction {

    private final PipeApplicationController applicationController;

    public PasteAction(final String name, final String tooltip, final String keystroke,
                       PipeApplicationController applicationController) {
        super(name, tooltip, keystroke);
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        PetriNetController petriNetController = applicationController.getActivePetriNetController();
        petriNetController.paste();
    }
}