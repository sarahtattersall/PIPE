package pipe.actions.type;


import pipe.controllers.PetriNetController;
import pipe.models.component.Connectable;
import pipe.views.PipeApplicationView;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

public class TimedTransitionAction extends TransitionAction {

    @Override
    protected boolean isTimed() {
        return true;
    }

    public TimedTransitionAction() {
        super("Timed transition", "Add a timed transition (alt-T)", KeyEvent.VK_T, InputEvent.ALT_DOWN_MASK);
    }
}
