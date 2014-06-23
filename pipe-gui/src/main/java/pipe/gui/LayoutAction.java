package pipe.gui;

import pipe.actions.gui.GuiAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.application.PipeApplicationController;
import uk.ac.imperial.pipe.layout.Layout;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class LayoutAction extends GuiAction{


    private final PipeApplicationController pipeApplicationController;

    public LayoutAction(PipeApplicationController pipeApplicationController) {
        super("Layout", "Layout", KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK);
        this.pipeApplicationController = pipeApplicationController;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        PetriNetController petriNetController = pipeApplicationController.getActivePetriNetController();
        PetriNet petriNet = petriNetController.getPetriNet();
        Layout.layout(petriNet);
    }
}
