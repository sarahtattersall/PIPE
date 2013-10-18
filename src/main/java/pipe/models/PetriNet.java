package pipe.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class PetriNet extends Observable implements Serializable
{
    public String _pnmlName;
    private boolean _validated = false;
    private ArrayList _changeArrayList;

    private ArrayList<Transition> _transitions;
    private ArrayList<Place> _places;
    private ArrayList<Marking> _markings;
    private ArrayList<Arc> _arcs;

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

    public ArrayList<Transition> getTransitions()
    {
        return _transitions;
    }

    public void resetPNML()
    {
        _pnmlName = null;
    }
}
