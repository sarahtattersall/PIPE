package pipe.models.component;

import pipe.models.visitor.connectable.ConnectableVisitor;

import java.awt.geom.Point2D;

/**
 * This class is used for PetriNetComponents that can be connected
 * to and Connected from by an {@link pipe.models.component.Arc}
 */
public abstract class Connectable extends AbstractPetriNetComponent {
    /**
     * Connectable position x
     */
    @Pnml("positionX")
    protected double x = 0;

    /**
     * Connectable position y
     */
    @Pnml("positionY")
    protected double y = 0;

    /**
     * Connectable id
     */
    @Pnml("id")
    protected String id;

    /**
     * Connectable name
     */
    @Pnml("name")
    protected String name;

    /**
     * Connectable name x offset relative to its x coordinate
     */
    @Pnml("nameOffsetX")
    protected double nameXOffset = -5;

    /**
     * Connectable name y offset relative to its y coordinate
     */
    @Pnml("nameOffsetY")
    protected double nameYOffset = 35;

    protected Connectable(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public double getNameXOffset() {
        return nameXOffset;
    }

    public double getNameYOffset() {
        return nameYOffset;
    }

    public void setNameXOffset(double nameXOffset) {
        double oldValue = this.nameXOffset;
        this.nameXOffset = nameXOffset;
        changeSupport.firePropertyChange("nameOffsetX", oldValue, nameXOffset);
    }

    public void setNameYOffset(double nameYOffset) {
        double oldValue = this.nameYOffset;
        this.nameYOffset = nameYOffset;
        changeSupport.firePropertyChange("nameOffsetY", oldValue, nameXOffset);
    }

//    public void addInboundOrOutbound(ArcView newArcView) {
//        if (newArcView.getSource().getModel() == this) {
//            outboundArcs.add(newArcView);
//        } else {
//            inboundArcs.add(newArcView);
//        }
//    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        double oldValue = this.x;
        this.x = x;
        changeSupport.firePropertyChange("x", oldValue, x);

    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        double oldValue = this.y;
        this.y = y;
        changeSupport.firePropertyChange("y", oldValue, y);
    }

    public abstract int getHeight();

    public abstract int getWidth();

    public abstract Point2D.Double getCentre();

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

    public void setId(String id) {
        String old = this.id;
        this.id = id;
        changeSupport.firePropertyChange("changeId", old, id);
    }

    public void setName(String name) {
        String old = this.name;
        this.name = name;
        changeSupport.firePropertyChange("changeName", old, name);
    }

    public abstract void accept(ConnectableVisitor visitor);

    @Override
    public String toString() {
        return id;
    }

}
