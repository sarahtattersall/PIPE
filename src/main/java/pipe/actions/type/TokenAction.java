package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.controllers.PlaceController;
import pipe.gui.ApplicationSettings;
import pipe.models.component.Connectable;
import pipe.models.component.Place;
import pipe.models.component.Token;
import pipe.views.PipeApplicationView;

import java.awt.*;

public abstract class TokenAction extends TypeAction {

    public TokenAction(String name, int typeID, String tooltip, String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }

    /**
     * Subclasses should perform their relevant action on the token e.g. add/delete
     * @param placeController
     * @param token
     */
    protected abstract void performTokenAction(PlaceController placeController, Token token);

    @Override
    public void doAction(Point point, PetriNetController petriNetController) {
        // Do nothing unless clicked a connectable
    }

    @Override
    public void doConnectableAction(Connectable connectable, PetriNetController petriNetController) {
        //TODO: Maybe a method, connectable.containsTokens()
        if (connectable instanceof Place) {
            Place place = (Place) connectable;
            PlaceController placeController = petriNetController.getPlaceController(place);
            Token token = petriNetController.getSelectedToken();
            performTokenAction(placeController, token);
        }
    }
}
