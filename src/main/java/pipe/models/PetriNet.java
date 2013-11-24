package pipe.models;

import pipe.common.dataLayer.StateGroup;
import pipe.models.component.*;
import pipe.models.interfaces.IObserver;
import pipe.models.visitor.PetriNetComponentAddVisitor;
import pipe.models.visitor.PetriNetComponentRemovalVisitor;
import pipe.models.visitor.PetriNetComponentVisitor;
import pipe.views.viewComponents.RateParameter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PetriNet extends Observable implements IObserver
{
    public String _pnmlName = "";
    private boolean _validated = false;
    private ArrayList _changeArrayList;

    private Set<Transition> transitions = new HashSet<Transition>();
    private Set<Place> places = new HashSet<Place>();
    private Set<Token> tokens = new HashSet<Token>();
    private Set<Arc> arcs = new HashSet<Arc>();
    private Set<Annotation> annotations = new HashSet<Annotation>();
    private Set<RateParameter> rates = new HashSet<RateParameter>();
    private Set<StateGroup> stateGroups = new HashSet<StateGroup>();

    //TODO: CYCLIC DEPENDENCY BETWEEN CREATING THIS AND PETRINET/
    private final PetriNetComponentVisitor deleteVisitor = new PetriNetComponentRemovalVisitor(this);
    private PetriNetComponentVisitor addVisitor = new PetriNetComponentAddVisitor(this);

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
        place.registerObserver(this);
        notifyObservers();
    }

    public void addTransition(Transition transition)
    {
        transitions.add(transition);
        transition.registerObserver(this);
        notifyObservers();
    }

    public void addArc(Arc arc)
    {
        arcs.add(arc);
        arc.registerObserver(this);
        notifyObservers();
    }

    public void addToken(Token token)
    {
        tokens.add(token);
        token.registerObserver(this);
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
        annotation.registerObserver(this);
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
        component.accept(deleteVisitor);
        notifyObservers();
    }

    public void removeToken(Token token) {
        tokens.remove(token);
    }

    public void removeRateParameter(RateParameter parameter) {
        rates.remove(parameter);
    }

    public void removeStateGroup(StateGroup group) {
        stateGroups.remove(group);
    }

    public void removeAnnotaiton(Annotation annotation) {
        annotations.remove(annotation);
    }

    public Token getToken(String tokenId) {
        for (Token token : tokens) {
            if (token.getId().equals(tokenId)) {
                return token;
            }
        }
        throw new RuntimeException("No token " + tokenId + " exists in petrinet.");
    }

    public void add(PetriNetComponent component) {
        component.accept(addVisitor);
        notifyObservers();
    }

    @Override
    public void update() {
        notifyObservers();
    }
}
