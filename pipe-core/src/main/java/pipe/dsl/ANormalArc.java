package pipe.dsl;

import pipe.models.component.Connectable;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
import pipe.models.component.token.Token;

import java.util.HashMap;
import java.util.Map;

public class ANormalArc implements DSLCreator<Arc<? extends Connectable,? extends Connectable>> {
    private String source;
    private String target;
    private Map <String, String> weights = new HashMap<>();


    private ANormalArc() {}


    public static ANormalArc withSource(String source) {
        ANormalArc aNormalArc = new ANormalArc();
        aNormalArc.source = source;
        return aNormalArc;
    }

    public ANormalArc andTarget (String target) {
        this.target = target;
        return this;
    }

    public ANormalArc withTokenWeight(String tokenName, String tokenWeight) {
        weights.put(tokenName, tokenWeight);
        return this;
    }



    @Override
    public Arc<? extends Connectable, ? extends Connectable> create(Map<String, Token> tokens, Map<String, Connectable> connectables) {
        Map<Token, String> arcWeights = new HashMap<>();
        for (Map.Entry<String, String> entry : weights.entrySet()) {
            arcWeights.put(tokens.get(entry.getKey()), entry.getValue());
        }

        return new Arc<>(connectables.get(source), connectables.get(target), arcWeights,
                ArcType.NORMAL);
    }

    /**
     * Weights
     * @param tokenWeights a list of token name and their value it should always be divisible by two and be
     *                a list of tokens and their weights e.g.
     *               .withTokenWeights("Default", 2, "Red", 1, "Yellow", 5);
     * @return ANormalArc instance for chaining
     */
    public ANormalArc withTokenWeights(String... tokenWeights) {
        for (int i = 0; i < tokenWeights.length; i += 2) {
            String tokenName = tokenWeights[i];
            String tokenWeight = tokenWeights[i+1];
            weights.put(tokenName, tokenWeight);
        }
        return this;
    }
}
