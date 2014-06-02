package pipe.actions.gui;

import pipe.controllers.PetriNetController;
import pipe.controllers.PlaceController;
import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.Place;

import java.awt.event.MouseEvent;
import java.util.Map;

public abstract class TokenAction extends CreateAction {

    public TokenAction(String name, String tooltip, int key, int modifiers, PipeApplicationModel applicationModel) {
        super(name, tooltip, key, modifiers, applicationModel);
    }

    /**
     * Subclasses should perform their relevant action on the token e.g. add/delete
     * @param placeController
     * @param token
     */
    protected abstract void performTokenAction(PlaceController placeController, String token);

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        // Do nothing unless clicked a connectable
    }

    @Override
    public void doConnectableAction(Connectable connectable, PetriNetController petriNetController) {
        //TODO: Maybe a method, connectable.containsTokens()
        if (connectable instanceof Place) {
            Place place = (Place) connectable;
            String token = petriNetController.getSelectedToken();
            performTokenAction(petriNetController.getPlaceController(place), token);
        }
    }

    /**
     *
     * Sets the place to contain counts.
     *
     * Creates a new history edit
     *
     * @param placeController
     * @param counts
     */
    protected void setTokenCounts(PlaceController placeController, Map<String, Integer> counts) {
        placeController.setTokenCounts(counts);
    }
}
