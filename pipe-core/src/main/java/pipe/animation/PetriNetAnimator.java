package pipe.animation;

import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.IncidenceMatrix;
import pipe.models.petrinet.PetriNet;

import java.util.*;

/**
 * Contains methods to help with animating the Petri net and performs
 * in place modifications to the Petri net.
 */
public class PetriNetAnimator implements Animator {
    /**
     * Petri net to animate
     */
    private final PetriNet petriNet;

    private final AnimationLogic animationLogic;

    private Map<String, Map<Token, Integer>> savedStateTokens = new HashMap<>();

    public PetriNetAnimator(PetriNet petriNet) {
        this.petriNet = petriNet;
        animationLogic = new PetriNetAnimationLogic(petriNet);
        saveState();
    }


    @Override
    public final void saveState() {
        savedStateTokens.clear();
        for (Place place : petriNet.getPlaces()) {
            savedStateTokens.put(place.getId(), new HashMap<>(place.getTokenCounts()));
        }
    }

    @Override
    public void reset() {
        for (Place place : petriNet.getPlaces()) {
            Map<Token, Integer> originalTokens = savedStateTokens.get(place.getId());
            place.setTokenCounts(originalTokens);
        }
    }

    @Override
    public Transition getRandomEnabledTransition() {
        Collection<Transition> enabledTransitions = getEnabledTransitions();
        if (enabledTransitions.isEmpty()) {
            throw new RuntimeException("Error - no transitions to fire!");
        }

        Random random = new Random();
        int index = random.nextInt(enabledTransitions.size());

        Iterator<Transition> iter = enabledTransitions.iterator();
        Transition transition = iter.next();
        for (int i = 1; i < index; i++) {
            transition = iter.next();
        }
        return transition;
    }

    @Override
    public Set<Transition> getEnabledTransitions() {
        return animationLogic.getEnabledTransitions(getCurrentState());
    }

    /**
     * Creates a new state containing the token counts for the
     * current Petri net.
     *
     * @return current state of the Petri net
     */
    private State getCurrentState() {
        Map<String, Map<String, Integer>> tokenCounts = new HashMap<>();
        for (Place place : petriNet.getPlaces()) {
            Map<String, Integer> counts = new HashMap<>();
            for (Token token : petriNet.getTokens()) {
                counts.put(token.getId(), place.getTokenCount(token));
            }
            tokenCounts.put(place.getId(), counts);
        }
        return  new HashedState(tokenCounts);
    }



    @Override
    public void fireTransition(Transition transition) {
        State newState = animationLogic.getFiredState(getCurrentState(), transition);

        //Set all counts
        for (Place place : petriNet.getPlaces()) {
            try {
                for (Map.Entry<String, Integer> tokenEntry : newState.getTokens(place.getId()).entrySet()) {
                    Token token = petriNet.getComponent(tokenEntry.getKey(), Token.class);
                    place.setTokenCount(token, tokenEntry.getValue());
                }
            } catch (PetriNetComponentNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void fireTransitionBackwards(Transition transition) {
        //Increment previous places
        for (Arc<Place, Transition> arc : petriNet.inboundArcs(transition)) {
            Place place = arc.getSource();
            for (Token token : arc.getTokenWeights().keySet()) {
                IncidenceMatrix matrix = petriNet.getBackwardsIncidenceMatrix(token);
                int currentCount = place.getTokenCount(token);
                int newCount = currentCount + matrix.get(place, transition);
                place.setTokenCount(token, newCount);
            }
        }

        //Decrement new places
        for (Arc<Transition, Place> arc : petriNet.outboundArcs(transition)) {
            Place place = arc.getTarget();
            for (Token token : arc.getTokenWeights().keySet()) {
                IncidenceMatrix matrix = petriNet.getForwardsIncidenceMatrix(token);
                int oldCount = place.getTokenCount(token);
                int newCount = oldCount - matrix.get(place, transition);
                place.setTokenCount(token, newCount);
            }
        }
    }


}
