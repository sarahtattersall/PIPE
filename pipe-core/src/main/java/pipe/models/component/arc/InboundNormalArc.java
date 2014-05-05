package pipe.models.component.arc;

import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.parsers.FunctionalResults;

import java.util.Map;

public class InboundNormalArc extends InboundArc {
    public InboundNormalArc(Place source, Transition target, Map<Token, String> tokenWeights) {
        super(source, target, tokenWeights, ArcType.NORMAL);
    }

    @Override
    public boolean canFire(PetriNet petriNet, Map<String, Map<String, Integer>> state) {
        Place place = getSource();
        Map<String, Integer> tokenCounts = state.get(place.getId());

        for (Map.Entry<Token, String> entry : getTokenWeights().entrySet()) {
            int tokenCount = tokenCounts.get(entry.getKey().getId());
            FunctionalResults<Double> results = petriNet.parseExpression(entry.getValue());
            if (results.hasErrors()) {
                //TODO:
                throw new RuntimeException("Errors evaluating arc weight against Petri net. Needs handling in code");
            }

            double tokenWeight = results.getResult();

            if (tokenCount < tokenWeight && tokenCount != -1) {
                return false;
            }
        }
        return !place.getTokenCounts().isEmpty();
    }
}
