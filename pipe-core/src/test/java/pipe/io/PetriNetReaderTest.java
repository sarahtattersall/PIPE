package pipe.io;

import org.junit.Before;
import org.junit.Test;
import pipe.models.component.Connectable;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.parsers.UnparsableException;
import utils.FileUtils;

import javax.xml.bind.JAXBException;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.assertj.core.api.Assertions.*;

public class PetriNetReaderTest {

    private static final Token DEFAULT_TOKEN = new Token("Default", new Color(0, 0, 0));

    PetriNetReader reader;

    @Before
    public void setUp() throws JAXBException {
        reader = new PetriNetIOImpl();
    }

    @Test
    public void createsDefaultTokenIfDoesNotExist() throws UnparsableException {
        PetriNet petriNet = reader.read(FileUtils.fileLocation("/xml/noTokenPlace.xml"));
        assertTrue("Petri net has no tokens registered to it", petriNet.getTokens().size() > 0);
        Token expectedToken = new Token("Default", new Color(0, 0, 0));
        assertThat(petriNet.getTokens()).contains(expectedToken);
    }

    @Test
    public void createsDefaultTokenIfDoesNotExistAndPlaceMatchesThisToken() throws UnparsableException {
        PetriNet petriNet = reader.read(FileUtils.fileLocation("/xml/noTokenPlace.xml"));
        assertThat(petriNet.getTokens()).isNotEmpty();
        assertThat(petriNet.getPlaces()).isNotEmpty();

        Token expectedToken = new Token("Default", new Color(0, 0, 0));

        Place place = petriNet.getPlaces().iterator().next();
        assertThat(place.getTokenCounts()).containsKey(expectedToken);
    }

