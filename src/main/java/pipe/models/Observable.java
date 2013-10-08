package pipe.models;

import pipe.models.interfaces.IObservable;
import pipe.models.interfaces.IObserver;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Observable implements IObservable, Serializable
{
    private final ArrayList<IObserver> _observers;

    Observable()
    {
        _observers = new ArrayList<IObserver>();
    }

    public void registerObserver(IObserver observer)
    {
        if(!_observers.contains(observer))
            _observers.add(observer);
    }

    public void removeObserver(IObserver observer)
    {
        _observers.remove(observer);
    }

    public void notifyObservers()
    {
        for(IObserver observer : _observers)
            observer.update();
    }

    public ArrayList<IObserver> observers()
    {
        return _observers;
    }
}
