package pipe.petrinet;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pipe.models.Token;
import pipe.utilities.transformers.PNMLTransformer;

import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class TokenCreatorClass {
    TokenCreator creator;
    Element tokenElement;
    /**
     * Range in which to declare doubles equal
     */
    private static final double DOUBLE_DELTA = 0.001;

    @Before
    public void setUp()
    {
        PNMLTransformer transformer = new PNMLTransformer();
        Document document = transformer.transformPNML("src/test/resources/xml/token/token.xml");
        Element rootElement = document.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();
        tokenElement = (Element) nodes.item(1);
    }

    @Test
    public void createsToken() {
        creator = new TokenCreator();
        Token token = creator.create(tokenElement);

        assertNotNull(token);

        assertEquals("token0", token.getId());
        assertTrue(token.isEnabled());
        Color color = token.getColor();
        assertEquals(1, color.getRed());
        assertEquals(0, color.getGreen());
        assertEquals(9, color.getBlue());
    }
}
