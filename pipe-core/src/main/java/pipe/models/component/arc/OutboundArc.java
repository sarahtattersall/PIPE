package pipe.models.component.arc;

import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.visitor.component.PetriNetComponentVisitor;

import java.util.Map;

/**
 * An arc that goes from transitions to places
 */
public abstract class OutboundArc extends Arc<Transition, Place> {
    public OutboundArc(Transition source, Place target, Map<Token, String> tokenWeights, ArcType type) {
        super(source, target, tokenWeights, type);
    }

    @Override
    public void accept(PetriNetComponentVisitor visitor) {
        if (visitor instanceof ArcVisitor) {
            ((ArcVisitor) visitor).visit(this);
        }
    }
}
