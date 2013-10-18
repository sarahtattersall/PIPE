package pipe.models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public abstract class Arc extends Observable implements PetriNetComponent, Serializable
{
    private Connectable source;
    private Connectable target;


    //TODO: Does this need to be a List?
	private List<Marking> weight;

    public Arc(Connectable source, Connectable target, List<Marking> weight)
    {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public List<Marking> getWeight()
    {
        return weight;
    }

    public Connectable getSource()
    {
        return source;
    }

    public void setSource(Connectable source)
    {
        this.source = source;
    }

    public Connectable getTarget()
    {
        return target;
    }

    public void setTarget(Connectable target)
    {
        this.target = target;
    }

    /**
     *
     * @return true - Arcs are always selectable
     */
    @Override
    public boolean isSelectable()
    {
        return true;
    }
}
