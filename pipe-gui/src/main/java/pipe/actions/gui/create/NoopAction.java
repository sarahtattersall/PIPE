package pipe.actions.gui.create;

import pipe.controllers.PetriNetController;
import pipe.models.component.Connectable;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Noop class. Does nothing when clicking anywhere
 * Useful for when toggling in and out of animation mode
 */
public class NoopAction extends CreateAction {
    public NoopAction() {
        super("Noop", "Noop", KeyEvent.VK_N, InputEvent.ALT_DOWN_MASK);
    }

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {

    }

    @Override
    public <T extends Connectable> void doConnectableAction(T connectable, PetriNetController petriNetController) {

    }
}
