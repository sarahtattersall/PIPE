package pipe.models;

import java.io.Serializable;

public class Place extends Connectable implements PetriNetComponent, Serializable
{
    public Place(String id, String name)
    {
        super(id, name);
    }

    /**
     *
     * @return true - Place objects are always selectable
     */
    @Override
    public boolean isSelectable() {
        return true;
    }
}
