package pipe.models.component.token;

import pipe.models.component.AbstractPetriNetComponent;
import pipe.visitor.component.PetriNetComponentVisitor;

import java.awt.*;

public class Token extends AbstractPetriNetComponent {

    /**
     * Message fired when the token is enabled/disabled
     */
    public static final String TOKEN_ENABLED_CHANGE_MESSAGE = "enabled";

    /**
     * Message fired when the token color changes
     */
    public static final String COLOR_CHANGE_MESSAGE = "color";

    private String id;

    private Color color;

    public Token() {
        this("", Color.BLACK);
    }

    public Token(String id, Color color) {
        this.id = id;
        this.color = color;
    }

    public Token(Token token) {
        this.id = token.getId();
        this.color = token.getColor();
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        Color old = this.color;
        this.color = color;
        changeSupport.firePropertyChange(COLOR_CHANGE_MESSAGE, old, color);
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
        changeSupport.firePropertyChange(ID_CHANGE_MESSAGE, old, id);
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
