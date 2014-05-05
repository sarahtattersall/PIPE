package pipe.models.component.arc;

import pipe.animation.State;
import pipe.models.component.Connectable;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.parsers.FunctionalResults;

import java.util.Map;

/**
 * This is a normal arc that is from transitions to places.
 *
 * It is allowed to fire if its target place will have enough capacity for the number of tokens
 * this arc will produce
 */
public class OutboundNormalArc extends OutboundArc {
    public OutboundNormalArc(Transition source, Place target, Map<Token, String> tokenWeights) {
        super(source, target, tokenWeights, ArcType.NORMAL);
    }

    /**
     *
     * @param petriNet
     * @param state
     * @return true if there is no capacity restriction on the target or firing will
     *         not cause capacity overflow
     */
    @Override
    public boolean canFire(PetriNet petriNet, State state) {
        Place place = getTarget();
        if (!place.hasCapacityRestriction()) {
            return true;
        }

        int totalTokensIn = getTokenCounts(petriNet, this);
        int totalTokensOut = getNumberOfTokensLeavingPlace(petriNet);
        int tokensInPlace = getTokensInPlace(state);

        return (tokensInPlace + totalTokensIn - totalTokensOut <= place.getCapacity());
    }

    /**
     *
     * Calculates the number of tokens leaving the target due
     * to an arc loop
     *
     *
     * @return the number of tokens that leave the  place
     *         via this transition.
     *
     */
    private int getNumberOfTokensLeavingPlace(PetriNet petriNet) {
        Place place = getTarget();
        int count = 0;
        for (InboundArc arc : petriNet.outboundArcs(place)) {
            if (arc.getSource().equals(getTarget())  && arc.getTarget().equals(getSource())) {
                count += getTokenCounts(petriNet, arc);
            }
        }
        return count;
    }

    private int getTokenCounts(PetriNet petriNet, Arc<? extends Connectable, ? extends Connectable> arc) {
        int count = 0;
        for (Map.Entry<Token, String> entry : arc.tokenWeights.entrySet()) {
            FunctionalResults<Double> result =  petriNet.parseExpression(entry.getValue());
            if (result.hasErrors()) {
                throw new RuntimeException("Cannot parse outbound arc weight");
            }
            double weight = result.getResult();
            count += weight;
        }
        return count;
    }

    private int getTokensInPlace(State state) {
        Place place = getTarget();
        int count = 0;
        for (Integer value : state.getTokens(place.getId()).values()) {
            count += value;
        }
        return count;
    }
}
