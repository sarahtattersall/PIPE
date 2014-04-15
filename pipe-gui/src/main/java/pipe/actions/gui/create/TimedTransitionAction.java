package pipe.actions.gui.create;


import pipe.gui.model.PipeApplicationModel;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class TimedTransitionAction extends TransitionAction {

    @Override
    protected boolean isTimed() {
        return true;
    }

    public TimedTransitionAction(PipeApplicationModel applicationModel) {
        super("Timed transition", "Add a timed transition (alt-T)", KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK,
                applicationModel);
    }
}
