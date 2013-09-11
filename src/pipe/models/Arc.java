package pipe.models;

import pipe.models.interfaces.IObserver;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Vector;

public abstract class Arc implements Serializable
{
	private String _type;

    //LinkedList<Marking> _weight = new LinkedList<Marking>();
    private Connectable _source = null;
    private Connectable _target = null;
    
    private IObserver _observer;

	private LinkedList<Marking> _weight;


    public Arc(String type)
    {
        _type = type;
    }

//    public Arc(Connectable source, Connectable target, LinkedList<Marking> weight)
//    {
//        _source = source;
//        _target = target;
//        _weight=weight;
//    }
    public Arc(Connectable source, Connectable target)
    {
        _source = source;
        _target = target;
    }

    public void registerObserver(IObserver observer)
    {
        _observer = observer;
    }

//    public LinkedList<Marking> getWeight()
//    {
//        return _weight;
//    }

    public Connectable getSource()
    {
        return _source;
    }

    public void setSource(Connectable source)
    {
        _source = source;
    }

    public Connectable getTarget()
    {
        return _target;
    }

    public void setTarget(Connectable target)
    {
        _target = target;
    }
    
   
}
