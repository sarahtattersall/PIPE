package pipe.dsl;

import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;

import java.awt.Color;
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
    public Token create(Map<String, Token> tokens, Map<String, Place> places, Map<String, Transition> transitions, Map<String, RateParameter> rateParameters) {
        Token token = new Token(name, color);
        tokens.put(name, token);
        return token;
    }
}
