package pipe.views.builder;

import pipe.controllers.PetriNetController;
import pipe.views.TransitionView;
import uk.ac.imperial.pipe.models.component.transition.Transition;

public class TransitionViewBuilder {
    private final Transition transition;
    private final PetriNetController controller;

    public TransitionViewBuilder(Transition transition, PetriNetController controller) {
        this.transition = transition;
        this.controller = controller;
    }

    public TransitionView build() {
        return new TransitionView(transition, controller);
    }
}
