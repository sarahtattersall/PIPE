package pipe.models.component;

import pipe.models.visitor.PetriNetComponentVisitor;

import java.awt.geom.Point2D;

@Pnml("arcpath")
public final class ArcPoint extends AbstractPetriNetComponent {

    @Pnml("xCoord")
    private double x;

    @Pnml("yCoord")
    private double y;

    /**
     * If curved is true it implies this point is a bezier curve
     */
    @Pnml("arcPointType")
    private boolean curved;

    public ArcPoint(Point2D point, boolean curved) {
        setPoint(point);
        this.curved = curved;
    }

    public Point2D getPoint() {
        return new Point2D.Double(x, y);
    }

    public void setPoint(final Point2D point) {
        this.x = point.getX();
        this.y = point.getY();
        notifyObservers();
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
