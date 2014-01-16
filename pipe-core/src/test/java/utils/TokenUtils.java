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
        return new Token("Default", true, 0, new Color(0, 0, 0));
    }
}
