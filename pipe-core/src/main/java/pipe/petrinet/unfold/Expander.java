package pipe.petrinet.unfold;

import pipe.exceptions.PetriNetComponentException;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.component.Connectable;
import pipe.models.component.arc.*;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * This class unfolds a coloured petri net into an uncoloured net
 * The algorithm for doing this can be found in the Stochastic Petri Net
 * book by Bause and Kritzinger
 */
public class Expander {
    /**
     * Petri net to unfold
     */
    private final PetriNet petriNet;

    /**
     * Single token to unfold the net into
     */
    private final Token unfoldToken;

    /**
     * Places for new net mapped id -> place
     */
    private final Map<String, Place> newPlaces = new HashMap<String, Place>();

    /**
     * Transitions for new net mapped id -> transition
     */
    private final Map<String, Transition> newTransitions = new HashMap<String, Transition>();

    /**
     * Arcs for new net mapped arc -> transition
     */
    private final Map<String, Arc<? extends Connectable, ? extends Connectable>> newArcs =
            new HashMap<String, Arc<? extends Connectable, ? extends Connectable>>();


    public Expander(PetriNet petriNet) {
        this.petriNet = petriNet;
        unfoldToken = getCopiedToken();
    }

    /**
     * @return Copied token which will be added to the new petri net
     */
    private Token getCopiedToken() {
        return new Token(getToken());
    }

    /**
     * Finds the token that we will unfold the net down to
     *
     * @return First tries to find default token
     * Failing this tries to find black toke
     * Otherwise just returns first token it comes acorss
     */
    private Token getToken() {
        if (petriNet.containsDefaultToken()) {
            return getDefaultToken();
        }

        Token blackToken = getBlackToken();
        if (blackToken != null) {
            return blackToken;
        }
        return getFirstToken();
    }

    /**
     * PRE: Should only be called if we know the token exists in the petri net
     *
     * @return default token in petri net, null if does not exist
     */
    private Token getDefaultToken() {
        try {
            return petriNet.getComponent("Default", Token.class);
        } catch (PetriNetComponentNotFoundException ignored) {
            return null;
        }
    }

    /**
     * @return Black token in petri net
     */
    private Token getBlackToken() {
        for (Token token : petriNet.getTokens()) {
            if (token.getColor().equals(Color.BLACK)) {
                return token;
            }
        }
        return null;
    }

    /**
     * @return first token in petri net
     */
    private Token getFirstToken() {
        return petriNet.getTokens().iterator().next();

    }

    /**
     * @return new unfolded petri net
     */
    public PetriNet unfold() {
        unfoldTransitions();
        return createPetriNet();
    }

    /**
     * Iterate through each transition, analyse its input and output arcs
     * and create new places/arcs as necessary
     */
    private void unfoldTransitions() {
        for (Transition transition : petriNet.getTransitions()) {
            Transition newTransition = new Transition(transition);
            newTransitions.put(newTransition.getId(), newTransition);
            analyseOutboundArcs(newTransition, petriNet.outboundArcs(transition));
            analyseInboundArcs(newTransition, petriNet.inboundArcs(transition));
        }

    }

    private PetriNet createPetriNet() {
        PetriNet petriNet = new PetriNet();
        petriNet.addToken(unfoldToken);

        for (Place place : newPlaces.values()) {
            petriNet.addPlace(place);
        }

        for (Transition transition : newTransitions.values()) {
            petriNet.addTransition(transition);
        }

        for (Arc<? extends Connectable, ? extends Connectable> arc : newArcs.values()) {
            try {
                petriNet.add(arc);
            } catch (PetriNetComponentException e) {
                e.printStackTrace();
            }
        }
        return petriNet;
    }

    /**
     * Analyses all outbound arcs of the previous transition
     * Creates new outbound places with arcs for the transition
     *
     * @param newTransition transition for new petri net
     * @param arcs          outbound arcs of the old transition
     */
    public void analyseOutboundArcs(Transition newTransition, Iterable<OutboundArc> arcs) {
        for (Arc<Transition, Place> arc : arcs) {
            Place place = arc.getTarget();
            Data data = getPlaceData(arc, place);
            Place newPlace =
                    getNewPlace(place, newTransition.getX(), newTransition.getY(), data.placeTokenCount, data.name);
            createArc(newTransition, newPlace, data.arcWeight, arc.getType());
        }

    }

