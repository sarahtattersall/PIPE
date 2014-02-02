package pipe.io;

import org.custommonkey.xmlunit.XMLTestCase;
import org.xml.sax.SAXException;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.rate.NormalRate;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import utils.FileUtils;

import javax.xml.bind.JAXBException;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class PetriNetWriterTest extends XMLTestCase {
    PetriNetWriter writer;

    @Override
    public void setUp() throws JAXBException {
        writer = new PetriNetIOImpl();
    }

    public void testMarshalsPlace() throws IOException, SAXException {
        PetriNet petriNet = new PetriNet();
        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        Place place = new Place("P0", "P0");
        place.setX(255.0);
        place.setY(240.0);
        place.setNameXOffset(5);
        place.setNameYOffset(26);
        place.setTokenCount(token, 1);
        petriNet.addToken(token);
        petriNet.addPlace(place);

        assertResultsEqual(FileUtils.fileLocation(XMLUtils.getSinglePlacePath()), petriNet);
    }

    private void assertResultsEqual(String expectedPath, PetriNet petriNet) throws IOException, SAXException {
        StringWriter stringWriter = new StringWriter();
        writer.writeTo(stringWriter, petriNet);

        String expected = XMLUtils.readFile(expectedPath, Charset.defaultCharset());

        String actual = stringWriter.toString();
        System.out.println(actual);
        assertXMLEqual(expected, actual);
    }

    public void testMarshalsTransition() throws IOException, SAXException {
        PetriNet petriNet = new PetriNet();
        Transition transition = new Transition("T0", "T0");
        transition.setX(375.0);
        transition.setY(225.0);
        transition.setNameXOffset(-5.0);
        transition.setNameYOffset(35.0);
        transition.setRate(new NormalRate("1.0"));
        transition.setTimed(false);
        transition.setInfiniteServer(false);
        transition.setPriority(1);
        petriNet.addTransition(transition);
        assertResultsEqual(FileUtils.fileLocation(XMLUtils.getTransitionFile()), petriNet);
    }

    public void testMarshalsTransitionWithRateParameter() throws IOException, SAXException {
        PetriNet petriNet = new PetriNet();
        RateParameter rateParameter = new RateParameter("6.0", "foo", "foo");

        Transition transition = new Transition("T0", "T0");
        transition.setX(435);
        transition.setY(180.0);
        transition.setNameXOffset(-5.0);
        transition.setNameYOffset(35.0);
        transition.setRate(rateParameter);
        transition.setTimed(true);
        transition.setInfiniteServer(false);
        transition.setPriority(1);

        petriNet.addTransition(transition);
        petriNet.addRateParameter(rateParameter);

        assertResultsEqual(FileUtils.fileLocation(XMLUtils.getTransitionRateParameterFile()), petriNet);
    }

    public void testMarshalsArc() throws IOException, SAXException {
        PetriNet petriNet = new PetriNet();

        Token token = new Token("Default", true, 0, new Color(0, 0, 0));
        Place place = new Place("P0", "P0");
        Transition transition = new Transition("T0", "T0");

        Map<Token, String> weights = new HashMap<>();
        weights.put(token, "4");
        Arc<Place, Transition> arc = new Arc<>(place, transition, weights, ArcType.NORMAL);
        arc.addIntermediatePoint(new ArcPoint(new Point2D.Double(87, 36), true));

        petriNet.add(token);
        petriNet.add(place);
        petriNet.addTransition(transition);
        petriNet.add(arc);


        assertResultsEqual(FileUtils.fileLocation(XMLUtils.getNormalArcWithWeight()), petriNet);
    }


    public void testMarshalsToken() throws IOException, SAXException {
        PetriNet petriNet = new PetriNet();
        Token token = new Token("red", true, 0, new Color(255, 0, 0));
        petriNet.add(token);
        assertResultsEqual(FileUtils.fileLocation(XMLUtils.getTokenFile()), petriNet);
    }

    public void testMarshalsAnnotation() throws IOException, SAXException {
        PetriNet petriNet = new PetriNet();
        Annotation annotation = new Annotation(93, 145, "#P12s", 48, 20, false);
        petriNet.addAnnotaiton(annotation);
        assertResultsEqual(FileUtils.fileLocation(XMLUtils.getAnnotationFile()), petriNet);
    }
}


