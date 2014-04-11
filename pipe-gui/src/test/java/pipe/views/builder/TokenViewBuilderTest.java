package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.token.Token;
import pipe.views.TokenView;

import java.awt.*;

import static org.junit.Assert.assertEquals;

public class TokenViewBuilderTest {
    Token token;
    TokenViewBuilder builder;

    @Before
    public void setUp()
    {
        token = new Token("id", Color.BLACK);
        builder = new TokenViewBuilder(token);
    }

    @Test
    public void correctlySetsModel()
    {
        TokenView view = builder.build();
        assertEquals(token, view.getModel());
    }
}
