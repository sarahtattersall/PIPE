package pipe.models;

import org.junit.Before;
import org.junit.Test;
import pipe.common.dataLayer.StateGroup;
import pipe.models.interfaces.IObserver;
import pipe.views.viewComponents.RateParameter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PetriNetTest {
    PetriNet net;
    IObserver mockObserver;

    @Before
    public void setUp()
    {
        net = new PetriNet();
        mockObserver = mock(IObserver.class);
    }

    @Test
    public void addingPlaceNotifiesObservers() {
        net.registerObserver(mockObserver);
        Place place = new Place("", "");
        net.addPlace(place);

        verify(mockObserver).update();
    }

    @Test
    public void removingPlaceNotifiesObservers() {
        net.registerObserver(mockObserver);
        Place place = new Place("", "");
        net.addPlace(place);
        net.removePlace(place);

        verify(mockObserver, times(2)).update();
    }

    @Test
    public void addingArcNotifiesObservers() {
        net.registerObserver(mockObserver);
        Arc mockArc = mock(Arc.class);
        net.addArc(mockArc);

        verify(mockObserver).update();
    }

    @Test
    public void removingArcNotifiesObservers() {
        net.registerObserver(mockObserver);
        Arc mockArc = mock(Arc.class);
        net.addArc(mockArc);
        net.removeArc(mockArc);

        verify(mockObserver, times(2)).update();
    }

    @Test
    public void addingTransitionNotifiesObservers() {
        net.registerObserver(mockObserver);
        Transition transition = new Transition("", "");
        net.addTransition(transition);
        verify(mockObserver).update();
    }


    @Test
    public void removingTransitionNotifiesObservers() {
        net.registerObserver(mockObserver);
        Transition transition = new Transition("", "");
        net.addTransition(transition);
        net.removeTransition(transition);
        verify(mockObserver, times(2)).update();
    }


    @Test
    public void addingAnnotationNotifiesObservers() {
        net.registerObserver(mockObserver);
        Annotation annotation = new Annotation(10, 10, "", 10, 10, false);
        net.addAnnotaiton(annotation);
        verify(mockObserver).update();
    }

    @Test
    public void addingRateParameterNotifiesObservers() {

        net.registerObserver(mockObserver);
        RateParameter rateParameter = new RateParameter("", 0., 0, 0);
        net.addRate(rateParameter);
        verify(mockObserver).update();
    }


    @Test
    public void addingTokenNotifiesObservers() {

        net.registerObserver(mockObserver);
        Token token = new Token();
        net.addToken(token);
        verify(mockObserver).update();
    }

    @Test
    public void addingStateGroupNotifiesObservers() {

        net.registerObserver(mockObserver);
        StateGroup group = new StateGroup();
        net.addStateGroup(group);
        verify(mockObserver).update();
    }

    @Test
    public void genericRemoveMethodRemovesPlace() {
        Place place = new Place("","");
        net.addPlace(place);

        assertEquals(1, net.getPlaces().size());
        net.remove(place);
        assertTrue(net.getPlaces().isEmpty());
    }

    @Test
    public void genericRemoveMethodRemovesArc() {
        Place place = new Place("source", "source");
        Transition transition = new Transition("target", "target");
        Map<Token, String> weights = new HashMap<Token, String>();
        NormalArc arc = new NormalArc(place, transition, weights);
        net.addArc(arc);

        assertEquals(1, net.getArcs().size());
        net.remove(arc);
        assertTrue(net.getArcs().isEmpty());
    }
}
