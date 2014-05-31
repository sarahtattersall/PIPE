package pipe.gui;

import com.google.common.collect.Sets;
import pipe.controllers.PetriNetController;
import pipe.controllers.PipeApplicationController;
import pipe.historyActions.AnimationHistory;
import uk.ac.imperial.pipe.animation.Animator;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Set;


/**
 * This class is used to process clicks by the user to manually step
 * through enabled transitions in the net.
 */
public class GUIAnimator {

    private final Timer timer = new Timer(0, new TimedTransitionActionListener());

    private final Animator animator;

    private final AnimationHistory animationHistory;

    private int numberSequences = 0;

    public GUIAnimator(Animator animator, AnimationHistory animationHistory) {
        this.animator = animator;
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
        markEnabledTransitions(new HashSet<Transition>(), animator.getEnabledTransitions());
    }

    /**
     * Computes transitions which need to be disabled because they are no longer enabled and
     * those that need to be enabled because they have been newly enabled.
     */
    private void markEnabledTransitions(Set<Transition> previouslyEnabled, Set<Transition> enabled) {
        for (Transition transition : Sets.difference(previouslyEnabled, enabled)) {
            transition.disable();
        }

        for (Transition transition : Sets.difference(enabled, previouslyEnabled)) {
            transition.enable();
        }
    }

    /**
     * Saves the current tokens in places
     */
    private void saveCurrentTokenState() {
        animator.saveState();
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
        Transition transition = animator.getRandomEnabledTransition();
        fireTransition(transition);
    }

    /**
     * This method keeps track of a fired transition in the AnimationHistoryView
     * object, enables transitions after the recent firing, and properly displays
     * the transitions.
     *
     * @param transition
     */
    public void fireTransition(Transition transition) {
        Set<Transition> previouslyEnabled = animator.getEnabledTransitions();
        animationHistory.clearStepsForward();
        animationHistory.addHistoryItem(transition);
        animator.fireTransition(transition);

        Set<Transition> enabled = animator.getEnabledTransitions();
        markEnabledTransitions(previouslyEnabled, enabled);

    }

    /**
     * Steps back through previously fired transitions
     */
    public void stepBack() {
        if (animationHistory.isStepBackAllowed()) {
            Transition transition = animationHistory.getCurrentTransition();
            animationHistory.stepBackwards();
            animator.fireTransitionBackwards(transition);

        }
    }

    /**
     * Steps forward through previously fired transitions
     */
    public void stepForward() {
        if (isStepForwardAllowed()) {
            int nextPosition = animationHistory.getCurrentPosition() + 1;
            Transition transition = animationHistory.getTransition(nextPosition);
            animator.fireTransition(transition);
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
    }

    /**
     * Restores all places to their original token counts.
     * Disables all transitions
     */
    private void restoreModel() {
        animator.reset();
        for (Transition transition : animator.getEnabledTransitions()) {
            transition.disable();
        }
    }

    private class TimedTransitionActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            PipeApplicationController applicationController = ApplicationSettings.getApplicationController();
            PetriNetController controller = applicationController.getActivePetriNetController();
            if ((getNumberSequences() < 1) || !controller.isInAnimationMode()) {
                timer.stop();
                return;
            }
            doRandomFiring();
            setNumberSequences(getNumberSequences() - 1);
        }
    }
}
