package pipe.petrinet;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pipe.common.dataLayer.StateGroup;
import pipe.models.*;
import pipe.utilities.transformers.PNMLTransformer;
import pipe.views.viewComponents.RateParameter;

import java.awt.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PetriNetReaderTest {

    Document doc;
    PetriNetReader reader;
    private CreatorStruct creators;

    Place place;
    Place otherPlace;
    Transition transition;
    Arc arc;
    Annotation annotation;
    RateParameter parameter;
    Token token;
    StateGroup group;


    /**
     * Delta in which to mark doubles equal to each other as.
     */
    private static final double DOUBLE_DELTA = 0.01;

    @Before
    public void setUp() {
        PNMLTransformer transformer = new PNMLTransformer();
        doc = transformer.transformPNML("src/test/resources/xml/petriNet.xml");
//        Document doc = mock(Document.class);

        PlaceCreator placeCreator = mock(PlaceCreator.class);
        TransitionCreator transitionCreator = mock(TransitionCreator.class);
        ArcCreator arcCreator = mock(ArcCreator.class);
        AnnotationCreator annotationCreator = mock(AnnotationCreator.class);
        RateParameterCreator rateParameterCreator = mock(RateParameterCreator.class);
        TokenCreator tokenCreator = mock(TokenCreator.class);
        StateGroupCreator stateGroupCreator = mock(StateGroupCreator.class);


        creators = new CreatorStruct(placeCreator, transitionCreator, arcCreator,
                                    annotationCreator, rateParameterCreator,
                                    tokenCreator, stateGroupCreator);


        reader = new PetriNetReader(creators);
        setupCreators();
    }

    private void setupCreators()
    {

        place = new Place("P0", "P0");
        otherPlace = new Place("P1", "P1");
        transition = new Transition("T0", "T0");
        List<Marking> markings = new LinkedList<Marking>();
        arc = new NormalArc(place, otherPlace, markings);
        annotation = new Annotation(10, 10, "hello", 10, 10, true);
        parameter = new RateParameter("id", 10.2, 10, 10);

        Color color = new Color(1,0,0);
        token =  new Token("id", true, 10, color);
        group = new StateGroup();

        when(creators.placeCreator.create(any(Element.class))).thenReturn(place)
                .thenReturn(otherPlace);

        when(creators.transitionCreator.create(any(Element.class))).thenReturn(transition);

        when(creators.arcCreator.create(any(Element.class))).thenReturn(arc);

        when(creators.annotationCreator.create(any(Element.class))).thenReturn(annotation);

        when(creators.rateParameterCreator.create(any(Element.class))).thenReturn(parameter);

        when(creators.tokenCreator.create(any(Element.class))).thenReturn(token);

        when(creators.stateGroupCreator.create(any(Element.class))).thenReturn(group);
    }

    @Test
    public void createsPlace()
    {
        PetriNet net = reader.createFromFile(doc);
        Collection<Place> places = net.getPlaces();
        assertEquals(2, places.size());
        assertTrue(places.contains(place));
        assertTrue(places.contains(otherPlace));
    }

    @Test
    public void createsTransition()
    {
        PetriNet net = reader.createFromFile(doc);
        Collection<Transition> transitions = net.getTransitions();
        assertEquals(1, transitions.size());
        assertTrue(transitions.contains(transition));
    }

    @Test
    public void createsArc()
    {
        PetriNet net = reader.createFromFile(doc);
        Collection<Arc> arcs = net.getArcs();
        assertEquals(1, arcs.size());
        assertTrue(arcs.contains(arc));
    }

    @Test
    public void createsAnnotation()
    {
        PetriNet net = reader.createFromFile(doc);
        Collection<Annotation> annotations = net.getAnnotations();
        assertEquals(1, annotations.size());
        assertTrue(annotations.contains(annotation));
    }

    @Test
    public void createsRateParameter()
    {
        PetriNet net = reader.createFromFile(doc);
        Collection<RateParameter> rates = net.getRateParameters();
        assertEquals(1, rates.size());
        assertTrue(rates.contains(parameter));
    }

    @Test
    public void createsToken()
    {

        PetriNet net = reader.createFromFile(doc);
        Collection<Token> tokens = net.getTokens();
        assertEquals(1, tokens.size());
        assertTrue(tokens.contains(token));

    }

    @Test
    public void createsStateGroup()
    {
        PetriNet net = reader.createFromFile(doc);
        Collection<StateGroup> groups = net.getStateGroups();
        assertEquals(1, groups.size());
        assertTrue(groups.contains(group));

    }

}
