package pipe.models.component;

import pipe.visitor.PetriNetComponentVisitor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import java.awt.geom.Point2D;

@Pnml("arcpath")
public class ArcPoint extends AbstractPetriNetComponent {

    @Pnml("xCoord")
    private double x;

    @Pnml("yCoord")
    private double y;

    /**
     * If curved is true it implies this point is a bezier curve
     */
    @Pnml("arcPointType")
    @XmlAttribute(name="curvePoint")
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
//        notifyObservers();
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
//        notifyObservers();
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
}
