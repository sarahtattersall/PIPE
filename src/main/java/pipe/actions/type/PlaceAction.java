package pipe.actions.type;

import pipe.actions.GuiAction;
import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.gui.ApplicationSettings;
import pipe.gui.Grid;
import pipe.historyActions.AddPetriNetObject;
import pipe.models.PetriNet;
import pipe.models.PipeApplicationModel;
import pipe.models.component.Connectable;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.Place;

import java.awt.*;

/**
 * Creates a new Place, adds it to a petri net and adds a history item
 */
public class PlaceAction extends TypeAction {

    private String getNewPetriNetName(PetriNetController petriNetController) {
        int number = petriNetController.getUniquePlaceNumber();
        String id = "P" + number;
        return id;
    }

    private Place newPlace(Point point, PetriNetController petriNetController)
    {
        String id = getNewPetriNetName(petriNetController);
        Place place = new Place(id, id);
        place.setX(Grid.getModifiedX(point.x));
        place.setY(Grid.getModifiedY(point.y));

        PetriNet petriNet = petriNetController.getPetriNet();
        petriNet.addPlace(place);
        petriNet.notifyObservers();

        return place;
    }

    @Override
    public void doAction(Point point, PetriNetController petriNetController) {
        Place place = newPlace(point, petriNetController);
        PetriNet net = petriNetController.getPetriNet();
        petriNetController.getHistoryManager().addNewEdit(new AddPetriNetObject(place, net));

    }

    @Override
    public void doConnectableAction(Connectable connectable, PetriNetController petriNetController) {
        // Do nothing if clicked on existing connectable
    }

    public PlaceAction(final String name, final int typeID,
                       final String tooltip, final String keystroke) {
        super(name, typeID, tooltip, keystroke);
    }

}
