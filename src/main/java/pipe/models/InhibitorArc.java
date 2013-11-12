package pipe.models;

import pipe.models.visitor.PetriNetComponentVisitor;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class InhibitorArc extends Arc implements Serializable
{
    public InhibitorArc(Connectable source, Connectable target, Map<Token, String> weight)
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

