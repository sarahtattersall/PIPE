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
        notifyObservers();
    }

    public Connectable getTarget()
    {
        return target;
    }

    public void setTarget(Connectable target)
    {
        this.target = target;
        notifyObservers();
    }

    /**
     *
     * @return angle in randians between source and target
     */
    private double getAngleBetweenSourceAndTarget()
    {
        double deltax = source.getX() - target.getX();
        double deltay = source.getY() - target.getY();
        return Math.atan2(deltax, deltay);
    }

    /**
     *
     * @return The start coordinate of the arc
     */
    public Point2D.Double getStartPoint() {
        double angle = getAngleBetweenSourceAndTarget();
        return source.getArcEdgePoint(angle);
    }


    /**
     *
     * @return The end coordinate of the arc
     */
    public Point2D.Double getEndPoint() {
        double angle = getAngleBetweenSourceAndTarget();
        return target.getArcEdgePoint(Math.PI + angle);
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
        notifyObservers();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyObservers();
    }
}
