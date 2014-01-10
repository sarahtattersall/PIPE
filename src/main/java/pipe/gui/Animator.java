package pipe.gui;

import pipe.historyActions.AnimationHistory;
import pipe.models.PetriNet;
import pipe.models.component.Transition;
import pipe.views.ArcView;
import pipe.views.PetriNetView;
import pipe.views.PipeApplicationView;
import pipe.views.TransitionView;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;


/**
 * This class is used to process clicks by the user to manually step
 * through enabled transitions in the net.
 */
public class Animator {

    private final Timer timer = new Timer(0, new TimedTransitionActionListener());;
    private int numberSequences = 0;
    private final PetriNet petriNet;
    private final AnimationHistory animationHistory;


    public Animator(PetriNet petriNet, AnimationHistory animationHistory) {
        this.petriNet = petriNet;
        this.animationHistory = animationHistory;
    }

    /**
     * Restores model at end of animation and sets all transitions to false and
     * unhighlighted
     */
    public void restoreModel() {
//        PetriNetView petriNetView = ApplicationSettings.getApplicationView().getCurrentPetriNetView();
//        petriNetView.restorePreviousMarking();
//        disableTransitions(petriNetView.getModel());
    }


    public void startRandomFiring() {
        animationHistory.clearStepsForward();
        if (getNumberSequences() > 0) {
            // stop animation
            setNumberSequences(0);
        } else {
            try {
                String s = JOptionPane.showInputDialog(
                        "Enter number of firings to perform", "1");
                this.numberSequences = Integer.parseInt(s);
                s = JOptionPane.showInputDialog(
                        "Enter time delay between firing /ms", "50");
                timer.setDelay(Integer.parseInt(s));
                timer.start();
            } catch (NumberFormatException e) {
                ApplicationSettings.getApplicationView().setAnimationMode(false);
            }
        }
    }

    /**
     * Randomly fires one of the enabled transitions.
     */
    public void doRandomFiring() {
        Transition transition = petriNet.getRandomTransition();
        fireTransition(transition);
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

    public synchronized int getNumberSequences() {
        return numberSequences;
    }


    public synchronized void setNumberSequences(int numberSequences) {
        this.numberSequences = numberSequences;
    }

    public boolean isStepForwardAllowed() {
        return animationHistory.isStepForwardAllowed();
    }

    public boolean isStepBackAllowed() {
        return animationHistory.isStepBackAllowed();
    }


    public void clear() {
        animationHistory.clear();
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
