package pipe.petrinet;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pipe.models.Marking;
import pipe.models.Place;
import pipe.models.Token;
import pipe.utilities.transformers.PNMLTransformer;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class PlaceCreatorTest {
    PlaceCreator creator;
    Element placeElement;
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
        creator = new PlaceCreator(new HashMap<String, Token>());
        Place place = creator.createPlace(placeElement);

        assertNotNull(place);

        assertEquals("P0", place.getName());
        assertEquals(226, place.getX(), DOUBLE_DELTA);
        assertEquals(241, place.getY(), DOUBLE_DELTA);

        assertEquals(0, place.getMarkingXOffset(), DOUBLE_DELTA);
        assertEquals(0, place.getMarkingYOffset(), DOUBLE_DELTA);
        assertEquals(0, place.getNameXOffset(), DOUBLE_DELTA);
        assertEquals(0, place.getNameYOffset(), DOUBLE_DELTA);
        assertEquals(0, place.getCapacity(), DOUBLE_DELTA);
        assertEquals(1, place.getMarkings().size());
    }

    @Test
    public void createsMarkingCorrectlyWithNoTokenMap() {
        creator = new PlaceCreator(new HashMap<String, Token>());
        Place place = creator.createPlace(placeElement);
        List<Marking> markings = place.getMarkings();

        Marking marking = markings.get(0);
        assertNull(marking.getToken());
        assertEquals("1", marking.getCurrentMarking());
    }

    @Test
    public void createsMarkingCorrectlyWithTokenMap() {
        Token token = new Token();
        Map<String, Token> tokens = new HashMap<String, Token>();
        tokens.put("Default", token);

        creator = new PlaceCreator(tokens);
        Place place = creator.createPlace(placeElement);
        List<Marking> markings = place.getMarkings();

        Marking marking = markings.get(0);
        assertEquals(token, marking.getToken());
        assertEquals("1", marking.getCurrentMarking());
    }
}
