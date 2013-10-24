package pipe.petrinet;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pipe.gui.Constants;
import pipe.utilities.transformers.PNMLTransformer;
import pipe.views.viewComponents.RateParameter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RateParameterCreatorTest {
    RateParameterCreator creator;
    Element rateParameterElement;
    /**
     * Range in which to declare doubles equal
     */
    private static final double DOUBLE_DELTA = 0.001;

    @Before
    public void setUp()
    {
        PNMLTransformer transformer = new PNMLTransformer();
        Document document = transformer.transformPNML("src/test/resources/xml/rateParameter/rateParameter.xml");
        Element rootElement = document.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();
        rateParameterElement = (Element) nodes.item(1);
    }

    @Test
    public void createsRateParameter() {
        creator = new RateParameterCreator();
        RateParameter rateParameter = creator.create(rateParameterElement);

        assertNotNull(rateParameter);

        assertEquals(400 - Constants.RESERVED_BORDER/2, rateParameter.getX(), DOUBLE_DELTA);
        assertEquals(219 - Constants.RESERVED_BORDER/2, rateParameter.getY(), DOUBLE_DELTA);
        assertEquals("rate0", rateParameter.getName());
        assertEquals(5.0, rateParameter.getValue(), DOUBLE_DELTA);
    }
}
