package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.models.component.Connectable;
import pipe.models.component.Place;
import pipe.models.component.Token;
import pipe.views.PipeApplicationView;

import java.awt.*;

public class InhibitorArcAction extends ArcAction {
    //TODO Perhaps move into connectable in model.
    @Override
    protected boolean canCreateArcHere(Connectable connectable) {
        return connectable instanceof Place;
    }

    @Override
    protected void createArc(Connectable connectable, PetriNetController controller) {
        PipeApplicationView view = ApplicationSettings.getApplicationView();
        Token activeToken = controller.getToken(view.getSelectedTokenName());
        controller.startCreatingInhibitorArc(connectable, activeToken);
    }

    public InhibitorArcAction(final String name, final int typeID,
                     final String tooltip, final String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }

}
