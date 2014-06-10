package pipe.controllers;

import pipe.actions.gui.PipeApplicationModel;
import pipe.handlers.AnnotationNoteHandler;
import pipe.views.AnnotationView;
import uk.ac.imperial.pipe.models.petrinet.Annotation;

import java.awt.Container;

/**
 * Builds an annotation view representation of the annotation
 */
public final class AnnotationViewBuilder {
    /**
     * Underlying model
     */
    private final Annotation annotation;

    /**
     * Controller of the Petri net the annotation belongs to
     */
    private final PetriNetController controller;

    public AnnotationViewBuilder(Annotation annotation, PetriNetController controller) {
        this.annotation = annotation;
        this.controller = controller;
    }

    /**
     *
     * @param parent
     * @return a new annotation view who belongs in the specified parent
     */
    public AnnotationView build(Container parent, PipeApplicationModel model) {
        AnnotationNoteHandler noteHandler = new AnnotationNoteHandler(parent, annotation, controller, model);
        return new AnnotationView(annotation, controller, parent, noteHandler);
    }
}
