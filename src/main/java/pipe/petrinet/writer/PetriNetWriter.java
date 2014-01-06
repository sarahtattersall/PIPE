package pipe.petrinet.writer;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pipe.models.PetriNet;
import pipe.models.component.Arc;
import pipe.models.component.Place;
import pipe.models.component.Token;
import pipe.models.component.Transition;
import pipe.petrinet.writer.reflectionCreator.ElementCreator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.lang.reflect.InvocationTargetException;


public class PetriNetWriter {



    public void writeToFile(PetriNet net, String path) throws ParserConfigurationException, TransformerException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = builderFactory.newDocumentBuilder();
        Document document = builder.newDocument();

       writeToDocument(net, document);

        // Create Transformer with XSL Source File
        Source xsltSource = new StreamSource(Thread.currentThread().
                getContextClassLoader().getResourceAsStream("xslt" +
                System.getProperty("file.separator") + "GeneratePNML.xsl"));

        Transformer transformer = TransformerFactory.newInstance().newTransformer(xsltSource);
        // Write file and do XSLT transformation to generate correct PNML
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(path);
        transformer.transform(source, result);
    }

    public void writeToDocument(PetriNet net, Document document) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Element pnmlElement = createPNMLElement(document);
        Element netElement = createNetElement(document, pnmlElement);
        ElementCreator creator = new ElementCreator(document);
        for (Token token : net.getTokens())
        {
            Element element = creator.createElement(token);
            netElement.appendChild(element);
        }

        for (Place place : net.getPlaces()) {
            Element element = creator.createElement(place);
            netElement.appendChild(element);
        }

        for (Transition transition : net.getTransitions()) {
            Element element = creator.createElement(transition);
            netElement.appendChild(element);
        }

        for (Arc arc : net.getArcs()) {
            Element element = creator.createElement(arc);
            netElement.appendChild(element);

        }
        document.normalize();
    }

    private Element createNetElement(Document document, Element pnmlElement) {
        Element netElement = document.createElement("net"); // Net Element
        pnmlElement.appendChild(netElement);
        Attr netAttrId = document.createAttribute("id"); // Net "id" Attribute
        netAttrId.setValue("Net-One");
        netElement.setAttributeNode(netAttrId);
        Attr netAttrType = document.createAttribute("type"); // Net "type" Attribute
        netAttrType.setValue("P/T net");
        netElement.setAttributeNode(netAttrType);
        return netElement;
    }


    private Element createPNMLElement(Document document) {
        Element pnmlElement = document.createElement("pnml"); // PNML Top Level Element
        document.appendChild(pnmlElement);

        Attr pnmlAttr = document.createAttribute("xmlns"); // PNML "xmlns" Attribute
        pnmlAttr.setValue("http://www.informatik.hu-berlin.de/top/pnml/ptNetb");

        pnmlElement.setAttributeNode(pnmlAttr);

        return pnmlElement;
    }
}
