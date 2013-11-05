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

    /**
     * This class is used to be able to remove any generic PetriNetComponent
     * from the PetriNet
     */
    private final Map<Class<? extends PetriNetComponent>, Set<? extends PetriNetComponent>> allComponents = new HashMap<Class<? extends PetriNetComponent>, Set<? extends PetriNetComponent>>();


    public PetriNet()
    {
        allComponents.put(Place.class, places);
        allComponents.put(Transition.class, transitions);
        allComponents.put(Token.class, tokens);
        allComponents.put(Arc.class, arcs);
        allComponents.put(Annotation.class, annotations);
        //allComponents.put(RateParameter.class, annotations);
//        allComponents.put(StateGroup.class, stateGroups);
    }

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
        notifyObservers();
    }

    public void addTransition(Transition transition)
    {
        transitions.add(transition);
        notifyObservers();
    }

    public void addArc(Arc arc)
    {
        arcs.add(arc);
        notifyObservers();
    }

    public void addToken(Token token)
    {
        tokens.add(token);
        notifyObservers();
    }

    public Collection<Place> getPlaces()
    {
        return places;
    }

    public void addRate(RateParameter parameter)
    {
        rates.add(parameter);
        notifyObservers();
    }

    public Collection<RateParameter> getRateParameters()
    {
        return rates;
    }

    public void addAnnotaiton(Annotation annotation)
    {
        annotations.add(annotation);
        notifyObservers();
    }

    public Collection<Annotation> getAnnotations()
    {
        return annotations;
    }

    public void addStateGroup(StateGroup group) {
        stateGroups.add(group);
        notifyObservers();
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

    public void removePlace(Place place) {
        this.places.remove(place);
        notifyObservers();
    }

    public void removeTransition(Transition transition) {
        this.transitions.remove(transition);
        notifyObservers();
    }

    public void removeArc(Arc arc) {
        this.arcs.remove(arc);
        notifyObservers();
    }

    public void remove(PetriNetComponent component) {
        Set<? extends PetriNetComponent> matchingComponents = allComponents.get(component.getClass());
        //TODO: SHOULDNT HAVE TO TEST FOR THIS. SET STATEGROUP AND RATEPARAM TO BE PETRINET COMPONENTS
        if (matchingComponents != null) {
            matchingComponents.remove(component);
        }
    }
}
