package pipe.actions.gui;


import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class TimedTransitionAction extends TransitionAction {

    public TimedTransitionAction(PipeApplicationModel applicationModel) {
        super("Timed transition", "Add a timed transition (alt-T)", KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK,
                applicationModel);
    }

    @Override
    protected boolean isTimed() {
        return true;
    }
}
