package pipe.animation;

import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.*;

import java.util.*;

/**
 * Class that contains methods to help with animating the Petri net
 */
public class PetriNetAnimator implements Animator {
    /**
     * Petri net to animate
     */
    private final PetriNet petriNet;

    private Map<String, Map<Token, Integer>> savedStateTokens = new HashMap<>();

    public PetriNetAnimator(PetriNet petriNet) {
        this.petriNet = petriNet;
        saveState();
        backwardsStrategies.put(ArcType.NORMAL, new BackwardsNormalStrategy());
        backwardsStrategies.put(ArcType.INHIBITOR, new InhibitorStrategy());
        forwardStrategies.put(ArcType.NORMAL, new ForwardsNormalStrategy());
    }


    /**
     * Houses the backwards strategies for arcs place -> transition
     * There can be two kinds, normal and inhibitor
     */
    private final Map<ArcType, ArcStrategy<Place, Transition>> backwardsStrategies = new HashMap<>();

    /**
     * Houses forward stratergies for arcs transition -> place
     * There can be only one kind, normal.
     */
    private final Map<ArcType, ArcStrategy<Transition, Place>> forwardStrategies = new HashMap<>();

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

        Set<Transition> enabledTransitions = findEnabledTransitions();
        boolean hasImmediate = areAnyTransitionsImmediate(enabledTransitions);
        int maxPriority = hasImmediate ? getMaxPriority(enabledTransitions) : 0;

        if (hasImmediate) {
            removeTimedTransitions(enabledTransitions);
        }

        removePrioritiesLessThan(maxPriority, enabledTransitions);
        return enabledTransitions;
    }

    /**
     * @param transitions to check if any are timed
     * @return true if any of the transitions are timed
     */
    private boolean areAnyTransitionsImmediate(Iterable<Transition> transitions) {
        for (Transition transition : transitions) {
            if (!transition.isTimed()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Note we must use an iterator in order to ensure save removal
     * whilst looping
     *
     * @param priority    minimum priority of transitions allowed to remain in the Collection
     * @param transitions to remove if their priority is less than the specified value
     */
    private void removePrioritiesLessThan(int priority, Iterable<Transition> transitions) {
        Iterator<Transition> transitionIterator = transitions.iterator();
        while (transitionIterator.hasNext()) {
            Transition transition = transitionIterator.next();
            if (!transition.isTimed() && transition.getPriority() < priority) {
                transitionIterator.remove();
            }
        }
    }

    /**
     * Removes timed transitions from transitions
     * <p/>
     * Note have to use an iterator for save deletions whilst
     * iterating through the list
     *
     * @param transitions to remove timed transitions from
     */
    private void removeTimedTransitions(Set<Transition> transitions) {
        Iterator<Transition> transitionIterator = transitions.iterator();
        while (transitionIterator.hasNext()) {
            Transition transition = transitionIterator.next();
            if (transition.isTimed()) {
                transitionIterator.remove();
            }
        }
    }

    /**
     * @param transitions to find max prioirty of
     * @return the maximum priority of immediate transitions in the collection
     */
    private int getMaxPriority(Iterable<Transition> transitions) {
        int maxPriority = 0;
        for (Transition transition : transitions) {
            if (!transition.isTimed()) {
                maxPriority = Math.max(maxPriority, transition.getPriority());
            }
        }
        return maxPriority;
    }

    /**
     * @return all the currently enabed transitions in the petri net
     */
    private Set<Transition> findEnabledTransitions() {

        Set<Transition> enabledTransitions = new HashSet<>();
        for (Transition transition : petriNet.getTransitions()) {
            if (isEnabled(transition)) {
                enabledTransitions.add(transition);
            }
        }
        return enabledTransitions;
    }


    /**
     * Works out if an transition is enabled. This means that it checks if
     * a) places connected by an incoming arc to this transition have enough tokens to fire
     * b) places connected by an outgoing arc to this transition have enough space to fit the
     * new tokens (that is enough capacity).
     *
     * @param transition to see if it is enabled
     * @return true if transition is enabled
     */
    private boolean isEnabled(Transition transition) {
        boolean enabledForArcs = true;
        for (Arc<Place, Transition> arc : petriNet.inboundArcs(transition)) {
            ArcStrategy<Place, Transition> strategy = backwardsStrategies.get(arc.getType());
            enabledForArcs &= strategy.canFire(petriNet, arc);
        }
        for (Arc<Transition, Place> arc : petriNet.outboundArcs(transition)) {
            ArcStrategy<Transition, Place> strategy = forwardStrategies.get(arc.getType());
            enabledForArcs &= strategy.canFire(petriNet, arc);
        }
        return enabledForArcs;
    }


    /**
     *
     * @param transition
     * @param initialState
     * @return Map of places whose token counts differ from those in the initial state
     */
    public Map<Place, Map<Token, Integer>> getFiredState(Transition transition, Map<Place, Map<Token, Integer>> initialState) {
        Map<Place, Map<Token, Integer>> placeTokenCounts = new HashMap<>();
        Set<Transition> enabled = getEnabledTransitions();
        if (enabled.contains(transition)) {
            //Decrement previous places
            for (Arc<Place, Transition> arc : petriNet.inboundArcs(transition)) {
                Place place = arc.getSource();
                Map<Token, Integer> tokenCounts = new HashMap<>();
                placeTokenCounts.put(place, tokenCounts);
                for (Token token : arc.getTokenWeights().keySet()) {
                    IncidenceMatrix matrix = petriNet.getBackwardsIncidenceMatrix(token);
                    int currentCount = getTokenCount(initialState, place, token);
                    int newCount = currentCount - matrix.get(place, transition);
                    tokenCounts.put(token, newCount);
                }
            }

            //Increment new places
            for (Arc<Transition, Place> arc : petriNet.outboundArcs(transition)) {
                Place place = arc.getTarget();
                Map<Token, Integer> tokenCounts = new HashMap<>();
                placeTokenCounts.put(place, tokenCounts);
                for (Token token : arc.getTokenWeights().keySet()) {
                    IncidenceMatrix matrix = petriNet.getForwardsIncidenceMatrix(token);
                    int currentCount = getTokenCount(initialState, place, token);
                    int newCount = currentCount + matrix.get(place, transition);
                    tokenCounts.put(token, newCount);
                }
            }
        }
        return placeTokenCounts;
    }

    /**
     * Auxillary method used to extract token counts from a map
     *
     * If a Place does not have any tokens of a particular type in it then the entry
     * will not be in the map. Therefore this method returns 0 for the token count
     * if it is not contained in the map
     *
     * @param placeTokens
     * @param place
     * @param token
     * @return
     */
    private int getTokenCount(Map<Place, Map<Token, Integer>> placeTokens, Place place, Token token) {
        if (placeTokens.get(place).containsKey(token)){
            return placeTokens.get(place).get(token);
        }
        return 0;
    }


    @Override
    public void fireTransition(Transition transition) {

        Map<Place, Map<Token, Integer>> initialTokenCounts = new HashMap<>();
        for (Place place : petriNet.getPlaces()) {
            initialTokenCounts.put(place, new HashMap<>(place.getTokenCounts()));
        }
        Map<Place, Map<Token, Integer>> updatedPlaces = getFiredState(transition, initialTokenCounts);

        //Set all counts
        for (Map.Entry<Place, Map<Token, Integer>> entry : updatedPlaces.entrySet()) {
            entry.getKey().setTokenCounts(entry.getValue());
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
