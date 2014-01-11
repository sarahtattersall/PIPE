package pipe.petrinet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pipe.models.component.Place;
import pipe.models.component.Token;
import pipe.petrinet.reader.creator.PlaceCreator;
import pipe.utilities.transformers.PNMLTransformer;
import utils.TokenUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class PlaceCreatorTest {
    private PlaceCreator creator;
    private Map<String, Token> tokens = new HashMap<String, Token>();

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    /**
     * Range in which to declare doubles equal
     */
    private static final double DOUBLE_DELTA = 0.001;

    private Element getSinglePlaceElement() {
        return getElementForFile("src/test/resources/xml/place/singlePlace.xml");
    }

    private Element getPlaceElementNoTokens() {
        return getElementForFile("src/test/resources/xml/place/noTokenPlace.xml");
    }

    private Element getElementForFile(String file) {
        PNMLTransformer transformer = new PNMLTransformer();
        Document document = transformer.transformPNML(file);
        Element rootElement = document.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();
        return (Element) nodes.item(1);
    }

    @Test
    public void createsPlace() {
        creator = new PlaceCreator();
        addDefaultTokenToTokens();
        creator.setTokens(tokens);

        Element placeElement = getSinglePlaceElement();
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

        Element placeElement = getSinglePlaceElement();
        creator.create(placeElement);
    }

    /**
     *
     * @return default token added
     */
    private Token addDefaultTokenToTokens() {
        Token token = TokenUtils.createDefaultToken();
        tokens.put("Default", token);
        return token;
    }

    @Test
    public void createsMarkingCorrectlyWithTokenMap() {
        Token defaultToken = addDefaultTokenToTokens();

        creator = new PlaceCreator();
        creator.setTokens(tokens);

        Element placeElement = getSinglePlaceElement();
        Place place = creator.create(placeElement);
        Map<Token, Integer> counts = place.getTokenCounts();

        assertTrue(counts.containsKey(defaultToken));
        Integer count = counts.get(defaultToken);
        assertEquals(1, count.intValue());
    }

    @Test
    public void createsMarkingIfNoTokensSet() {

        creator = new PlaceCreator();
        creator.setTokens(tokens);

        Element placeElement = getPlaceElementNoTokens();
        Place place = creator.create(placeElement);
        Map<Token, Integer> counts = place.getTokenCounts();
        assertTrue(counts.isEmpty());
    }
}
