package pipe.petrinet.writer;


import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pipe.models.*;
import pipe.petrinet.writer.reflectionCreator.ElementCreator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ArcElementCreatorTest {
    private ElementCreator creator;
    private Document document;
    private static final String SOURCE = "P0";
    private static final String TARGET = "T0";
    private static final String TOKEN_COUNT = "10";
    private static final String ID = "id";

    @Before
    public void setUp() throws ParserConfigurationException {
        creator = new ElementCreator();
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        document = builder.newDocument();
    }

    private Arc createArc() {
        Place source = new Place(SOURCE, SOURCE);
        Transition target = new Transition(TARGET, TARGET);
        Map<Token, String> tokenCounts = createTokenCounts();
        Arc arc = new NormalArc(source, target, tokenCounts);
        arc.setId(ID);
        return arc;
    }

    private Map<Token, String> createTokenCounts() {
        Map<Token, String> tokens = new HashMap<Token, String>();
        Token token = new Token("Default", true, 0, new Color(0,0,0));
        tokens.put(token, TOKEN_COUNT);
        return tokens;
    }

    @Test
    public void setsCorrectTag() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Arc arc = createArc();
        Element element = creator.createElement(arc, document);

        assertEquals("arc", element.getTagName());
    }

    @Test
    public void writesCorrectSource() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Arc arc = createArc();
        Element element = creator.createElement(arc, document);

        String source = element.getAttribute("source");
        assertEquals(SOURCE, source);
    }

    @Test
    public void writesCorrectTarget() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Arc arc = createArc();
        Element element = creator.createElement(arc, document);

        String target = element.getAttribute("target");
        assertEquals(TARGET, target);
    }

    @Test
    public void writesCorrectId() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Arc arc = createArc();
        Element element = creator.createElement(arc, document);

        String id = element.getAttribute("id");
        assertEquals(ID, id);
    }


}
