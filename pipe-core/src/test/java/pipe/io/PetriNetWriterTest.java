package pipe.io;

import org.custommonkey.xmlunit.XMLTestCase;
import org.xml.sax.SAXException;
import pipe.dsl.*;
import pipe.exceptions.InvalidRateException;
import pipe.exceptions.PetriNetComponentException;
import pipe.models.PetriNetHolder;
import pipe.models.component.annotation.Annotation;
import pipe.models.component.place.Place;
import pipe.models.component.rate.NormalRate;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import utils.FileUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.awt.Color;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.Charset;

public class PetriNetWriterTest extends XMLTestCase {
    PetriNetWriter writer;

    @Override
    public void setUp() throws JAXBException {
        writer = new PetriNetIOImpl();
    }

    public void testUsesMoxyProvider() throws JAXBException {
        assertEquals("class org.eclipse.persistence.jaxb.JAXBContext",
                (JAXBContext.newInstance(PetriNetHolder.class).getClass()).toString());
        assertEquals("class org.eclipse.persistence.jaxb.JAXBContext",
                (JAXBContext.newInstance(PetriNet.class).getClass()).toString());
    }

    public void testMarshalsPlace() throws IOException, SAXException {
        PetriNet petriNet = new PetriNet();
        Token token = new Token("Red", new Color(255, 0, 0));
        Place place = new Place("P0", "P0");
        place.setX(255);
        place.setY(240);
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
//        System.out.println("ACTUAL:");  // sjd -- less clutter on System.out
//        System.out.println(actual);
//        System.out.println("EXPECTED:");
//        System.out.println(expected);
        assertXMLEqual(expected, actual);
    }

    public void testMarshalsTransition() throws IOException, SAXException {
        PetriNet petriNet = new PetriNet();
        Transition transition = new Transition("T0", "T0");
        transition.setX(375);
        transition.setY(225);
        transition.setNameXOffset(-5.0);
        transition.setNameYOffset(35.0);
        transition.setRate(new NormalRate("1.0"));
        transition.setTimed(false);
        transition.setInfiniteServer(false);
        transition.setPriority(1);
        petriNet.addTransition(transition);
        assertResultsEqual(FileUtils.fileLocation(XMLUtils.getTransitionFile()), petriNet);
    }

    public void testMarshalsTransitionWithRateParameter() throws IOException, SAXException, InvalidRateException {
        PetriNet petriNet = new PetriNet();
        RateParameter rateParameter = new RateParameter("6.0", "foo", "foo");

        Transition transition = new Transition("T0", "T0");
        transition.setX(435);
        transition.setY(180);
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
        PetriNet petriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(
                APlace.withId("P0").locatedAt(0, 0)).and(ATransition.withId("T0").locatedAt(0, 0)).andFinally(
                ANormalArc.withSource("P0").andTarget("T0").and("4", "Default").tokens());


        assertResultsEqual(FileUtils.fileLocation(XMLUtils.getNormalArcWithWeight()), petriNet);
    }


    public void testMarshalsToken() throws IOException, SAXException, PetriNetComponentException {
        PetriNet petriNet = new PetriNet();
        Token token = new Token("red", new Color(255, 0, 0));
        petriNet.add(token);
        assertResultsEqual(FileUtils.fileLocation(XMLUtils.getTokenFile()), petriNet);
    }

    public void testMarshalsAnnotation() throws IOException, SAXException {
        PetriNet petriNet = new PetriNet();
        Annotation annotation = new Annotation(93, 145, "#P12s", 48, 20, false);
        petriNet.addAnnotation(annotation);
        assertResultsEqual(FileUtils.fileLocation(XMLUtils.getAnnotationFile()), petriNet);
    }
}


