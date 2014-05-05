package pipe.models.component.arc;

import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.util.HashMap;
import java.util.Map;

/**
 * An inhibitor arc maps from places to tokens and is allowed to fire
 * if and only if its source place has no tokens whatsoever.
 */
public class InboundInhibitorArc extends InboundArc {
    public InboundInhibitorArc(Place source, Transition target) {
        super(source, target, new HashMap<Token, String>(), ArcType.INHIBITOR);
    }

    /**
     *
     * Analyses the state to see if the arcs source has no tokens
     * @param petriNet
     * @param state
     * @return true if the arc can fire
     */
    @Override
    public boolean canFire(PetriNet petriNet, Map<String, Map<String, Integer>> state) {
        Map<String, Integer> tokens = state.get(getSource().getId());
        for (Map.Entry<String, Integer> entry : tokens.entrySet()) {
            if (entry.getValue() != 0) {
                return false;
            }
        }

        return true;
    }
}
