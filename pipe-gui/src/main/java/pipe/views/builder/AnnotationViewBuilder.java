package pipe.views.builder;

import pipe.controllers.PetriNetController;
import pipe.models.component.annotation.Annotation;
import pipe.views.viewComponents.AnnotationView;

public class AnnotationViewBuilder {
    final Annotation annotation;

    private final PetriNetController controller;

    public AnnotationViewBuilder(Annotation annotation, PetriNetController controller) {
        this.annotation = annotation;
        this.controller = controller;
    }

    public AnnotationView build() {
        return new AnnotationView(annotation, controller);
    }
}
