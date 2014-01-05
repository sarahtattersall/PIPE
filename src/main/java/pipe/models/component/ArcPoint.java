package pipe.models.component;

import pipe.models.visitor.PetriNetComponentVisitor;

import java.awt.geom.Point2D;

public class ArcPoint extends AbstractPetriNetComponent {
    Point2D point;
    /**
     * If curved is true it implies this point is a bezier curve
     */
    boolean curved;

    public ArcPoint(Point2D point, boolean curved) {
        this.point = point;
        this.curved = curved;
    }

    @Override
    public int hashCode() {
        int result = point.hashCode();
        result = 31 * result + (curved ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final ArcPoint arcPoint = (ArcPoint) o;

        if (curved != arcPoint.curved) return false;
        if (!point.equals(arcPoint.point)) return false;

        return true;
    }

    public Point2D getPoint() {
        return point;
    }

    public void setPoint(final Point2D point) {
        this.point = point;
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
        this.curved = curved;
        notifyObservers();
    }
}
