package pipe.models;

import java.io.Serializable;
import java.util.LinkedList;

public class InhibitorArc extends Arc implements Serializable
{
    public InhibitorArc()
    {
        super("inhibitor");
    }

    public InhibitorArc(Connectable source, Connectable target)
    {
        super(source, target);
    }
//    public InhibitorArc(Connectable source, Connectable target, LinkedList<Marking> weight)
//    {
//        super(source, target, weight);
//    }

}

