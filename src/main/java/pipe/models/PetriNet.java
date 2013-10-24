package pipe.models;

import pipe.common.dataLayer.StateGroup;
import pipe.views.viewComponents.RateParameter;

import java.io.Serializable;
import java.util.*;

public class PetriNet extends Observable implements Serializable
{
    public String _pnmlName;
    private boolean _validated = false;
    private ArrayList _changeArrayList;

    private Set<Transition> transitions = new HashSet<Transition>();
    private Set<Place> places = new HashSet<Place>();
    private Set<Token> tokens = new HashSet<Token>();
    private Set<Arc> arcs = new HashSet<Arc>();
    private Set<Annotation> annotations = new HashSet<Annotation>();
    private Set<RateParameter> rates = new HashSet<RateParameter>();
    private Set<StateGroup> stateGroups = new HashSet<StateGroup>();

    public String getPnmlName()
    {
        return _pnmlName;
    }

    public void setPnmlName(String pnmlName)
    {
        _pnmlName = pnmlName;
    }

    public boolean isValidated()
    {
        return _validated;
    }

    public void setValidated(boolean validated)
    {
        _validated = validated;
    }

    public void resetPNML()
    {
        _pnmlName = null;
    }

    public void addPlace(Place place)
    {
        places.add(place);
    }

    public void addTransition(Transition transition)
    {
        transitions.add(transition);
    }

    public void addArc(Arc arc)
    {
        arcs.add(arc);
    }

    public void addToken(Token token)
    {
        tokens.add(token);
    }

    public Collection<Place> getPlaces()
    {
        return places;
    }

    public void addRate(RateParameter parameter)
    {
        rates.add(parameter);
    }

    public Collection<RateParameter> getRateParameters()
    {
        return rates;
    }

    public void addAnnotaiton(Annotation annotation)
    {
        annotations.add(annotation);
    }

    public Collection<Annotation> getAnnotations()
    {
        return annotations;
    }

    public void addStateGroup(StateGroup group) {
        stateGroups.add(group);
    }

    public Collection<StateGroup> getStateGroups() {
        return stateGroups;
    }

    public Collection<Transition> getTransitions() {
        return transitions;
    }

    public Collection<Arc> getArcs() {
        return arcs;
    }

    public Collection<Token> getTokens() {
        return tokens;
    }
}
