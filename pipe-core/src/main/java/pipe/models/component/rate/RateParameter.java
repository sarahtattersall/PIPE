package pipe.models.component.rate;

import pipe.models.component.AbstractPetriNetComponent;
import pipe.visitor.foo.PetriNetComponentVisitor;

public class RateParameter extends AbstractPetriNetComponent {

    private final String expression;

    private final String id;

    private final String name;

    public RateParameter(String expression, String id, String name) {
        this.expression = expression;
        this.id = id;
        this.name = name;
    }

    public String getExpression() {
        return expression;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {

    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public boolean isDraggable() {
        return false;
    }

    @Override
    public void accept(PetriNetComponentVisitor visitor) {

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {

    }
}
