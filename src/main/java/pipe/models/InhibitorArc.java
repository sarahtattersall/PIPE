package pipe.models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class InhibitorArc extends Arc implements Serializable
{
    public InhibitorArc(Connectable source, Connectable target, List<Marking> weight)
    {
        super(source, target, weight);
    }

}

