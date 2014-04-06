package pipe.controllers.arcCreator;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.historyActions.AddPetriNetObject;
import pipe.historyActions.HistoryItem;
import pipe.historyActions.HistoryManager;
import pipe.models.component.Connectable;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.util.HashMap;
import java.util.List;

public class InhibitorCreator implements ArcActionCreator {

    private final PipeApplicationController controller;


    public InhibitorCreator(PipeApplicationController controller) {

        this.controller = controller;
    }

    @Override
    public <S extends Connectable, T extends Connectable> void create(S source, T target) {
        createArc(source, target);
    }

    private <S extends Connectable, T extends Connectable> Arc<Place, Transition> createArc(S source, T target) {
        PetriNetController petriNetController = controller.getActivePetriNetController();
        if (source.getClass().equals(Place.class) && target.getClass().equals(Transition.class)) {
            Place place = (Place) source;
            Transition transition = (Transition) target;
            Arc<Place, Transition> arc =
                    new Arc<>(place, transition, new HashMap<Token, String>(), ArcType.INHIBITOR);
            PetriNet petriNet = petriNetController.getPetriNet();
            petriNet.addArc(arc);
            addToHistory(arc);
            return arc;
        }
        return null;
    }

    @Override
    public <S extends Connectable, T extends Connectable> void create(S source, T target, List<ArcPoint> arcPoints) {
        Arc<Place, Transition> arc = createArc(source, target);
        if (arc != null) {
            arc.addIntermediatePoints(arcPoints);
        }
    }

    @Override
    public <S extends Connectable, T extends Connectable> boolean canCreate(S source, T target) {
        return source.getClass().equals(Place.class) && target.getClass().equals(Transition.class);
    }


    private void addToHistory(Arc<? extends Connectable, ? extends Connectable> arc) {
        PetriNetController netController = controller.getActivePetriNetController();
        PetriNet petriNet = netController.getPetriNet();
        HistoryItem item = new AddPetriNetObject(arc, petriNet);
        HistoryManager manager = netController.getHistoryManager();
        manager.addNewEdit(item);
    }
}
