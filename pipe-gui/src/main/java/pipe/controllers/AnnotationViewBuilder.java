package pipe.controllers;

import pipe.controllers.PetriNetController;
import pipe.gui.model.PipeApplicationModel;
import pipe.handlers.AnnotationNoteHandler;
import pipe.views.AnnotationView;
import uk.ac.imperial.pipe.models.petrinet.Annotation;

import java.awt.Container;

public class AnnotationViewBuilder {
    final Annotation annotation;

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
