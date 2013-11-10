package pipe.models;

import pipe.views.ArcView;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;

/*
 * 
 *  @author yufei wang(modification)
 */
public abstract class Connectable extends Observable implements Serializable, PetriNetComponent {
    private final Collection<Arc> inboundArcs = new HashSet<Arc>();
    private final Collection<Arc> outboundArcs = new HashSet<Arc>();

    /**
     * Connectable position x
     */
    double x = 0;

    /**
     * Connectable position y
     */
    double y = 0;

    /**
     * Connectable id
     */
    private String _id;

    /**
     * Connectable name
     */
    private String _name;

    /**
     * Connectable name x offset relative to its x coordinate
     */
    protected double nameXOffset = -5;

    /**
     * Connectable name y offset relative to its y coordinate
     */
    protected double nameYOffset = 35;

    protected Connectable(String id, String name) {
        _id = id;
        _name = name;
    }

    public double getNameXOffset() {
        return nameXOffset;
    }

    public double getNameYOffset() {
        return nameYOffset;
    }

    public void setNameXOffset(double nameXOffset) {
        this.nameXOffset = nameXOffset;
        notifyObservers();
    }

    public void setNameYOffset(double nameYOffset) {
        this.nameYOffset = nameYOffset;
        notifyObservers();
    }

    public Collection<Arc> outboundArcs() {
        return outboundArcs;
    }

    public Collection<Arc> inboundArcs() {
        return inboundArcs;
    }

    public void addInbound(Arc arc) {
        inboundArcs.add(arc);
    }

    public void addOutbound(Arc arc) {
        outboundArcs.add(arc);
    }

//    public void addInboundOrOutbound(ArcView newArcView) {
//        if (newArcView.getSource().getModel() == this) {
//            outboundArcs.add(newArcView);
//        } else {
//            inboundArcs.add(newArcView);
//        }
//    }

    public void removeOutboundArc(Arc arc) {
        outboundArcs.remove(arc);
    }

    public void removeInboundArc(Arc arc) {
        inboundArcs.remove(arc);
    }

    public String getName() {
        return _name;
    }

    public String getId() {
        return _id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        notifyObservers();
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        notifyObservers();
    }

    public void setCentre(double x, double y) {
        setX(x - (getWidth() / 2.0));
        setY(y - (getHeight() / 2.0));
    }

    public abstract int getHeight();

    public abstract int getWidth();

    public abstract double getCentreX();

    public abstract double getCentreY();

    /**
     * @return coords for an arc to connect to
     *         <p/>
     *         x, y are the top left corner so A
     *         would return (4, 1) and B would
     *         return (14, 1)
     *         <p/>
     *         +---+         +---+
     *         | A |-------->| B |
     *         +---+         +---+
     */
    public abstract Point2D.Double getArcEdgePoint(double angle);

    /**
     * @return true if the arc can finish at this point.
     *         I.e it is not a temporary connectable
     */
    public abstract boolean isEndPoint();

}
