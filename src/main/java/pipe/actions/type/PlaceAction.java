package pipe.actions.type;

import pipe.actions.TypeAction;
import pipe.controllers.PetriNetController;
import pipe.gui.Grid;
import pipe.historyActions.AddPetriNetObject;
import pipe.models.PetriNet;
import pipe.models.component.Connectable;
import pipe.models.component.Place;
import pipe.views.PipeApplicationView;
import pipe.views.PlaceView;
import pipe.views.builder.PlaceViewBuilder;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * Creates a new Place, adds it to a petri net and adds a history item
 */
public class PlaceAction extends TypeAction {

    private final PipeApplicationView applicationView;

    public PlaceAction(final String name, final int typeID,
                       final String tooltip, final String keystroke, PipeApplicationView applicationView) {
        super(name, typeID, tooltip, keystroke);
        this.applicationView = applicationView;
    }

    @Override
    public void doAction(MouseEvent event, PetriNetController petriNetController) {
        if (event.getClickCount() > 0) {
            Place place = newPlace(event.getPoint(), petriNetController);
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
        place.setX(Grid.getModifiedX(point.x));
        place.setY(Grid.getModifiedY(point.y));

        PetriNet petriNet = petriNetController.getPetriNet();
        petriNet.addPlace(place);

        PlaceViewBuilder builder = new PlaceViewBuilder(place, petriNetController);
        PlaceView view = builder.build();

        applicationView.getCurrentTab().addNewPetriNetObject(view);

        return place;
    }

    private String getNewPetriNetName(PetriNetController petriNetController) {
        int number = petriNetController.getUniquePlaceNumber();
        String id = "P" + number;
        return id;
    }

}
