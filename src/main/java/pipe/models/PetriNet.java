package pipe.models;

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
    //private Set<Marking> markings = new HashSet<Marking>();
    private Set<Arc> arcs = new HashSet<Arc>();

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

}
