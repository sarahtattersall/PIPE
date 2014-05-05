package pipe.models.component.arc;

import pipe.visitor.component.PetriNetComponentVisitor;

public interface ArcVisitor extends PetriNetComponentVisitor {
    void visit (InboundArc inboundArc);
    void visit (OutboundArc outboundArc);
}