    /**
     * Analyses all inbound arcs of the previous transition
     * Creates new inbound places with arcs for the transition
     *
     * @param newTransition transition for new petri net
     * @param arcs          inbound arcs of the old transition
     */
    public void analyseInboundArcs(Transition newTransition, Iterable<InboundArc> arcs) {
        for (Arc<Place, Transition> arc : arcs) {
            Place place = arc.getSource();
            Data data = getPlaceData(arc, place);
            Place newPlace =
                    getNewPlace(place, newTransition.getX(), newTransition.getY(), data.placeTokenCount, data.name);
            createArc(newPlace, newTransition, data.arcWeight, arc.getType());
        }
    }

    /**
     * @param arc   original arc
     * @param place original place
     * @return Data needed to create a new place in the unfolded net
     */
    private Data getPlaceData(Arc<? extends Connectable, ? extends Connectable> arc, Place place) {

        StringBuilder newNameBuilder = new StringBuilder(place.getName());
        int placeTokenCount = 0;
        int arcWeight = 0;
        for (Map.Entry<Token, String> entry : arc.getTokenWeights().entrySet()) {
            Token token = entry.getKey();
            String weight = entry.getValue();
            //TODO: THIS IS ASUMING IT ISNT FUNCTIONAL :/
            arcWeight = Integer.valueOf(weight);
            if (arcWeight > 0) {
                newNameBuilder.append("_").append(token.getId());
                placeTokenCount = place.getTokenCount(token);
            }

        }
        return new Data(placeTokenCount, arcWeight, newNameBuilder.toString());
    }

    private Place getNewPlace(Place original, int newX, int newY, int tokenCount, String id) {
        if (newPlaces.containsKey(id)) {
            return newPlaces.get(id);
        }

        Place place = new Place(original);

        Map<Token, Integer> newTokenCounts = new HashMap<>();
        if (tokenCount > 0) {
            newTokenCounts.put(unfoldToken, tokenCount);
        }

        place.setTokenCounts(newTokenCounts);
        place.setName(id);
        place.setId(id);
        place.setX(newX);
        place.setY(newY);
        newPlaces.put(place.getId(), place);
        return place;

    }

    private void createArc(Transition source, Place target, int arcWeight, ArcType type) {
        Arc<Transition, Place> newArc = new OutboundNormalArc(source, target, getNewArcWeight(arcWeight));
        newArcs.put(newArc.getId(), newArc);
    }

    /**
     * Creates an arc from source to transition
     * Adds it to internal storage
     *
     * @param source    unfolded arc source
     * @param target    unfolded arc target
     * @param arcWeight unfolded arc weight
     */
    private void createArc(Place source, Transition target, int arcWeight, ArcType type) {
        Arc<Place, Transition> newArc;
        switch (type) {
            case INHIBITOR:
                newArc = new InboundInhibitorArc(source, target);
            default:
                newArc = new InboundNormalArc(source, target, getNewArcWeight(arcWeight));
        }
        newArcs.put(newArc.getId(), newArc);
    }

    /**
     * @param arcWeight new weight for unfolded token
     * @return single entry mapping the unfolded token set in the constructor to the arc weight specified
     */
    private Map<Token, String> getNewArcWeight(int arcWeight) {
        Map<Token, String> arcWeights = new HashMap<Token, String>();
        arcWeights.put(unfoldToken, Integer.toString(arcWeight));
        return arcWeights;
    }

    /**
     * A class used to return multiple items from a method
     */
    private static class Data {
        /**
         * New place token count
         */
        public final int placeTokenCount;

        /**
         * New arc weight
         */
        public final int arcWeight;

        /**
         * New place name
         */
        public final String name;

        public Data(int placeTokenCount, int arcWeight, String name) {
            this.placeTokenCount = placeTokenCount;
            this.arcWeight = arcWeight;
            this.name = name;
        }
    }
}
