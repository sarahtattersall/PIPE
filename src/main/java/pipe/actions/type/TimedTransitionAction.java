package pipe.actions.type;


import pipe.controllers.PetriNetController;
import pipe.models.component.Connectable;
import pipe.views.PipeApplicationView;

public class TimedTransitionAction extends TransitionAction {

    @Override
    protected boolean isTimed() {
        return true;
    }

    public TimedTransitionAction(final String name, final int typeID,
                                 final String tooltip,
                                 final String keystroke, PipeApplicationView applicationView) {
        super(name, typeID, tooltip, keystroke, applicationView);
    }
}
