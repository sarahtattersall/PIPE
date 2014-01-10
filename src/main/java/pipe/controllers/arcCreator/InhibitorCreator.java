package pipe.controllers.arcCreator;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.models.component.Connectable;
import pipe.models.component.Place;
import pipe.models.component.Token;
import pipe.models.component.Transition;

public class InhibitorCreator implements ArcActionCreator {

    private final PipeApplicationController controller;

    public InhibitorCreator(PipeApplicationController controller) {

        this.controller = controller;
    }

    @Override
    public <S extends Connectable, T extends Connectable> void create(S source,
                                                                      T target,
                                                                      Token token) {
        PetriNetController currentPetriNetController = controller.getActivePetriNetController();
        currentPetriNetController.startCreatingInhibitorArc(source, target, token);
    }

    @Override
    public <S extends Connectable, T extends Connectable> boolean canCreate(S source, T target) {
        return source.getClass().equals(Place.class) && target.getClass().equals(Transition.class);
    }
}
