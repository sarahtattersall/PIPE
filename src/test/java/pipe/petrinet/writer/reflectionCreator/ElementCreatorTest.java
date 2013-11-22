package pipe.petrinet.writer.reflectionCreator;


import org.junit.Test;
import org.w3c.dom.Document;
import pipe.models.Place;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created with IntelliJ IDEA.
 * User: st809
 * Date: 22/11/2013
 * Time: 16:37
 * To change this template use File | Settings | File Templates.
 */
public class ElementCreatorTest {

    @Test
    public void foo() throws ParserConfigurationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.newDocument();

        Place place = new Place("hello", "hello");
        ElementCreator creator = new ElementCreator();
        creator.createElement(place, document);
    }
}
