package pipe.models;

import java.awt.geom.Point2D;
import java.io.Serializable;

public class ConditionalPlace extends Connectable implements Serializable
{
    private static int DIAMETER = 30;

    public ConditionalPlace(String id, String name)
    {
        super(id, name);
    }

    @Override
    public int getHeight() {
        return DIAMETER;
    }

    @Override
    public int getWidth() {
        return DIAMETER;
    }

    @Override
    public double getCentreX() {
        return getX() - getWidth()/2;
    }

    @Override
    public double getCentreY() {
        return getX() - getWidth()/2;
    }

    @Override
    public Point2D.Double getArcEdgePoint(double angle) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean isEndPoint() {
        return true;
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public boolean isDraggable() {
        return true;
    }
}
