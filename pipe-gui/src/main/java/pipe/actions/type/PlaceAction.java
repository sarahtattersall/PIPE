package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.gui.Grid;
import pipe.gui.ZoomController;
import pipe.historyActions.AddPetriNetObject;
import pipe.models.petrinet.PetriNet;
import pipe.models.component.Connectable;
import pipe.models.component.place.Place;
import pipe.utilities.gui.GuiUtils;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Creates a new Place, adds it to a petri net and adds a history item
 */
public class PlaceAction extends TypeAction {


    public PlaceAction(final String name, final int typeID,
                       final String tooltip, final String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        if (event.getClickCount() > 0) {
            Point point = GuiUtils.getUnZoomedPoint(event.getPoint(), petriNetController);
            Place place = newPlace(point, petriNetController);
            PetriNet net = petriNetController.getPetriNet();
            petriNetController.getHistoryManager().addNewEdit(new AddPetriNetObject(place, net));
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
