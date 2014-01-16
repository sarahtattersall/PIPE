package pipe.io.adapters.modelAdapter;

import pipe.io.adapters.model.AdaptedToken;
import pipe.models.component.token.Token;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class TokenAdapter extends XmlAdapter<AdaptedToken, Token> {
    private final Map<String, Token> tokens;

    /**
     * Empty contructor needed for marshelling. Since the method to marshell does not actually
     * use these fields it's ok to initialise them as empty/null.
     */
    public TokenAdapter() {
        tokens = new HashMap<String, Token>();
    }

    public TokenAdapter(Map<String, Token> tokens) {

        this.tokens = tokens;
    }

    @Override
    public Token unmarshal(AdaptedToken adaptedToken) throws Exception {
        Color color = new Color(adaptedToken.getRed(), adaptedToken.getGreen(), adaptedToken.getBlue());
        Token token = new Token(adaptedToken.getId(), adaptedToken.isEnabled(), 0, color);
        tokens.put(token.getId(), token);
        return token;
    }

    @Override
    public AdaptedToken marshal(Token token) throws Exception {
        AdaptedToken adapted = new AdaptedToken();
        adapted.setId(token.getId());
        adapted.setEnabled(token.isEnabled());
        Color color = token.getColor();
        adapted.setRed(color.getRed());
        adapted.setGreen(color.getGreen());
        adapted.setBlue(color.getBlue());
        return adapted;
    }
}
