package pipe.controllers;

import pipe.actions.gui.PipeApplicationModel;
import pipe.handlers.TransitionAnimationHandler;
import pipe.handlers.TransitionHandler;
import pipe.views.TransitionView;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.awt.Container;

/**
 * Transition view builder
 */
public class TransitionViewBuilder {
    /**
     * Underlying transition model
     */
    private final Transition transition;

    /**
     * Petri net controller for the Petri net the transition is contained in
     */
    private final PetriNetController controller;

    /**
     * Constructor
     * @param transition Underlying transition model
     * @param controller Petri net controller for the Petri net the transition is contained in
     */
    public TransitionViewBuilder(Transition transition, PetriNetController controller) {
        this.transition = transition;
        this.controller = controller;
    }

    /**
     *
     * @param parent parent that the view will be contained in
     * @param model main PIPE application model
     * @return created Transition view
     */
    public TransitionView build(Container parent, PipeApplicationModel model) {
        TransitionHandler transitionHandler = new TransitionHandler(parent, transition, controller, model);
        TransitionAnimationHandler animationHandler = new TransitionAnimationHandler(transition, controller);
        return new TransitionView(transition, controller, parent, transitionHandler, animationHandler);
    }
}
