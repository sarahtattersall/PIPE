package pipe.models.component.rate;

import pipe.exceptions.InvalidRateException;
import pipe.models.component.AbstractPetriNetComponent;
import pipe.visitor.component.PetriNetComponentVisitor;

public class RateParameter extends AbstractPetriNetComponent implements Rate {

    /**
     * Message fired when the places tokens change in any way
     */
    public final static String EXPRESSION_CHANGE_MESSAGE = "expression";

    private String expression;

    private String id;

    private String name;

    /**
     * Copy constructor
     *
     * @param rateParameter
     */
    public RateParameter(RateParameter rateParameter) {
        this(rateParameter.expression, rateParameter.id, rateParameter.name);
    }

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
        String old = this.expression;
        this.expression = expression;
        changeSupport.firePropertyChange(EXPRESSION_CHANGE_MESSAGE, old, expression);
    }

    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {

        String old = this.name;
        this.name = name;
        changeSupport.firePropertyChange(NAME_CHANGE_MESSAGE, old, name);
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
        if (visitor instanceof RateParameterVisitor) {
            try {
                ((RateParameterVisitor) visitor).visit(this);
            } catch (InvalidRateException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        String old = this.id;
        this.id = id;
        changeSupport.firePropertyChange(ID_CHANGE_MESSAGE, old, id);
    }

    @Override
    public int hashCode() {
        int result = expression.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + name.hashCode();
        return result;
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
    public String toString() {
        return id + ": " + expression;
    }
}
