package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.models.component.Connectable;
import pipe.models.component.Place;

import java.awt.*;

public class InhibitorArcAction extends ArcAction {
    //TODO Perhaps move into connectable in model.
    @Override
    protected boolean canCreateArcHere(Connectable connectable) {
        return connectable instanceof Place;
    }

    @Override
    protected void createArc(Connectable connectable, PetriNetController controller) {
        controller.startCreatingInhibitorArc(connectable);
    }

    public InhibitorArcAction(final String name, final int typeID,
                     final String tooltip, final String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }

}
