package pipe.petrinet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pipe.models.Transition;
import pipe.utilities.transformers.PNMLTransformer;
import pipe.views.viewComponents.RateParameter;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

public class TransitionCreatorTest {
    TransitionCreator creator;
    Element transitionElement;
    /**
     * Range in which to declare doubles equal
     */
    private static final double DOUBLE_DELTA = 0.001;

    @Before
    public void setUp()
    {
        PNMLTransformer transformer = new PNMLTransformer();
        Document document = transformer.transformPNML("src/test/resources/xml/transition/singleTransition.xml");
        Element rootElement = document.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();
        transitionElement = (Element) nodes.item(1);
    }

    @Test
    public void createsTransition() {
        creator = new TransitionCreator(new HashMap<String, RateParameter>());
        Transition transition = creator.createTransition(transitionElement);

        assertNotNull(transition);

        assertEquals(375, transition.getX(), DOUBLE_DELTA);
        assertEquals(225, transition.getY(), DOUBLE_DELTA);
        assertEquals("T0", transition.getName());
        assertEquals("T0", transition.getId());
        assertEquals(0, transition.getOrientation());
        assertEquals("1.0", transition.getRateExpr());
        assertFalse(transition.isTimed());
        assertFalse(transition.isInfiniteServer());
        assertEquals(1, transition.getPriority());
        assertEquals(-5, transition.getNameXOffset(), DOUBLE_DELTA);
        assertEquals(35, transition.getNameYOffset(), DOUBLE_DELTA);
        assertNull(transition.getRateParameter());
    }

    @Test
    public void createsTransitionWithAssociatedRateParameter() {
        RateParameter parameter = new RateParameter("rate", 10.0, 0, 0);
        Map<String, RateParameter> rates = new HashMap<String, RateParameter>();
        rates.put(parameter.getName(), parameter);

        creator = new TransitionCreator(rates);
        Transition transition = creator.createTransition(transitionElement);
        Assert.assertEquals(parameter, transition.getRateParameter());
    }
}
