package pipe.models;

import java.io.Serializable;
import java.util.LinkedList;

public class NormalArc extends Arc  implements Serializable
{

    public NormalArc()
    {
        super("normal");
    }

//    public NormalArc(Connectable source, Connectable target, LinkedList<Marking> weight)
//    {
//        super(source, target, weight);
//    }
    public NormalArc(Connectable source, Connectable target)
    {
        super(source, target);
    }
}
