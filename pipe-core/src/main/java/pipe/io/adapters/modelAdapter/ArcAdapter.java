package pipe.io.adapters.modelAdapter;

import com.google.common.base.Joiner;
import pipe.io.adapters.model.AdaptedArc;
import pipe.models.component.Connectable;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArcAdapter extends XmlAdapter<AdaptedArc, Arc<? extends Connectable, ? extends Connectable>> {


    private final Map<String, Place> places;

    private final Map<String, Transition> transitions;

    private final Map<String, Token> tokens;

    /**
     * Empty contructor needed for marshelling. Since the method to marshell does not actually
     * use these fields it's ok to initialise them as empty/null.
     */
    public ArcAdapter() {
        places = new HashMap<String, Place>();
        transitions = new HashMap<String, Transition>();
        tokens = new HashMap<String, Token>();
    }

    public ArcAdapter(Map<String, Place> places, Map<String, Transition> transitions, Map<String, Token> tokens) {
        this.places = places;
        this.transitions = transitions;
        this.tokens = tokens;
    }

    @Override
    public Arc<? extends Connectable, ? extends Connectable> unmarshal(AdaptedArc adaptedArc)  {
        Arc<? extends Connectable, ? extends Connectable> arc;
        String source = adaptedArc.getSource();
        String target = adaptedArc.getTarget();

        Map<Token, String> weights = stringToWeights(adaptedArc.getInscription().getTokenCounts());
        if (adaptedArc.getType().equals("inhibitor")) {
            Place place = places.get(source);
            Transition transition = transitions.get(target);
            arc = new Arc<Place, Transition>(place, transition, weights, ArcType.INHIBITOR);
        } else {
            if (places.containsKey(source)) {
                Place place = places.get(source);
                Transition transition = transitions.get(target);
                arc = new Arc<Place, Transition>(place, transition, weights, ArcType.NORMAL);
            } else {
                Place place = places.get(target);
                Transition transition = transitions.get(source);
                arc = new Arc<Transition, Place>(transition, place, weights, ArcType.NORMAL);
            }
        }
        arc.setId(adaptedArc.getId());
        //TODO:
        arc.setTagged(false);

        setRealArcPoints(arc, adaptedArc);
        return arc;
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

    /**
     * @return the default token to use if no token is specified in the
     * Arc weight XML.
     */
    private Token getDefaultToken() {
        return getTokenIfExists("Default");
    }

    /**
     * Sets the arc points in the arc based on the adapted.
     * Loses the source and end locations to just provide intermediate pints
     *
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

    @Override
    public AdaptedArc marshal(Arc<? extends Connectable, ? extends Connectable> arc) {
        AdaptedArc adapted = new AdaptedArc();
        setArcPoints(arc, adapted);
        adapted.setId(arc.getId());
        adapted.setSource(arc.getSource().getId());
        adapted.setTarget(arc.getTarget().getId());
        adapted.getInscription().setTokenCounts(weightToString(arc.getTokenWeights()));
        adapted.setType(arc.getType().name().toLowerCase());
        return adapted;
    }

    private String weightToString(Map<Token, String> weights) {
        return Joiner.on(",").withKeyValueSeparator(",").join(weights);
    }

    /**
     * Sets the arc points in adapted based on the arc.
     * Needs to save the source and end locations to be PNML complient in this
     *
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

}
