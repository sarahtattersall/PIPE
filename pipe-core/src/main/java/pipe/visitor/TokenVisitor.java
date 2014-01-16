package pipe.visitor;

import pipe.models.component.Token;

public interface TokenVisitor extends PetriNetComponentVisitor {
    public void visit(Token token);
}
