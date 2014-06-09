package pipe.actions.gui;

import pipe.controllers.PetriNetController;
import uk.ac.imperial.pipe.models.petrinet.Connectable;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Noop class. Does nothing when clicking anywhere
 * Useful for when toggling in and out of animation mode
 */
public class NoopAction extends CreateAction {
    /**
     * Constructor
     * @param applicationModel
     */
    public NoopAction(PipeApplicationModel applicationModel) {
        super("Noop", "Noop", KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK, applicationModel);
    }

    /**
     * Performs a noop
     * @param event              mouse event
     * @param petriNetController controller for the petri net
     */
    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        // Noop
    }

    /**
     * Performs a noop
     * @param connectable        item clicked
     * @param petriNetController controller for the petri net
     * @param <T>
     */
    @Override
    public <T extends Connectable> void doConnectableAction(T connectable, PetriNetController petriNetController) {
        // Noop
    }
}
