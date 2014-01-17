package pipe.models.component.arc;

import pipe.models.component.AbstractPetriNetComponent;
import pipe.visitor.foo.PetriNetComponentVisitor;

import java.awt.geom.Point2D;

public class ArcPoint extends AbstractPetriNetComponent {

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

    public void setPoint(final Point2D point) {
        Point2D old = new Point2D.Double(this.x, this.y);
        this.x = point.getX();
        this.y = point.getY();
        changeSupport.firePropertyChange("updateLocation", old, point);
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
    public void accept(final PetriNetComponentVisitor visitor) {
        if (visitor instanceof ArcPointVisitor) {
            ((ArcPointVisitor) visitor).visit(this);
        }
    }

    @Override
    public String getId() {
        return null;
    }

    @Override
    public void setId(final String id) {

    }

    @Override
    public void setName(final String name) {

    }

    public boolean isCurved() {
        return curved;
    }

    public void setCurved(final boolean curved) {
        boolean old = this.curved;
        this.curved = curved;
        changeSupport.firePropertyChange("updateCurved", old, curved);
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
