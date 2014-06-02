package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.TokenViewBuilder;
import pipe.views.TokenView;
import uk.ac.imperial.pipe.models.petrinet.ColoredToken;
import uk.ac.imperial.pipe.models.petrinet.Token;

import java.awt.Color;

import static org.junit.Assert.assertEquals;

public class TokenViewBuilderTest {
    Token token;
    TokenViewBuilder builder;

    @Before
    public void setUp()
    {
        token = new ColoredToken("id", Color.BLACK);
        builder = new TokenViewBuilder(token);
    }

    @Test
    public void correctlySetsModel()
    {
        TokenView view = builder.build();
        assertEquals(token, view.getModel());
    }
}
