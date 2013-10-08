package pipe.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

public class PetriNet extends Observable implements Serializable
{
    private static Random _randomNumber = new Random();

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

    public ArrayList getChangeArrayList()
    {
        return _changeArrayList;
    }

    public void setChangeArrayList(ArrayList changeArrayList)
    {
        _changeArrayList = changeArrayList;
    }

    public static Random getRandomNumber()
    {
        return _randomNumber;
    }

    public static void setRandomNumber(Random randomNumber)
    {
        _randomNumber = randomNumber;
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
