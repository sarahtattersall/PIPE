package pipe.actions.type;

import pipe.controllers.PetriNetController;
import pipe.models.component.Connectable;
import pipe.views.PipeApplicationView;

public class ImmediateTransitionAction extends TransitionAction {


    @Override
    protected boolean isTimed() {
        return false;
    }

    public ImmediateTransitionAction(final String name, final int typeID,
                                     final String tooltip,
                                     final String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }
}