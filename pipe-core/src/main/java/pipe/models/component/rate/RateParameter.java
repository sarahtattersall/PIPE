package pipe.models.component.rate;

import pipe.models.component.AbstractPetriNetComponent;
import pipe.visitor.foo.PetriNetComponentVisitor;

public class RateParameter extends AbstractPetriNetComponent implements Rate {

    private String expression;

    private String id;

    private String name;

    public RateParameter(String expression, String id, String name) {
        this.expression = expression;
        this.id = id;
        this.name = name;
    }

    @Override
    public String getExpression() {
        return expression;
    }

    @Override
    public RateType getRateType() {
        return RateType.RATE_PARAMETER;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
       this.name = name;
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RateParameter that = (RateParameter) o;

        if (!expression.equals(that.expression)) {
            return false;
        }
        if (!id.equals(that.id)) {
            return false;
        }
        if (!name.equals(that.name)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = expression.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }
}
