package pipe.animation;

import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.parsers.FunctionalResults;
import uk.ac.imperial.state.HashedStateBuilder;
import uk.ac.imperial.state.State;

import java.util.*;

/**
 * This class has useful functions relevant for the animation
 * of a Petri net. It does not alter the state of the Petri net.
 */
public class PetriNetAnimationLogic implements AnimationLogic {
    /**
     * Petri net this class represents the logic for
     */
    private final PetriNet petriNet;

    public PetriNetAnimationLogic(PetriNet petriNet) {
        this.petriNet = petriNet;
    }

    @Override
    public Set<Transition> getEnabledTransitions(State state) {

        Set<Transition> enabledTransitions = findEnabledTransitions(state);
        boolean hasImmediate = areAnyTransitionsImmediate(enabledTransitions);
        int maxPriority = hasImmediate ? getMaxPriority(enabledTransitions) : 0;

        if (hasImmediate) {
            removeTimedTransitions(enabledTransitions);
        }

        removePrioritiesLessThan(maxPriority, enabledTransitions);
        return enabledTransitions;
    }

    @Override
    public Map<State, Collection<Transition>> getSuccessors(State state) {
        Collection<Transition> enabled = getEnabledTransitions(state);
        Map<State, Collection<Transition>> successors = new HashMap<>();
        for (Transition transition : enabled) {

            State successor = getFiredState(state, transition);
            if (!successors.containsKey(successor)) {
                successors.put(successor, new LinkedList<Transition>());
            }
            successors.get(successor).add(transition);
        }
        return successors;

    }

    /**
     * Creates a map for the successor of the given state after firing the
     * transition.
     * calculating the decremented token counts and then calculating the incremented
     * token counts.
     * <p/>
     * We cannot set the token counts in the decrement phase incase an incrememnt
     * depends on this value.
     * <p/>
     * E.g. if P0 -> T0 -> P1 and T0 -> P1 has a weight of #(P0) then we expect
     * #(P0) to refer to the number of tokens before firing.
     *
     * @param state
     * @param transition
     * @return Map of places whose token counts differ from those in the initial state
     */
    //TODO: This method is a bit too long
    @Override
    public State getFiredState(State state, Transition transition) {
        HashedStateBuilder builder = new HashedStateBuilder();
        for (Place place : petriNet.getPlaces()) { //Copy tokens
            builder.placeWithTokens(place.getId(), state.getTokens(place.getId()));
        }

        Set<Transition> enabled = getEnabledTransitions(state);
        if (enabled.contains(transition)) {

            //Decrement previous places
            for (Arc<Place, Transition> arc : petriNet.inboundArcs(transition)) {
                String placeId = arc.getSource().getId();
                Map<String, String> arcWeights = arc.getTokenWeights();
                for (Map.Entry<String, Integer> entry : state.getTokens(placeId).entrySet()) {
                    String tokenId = entry.getKey();
                    if (arcWeights.containsKey(tokenId)) {
                        int currentCount = entry.getValue();
                        double arcWeight = getArcWeight(arcWeights.get(tokenId));
                        int newCount = (int) (currentCount - arcWeight);

                        builder.placeWithToken(placeId, tokenId, newCount);
                    }
                }
            }


            State temporaryState = builder.build();


            //Increment new places
            for (Arc<Transition, Place> arc : petriNet.outboundArcs(transition)) {
                String placeId = arc.getTarget().getId();
                Map<String, String> arcWeights = arc.getTokenWeights();
                for (Map.Entry<String, String> entry : arcWeights.entrySet()) {
                    String tokenId = entry.getKey();
                    int currentCount = temporaryState.getTokens(placeId).get(tokenId);
                    double arcWeight = getArcWeight(entry.getValue());
                    builder.placeWithToken(placeId, tokenId, (int) (currentCount + arcWeight));
                }
            }
        }
        return builder.build();
    }

    /**
     * Parses a string representation of a weight with respect to the Petri net
     *
     * @param weight
     * @return
     */
    private double getArcWeight(String weight) {
        FunctionalResults<Double> result = petriNet.parseExpression(weight);
        if (result.hasErrors()) {
            //TODO:
            throw new RuntimeException("Could not parse arc weight");
        }

        return result.getResult();
    }


    /**
     * @return all the currently enabled transitions in the petri net
     */
    private Set<Transition> findEnabledTransitions(State state) {

        Set<Transition> enabledTransitions = new HashSet<>();
        for (Transition transition : petriNet.getTransitions()) {
            if (isEnabled(transition, state)) {
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
    private boolean isEnabled(Transition transition, State state) {
        boolean enabledForArcs = true;
        for (Arc<Place, Transition> arc : petriNet.inboundArcs(transition)) {
            enabledForArcs &= arc.canFire(petriNet, state);
        }
        for (Arc<Transition, Place> arc : petriNet.outboundArcs(transition)) {
            enabledForArcs &= arc.canFire(petriNet, state);
        }
        return enabledForArcs;
    }


    /**
     * @param transitions to check if any are timed
     * @return true if any of the transitions are immediate
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
     * @param transitions to find max priority of
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
     * Performs in place removal transitions whose priority is less than the specified value
     * <p/>
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
     * In place removal of timed transitions from transitions
     * <p/>
     * Note have to use an iterator for save deletions whilst
     * iterating through the list
     *
     * @param transitions to remove timed transitions from
     */
    private void removeTimedTransitions(Iterable<Transition> transitions) {
        Iterator<Transition> transitionIterator = transitions.iterator();
        while (transitionIterator.hasNext()) {
            Transition transition = transitionIterator.next();
            if (transition.isTimed()) {
                transitionIterator.remove();
            }
        }
    }

}
