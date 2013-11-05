package pipe.models;

import pipe.models.visitor.PetriNetComponentVisitor;
import pipe.views.ArcView;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.LinkedList;

/*
 * 
 *  @author yufei wang(modification)
 */
public abstract class Connectable extends Observable implements Serializable, PetriNetComponent
{
    private final LinkedList<ArcView> _inboundArcViews =  new LinkedList<ArcView>();
    private final LinkedList<ArcView> _outboundArcViews = new LinkedList<ArcView>();

    /**
     * Place position x
     */
    double x = 0;
    /**
     * Place position y
     */
    double y = 0;
    private String _id;
    private String _name;

    Connectable(String id, String name)
    {
        _id = id;
        _name = name;
    }

    public LinkedList<ArcView> outboundArcs()
    {
        return _outboundArcViews;
    }

    public LinkedList<ArcView> inboundArcs()
    {
        return _inboundArcViews;
    }

    public void addInbound(ArcView newArcView)
    {
        _inboundArcViews.add(newArcView);
    }

    public void addOutbound(ArcView newArcView)
    {
        _outboundArcViews.add(newArcView);
    }

    public void addInboundOrOutbound(ArcView newArcView)
    {
        if(newArcView.getSource()._model == this)
            _outboundArcViews.add(newArcView);
        else
            _inboundArcViews.add(newArcView);
    }

    public void removeFromArcs(ArcView oldArcView)
    {
        _outboundArcViews.remove(oldArcView);
    }

    public void removeToArc(ArcView oldArcView)
    {
        _inboundArcViews.remove(oldArcView);
    }
    
    public String getName(){
    	return _name;
    }
    
    public String getId(){
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

    public void setCentre(double x, double y)
    {
        setX(x - (getWidth() / 2.0));
        setY(y - (getHeight() / 2.0));
    }

    public abstract int getHeight();
    public abstract int getWidth();
    public abstract double getCentreX();
    public abstract double getCentreY();

    /**
     *
     * @return coords for an arc to connect to
     *
     * x, y are the top left corner so A
     * would return (4, 1) and B would
     * return (14, 1)
     *
     * +---+         +---+
     * | A |-------->| B |
     * +---+         +---+
     *
     */
    public abstract Point2D.Double getArcEdgePoint(double angle);

    /**
     * @return true if the arc can finish at this point.
     * I.e it is not a temporary connectable
     */
    public abstract boolean isEndPoint();

}
