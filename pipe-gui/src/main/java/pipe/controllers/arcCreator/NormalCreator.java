package pipe.controllers.arcCreator;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.models.PetriNet;
import pipe.models.component.*;
import pipe.views.PipeApplicationView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NormalCreator implements ArcActionCreator {

    private final PipeApplicationController controller;

    private final PipeApplicationView applicationView;

    public NormalCreator(PipeApplicationController controller, PipeApplicationView applicationView) {

        this.controller = controller;
        this.applicationView = applicationView;
    }

    @Override
    public <S extends Connectable, T extends Connectable> void create(S source, T target) {
        createArc(source, target);
    }

    @Override
    public <S extends Connectable, T extends Connectable> void create(S source, T target, List<ArcPoint> arcPoints) {
        Arc<? extends Connectable, ? extends Connectable> arc = createArc(source, target);
        arc.addIntermediatePoints(arcPoints);
    }

    @Override
    public <S extends Connectable, T extends Connectable> boolean canCreate(S source, T target) {

        return !source.getClass().equals(target.getClass());
    }

    private <S extends Connectable, T extends Connectable> Arc<? extends Connectable, ? extends Connectable> createArc(
            S source, T target) {
        PetriNetController netController = controller.getActivePetriNetController();
        Token token = netController.getSelectedToken();

        PetriNetController petriNetController = controller.getActivePetriNetController();

        Map<Token, String> tokens = new HashMap<Token, String>();
        tokens.put(token, "1");

        Arc<? extends Connectable, ? extends Connectable> arc = null;
        if (source.getClass().equals(Place.class) && target.getClass().equals(Transition.class)) {
            Place place = (Place) source;
            Transition transition = (Transition) target;
            arc = new Arc<Place, Transition>(place, transition, tokens, petriNetController.getBackwardsStrategy());
        } else if (source.getClass().equals(Transition.class) && target.getClass().equals(Place.class)) {
            Place place = (Place) target;
            Transition transition = (Transition) source;
            arc = new Arc<Transition, Place>(transition, place, tokens, petriNetController.getForwardStrategy());
        }

        PetriNet petriNet = netController.getPetriNet();
        petriNet.addArc(arc);

        return arc;
    }

}
