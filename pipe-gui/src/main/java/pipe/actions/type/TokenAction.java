package pipe.actions.type;

import pipe.controllers.PetriNetController;
import pipe.controllers.PlaceController;
import pipe.models.component.Connectable;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;

import java.awt.event.MouseEvent;

public abstract class TokenAction extends TypeAction {

    public TokenAction(String name, String tooltip, int key, int modifiers) {
        super(name, tooltip, key, modifiers);
    }

    /**
     * Subclasses should perform their relevant action on the token e.g. add/delete
     * @param placeController
     * @param token
     */
    protected abstract void performTokenAction(PlaceController placeController, Token token);

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
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
