package pipe.models;

import java.awt.geom.Point2D;

/**
 * This is a temporary class, if an arc does not yet have a target
 * this class can be used in place. It therefore is not a permenant end point
 */
public class TemporaryArcTarget extends Connectable {

    public TemporaryArcTarget(double x, double y) {
        super("temp", "temp");
        super.setX(x);
        super.setY(y);
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
    public double getCentreX() {
        return 0;
    }

    @Override
    public double getCentreY() {
        return 0;
    }

    @Override
    public Point2D.Double getArcEdgePoint(double angle) {
        return new Point2D.Double(x, y);
    }

    @Override
    public boolean isEndPoint() {
        return false;
    }
}
