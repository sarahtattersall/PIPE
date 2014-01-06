package pipe.petrinet.writer;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pipe.models.component.Token;
import pipe.petrinet.writer.reflectionCreator.ElementCreator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class TokenElementCreatorTest {
    private ElementCreator creator;
    private Document document;
    private static final String ID = "red";
    private static final Boolean ENABLED = true;
    private static final Color RED = new Color(255, 0, 0);

    @Before
    public void setUp() throws ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        document = builder.newDocument();
        creator = new ElementCreator(document);
    }

    private Token createToken() {
        return new Token(ID, ENABLED, 0, RED);
    }


    @Test
    public void createsCorrectTag() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Token token = createToken();
        Element element = creator.createElement(token);
        assertEquals("token", element.getTagName());
    }

    @Test
    public void writesCorrectId() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Token token = createToken();
        Element element = creator.createElement(token);

        String id = element.getAttribute("id");
        assertFalse(id.isEmpty());
        assertEquals(ID, id);
    }


    @Test
    public void writesCorrectEnabledValue() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Token token = createToken();
        Element element = creator.createElement(token);

        String enabled = element.getAttribute("enabled");
        assertFalse(enabled.isEmpty());
        assertEquals(String.valueOf(ENABLED), enabled);
    }

    @Test
    public void writesCorrectColor() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Token token = createToken();
        Element element = creator.createElement(token);

        String red = element.getAttribute("red");
        assertFalse(red.isEmpty());
        assertEquals(String.valueOf(RED.getRed()), red);

        String green = element.getAttribute("green");
        assertFalse(green.isEmpty());
        assertEquals(String.valueOf(RED.getGreen()), green);

        String blue = element.getAttribute("blue");
        assertFalse(blue.isEmpty());
        assertEquals(String.valueOf(RED.getBlue()), blue);
    }
}
