package pipe.dsl;

import pipe.models.component.Connectable;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;

import java.awt.*;
import java.util.Map;

public class AToken implements DSLCreator<Token> {
    private String name;
    private Color color = Color.BLACK;

    private AToken(String name) { this.name = name; }

    public static AToken called(String name) {
        return new AToken(name);
    }

    public AToken withColor(Color color) {
        this.color = color;
        return this;
    }

    @Override
    public Token create(Map<String, Token> tokens, Map<String, Connectable> connectables,
                        Map<String, RateParameter> rateParameters) {
        Token token = new Token(name, true, 0, color);
        tokens.put(name, token);
        return token;
    }
}
