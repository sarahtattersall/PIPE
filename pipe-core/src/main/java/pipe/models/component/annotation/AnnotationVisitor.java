package pipe.models.component.annotation;

import pipe.visitor.foo.PetriNetComponentVisitor;

public interface AnnotationVisitor extends PetriNetComponentVisitor {
    public void visit(Annotation annotation);
}
