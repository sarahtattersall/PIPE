package pipe.petrinet.writer;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pipe.models.component.Place;
import pipe.models.component.Token;
import pipe.petrinet.writer.reflectionCreator.ElementCreator;
import utils.TokenUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PlaceElementCreatorTest {
    private ElementCreator creator;
    private Document document;

    private static final String ID = "id";
    private static final String NAME = "name";
    private static final int CAPACITY = 10;
    private static final double MARKING_X = 2.0;
    private static final double MARKING_Y = 1.0;
    private static final double NAME_X = 5.0;
    private static final double NAME_Y = 2.4;
    private static final int TOKEN_COUNT = 6;

    @Before
    public void setUp() throws ParserConfigurationException {
        DocumentBuilderFactory builderFactory =
                DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        document = builder.newDocument();
        creator = new ElementCreator(document);
    }

    private Place createPlace() {
        Place place = new Place(ID, NAME);
        place.setCapacity(CAPACITY);
        place.setMarkingXOffset(MARKING_X);
        place.setMarkingYOffset(MARKING_Y);
        place.setNameXOffset(NAME_X);
        place.setNameYOffset(NAME_Y);
        place.setTokenCounts(createTokenCounts());
        return place;
    }

    private Map<Token, Integer> createTokenCounts() {
        Map<Token, Integer> tokens = new HashMap<Token, Integer>();
        Token token = TokenUtils.createDefaultToken();
        tokens.put(token, TOKEN_COUNT);
        return tokens;
    }

    @Test
    public void setsCorrectTag()
            throws IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Place place = createPlace();
        Element element = creator.createElement(place);

        assertEquals("place", element.getTagName());
    }

    @Test
    public void writesCorrectId()
            throws IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Place place = createPlace();
        Element element = creator.createElement(place);
        String attribute = element.getAttribute("id");

        assertFalse(attribute.isEmpty());
        assertEquals(ID, attribute);
    }

    @Test
    public void writesCorrectName()
            throws IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Place place = createPlace();
        Element element = creator.createElement(place);
        String attribute = element.getAttribute("name");

        assertFalse(attribute.isEmpty());
        assertEquals(NAME, attribute);
    }

    @Test
    public void writesCorrectNameOffset()
            throws IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Place place = createPlace();
        Element element = creator.createElement(place);

        String nameOffsetX = element.getAttribute("nameOffsetX");
        assertFalse(nameOffsetX.isEmpty());
        assertEquals(String.valueOf(NAME_X), nameOffsetX);

        String nameOffsetY = element.getAttribute("nameOffsetY");
        assertFalse(nameOffsetY.isEmpty());
        assertEquals(String.valueOf(NAME_Y), nameOffsetY);
    }

    @Test
    public void writesCorrectMarkingOffset()
            throws IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Place place = createPlace();
        Element element = creator.createElement(place);

        String markingOffsetX = element.getAttribute("markingOffsetX");
        assertFalse(markingOffsetX.isEmpty());
        assertEquals(String.valueOf(MARKING_X), markingOffsetX);

        String markingOffsetY = element.getAttribute("markingOffsetY");
        assertFalse(markingOffsetY.isEmpty());
        assertEquals(String.valueOf(MARKING_Y), markingOffsetY);
    }

    @Test
    public void writesCorrectCapacity()
            throws IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Place place = createPlace();
        Element element = creator.createElement(place);

        String capacity = element.getAttribute("capacity");
        assertFalse(capacity.isEmpty());
        assertEquals(String.valueOf(CAPACITY), capacity);
    }

    @Test
    public void writesCorrectTokens()
            throws IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Place place = createPlace();
        Element element = creator.createElement(place);

        String tokens = element.getAttribute("initialMarking");
        assertFalse(tokens.isEmpty());
        assertEquals("Default," + TOKEN_COUNT, tokens);
    }

}
