package pipe.petrinet.adapters.utils;

import pipe.models.component.Token;

import java.awt.*;
import java.util.Map;

public class TokenUtils {
    public static Token getOrCreateDefaultToken(Map<String, Token> tokens) {
        if (tokens.containsKey("Default")) {
            return tokens.get("Default");
        } else {
            Token token = new Token("Default", true, 0, new Color(0,0,0));
            tokens.put(token.getId(), token);
            return token;
        }
    }
}
