package pipe.models.component.arc;

import pipe.models.component.AbstractPetriNetComponent;
import pipe.models.component.PlaceablePetriNetComponent;
import pipe.visitor.component.PetriNetComponentVisitor;

import java.awt.geom.Point2D;

/**
 * Represents a point on the arc
 */
public class ArcPoint extends PlaceablePetriNetComponent {

    /**
     * Message fired when the curved attribute changes
     */
    public static final String UPDATE_CURVED_CHANGE_MESSAGE = "updateCurved";

    /**
     * Message fired when the location attribute changes
     */
    public static final String UPDATE_LOCATION_CHANGE_MESSAGE = "updateLocation";

    private int x;

    private int y;

    /**
     * If curved is true it implies this point is a bezier curve
     */
    private boolean curved;

    /**
     * Denotes if the arcpoint is draggable in GUI mode
     */
    private final boolean draggable;

    /**
     * Constructor, sets draggable to true by default
     * @param point
     * @param curved
     */
    public ArcPoint(Point2D point, boolean curved) {
        this(point, curved, true);
    }

    /**
     * Constructor that allows you to choose whether the point is draggable
     * @param point
     * @param curved
     * @param draggable
     */
    public ArcPoint(Point2D point, boolean curved, boolean draggable) {
        setPoint(point);
        this.curved = curved;
        this.draggable = draggable;
    }

    /**
     * Copy constructor
     * @param arcPoint to copy
     */
    public ArcPoint(ArcPoint arcPoint) {
        this.x = arcPoint.x;
        this.y = arcPoint.y;
        this.curved = arcPoint.curved;
        this.draggable = arcPoint.draggable;
    }

    public Point2D getPoint() {
        return new Point2D.Double(x, y);
    }

    public void setPoint(Point2D point) {
        Point2D old = new Point2D.Double(this.x, this.y);
        this.x = (int) point.getX();
        this.y = (int) point.getY();
        changeSupport.firePropertyChange(UPDATE_LOCATION_CHANGE_MESSAGE, old, point);
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getHeight() {
        return 0;
    }

    @Override
    public int getWidth() {
        return 0;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public boolean isDraggable() {
        return draggable;
    }

    @Override
    public void accept(PetriNetComponentVisitor visitor) {
        if (visitor instanceof ArcPointVisitor) {
            ((ArcPointVisitor) visitor).visit(this);
        }
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(String id) {

    }

    @Override
    public void setName(String name) {

    }

    public boolean isCurved() {
        return curved;
    }

    public void setCurved(boolean curved) {
        boolean old = this.curved;
        this.curved = curved;
        changeSupport.firePropertyChange(UPDATE_CURVED_CHANGE_MESSAGE, old, curved);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (curved ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ArcPoint arcPoint = (ArcPoint) o;

        if (curved != arcPoint.curved) {
            return false;
        }
        if (Double.compare(arcPoint.x, x) != 0) {
            return false;
        }
        if (Double.compare(arcPoint.y, y) != 0) {
            return false;
        }

        return true;
    }
}
