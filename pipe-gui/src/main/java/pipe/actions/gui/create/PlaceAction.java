package pipe.actions.gui.create;

import pipe.controllers.PetriNetController;
import pipe.gui.model.PipeApplicationModel;
import pipe.historyActions.component.AddPetriNetObject;
import uk.ac.imperial.pipe.models.component.Connectable;
import uk.ac.imperial.pipe.models.component.place.Place;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;

import java.awt.Point;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

/**
 * Creates a new Place, adds it to a petri net and adds a history item
 */
public class PlaceAction extends CreateAction {


    public PlaceAction(PipeApplicationModel applicationModel) {
        super("Place", "Add a place", KeyEvent.VK_P, InputEvent.ALT_DOWN_MASK, applicationModel);
    }

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        if (event.getClickCount() > 0) {
            Point point = event.getPoint();
            Place place = newPlace(point, petriNetController);
            PetriNet net = petriNetController.getPetriNet();


            registerUndoEvent(new AddPetriNetObject(place, net));

        }

    }

    @Override
    public void doConnectableAction(Connectable connectable, PetriNetController petriNetController) {
        // Do nothing if clicked on existing connectable
    }

    private Place newPlace(Point point, PetriNetController petriNetController) {
        String id = getNewPetriNetName(petriNetController);
        Place place = new Place(id, id);
        place.setX(point.x);
        place.setY(point.y);

        PetriNet petriNet = petriNetController.getPetriNet();
        petriNet.addPlace(place);

        return place;
    }

    private String getNewPetriNetName(PetriNetController petriNetController) {
        return petriNetController.getUniquePlaceName();
    }

}
