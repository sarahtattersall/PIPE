package pipe.models;

import java.io.Serializable;

public class Place extends Connectable implements Serializable
{
    public Place(String id, String name)
    {
        super(id, name);
    }
}
