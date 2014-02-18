package pipe.models.component.arc;

import pipe.models.component.AbstractPetriNetComponent;
import pipe.visitor.component.PetriNetComponentVisitor;

import java.awt.geom.Point2D;

public class ArcPoint extends AbstractPetriNetComponent {

    /**
     * Message fired when the curved attribute changes
     */
    public static final String UPDATE_CURVED_CHANGE_MESSAGE = "updateCurved";

    /**
     * Message fired when the location attribute changes
     */
    public static final String UPDATE_LOCATION_CHANGE_MESSAGE = "updateLocation";

    private double x;

    private double y;

    /**
     * If curved is true it implies this point is a bezier curve
     */
    private boolean curved;

    public ArcPoint(Point2D point, boolean curved) {
        setPoint(point);
        this.curved = curved;
    }

    /**
     * Copy constructor
     * @param arcPoint to copy
     */
    public ArcPoint(ArcPoint arcPoint) {
        this.x = arcPoint.x;
        this.y = arcPoint.y;
        this.curved = arcPoint.curved;
    }

    public Point2D getPoint() {
        return new Point2D.Double(x, y);
    }

    public void setPoint(Point2D point) {
        Point2D old = new Point2D.Double(this.x, this.y);
        this.x = point.getX();
        this.y = point.getY();
        changeSupport.firePropertyChange(UPDATE_LOCATION_CHANGE_MESSAGE, old, point);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public boolean isDraggable() {
        return false;
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
