package pipe.petrinet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pipe.models.Marking;
import pipe.models.Place;
import pipe.models.Token;
import pipe.utilities.transformers.PNMLTransformer;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class PlaceCreatorTest {
    PlaceCreator creator;
    Element placeElement;
    Map<String, Token> tokens = new HashMap<String, Token>();

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    /**
     * Range in which to declare doubles equal
     */
    private static final double DOUBLE_DELTA = 0.001;

    @Before
    public void setUp()
    {
        PNMLTransformer transformer = new PNMLTransformer();
        Document document = transformer.transformPNML("src/test/resources/xml/place/singlePlace.xml");
        Element rootElement = document.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();
        placeElement = (Element) nodes.item(1);
    }

    @Test
    public void createsPlace() {
        creator = new PlaceCreator();
        addDefaultTokenToTokens();
        creator.setTokens(tokens);

        Place place = creator.create(placeElement);

        assertNotNull(place);

        assertEquals("P0", place.getName());
        assertEquals(225, place.getX(), DOUBLE_DELTA);
        assertEquals(240, place.getY(), DOUBLE_DELTA);

        assertEquals(0, place.getMarkingXOffset(), DOUBLE_DELTA);
        assertEquals(0, place.getMarkingYOffset(), DOUBLE_DELTA);
        assertEquals(0, place.getNameXOffset(), DOUBLE_DELTA);
        assertEquals(0, place.getNameYOffset(), DOUBLE_DELTA);
        assertEquals(0, place.getCapacity(), DOUBLE_DELTA);
        assertEquals(1, place.getTokenCounts().size());
    }

    @Test
    public void willNotCreatePlaceIfNoToken() {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("No Default token exists!");

        creator = new PlaceCreator();
        creator.create(placeElement);
    }

    /**
     *
     * @return default token added
     */
    private Token addDefaultTokenToTokens() {
        Token token = new Token("Default", true, 0, new Color(0, 0,0));
        tokens.put("Default", token);
        return token;
    }

    @Test
    public void createsMarkingCorrectlyWithTokenMap() {
        Token defaultToken = addDefaultTokenToTokens();

        creator = new PlaceCreator();
        creator.setTokens(tokens);
        Place place = creator.create(placeElement);
        Map<Token, Integer> counts = place.getTokenCounts();

        assertTrue(counts.containsKey(defaultToken));
        Integer count = counts.get(defaultToken);
        assertEquals(1, count.intValue());
    }
}
