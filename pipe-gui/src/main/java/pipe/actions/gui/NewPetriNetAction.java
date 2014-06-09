package pipe.actions.gui;

import pipe.controllers.application.PipeApplicationController;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Action that when pressed creates a new Petri net in a new tab
 */
public class NewPetriNetAction extends GuiAction {


    /**
     * Main PIPE application controller
     */
    private final PipeApplicationController applicationController;

    /**
     * Constructor
     * @param applicationController main PIPE application controller
     */
    public NewPetriNetAction(PipeApplicationController applicationController) {
        super("New", "Create a new Petri net", KeyEvent.VK_N, InputEvent.META_DOWN_MASK);
        this.applicationController = applicationController;
    }

    /**
     * Creates an empty Petri net. This in turn triggers a new tab to be displayed with
     * the blank Petri net.
     * @param e
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        applicationController.createEmptyPetriNet();
    }
}
