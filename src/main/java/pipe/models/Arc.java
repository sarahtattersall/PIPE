package pipe.models;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public abstract class Arc extends Observable implements PetriNetComponent, Serializable
{
    private Connectable source;
    private Connectable target;
    private String id;

    private Point2D.Double targetLocation = new Point2D.Double();

    private boolean tagged = false;

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

    public Point2D.Double getTargetLocation() {
        return targetLocation;
    }

    public void setTargetLocation(Point2D.Double targetLocation) {
        this.targetLocation = targetLocation;
        notifyObservers();
    }

    public void setTarget(Connectable target)
    {
        this.target = target;
        setTargetLocation(new Point2D.Double(target.getX(), target.getY()));
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

    public boolean isTagged() {
        return tagged;
    }

    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
