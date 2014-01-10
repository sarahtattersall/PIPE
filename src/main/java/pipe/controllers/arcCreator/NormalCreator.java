package pipe.controllers.arcCreator;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.gui.PetriNetTab;
import pipe.models.PetriNet;
import pipe.models.component.*;
import pipe.views.ArcView;
import pipe.views.PipeApplicationView;
import pipe.views.builder.NormalArcViewBuilder;

import java.util.HashMap;
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
        }else if (source.getClass().equals(Transition.class) && target.getClass().equals(Place.class)) {
            Place place = (Place) target;
            Transition transition = (Transition) source;
            arc = new Arc<Transition, Place>(transition, place, tokens, petriNetController.getForwardStrategy());
        }

        PetriNet petriNet = petriNetController.getPetriNet();
        petriNet.addArc(arc);

        NormalArcViewBuilder builder = new NormalArcViewBuilder(arc, petriNetController);
        ArcView<? extends Connectable, ? extends Connectable> view = builder.build();
        PetriNetTab tab = applicationView.getCurrentTab();
        tab.addNewPetriNetObject(view);
    }

    @Override
    public <S extends Connectable, T extends Connectable> boolean canCreate(S source, T target) {

        return !source.getClass().equals(target.getClass());
    }

}
