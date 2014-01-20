package pipe.models.component.token;

import pipe.exceptions.TokenLockedException;
import pipe.models.component.AbstractPetriNetComponent;
import pipe.visitor.foo.PetriNetComponentVisitor;

import java.awt.*;

public class Token extends AbstractPetriNetComponent {

    private String id;

    private boolean enabled;

    private int currentMarking;

    private Color color;

    public Token() {
        this("", false, 0, Color.BLACK);
    }

    public Token(String id, boolean enabled, int currentMarking, Color color) {
        this.id = id;
        this.enabled = enabled;
        this.currentMarking = currentMarking;
        this.color = color;
    }

    public Token(Token token) {
        this.id = token.getId();
        this.color = token.getColor();
        this.enabled = token.isEnabled();
    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     * @throws TokenLockedException if the Token is locked
     */
    public void setEnabled(boolean enabled) throws TokenLockedException {
        boolean old = this.enabled;
        this.enabled = enabled;
        changeSupport.firePropertyChange("enabled", old, enabled);
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        Color old = this.color;
        this.color = color;
        changeSupport.firePropertyChange("color", old, color);
    }

    public int getCurrentMarking() {
        return currentMarking;
    }

    public void setCurrentMarking(int currentMarking) {
        this.currentMarking = currentMarking;
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
        if (visitor instanceof TokenVisitor) {
            ((TokenVisitor) visitor).visit(this);
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
        changeSupport.firePropertyChange("id", old, id);
    }

    @Override
    public void setName(String name) {
        setId(name);
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + color.hashCode();
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

        Token token = (Token) o;

        if (!color.equals(token.color)) {
            return false;
        }
        if (!id.equals(token.id)) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return getId();
    }
}
