package pipe.models.component;

import pipe.models.visitor.PetriNetComponentVisitor;

import java.io.Serializable;
import java.util.Map;

@Pnml("arc")
public class InhibitorArc<T extends Connectable> extends Arc<Place, T> implements Serializable
{
    public InhibitorArc(Place source, T target, Map<Token, String> weight)
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
}

