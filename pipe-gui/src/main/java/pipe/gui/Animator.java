package pipe.gui;

import pipe.historyActions.AnimationHistory;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;
import pipe.views.PipeApplicationView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;


/**
 * This class is used to process clicks by the user to manually step
 * through enabled transitions in the net.
 */
public class Animator {

    private final Timer timer = new Timer(0, new TimedTransitionActionListener());

    private final PetriNet petriNet;

    private final AnimationHistory animationHistory;

    /**
     * Map of place id -> token count
     * used to restore tokens at end of animation
     */
    private final Map<String, Map<Token, Integer>> placeTokens = new HashMap<String, Map<Token, Integer>>();

    private int numberSequences = 0;

    public Animator(PetriNet petriNet, AnimationHistory animationHistory) {
        this.petriNet = petriNet;
        this.animationHistory = animationHistory;
    }

    /**
     * Saves the current petri net token counts for restoring later.
     * When exit animation mode we expect the petri net to return to
     * it's original state, hence this method must be called before animating.
     * <p/>
     * Also marks the enabled transitions in the petri net.
     */
    public void startAnimation() {
        saveCurrentTokenState();
        petriNet.markEnabledTransitions();
    }

    /**
     * Saves the current tokens in places
     */
    private void saveCurrentTokenState() {
        for (Place place : petriNet.getPlaces()) {
            Map<Token, Integer> savedTokenCounts = new HashMap<Token, Integer>(place.getTokenCounts());
            placeTokens.put(place.getId(), savedTokenCounts);
        }
    }

    public void startRandomFiring() {
        animationHistory.clearStepsForward();
        if (getNumberSequences() > 0) {
            // stop animation
            setNumberSequences(0);
        } else {
            try {
                String s = JOptionPane.showInputDialog("Enter number of firings to perform", "1");
                this.numberSequences = Integer.parseInt(s);
                s = JOptionPane.showInputDialog("Enter time delay between firing /ms", "50");
                timer.setDelay(Integer.parseInt(s));
                timer.start();
            } catch (NumberFormatException e) {
                ApplicationSettings.getApplicationView().setAnimationMode(false);
            }
        }
    }

    public synchronized int getNumberSequences() {
        return numberSequences;
    }

    public synchronized void setNumberSequences(int numberSequences) {
        this.numberSequences = numberSequences;
    }

    /**
     * Randomly fires one of the enabled transitions.
     */
    public void doRandomFiring() {
        Transition transition = petriNet.getRandomTransition();
        fireTransition(transition);
    }

    /**
     * This method keeps track of a fired transition in the AnimationHistoryView
     * object, enables transitions after the recent firing, and properly displays
     * the transitions.
     *
     * @param transition
     * @author David Patterson renamed this method and changed the
     * AnimationHandler to make it fire the transition before calling this method.
     * This prevents double-firing a transition.
     * @author Pere Bonet modified this method so that it now stores transitions
     * that has just been fired in an array so that it can be accessed during
     * backwards and stepping to fix the unexcepted behaviour observed during
     * animation playback.
     * The method is renamed back to fireTransition.
     */
    public void fireTransition(Transition transition) {
        animationHistory.clearStepsForward();
        animationHistory.addHistoryItem(transition);
        petriNet.fireTransition(transition);
    }

    /**
     * Steps back through previously fired transitions
     */
    public void stepBack() {
        if (animationHistory.isStepBackAllowed()) {
            Transition transition = animationHistory.getCurrentTransition();
            animationHistory.stepBackwards();
            petriNet.fireTransitionBackwards(transition);

        }
    }

    /**
     * Steps forward through previously fired transitions
     */
    public void stepForward() {
        if (isStepForwardAllowed()) {
            int nextPosition = animationHistory.getCurrentPosition() + 1;
            Transition transition = animationHistory.getTransition(nextPosition);
            petriNet.fireTransition(transition);
            animationHistory.stepForward();
        }
    }

    public boolean isStepForwardAllowed() {
        return animationHistory.isStepForwardAllowed();
    }

    public boolean isStepBackAllowed() {
        return animationHistory.isStepBackAllowed();
    }

    /**
     * Finishes the animation
     * Resets the petri net state to before animation
     */
    public void finish() {
        restoreModel();
        animationHistory.clear();
        placeTokens.clear();
    }

    /**
     * Restores all places to their original token counts.
     */
    private void restoreModel() {
        for (Place place : petriNet.getPlaces()) {
            Map<Token, Integer> originalTokens = placeTokens.get(place.getId());
            place.setTokenCounts(originalTokens);
        }
    }

    private class TimedTransitionActionListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent actionEvent) {
            PipeApplicationView applicationView = ApplicationSettings.getApplicationView();
            if ((getNumberSequences() < 1) || !applicationView.getCurrentTab().isInAnimationMode()) {
                timer.stop();
                return;
            }
            doRandomFiring();
            setNumberSequences(getNumberSequences() - 1);
        }
    }
}
