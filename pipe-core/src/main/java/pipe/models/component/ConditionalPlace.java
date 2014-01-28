package pipe.models.component;

import pipe.visitor.foo.PetriNetComponentVisitor;

import java.awt.geom.Point2D;
import java.io.Serializable;

//TODO: FIGURE UT WHAT THIS DOES AND FIX GENERICS
public class ConditionalPlace extends Connectable  {
    private static final int DIAMETER = 30;

    public ConditionalPlace(String id, String name) {
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
    public Point2D.Double getCentre() {
        return new Point2D.Double(getX() - getWidth() / 2, getX() - getWidth() / 2);
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

    @Override
    public void accept(PetriNetComponentVisitor visitor) {
        //        visitor.visit(this);
    }
}
