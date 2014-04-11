package utils;

import pipe.models.component.token.Token;

import java.awt.*;

/**
 * Static class for useful token utilities
 */
public class TokenUtils {
    private TokenUtils() {
    }

    public static Token createDefaultToken() {
        return new Token("Default", new Color(0, 0, 0));
    }
}
