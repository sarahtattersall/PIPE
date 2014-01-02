package pipe.petrinet;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pipe.models.component.*;
import pipe.petrinet.reader.creator.ArcCreator;
import pipe.utilities.transformers.PNMLTransformer;
import utils.TokenUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArcCreatorTest {
    ArcCreator creator;
    Map<String, Place> places = new HashMap<String, Place>();
    Map<String, Transition> transitions = new HashMap<String, Transition>();
    Place source = new Place("P0", "P0");
    Transition target = new Transition("T0", "T0");

    Map<String, Token> tokens = new HashMap<String, Token>();


    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    /**
     * Range in which to declare doubles equal
     */
    private static final double DOUBLE_DELTA = 0.001;

    private Element createNormalArcNoWeight()
    {
        return createFromFile("src/test/resources/xml/arc/arcNoWeight.xml");
    }

    private Element createInhibitorArc() {
        return createFromFile("src/test/resources/xml/arc/inhibitorArc.xml");
    }

    private Element createArcWeightNoToken() {
        return createFromFile("src/test/resources/xml/arc/arcWeightNoToken.xml");
    }

    private Element createFromFile(String path)
    {
        PNMLTransformer transformer = new PNMLTransformer();
        Document document = transformer.transformPNML(path);
        Element rootElement = document.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();
        Element arcElement = (Element) nodes.item(1);
        return arcElement;
    }

    private Element createNormalArcWithWeight()
    {
        return createFromFile("src/test/resources/xml/arc/normalArcWithWeight.xml");
    }

    private Token addDefaultTokenToTokens()
    {
        Token token = TokenUtils.createDefaultToken();
        tokens.put(token.getId(), token);
        return token;
    }


    @Before
    public void setUp()
    {
        places.put(source.getId(), source);
        transitions.put(target.getId(), target);
        creator = new ArcCreator();
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
        assertEquals(NormalArc.class, arc.getClass());
        assertEquals(source, arc.getSource());
        assertEquals(target, arc.getTarget());
        assertEquals("Arc0", arc.getId());
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
    public void createsInhibitoryArc()
    {
        addDefaultTokenToTokens();
        creator.setTokens(tokens);
        Element arcElement = createInhibitorArc();
        Arc arc = creator.create(arcElement);

        assertNotNull(arc);
        assertEquals(InhibitorArc.class, arc.getClass());
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
}
