package pipe.actions.gui;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Action used to create immediate transitions
 */
public class ImmediateTransitionAction extends TransitionAction {


    /**
     * Constructor
     * @param applicationModel PIPE current application model
     */
    public ImmediateTransitionAction(PipeApplicationModel applicationModel) {
        super("Immediate transition", "Add an immediate transition (alt-I)", KeyEvent.VK_I, InputEvent.ALT_DOWN_MASK,
                applicationModel);
    }

    /**
     *
     * @return false, immediate actions are not timed
     */
    @Override
    protected boolean isTimed() {
        return false;
    }
}