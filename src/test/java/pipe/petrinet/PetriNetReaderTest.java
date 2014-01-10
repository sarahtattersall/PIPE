package pipe.petrinet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pipe.common.dataLayer.StateGroup;
import pipe.models.*;
import pipe.models.component.*;
import pipe.models.strategy.arc.ArcStrategy;
import pipe.petrinet.reader.*;
import pipe.petrinet.reader.creator.*;
import pipe.utilities.transformers.PNMLTransformer;
import pipe.views.viewComponents.RateParameter;
import utils.TokenUtils;

import java.awt.*;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

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

    PNMLTransformer transformer;
    PetriNet net;

    @Before
    public void setUp() {
        net = new PetriNet();
        transformer = new PNMLTransformer();
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
        Map<Token, String> weights = new HashMap<Token, String>();
        ArcStrategy mockStrategy = mock(ArcStrategy.class);
        arc = new Arc<Place, Transition>(place, transition, weights, mockStrategy);
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
        net = reader.createFromFile(net, doc);

        Collection<Place> places = net.getPlaces();
        assertEquals(2, places.size());
        assertTrue(places.contains(place));
        assertTrue(places.contains(otherPlace));
    }

    @Test
    public void setsTokensForPlaceCreator()
    {
        reader.createFromFile(net, doc);

        Map<String, Token> tokens = new HashMap<String, Token>();
        tokens.put(token.getId(), token);
        verify(creators.placeCreator, atLeastOnce()).setTokens(argThat(new MatchesThisMap<Token>(tokens)));
    }

    @Test
    public void createsDefaultTokenIfNoneSpecified()
    {
        Document noTokenDoc = transformer.transformPNML("src/test/resources/xml/noTokenPlace.xml");
        net = reader.createFromFile(net, noTokenDoc);
        assertEquals(1, net.getTokens().size());
        assertNotNull(net.getToken("Default"));
    }

    @Test
    public void createsDefaultTokenIfNoneSpecifiedAndAddsToPetrinet()
    {
        Document noTokenDoc = transformer.transformPNML("src/test/resources/xml/noTokenPlace.xml");
        reader.createFromFile(net, noTokenDoc);

        Token defaultToken = TokenUtils.createDefaultToken();
        verify(creators.placeCreator, atLeastOnce()).setTokens(argThat(new ContainsToken(defaultToken)));
    }

    @Test
    public void createsTransition()
    {
        net = reader.createFromFile(net, doc);

        Collection<Transition> transitions = net.getTransitions();
        assertEquals(1, transitions.size());
        assertTrue(transitions.contains(transition));
    }

    @Test
    public void setsRateParametersForTransition()
    {
        reader.createFromFile(net, doc);

        Map<String, RateParameter> params = new HashMap<String, RateParameter>();
        params.put(parameter.getId(), parameter);

        verify(creators.transitionCreator).setRates(argThat(new MatchesThisMap<RateParameter>(params)));
    }

    @Test
    public void createsArc()
    {
        net = reader.createFromFile(net, doc);

        Collection<Arc> arcs = net.getArcs();
        assertEquals(1, arcs.size());
        assertTrue(arcs.contains(arc));
    }

    @Test
    public void setsTokensForArcCreator()
    {
        reader.createFromFile(net, doc);

        Map<String, Token> tokens = new HashMap<String, Token>();
        tokens.put(token.getId(), token);
        verify(creators.arcCreator, atLeastOnce()).setTokens(argThat(new MatchesThisMap<Token>(tokens)));
    }

    @Test
    public void setsConnectablesForArcCreator()
    {
        reader.createFromFile(net, doc);

        Map<String, Place> places = new HashMap<String, Place>();
        places.put(place.getId(), place);
        places.put(otherPlace.getId(), otherPlace);

        Map<String, Transition> transitions = new HashMap<String, Transition>();
        transitions.put(transition.getId(), transition);
        //TODO FIX
//        verify(creators.arcCreator, atLeastOnce()).setPlaces(argThat(new MatchesThisMap<Place>(places)));
        verify(creators.arcCreator, atLeastOnce()).setTransitions(argThat(new MatchesThisMap<Transition>(transitions)));
    }

    @Test
    public void createsAnnotation()
    {
        net = reader.createFromFile(net, doc);
        Collection<Annotation> annotations = net.getAnnotations();
        assertEquals(1, annotations.size());
        assertTrue(annotations.contains(annotation));
    }

    @Test
    public void createsRateParameter()
    {
        net = reader.createFromFile(net, doc);
        Collection<RateParameter> rates = net.getRateParameters();
        assertEquals(1, rates.size());
        assertTrue(rates.contains(parameter));
    }

    @Test
    public void createsToken()
    {

        net = reader.createFromFile(net, doc);
        Collection<Token> tokens = net.getTokens();
        assertEquals(1, tokens.size());
        assertTrue(tokens.contains(token));

    }

    @Test
    public void createsStateGroup()
    {
        net = reader.createFromFile(net, doc);
        Collection<StateGroup> groups = net.getStateGroups();
        assertEquals(1, groups.size());
        assertTrue(groups.contains(group));

    }

    private static class ContainsToken extends ArgumentMatcher<Map<String, Token>> {

        private final Token token;

        public ContainsToken(Token token)
        {
            this.token = token;
        }

        @Override
        public boolean matches(Object argument) {
            Map<String, Token> mapArgument = (Map<String, Token>) argument;
            return mapArgument.containsKey(token.getId()) &&
                    mapArgument.get(token.getId()).equals(token);
        }
    }

    private static class MatchesThisMap<V> extends ArgumentMatcher<Map<String, V>> {
        private final Map<String, V> map;

        public MatchesThisMap(Map<String, V> map)
        {
            this.map = map;
        }

        /**
         * Loops over key value pairs in arugment and ensures they are the same in
         * map.
         * Then return true if each have the same number of elements. If not argument
         * is a subset of map
         * @param argument
         * @return
         */
        @Override
        public boolean matches(Object argument) {
            Map<String, V> mapArgument = (Map<String, V>) argument;
            for (Map.Entry<String, V> entry : mapArgument.entrySet())
            {
                if (!map.get(entry.getKey()).equals(entry.getValue()))
                {
                    return false;
                }
            }
            return mapArgument.size() == map.size();
        }
    }

}
