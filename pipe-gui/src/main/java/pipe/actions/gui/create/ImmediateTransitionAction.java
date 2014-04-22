package pipe.actions.gui.create;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class ImmediateTransitionAction extends TransitionAction {


    @Override
    protected boolean isTimed() {
        return false;
    }

    public ImmediateTransitionAction() {
        super("Immediate transition", "Add an immediate transition (alt-I)", KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK);
    }
}