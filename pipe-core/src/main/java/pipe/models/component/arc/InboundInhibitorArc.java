package pipe.models.component.arc;

import pipe.animation.State;
import pipe.animation.TokenCount;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.util.Collection;
import java.util.HashMap;

/**
 * An inhibitor arc maps from places to tokens and is allowed to fire
 * if and only if its source place has no tokens whatsoever.
 */
public class InboundInhibitorArc extends InboundArc {
    public InboundInhibitorArc(Place source, Transition target) {
        super(source, target, new HashMap<String, String>(), ArcType.INHIBITOR);
    }

    /**
     *
     * Analyses the state to see if the arcs source has no tokens
     * @param petriNet
     * @param state
     * @return true if the arc can fire
     */
    @Override
    public boolean canFire(PetriNet petriNet, State state) {
        Collection<TokenCount> tokens = state.getTokens(getSource().getId());
        for (TokenCount tokenCount : tokens) {
            if (tokenCount.count != 0) {
                return false;
            }
        }

        return true;
    }
}
