package pipe.io.adapters.modelAdapter;

import com.google.common.base.Joiner;
import pipe.models.component.*;
import pipe.models.strategy.arc.ArcStrategy;
import pipe.io.adapters.model.AdaptedArc;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArcAdapter extends XmlAdapter<AdaptedArc, Arc<? extends Connectable, ? extends Connectable>> {



    private final Map<String, Place> places;
    private final Map<String, Transition> transitions;
    private final Map<String, Token> tokens;

    private final ArcStrategy<Place, Transition> inhibitorStrategy;

    private final ArcStrategy<Transition, Place> normalForwardStrategy;

    private final ArcStrategy<Place, Transition> normalBackwardStrategy;

    /**
     * Empty contructor needed for marshelling. Since the method to marshell does not actually
     * use these fields it's ok to initialise them as empty/null.
     */
    public ArcAdapter() {
        places = new HashMap<String, Place>();
        transitions = new HashMap<String, Transition>();
        tokens = new HashMap<String, Token>();
        inhibitorStrategy = null;
        normalBackwardStrategy = null;
        normalForwardStrategy = null;
    }

    public ArcAdapter(Map<String, Place> places, Map<String, Transition> transitions, Map<String, Token> tokens, ArcStrategy<Place, Transition> inhibitorStrategy,
                      ArcStrategy<Transition, Place> normalForwardStrategy,
                      ArcStrategy<Place, Transition> normalBackwardStrategy) {
        this.places = places;
        this.transitions = transitions;
        this.tokens = tokens;
        this.inhibitorStrategy = inhibitorStrategy;
        this.normalForwardStrategy = normalForwardStrategy;
        this.normalBackwardStrategy = normalBackwardStrategy;
    }


    @Override
    public Arc<? extends Connectable, ? extends Connectable> unmarshal(AdaptedArc adaptedArc) throws Exception {
        Arc<? extends Connectable, ? extends Connectable> arc;
        String source = adaptedArc.getSource();
        String target = adaptedArc.getTarget();

        Map<Token, String> weights = stringToWeights(adaptedArc.getInscription().getTokenCounts());
        if (adaptedArc.getType().equals("inhibitor")) {
            Place place = places.get(source);
            Transition transition = transitions.get(target);
            arc = new Arc<Place, Transition>(place, transition, weights, inhibitorStrategy);
        } else {
            if (places.containsKey(source)) {
                Place place = places.get(source);
                Transition transition = transitions.get(target);
                arc = new Arc<Place, Transition>(place, transition,weights, normalBackwardStrategy);
            } else {
                Place place = places.get(target);
                Transition transition = transitions.get(source);
                arc = new Arc<Transition, Place>(transition, place, weights, normalForwardStrategy);
            }
        }
        arc.setId(adaptedArc.getId());
        //TODO:
        arc.setTagged(false);

        setRealArcPoints(arc, adaptedArc);
        return arc;
    }

    @Override
    public AdaptedArc marshal(Arc<? extends Connectable, ? extends Connectable> arc) throws Exception {
        AdaptedArc adapted = new AdaptedArc();
        setArcPoints(arc, adapted);
        adapted.setId(arc.getId());
        adapted.setSource(arc.getSource().getId());
        adapted.setTarget(arc.getTarget().getId());
        adapted.getInscription().setTokenCounts(weightToString(arc.getTokenWeights()));
        adapted.setType(arc.getType().name().toLowerCase());
        return adapted;
    }

    /**
     * Sets the arc points in adapted based on the arc.
     * Needs to save the source and end locations to be PNML complient in this
     * @param arc
     * @param adapted
     */
    private void setArcPoints(Arc<? extends Connectable, ? extends Connectable> arc, AdaptedArc adapted) {

        List<ArcPoint> arcPoints = adapted.getArcPoints();
        ArcPoint source = new ArcPoint(arc.getStartPoint(), false);
        arcPoints.add(source);
        arcPoints.addAll(arc.getIntermediatePoints());
        ArcPoint target = new ArcPoint(arc.getEndPoint(), false);
        arcPoints.add(target);
    }


    /**
     * Sets the arc points in the arc based on the adapted.
     * Loses the source and end locations to just provide intermediate pints
     * @param arc
     * @param adapted
     */
    private void setRealArcPoints(Arc<? extends Connectable, ? extends Connectable> arc, AdaptedArc adapted) {


        List<ArcPoint> arcPoints = adapted.getArcPoints();
        if (arcPoints.isEmpty()) {
            return;
        }

        for (int i = 1; i < arcPoints.size() - 1; i++) {
           arc.addIntermediatePoint(arcPoints.get(i));
        }

    }

    private String weightToString(Map<Token, String> weights) {
        return Joiner.on(",").withKeyValueSeparator(",").join(weights);
    }

    private Map<Token, String> stringToWeights(String weights) {

        Map<Token, String> tokenWeights = new HashMap<Token, String>();
        if (weights.isEmpty()) {
            return tokenWeights;
        }

            String[] commaSeperatedMarkings = weights.split(",");
        if (commaSeperatedMarkings.length == 1) {
            Token token = getDefaultToken();
            String weight = commaSeperatedMarkings[0];
            tokenWeights.put(token, weight);
        } else {
            for (int i = 0; i < commaSeperatedMarkings.length; i += 2) {
                String weight = commaSeperatedMarkings[i + 1].replace("@", ",");
                String tokenName = commaSeperatedMarkings[i];
                Token token = getTokenIfExists(tokenName);
                tokenWeights.put(token, weight);
            }
        }
        return tokenWeights;
    }

    /**
     * @return the default token to use if no token is specified in the
     * Arc weight XML.
     */
    private Token getDefaultToken() {
        return getTokenIfExists("Default");
    }

    /**
     * @param tokenName token to find in {@link this.tokens}
     * @return token if exists
     * @throws RuntimeException if token does not exist
     */
    private Token getTokenIfExists(String tokenName) {
        if (!tokens.containsKey(tokenName)) {
            throw new RuntimeException("No " + tokenName + " token exists!");
        }
        return tokens.get(tokenName);
    }

}
