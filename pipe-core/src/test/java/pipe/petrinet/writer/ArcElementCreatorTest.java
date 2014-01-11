package pipe.petrinet.writer;


import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pipe.models.component.*;
import pipe.models.strategy.arc.ArcStrategy;
import pipe.petrinet.writer.reflectionCreator.ElementCreator;
import utils.TokenUtils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.geom.Point2D;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class ArcElementCreatorTest {
    private static final String SOURCE = "P0";
    private static final String TARGET = "T0";
    private static final String TOKEN_COUNT = "10";
    private static final String ID = "id";
    private static final ArcPoint ARC_POINT = new ArcPoint(new Point2D.Double(10, 56), true);
    private ElementCreator creator;
    private Document document;

    @Before
    public void setUp() throws ParserConfigurationException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        document = builder.newDocument();
        creator = new ElementCreator(document);
    }

    @Test
    public void setsCorrectTag() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Arc arc = createArc();
        Element element = creator.createElement(arc);

        assertEquals("arc", element.getTagName());
    }

    private Arc createArc() {
        Place source = new Place(SOURCE, SOURCE);
        Transition target = new Transition(TARGET, TARGET);
        Map<Token, String> tokenCounts = createTokenCounts();
        ArcStrategy<Place, Transition> mockStrategy = mock(ArcStrategy.class);
        Arc<Place, Transition> arc = new Arc<Place, Transition>(source, target, tokenCounts, mockStrategy);
        arc.setId(ID);
        arc.addIntermediatePoint(ARC_POINT);
        return arc;
    }

    private Map<Token, String> createTokenCounts() {
        Map<Token, String> tokens = new HashMap<Token, String>();
        Token token = TokenUtils.createDefaultToken();
        tokens.put(token, TOKEN_COUNT);
        return tokens;
    }

    @Test
    public void writesCorrectSource() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Arc arc = createArc();
        Element element = creator.createElement(arc);

        String source = element.getAttribute("source");
        assertEquals(SOURCE, source);
    }

    @Test
    public void writesCorrectTarget() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Arc arc = createArc();
        Element element = creator.createElement(arc);

        String target = element.getAttribute("target");
        assertEquals(TARGET, target);
    }

    @Test
    public void writesCorrectId() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Arc arc = createArc();
        Element element = creator.createElement(arc);

        String id = element.getAttribute("id");
        assertEquals(ID, id);
    }

    @Test
    public void writesArcPathPoints() throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Arc arc = createArc();
        Element element = creator.createElement(arc);

        NodeList nodes = element.getElementsByTagName("arcpath");
        assertEquals(1, nodes.getLength());
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                Element arcPointInfo = (Element) node;
                double x = Double.valueOf(arcPointInfo.getAttribute("xCoord"));
                double y = Double.valueOf(arcPointInfo.getAttribute("yCoord"));
                boolean isCurved = Boolean.valueOf(arcPointInfo.getAttribute("arcPointType"));
                assertEquals(10, x, 0.001);
                assertEquals(56, y, 0.001);
                assertTrue(isCurved);
            }
        }
    }


}
