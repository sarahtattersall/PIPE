package pipe.models;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public abstract class Arc extends Observable implements PetriNetComponent, Serializable
{

    @Pnml("source")
    private Connectable source;

    @Pnml("target")
    private Connectable target;

    @Pnml("id")
    private String id;

    private boolean tagged = false;

    //TODO: Does this need to be a List?
//	private List<Marking> weight;
    /**
     * Map of Token to corresponding weights
     * Weights can be functional e.g '> 5'
     */
    @Pnml("inscription")
    private Map<Token, String> tokenWeights = new HashMap<Token, String>();

    public Arc(Connectable source, Connectable target, Map<Token, String> tokenWeights)
    {
        this.source = source;
        this.target = target;
        this.tokenWeights = tokenWeights;
    }

    public Map<Token, String> getTokenWeights()
    {
        return tokenWeights;
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
