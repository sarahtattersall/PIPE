package pipe.animation;

import pipe.models.component.transition.Transition;

import java.util.Set;

/**
 * Class responsible for calculating transitions that can fire etc.
 */
public interface Animator {
    /**
     * Saves the current state of the Petri net, allowing it to
     * be reset at any point
     */
    void saveState();

    /**
     * Resets the state to the last saved net
     */
    void reset();

    /**
     * @return a random transition that can fire
     */
    Transition getRandomEnabledTransition();


    /**
     * Finds all of the transitions which are enabled
     * If there are any immediate transitions then these take priority
     * and timed transactions are not counted as enabled
     * <p/>
     * It also disables any immediate transitions with a lower
     * priority than the highest available priority.
     * <p/>
     *
     * @return all transitions that can be enabled
     */
    Set<Transition> getEnabledTransitions();


    /**
     * Removes the relevant number tokens from places into the transition
     * Adds tokens to the places out of the transition according to the arc weight
     * <p/>
     * Handles functional weights e.g. removing all of a places tokens and adding them
     * to the receiving place by calculating all incidence matricies before setting any token counts
     * <p/>
     * Recalculates enabled transitions
     *
     * @param transition transition to fire
     */
    void fireTransition(Transition transition);

    /**
     * Removes tokens from places out of the transition
     * Adds tokens to the places into the transition according to the arc weight
     * Enables fired transition
     *
     * @param transition transition to fire backwards
     */
    //TODO: NOT SURE IF BETTER TO JUST HAVE UNDO/REDO IN ANIMATION HISTORY? HAVE TO STORE ENTIRE PETRI
    //      NET STATES SO MAYBE NOT?
    void fireTransitionBackwards(Transition transition);
}
