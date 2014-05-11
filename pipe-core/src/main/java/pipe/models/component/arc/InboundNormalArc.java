package pipe.models.component.arc;

import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.parsers.FunctionalResults;
import uk.ac.imperial.state.State;

import java.util.Map;

public class InboundNormalArc extends InboundArc {
    public InboundNormalArc(Place source, Transition target, Map<String, String> tokenWeights) {
        super(source, target, tokenWeights, ArcType.NORMAL);
    }

    @Override
    public boolean canFire(PetriNet petriNet, State state) {
        Place place = getSource();

        Map<String, Integer> tokenCounts = state.getTokens(place.getId());

        Map<String, String> tokenWeights = getTokenWeights();
        for (Map.Entry<String, String> entry : tokenWeights.entrySet()) {
            String tokenId = entry.getKey();
            FunctionalResults<Double> results = petriNet.parseExpression(entry.getValue());
            if (results.hasErrors()) {
                //TODO:
                throw new RuntimeException("Errors evaluating arc weight against Petri net. Needs handling in code");
            }

            double tokenWeight = results.getResult();

            int currentCount = tokenCounts.get(tokenId);
            if (currentCount < tokenWeight && currentCount != -1) {
                return false;
            }
        }
        return true;

        //
        //        for (Map.Entry<String, String> entry : getTokenWeights().entrySet()) {
        //            int tokenCount = tokenCounts.get(entry.getKey().getId());
        //            FunctionalResults<Double> results = petriNet.parseExpression(entry.getValue());
        //            if (results.hasErrors()) {
        //                //TODO:
        //                throw new RuntimeException("Errors evaluating arc weight against Petri net. Needs handling in code");
        //            }
        //
        //            double tokenWeight = results.getResult();
        //
        //            if (tokenCount < tokenWeight && tokenCount != -1) {
        //                return false;
        //            }
        //        }
        //        return !tokenCounts.isEmpty();
    }
}
