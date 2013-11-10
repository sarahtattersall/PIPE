package pipe.petrinet;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pipe.models.*;
import pipe.utilities.transformers.PNMLTransformer;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArcCreatorTest {
    ArcCreator creator;
    Map<String, Connectable> connectables = new HashMap<String, Connectable>();
    Connectable source = new Place("P0", "P0");
    Connectable target = new Transition("T0", "T0");
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


    @Before
    public void setUp()
    {
        connectables.put(source.getId(), source);
        connectables.put(target.getId(), target);
        creator = new ArcCreator();
        creator.setConnectables(connectables);
    }

    @Test
    public void createsArc() {

        Element arcElement = createNormalArcNoWeight();
        Arc arc = creator.create(arcElement);

        assertNotNull(arc);
        assertEquals(NormalArc.class, arc.getClass());
        assertEquals(source, arc.getSource());
        assertEquals(target, arc.getTarget());
        assertEquals("Arc0", arc.getId());
    }

    @Test
    public void createsCorrectDefaultMarkingIfNoWeight() {
        Element arcElement = createNormalArcNoWeight();
        Arc arc = creator.create(arcElement);

        List<Marking> markings = arc.getWeight();
        assertEquals(1, markings.size());

        Marking marking = markings.get(0);
        assertNotNull(marking);
        assertEquals("1", marking.getCurrentMarking());
        assertNull(marking.getToken());
    }

    @Test
    public void setsSourceAndTargetArcs() {
        Connectable mockSource = mock(Connectable.class);
        when(mockSource.getId()).thenReturn("P0");

        Connectable mockTarget = mock(Connectable.class);
        when(mockTarget.getId()).thenReturn("T0");

        connectables.clear();
        connectables.put(mockSource.getId(), mockSource);
        connectables.put(mockTarget.getId(), mockTarget);

        creator.setConnectables(connectables);

        Element arcElement = createNormalArcNoWeight();
        Arc arc = creator.create(arcElement);
        verify(mockSource).addOutbound(arc);
        verify(mockTarget).addInbound(arc);
    }

    @Test
    public void createsCorrectMarkingIfWeightSpecified() {
        Element arcElement = createNormalArcWithWeight();
        Arc arc = creator.create(arcElement);

        List<Marking> markings = arc.getWeight();
        assertEquals(1, markings.size());

        Marking marking = markings.get(0);
        assertNotNull(marking);
        assertEquals("4", marking.getCurrentMarking());
        assertNull(marking.getToken());
    }

    @Test
    public void createsMarkingWithCorrectToken() {
        Map<String, Token> tokens = new HashMap<String, Token>();
        Token token = new Token("Default", true, 10, new Color(1,0,0));
        tokens.put(token.getId(), token);
        creator.setTokens(tokens);

        Element arcElement = createNormalArcWithWeight();
        Arc arc = creator.create(arcElement);

        List<Marking> markings = arc.getWeight();
        assertEquals(1, markings.size());

        Marking marking = markings.get(0);
        assertNotNull(marking);
        assertEquals(token, marking.getToken());
    }

    @Test
    public void createsInhibitoryArc()
    {
        Element arcElement = createInhibitorArc();
        Arc arc = creator.create(arcElement);

        assertNotNull(arc);
        assertEquals(InhibitorArc.class, arc.getClass());
    }

    @Test
    public void createsArcWithDefaultTokenIfNoTokenSpecifiedForWeight() {
        Element arcElement = createArcWeightNoToken();
        Map<String, Token> tokens = new HashMap<String, Token>();
        Token token = new Token("Default", true, 10, new Color(1,0,0));
        tokens.put(token.getId(), token);
        creator.setTokens(tokens);
        Arc arc = creator.create(arcElement);

        assertNotNull(arc);
        List<Marking> weights = arc.getWeight();
        Marking weight = weights.get(0);
        assertEquals(token, weight.getToken());
    }
}