    @Test
    public void losesSourceAndTargetArcPath() throws UnparsableException {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(XMLUtils.getNormalArcWithWeight()));
        assertThat(petriNet.getArcs()).isNotEmpty();
        assertThat(petriNet.getArcs()).hasSize(1);
    }

    @Test
    public void keepsIntermediatePoints() throws UnparsableException {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(XMLUtils.getNormalArcWithWeight()));
        ArcPoint expected = new ArcPoint(new Point2D.Double(87, 36), true);
        Arc<? extends Connectable, ? extends Connectable> arc = petriNet.getArcs().iterator().next();
        assertThat(arc.getArcPoints().contains(expected));
    }

    @Test
    public void createsPlace() throws UnparsableException {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(XMLUtils.getSinglePlacePath()));

        assertThat(petriNet.getPlaces()).hasSize(1);
        assertThat(extractProperty("name").from(petriNet.getPlaces())).containsExactly("P0");
        assertThat(petriNet.getPlaces()).extracting("x", "y").containsExactly(tuple(255, 240));
        assertThat(petriNet.getPlaces()).extracting("markingXOffset", "markingYOffset").containsExactly(
                tuple(0.0, 0.0));
        assertThat(petriNet.getPlaces()).extracting("nameXOffset", "nameYOffset").containsExactly(tuple(5.0, 26.0));
        assertThat(extractProperty("capacity").from(petriNet.getPlaces())).containsExactly(0);
        assertThat(extractProperty("name").from(petriNet.getPlaces())).containsExactly("P0");
        assertThat(extractProperty("tokenCounts").from(petriNet.getPlaces())).hasSize(1);
    }

    @Test
    public void createsMarkingCorrectlyWithTokenMap() throws UnparsableException {

        PetriNet petriNet = reader.read(FileUtils.fileLocation(XMLUtils.getSinglePlacePath()));
        assertThat(petriNet.getPlaces()).hasSize(1);
        Place place = petriNet.getPlaces().iterator().next();

        Map<Token, Integer> counts = place.getTokenCounts();
        assertThat(counts).containsEntry(DEFAULT_TOKEN, 1);
    }

    @Test
    public void createsMarkingIfNoTokensSet() throws UnparsableException {

        PetriNet petriNet = reader.read(FileUtils.fileLocation(getNoPlaceTokenPath()));
        assertThat(petriNet.getPlaces()).isNotEmpty();
        Place place = petriNet.getPlaces().iterator().next();
        assertThat(place.getTokenCounts()).isEmpty();
    }

    private String getNoPlaceTokenPath() {
        return "/xml/place/noTokenPlace.xml";
    }

    @Test
    public void createsTransition() throws UnparsableException {

        PetriNet petriNet = reader.read(FileUtils.fileLocation(XMLUtils.getTransitionFile()));

        assertThat(petriNet.getTransitions()).extracting("x", "y").containsExactly(tuple(375, 225));
        assertThat(petriNet.getTransitions()).extracting("id", "name").containsExactly(tuple("T0", "T0"));
        assertThat(petriNet.getTransitions()).extracting("rate.expression").containsExactly("1.0");
        assertThat(petriNet.getTransitions()).extractingResultOf("isTimed").containsExactly(false);
        assertThat(petriNet.getTransitions()).extractingResultOf("isInfiniteServer").containsExactly(false);
        assertThat(petriNet.getTransitions()).extracting("nameXOffset", "nameYOffset").containsExactly(
                tuple(-5.0, 35.0));
        assertThat(petriNet.getTransitions()).extracting("priority").containsExactly(1);
    }

    @Test
    public void createsArc() throws UnparsableException {

        PetriNet petriNet = reader.read(FileUtils.fileLocation(XMLUtils.getArcNoWeightFile()));
        Place expectedSource = new Place("P0", "P0");
        Transition expectedTarget = new Transition("T0", "T0");
        assertThat(petriNet.getArcs()).extracting("type", "source", "target", "id").contains(
                tuple(ArcType.NORMAL, expectedSource, expectedTarget, "P0 TO T0"));
    }

    @Test
    public void createsCorrectMarkingIfWeightSpecified() throws UnparsableException {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(XMLUtils.getNormalArcWithWeight()));
        Arc<? extends Connectable, ? extends Connectable> arc = petriNet.getArcs().iterator().next();

        Map<Token, String> weights = arc.getTokenWeights();
        assertEquals(1, weights.size());

        assertTrue(weights.containsKey(DEFAULT_TOKEN));
        String weight = weights.get(DEFAULT_TOKEN);
        assertEquals("4", weight);
    }

    @Test
    public void createsMarkingWithCorrectToken() throws UnparsableException {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(XMLUtils.getNormalArcWithWeight()));
        Arc<? extends Connectable, ? extends Connectable> arc = petriNet.getArcs().iterator().next();
        Map<Token, String> weights = arc.getTokenWeights();
        assertEquals(1, weights.size());
        assertTrue(weights.containsKey(DEFAULT_TOKEN));
    }

    @Test
    public void createsInhibitoryArc() throws UnparsableException {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(XMLUtils.getInhibitorArcFile()));
        assertThat(petriNet.getArcs()).extracting("type").containsExactly(ArcType.INHIBITOR);
    }

    @Test
    public void createsRedToken() throws UnparsableException {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(XMLUtils.getTokenFile()));
        Token redToken = new Token("red", new Color(255, 0, 0));
        assertThat(petriNet.getTokens()).containsExactly(redToken);
    }

    @Test
    public void createsAnnotation() throws UnparsableException {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(XMLUtils.getAnnotationFile()));
        assertThat(petriNet.getAnnotations()).extracting("text", "x", "y", "height", "width").containsExactly(
                tuple("#P12s", 93, 145, 20, 48));
    }

    @Test
    public void createsRateParameter() throws UnparsableException {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(XMLUtils.getRateParameterFile()));
        assertThat(petriNet.getRateParameters()).extracting("id", "name", "expression").containsExactly(
                tuple("rate0", "rate0", "5.0"));
    }

    @Test
    public void transitionReferencesRateParameter() throws UnparsableException {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(XMLUtils.getTransitionRateParameterFile()));
        RateParameter rateParameter = petriNet.getRateParameters().iterator().next();
        Transition transition = petriNet.getTransitions().iterator().next();
        assertEquals(rateParameter, transition.getRate());
    }


}
