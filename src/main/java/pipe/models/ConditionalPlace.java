package pipe.models;

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
}
