package pipe.actions.gui.file;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PipeApplicationController;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class NewPetriNetAction extends GuiAction {


    private final PipeApplicationController applicationController;

    public NewPetriNetAction(PipeApplicationController applicationController) {
        super("New", "Create a new Petri net", KeyEvent.VK_N, InputEvent.META_DOWN_MASK);
        this.applicationController = applicationController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        applicationController.createEmptyPetriNet();
    }
}
