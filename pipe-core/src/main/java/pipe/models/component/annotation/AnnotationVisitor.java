package pipe.models.component.annotation;

import pipe.visitor.component.PetriNetComponentVisitor;

public interface AnnotationVisitor extends PetriNetComponentVisitor {
    void visit(Annotation annotation);
}
