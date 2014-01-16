package pipe.visitor;

import pipe.models.component.Annotation;

public interface AnnotationVisitor extends PetriNetComponentVisitor {
    public void visit(Annotation annotation);
}
