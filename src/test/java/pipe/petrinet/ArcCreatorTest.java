package pipe.petrinet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pipe.models.PetriNet;
import pipe.models.component.*;
import pipe.models.strategy.arc.ArcStrategy;
import pipe.models.strategy.arc.BackwardsNormalStrategy;
import pipe.models.strategy.arc.ForwardsNormalStrategy;
import pipe.models.strategy.arc.InhibitorStrategy;
import pipe.petrinet.reader.creator.ArcCreator;
import pipe.utilities.transformers.PNMLTransformer;
import utils.TokenUtils;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ArcCreatorTest {
    @Rule
    public ExpectedException expectedEx = ExpectedException.none();
    ArcCreator creator;
    PetriNet mockNet;
    Map<String, Place> places = new HashMap<String, Place>();
    Map<String, Transition> transitions = new HashMap<String, Transition>();
    Place source = new Place("P0", "P0");
    Transition target = new Transition("T0", "T0");
    Map<String, Token> tokens = new HashMap<String, Token>();

    @Before
    public void setUp() {
        mockNet = mock(PetriNet.class);
        places.put(source.getId(), source);
        transitions.put(target.getId(), target);

        ArcStrategy inhibitorStrategy = new InhibitorStrategy();
        ArcStrategy normalForwardStrategy = new ForwardsNormalStrategy(mockNet);
        ArcStrategy normalBackwardStrategy = new BackwardsNormalStrategy(mockNet);
        creator = new ArcCreator(inhibitorStrategy, normalForwardStrategy, normalBackwardStrategy);
        creator.setPlaces(places);
        creator.setTransitions(transitions);
    }

    @Test
    public void createsArc() {

        Element arcElement = createNormalArcNoWeight();
        addDefaultTokenToTokens();
        creator.setTokens(tokens);
        Arc arc = creator.create(arcElement);

        assertNotNull(arc);
        assertEquals(ArcType.NORMAL, arc.getType());
        assertEquals(source, arc.getSource());
        assertEquals(target, arc.getTarget());
        assertEquals("Arc0", arc.getId());
    }

    private Token addDefaultTokenToTokens() {
        Token token = TokenUtils.createDefaultToken();
        tokens.put(token.getId(), token);
        return token;
    }

    private Element createNormalArcNoWeight() {
        return createFromFile("src/test/resources/xml/arc/arcNoWeight.xml");
    }

    private Element createFromFile(String path) {
        PNMLTransformer transformer = new PNMLTransformer();
        Document document = transformer.transformPNML(path);
        Element rootElement = document.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();
        Element arcElement = (Element) nodes.item(1);
        return arcElement;
    }

    @Test
    public void willNotCreateArcIfTokenNotSet() {
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("No Default token exists!");

        Element arcElement = createNormalArcNoWeight();
        creator.create(arcElement);
    }

    @Test
    public void setsSourceAndTargetArcs() {
        Place mockSource = mock(Place.class);
        when(mockSource.getId()).thenReturn("P0");

        Transition mockTarget = mock(Transition.class);
        when(mockTarget.getId()).thenReturn("T0");

        places.clear();
        places.put(mockSource.getId(), mockSource);
        transitions.put(mockTarget.getId(), mockTarget);

        addDefaultTokenToTokens();
        creator.setTokens(tokens);
        creator.setPlaces(places);
        creator.setTransitions(transitions);

        Element arcElement = createNormalArcNoWeight();
        Arc arc = creator.create(arcElement);
        verify(mockSource).addOutbound(arc);
        verify(mockTarget).addInbound(arc);
    }

    @Test
    public void createsCorrectMarkingIfWeightSpecified() {
        Token token = addDefaultTokenToTokens();
        creator.setTokens(tokens);
        Element arcElement = createNormalArcWithWeight();
        Arc arc = creator.create(arcElement);

        Map<Token, String> weights = arc.getTokenWeights();
        assertEquals(1, weights.size());

        assertTrue(weights.containsKey(token));
        String weight = weights.get(token);
        assertEquals("4", weight);
    }

    private Element createNormalArcWithWeight() {
        return createFromFile("src/test/resources/xml/arc/normalArcWithWeight.xml");
    }

    @Test
    public void createsMarkingWithCorrectToken() {
        Token token = addDefaultTokenToTokens();
        creator.setTokens(tokens);

        Element arcElement = createNormalArcWithWeight();
        Arc arc = creator.create(arcElement);

        Map<Token, String> weights = arc.getTokenWeights();
        assertEquals(1, weights.size());
        assertTrue(weights.containsKey(token));
    }

    @Test
    public void createsInhibitoryArc() {
        addDefaultTokenToTokens();
        creator.setTokens(tokens);
        Element arcElement = createInhibitorArc();
        Arc arc = creator.create(arcElement);

        assertNotNull(arc);
        assertEquals(ArcType.INHIBITOR, arc.getType());
    }

    private Element createInhibitorArc() {
        return createFromFile("src/test/resources/xml/arc/inhibitorArc.xml");
    }

    @Test
    public void createsArcWithDefaultTokenIfNoTokenSpecifiedForWeight() {
        Token token = addDefaultTokenToTokens();
        creator.setTokens(tokens);

        Element arcElement = createArcWeightNoToken();
        Arc arc = creator.create(arcElement);

        assertNotNull(arc);
        Map<Token, String> weights = arc.getTokenWeights();
        assertEquals(1, weights.size());
        assertTrue(weights.containsKey(token));
    }

    private Element createArcWeightNoToken() {
        return createFromFile("src/test/resources/xml/arc/arcWeightNoToken.xml");
    }

    @Test
    public void addsPoints() {
        Token token = addDefaultTokenToTokens();
        creator.setTokens(tokens);
        Element arcElement = createNormalArcNoWeight();
        Arc arc = creator.create(arcElement);

        List<ArcPoint> points = arc.getIntermediatePoints();
        assertEquals(3, points.size());
        ArcPoint point1 = new ArcPoint(new Point2D.Double(294, 259), false);
        assertEquals(point1, points.get(0));

        ArcPoint point2 = new ArcPoint(new Point2D.Double(395, 243), true);
        assertEquals(point2, points.get(1));

        ArcPoint point3 = new ArcPoint(new Point2D.Double(417, 297), false);
        assertEquals(point3, points.get(2));
    }
}
