package pipe.models.component.token;

import pipe.visitor.component.PetriNetComponentVisitor;

public interface TokenVisitor extends PetriNetComponentVisitor {
    void visit(Token token);
}
