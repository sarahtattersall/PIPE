package pipe.controllers;

import pipe.controllers.PetriNetController;
import pipe.gui.model.PipeApplicationModel;
import pipe.handlers.TransitionAnimationHandler;
import pipe.handlers.TransitionHandler;
import pipe.views.TransitionView;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.awt.Container;

public class TransitionViewBuilder {
    private final Transition transition;
    private final PetriNetController controller;

    public TransitionViewBuilder(Transition transition, PetriNetController controller) {
        this.transition = transition;
        this.controller = controller;
    }

    public TransitionView build(Container parent, PipeApplicationModel model) {
        TransitionHandler transitionHandler = new TransitionHandler(parent, transition, controller, model);
        TransitionAnimationHandler animationHandler = new TransitionAnimationHandler(transition, controller);
        return new TransitionView(transition, controller, parent, transitionHandler, animationHandler);
    }
}
