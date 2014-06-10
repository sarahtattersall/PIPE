package pipe.controllers;

import com.google.common.collect.Sets;
import pipe.controllers.application.PipeApplicationController;
import pipe.historyActions.AnimationHistory;
import pipe.utilities.gui.GuiUtils;
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

    /**
     * Timer used for spacing between random transition firings
     */
    private final Timer timer = new Timer(0, new TimedTransitionActionListener());

    /**
     * Petri net animator
     */
    private final Animator animator;

    /**
     * Petri net animation history which is responsible
     * for displaying the fired transitions and stepping forwards/backwards
     */
    private final AnimationHistory animationHistory;

    /**
     * Main PIPE application controller
     */
    private final PipeApplicationController applicationController;

    /**
     * Number of transitions fired in the current sequence
     */
    private int numberSequences = 0;

    /**
     * Constructor
     * @param animator Petri net animator
     * @param animationHistory History for animation
     * @param applicationController Pipe main application controller
     */
    public GUIAnimator(Animator animator, AnimationHistory animationHistory,
                       PipeApplicationController applicationController) {
        this.animator = animator;
        this.animationHistory = animationHistory;
        this.applicationController = applicationController;
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
     * Saves the current tokens in places
     */
    private void saveCurrentTokenState() {
        animator.saveState();
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
     * Starts a random firing sequence for the specified number of transitions
     */
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
                GuiUtils.displayErrorMessage(null, "Error in animator: " + e.getMessage());
            }
        }
    }

    /**
     *
     * @return the number of transitions in the sequence
     */
    public synchronized int getNumberSequences() {
        return numberSequences;
    }

    /**
     *
     * @param numberSequences set the number of transitions in the sequene
     */
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

    /**
     *
     * @return true if a step forward can happen in the animation history
     */
    public boolean isStepForwardAllowed() {
        return animationHistory.isStepForwardAllowed();
    }

    /**
     *
     * @return true if a step backward can happen in the animation history
     */
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

    /**
     * Listens for the timer to run down to 0 and then performs the action
     */
    private class TimedTransitionActionListener implements ActionListener {
        /**
         * When the timer runs down to zero this will be triggered.
         *
         * It performs a random firing of a single transition
         * @param actionEvent
         */
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            PetriNetController controller = applicationController.getActivePetriNetController();
            if (getNumberSequences() < 1 || !controller.isInAnimationMode()) {
                timer.stop();
                return;
            }
            doRandomFiring();
            setNumberSequences(getNumberSequences() - 1);
        }
    }
}
