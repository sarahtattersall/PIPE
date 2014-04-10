package pipe.controllers.arcCreator;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
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
import java.util.Map;

public class NormalCreator implements ArcActionCreator {

    private final PipeApplicationController controller;

    public NormalCreator(PipeApplicationController controller) {

        this.controller = controller;
    }

    @Override
    public <S extends Connectable, T extends Connectable> Arc<S, T> create(S source, T target) {
        Arc<S, T> arc = createArc(source, target);
        //        addArcToPetriNet(arc);
        return arc;
    }

    @Override
    public <S extends Connectable, T extends Connectable> Arc<S, T> create(S source, T target,
                                                                           List<ArcPoint> arcPoints) {
        Arc<S, T> arc = createArc(source, target);
        arc.addIntermediatePoints(arcPoints);
        //        addArcToPetriNet(arc);
        return arc;
    }

    @Override
    public <S extends Connectable, T extends Connectable> boolean canCreate(S source, T target) {

        return !source.getClass().equals(target.getClass());
    }

    private <S extends Connectable, T extends Connectable> Arc<S, T> createArc(S source, T target) {
        PetriNetController netController = controller.getActivePetriNetController();
        Token token = netController.getSelectedToken();

        Map<Token, String> tokens = new HashMap<Token, String>();
        tokens.put(token, "1");

        Arc<S, T> arc = null;
        if (source.getClass().equals(Place.class) && target.getClass().equals(Transition.class)) {
            Place place = (Place) source;
            Transition transition = (Transition) target;
            arc = new Arc<>((S) place, (T) transition, tokens, ArcType.NORMAL);
        } else if (source.getClass().equals(Transition.class) && target.getClass().equals(Place.class)) {
            Place place = (Place) target;
            Transition transition = (Transition) source;
            arc = new Arc<>((S) transition, (T) place, tokens, ArcType.NORMAL);
        }

        return arc;
    }

    private void addArcToPetriNet(Arc<? extends Connectable, ? extends Connectable> arc) {
        //        PetriNetController netController = controller.getActivePetriNetController();
        //        PetriNet petriNet = netController.getPetriNet();
        //        petriNet.addArc(arc);
        //        addToHistory(arc);

    }

    private void addToHistory(Arc<? extends Connectable, ? extends Connectable> arc) {
        PetriNetController netController = controller.getActivePetriNetController();
        PetriNet petriNet = netController.getPetriNet();
        //        HistoryItem item = new AddPetriNetObject(arc, petriNet);
        //        HistoryManager manager = netController.getHistoryManager();
        //        manager.addNewEdit(item);
    }

}
