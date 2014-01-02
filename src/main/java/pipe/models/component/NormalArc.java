package pipe.models.component;

import pipe.models.visitor.PetriNetComponentVisitor;

import java.io.Serializable;
import java.util.Map;

@Pnml("arc")
public class NormalArc<S extends Connectable, T extends Connectable> extends Arc<S, T>  implements Serializable
{

    public NormalArc(S source, T target, Map<Token, String> weight)
    {
        super(source, target, weight);
    }

    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    public void accept(PetriNetComponentVisitor visitor) {
        visitor.visit(this);
    }
//    public NormalArc(Connectable source, Connectable target)
//    {
//        super(source, target);
//    }
}
