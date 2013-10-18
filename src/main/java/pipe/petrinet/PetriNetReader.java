package pipe.petrinet;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pipe.gui.Grid;
import pipe.models.Marking;
import pipe.models.PetriNet;
import pipe.models.Place;
import pipe.models.Token;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PetriNetReader {

    /**
     * Map of token names to tokens. Used to find tokens for a place.
     */
    private Map<String, Token> tokens = new HashMap<String, Token>();

    private Map<String, Method> elementMethods = new HashMap<String, Method>();

    /**
     * Uses Reflection to add any methods with the ElementParser annotation
     * to the elementMethods token.
     * ElementParser.value() must equal the XML element label.
     */
    public PetriNetReader()
    {
        for (Method method : PetriNetReader.class.getMethods())
        {
            if (method.isAnnotationPresent(ElementParser.class))
            {
                ElementParser parser = method.getAnnotation(ElementParser.class);
                elementMethods.put(parser.value(), method);
            }
        }
    }

    public PetriNet createFromFile(Document document)
    {
        PetriNet net = new PetriNet();
        Element rootElement = document.getDocumentElement();
        NodeList nodes = rootElement.getChildNodes();

        for (int i = 0; i < nodes.getLength(); i++)
        {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
            {
                String nodeName = node.getNodeName();
                if (elementMethods.containsKey(nodeName)) {
                    Method method = elementMethods.get(nodeName);
                    try {
                        method.invoke(this, (Element) node, net);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return net;
    }



    @ElementParser("place")
    private void createPlaceAndAddToNet(Element element, PetriNet net)
    {
        PlaceCreator creator = new PlaceCreator(tokens);
        Place place = creator.createPlace(element);
        net.addPlace(place);
    }


}
