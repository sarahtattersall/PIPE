package pipe.petrinet.io;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import pipe.models.PetriNet;
import pipe.models.component.*;
import utils.FileUtils;

import javax.xml.bind.JAXBException;
import java.awt.*;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.*;

public class PetriNetReaderTest {
    /**
     * Range in which to declare doubles equal
     */
    private static final double DOUBLE_DELTA = 0.001;

    PetriNetReader reader;
    private static final Token DEFAULT_TOKEN = new Token("Default", true, 0, new Color(0, 0, 0));

    @Before
    public void setUp() throws JAXBException {
        reader = new PetriNetIOImpl();
    }

    @Test
    public void createsPlace() {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(getSinglePlacePath()));
        Place place = petriNet.getPlaces().iterator().next();
        assertNotNull(place);

        assertEquals("P0", place.getName());
        assertEquals(225, place.getX(), DOUBLE_DELTA);
        assertEquals(240, place.getY(), DOUBLE_DELTA);

        assertEquals(0, place.getMarkingXOffset(), DOUBLE_DELTA);
        assertEquals(0, place.getMarkingYOffset(), DOUBLE_DELTA);
        assertEquals(-5, place.getNameXOffset(), DOUBLE_DELTA);
        assertEquals(35, place.getNameYOffset(), DOUBLE_DELTA);
        assertEquals(0, place.getCapacity(), DOUBLE_DELTA);
        assertEquals(1, place.getTokenCounts().size());
    }

    private String getSinglePlacePath() {
        return "/xml/place/singlePlace.xml";
    }

    @Test
    public void createsMarkingCorrectlyWithTokenMap() {

        PetriNet petriNet = reader.read(FileUtils.fileLocation(getSinglePlacePath()));
        Place place = petriNet.getPlaces().iterator().next();
        Map<Token, Integer> counts = place.getTokenCounts();

        assertTrue(counts.containsKey(DEFAULT_TOKEN));
        Integer count = counts.get(DEFAULT_TOKEN);
        assertEquals(1, count.intValue());
    }

    @Test
    public void createsMarkingIfNoTokensSet() {

        PetriNet petriNet = reader.read(FileUtils.fileLocation(getNoPlaceTokenPath()));
        Place place = petriNet.getPlaces().iterator().next();
        Map<Token, Integer> counts = place.getTokenCounts();
        assertTrue(counts.isEmpty());
    }

    private String getNoPlaceTokenPath() {
        return "/xml/place/noTokenPlace.xml";
    }

    @Test
    public void createsTransition() {

        PetriNet petriNet = reader.read(FileUtils.fileLocation(getTransitionFile()));
        Transition transition = petriNet.getTransitions().iterator().next();
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
    }

    @Test
    public void createsArc() {

        PetriNet petriNet = reader.read(FileUtils.fileLocation(getArcNoWeightFile()));

        assertTrue(petriNet.getArcs().size() > 0);

        Arc<? extends Connectable, ? extends Connectable> arc = petriNet.getArcs().iterator().next();

        assertNotNull(arc);
        assertEquals(ArcType.NORMAL, arc.getType());

        Place expectedSource =   new Place("P0", "P0");
        Transition expectedTarget = new Transition("T0", "T0");

        assertEquals(expectedSource, arc.getSource());
        assertEquals(expectedTarget, arc.getTarget());
        assertEquals("P0 TO T0", arc.getId());
    }

    @Test
    public void createsCorrectMarkingIfWeightSpecified() {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(getNormalArcWithWeight()));
        Arc<? extends Connectable, ? extends Connectable> arc = petriNet.getArcs().iterator().next();

        Map<Token, String> weights = arc.getTokenWeights();
        Assert.assertEquals(1, weights.size());

        assertTrue(weights.containsKey(DEFAULT_TOKEN));
        String weight = weights.get(DEFAULT_TOKEN);
        Assert.assertEquals("4", weight);
    }

    @Test
    public void createsMarkingWithCorrectToken() {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(getNormalArcWithWeight()));
        Arc<? extends Connectable, ? extends Connectable> arc = petriNet.getArcs().iterator().next();
        Map<Token, String> weights = arc.getTokenWeights();
        Assert.assertEquals(1, weights.size());
        assertTrue(weights.containsKey(DEFAULT_TOKEN));
    }

    @Test
    public void createsInhibitoryArc() {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(getInhibitorArcFile()));
        Arc<? extends Connectable, ? extends Connectable> arc = petriNet.getArcs().iterator().next();

        assertNotNull(arc);
        Assert.assertEquals(ArcType.INHIBITOR, arc.getType());
    }

    @Test
    public void createsRedToken() {
        PetriNet petriNet = reader.read(FileUtils.fileLocation(getTokenFile()));
        Token token = petriNet.getTokens().iterator().next();
        assertEquals("red", token.getId());
        assertTrue(token.isEnabled());
        Assert.assertEquals(new Color(255,0,0), token.getColor());
    }

    private  String getTokenFile() {
        return "/xml/token/token.xml";
    }

    private String getArcNoWeightFile() {
        return "/xml/arc/arcNoWeight.xml";
    }

    private String getArcWeightNoTokenFile() {
        return "/xml/arc/arcWeightNoToken.xml";
    }

    private String getArcWithSourceAndTargetFile() {
        return "/xml/arc/arcWithSourceAndTarget.xml";
    }


    private String getInhibitorArcFile() {
        return "/xml/arc/inhibitorArc.xml";
    }

    private String getNormalArcWithWeight() {
        return "/xml/arc/normalArcWithWeight.xml";
    }


    private String getTransitionFile() {
        return "/xml/transition/singleTransition.xml";
    }
}
