package pipe.views.builder;

import pipe.controllers.PetriNetController;
import pipe.models.component.Transition;
import pipe.views.TransitionView;

public class TransitionViewBuilder {
    private final Transition transition;
    private final PetriNetController controller;

    public TransitionViewBuilder(Transition transition, PetriNetController controller) {
        this.transition = transition;
        this.controller = controller;
    }

    public TransitionView build() {
        TransitionView view =
                new TransitionView(transition.getId(), transition.getName(),
                        transition.getNameXOffset(), transition.getNameYOffset(), transition.isTimed(),
                        transition.isInfiniteServer(), transition.getAngle(), transition, controller);
        return view;
    }
}
