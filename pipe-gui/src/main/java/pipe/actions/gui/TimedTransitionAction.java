package pipe.actions.gui;


import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

/**
 * Creates a timed transition on the Petri net canvas
 */
public class TimedTransitionAction extends TransitionAction {

    /**
     * Constructor
     * @param applicationModel current PIPE application model
     */
    public TimedTransitionAction(PipeApplicationModel applicationModel) {
        super("Timed transition", "Add a timed transition (alt-T)", KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK,
                applicationModel);
    }

    /**
     *
     * @return true because this is a timed transition
     */
    @Override
    protected boolean isTimed() {
        return true;
    }
}
