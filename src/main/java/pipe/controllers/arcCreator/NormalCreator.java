package pipe.controllers.arcCreator;

import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.models.component.Connectable;
import pipe.models.component.Token;

public class NormalCreator implements ArcActionCreator {

    private final PipeApplicationController controller;

    public NormalCreator(PipeApplicationController controller) {

        this.controller = controller;
    }

    @Override
    public <S extends Connectable, T extends Connectable> void create(S source, T target, Token token) {
        PetriNetController currentPetriNetController = controller.getActivePetriNetController();
        currentPetriNetController.createNormalArc(source, target, token);
    }

    @Override
    public <S extends Connectable, T extends Connectable> boolean canCreate(S source, T target) {

        return !source.getClass().equals(target.getClass());
    }
}
