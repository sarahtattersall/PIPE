package pipe.dsl;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.Connectable;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

import static org.assertj.core.api.Assertions.*;

public class APlaceTest {

    private Map<String, Token> tokens;
    private Map<String, Connectable> connectables;
    private Map<String, RateParameter> rateParameters;


    @Before
    public void setUp() {
        tokens = new HashMap<>();
        connectables = new HashMap<>();
        rateParameters = new HashMap<>();
    }

    @Test
    public void createsPlaceWithId() {
        Place place = APlace.withId("P0").create(tokens, connectables, rateParameters);
        Place expected = new Place("P0", "P0");
        assertEquals(expected, place);
    }

    @Test
    public void addsPlaceToConnectables() {
        Place place = APlace.withId("P0").create(tokens, connectables, rateParameters);
        assertThat(connectables).containsEntry("P0", place);
    }

    @Test
    public void createsPlaceWithCapacity() {
        Place place = APlace.withId("P0").andCapacity(5).create(tokens, connectables, rateParameters);
        Place expected = new Place("P0", "P0");
        expected.setCapacity(5);
        assertEquals(expected, place);
    }

    @Test
    public void createsPlaceWithMultipleTokens() {
        tokens.put("Default", new Token("Default", Color.BLACK));
        tokens.put("Red", new Token("Red", Color.RED));

        Place place = APlace.withId("P0").containing(5, "Red").tokens().and(1, "Default").token().create(tokens,
                connectables, rateParameters);
        Place expected = new Place("P0", "P0");
        expected.setTokenCount(tokens.get("Red"), 5);
        expected.setTokenCount(tokens.get("Default"), 1);
        assertEquals(expected, place);
    }
}
