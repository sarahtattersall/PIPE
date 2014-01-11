package pipe.petrinet;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pipe.models.component.Annotation;
import pipe.petrinet.reader.creator.AnnotationCreator;
import pipe.petrinet.transformer.PNMLTransformer;
import utils.FileUtils;


import static org.junit.Assert.*;

public class AnnotationCreatorTest {
    AnnotationCreator creator;
    Element annotationElement;
    /**
     * Range in which to declare doubles equal
     */
    private static final double DOUBLE_DELTA = 0.001;

    @Before
    public void setUp()
    {
        PNMLTransformer transformer = new PNMLTransformer();
        Document document = transformer.transformPNML(FileUtils.fileLocation("/xml/labels/label.xml"));
        Element rootElement = document.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();
        annotationElement = (Element) nodes.item(1);
    }

    @Test
    public void createsAnnotaiton() {
        creator = new AnnotationCreator();
        Annotation annotation = creator.create(annotationElement);

        assertNotNull(annotation);

        assertEquals("Hello World", annotation.getText());
        assertEquals(20, annotation.getX(), DOUBLE_DELTA);
        assertEquals(266, annotation.getY(), DOUBLE_DELTA);
        assertEquals(100, annotation.getWidth(), DOUBLE_DELTA);
        assertEquals(40, annotation.getHeight(), DOUBLE_DELTA);
        assertTrue(annotation.hasBoarder());
    }

}
