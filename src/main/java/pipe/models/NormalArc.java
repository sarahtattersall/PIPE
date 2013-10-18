package pipe.models;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class NormalArc extends Arc  implements Serializable
{

    public NormalArc(Connectable source, Connectable target, List<Marking> weight)
    {
        super(source, target, weight);
    }
//    public NormalArc(Connectable source, Connectable target)
//    {
//        super(source, target);
//    }
}
