package pipe.controllers;

import pipe.historyActions.transition.*;
import uk.ac.imperial.pipe.models.petrinet.Arc;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Rate;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Transition controller responsible for editing the underlying transition Petri net component
 */
public class TransitionController extends AbstractConnectableController<Transition> {

    /**
     * Constructor
     * @param component underlying transition model
     * @param listener undo edit listener
     */
    protected TransitionController(Transition component, UndoableEditListener listener) {
        super(component, listener);
    }

    /**
     *
     * @return true if the transition is timed
     */
    public boolean isTimed() {
        return component.isTimed();
    }

    /**
     *
     * @param timedValue true if the transition is timed
     */
    public void setTimed(boolean timedValue) {
        component.setTimed(timedValue);
        registerUndoableEdit(new TransitionTiming(component, timedValue));
    }

    /**
     *
     * @return true if the transition is an infinite sever
     */
    public boolean isInfiniteServer() {
        return component.isInfiniteServer();
    }

    /**
     *
     * @param infiniteValue true if the transition is an infinite server
     */
    public void setInfiniteServer(boolean infiniteValue) {
        component.setInfiniteServer(infiniteValue);
        UndoableEdit infiniteAction = new TransitionInfiniteServer(component, infiniteValue);
        registerUndoableEdit(infiniteAction);
    }

    /**
     *
     * @return transition name
     */
    public String getName() {
        return component.getId();
    }

    /**
     *
     * @return transition functional expression unevaluated
     */
    public String getRateExpr() {
        return component.getRateExpr();
    }

    /**
     *
     * @return priority of the transition
     */
    public int getPriority() {
        return component.getPriority();
    }

    /**
     *
     * @param priorityValue new priority of the transition
     */
    public void setPriority(int priorityValue) {
        int oldPriority = component.getPriority();
        component.setPriority(priorityValue);
        registerUndoableEdit(new TransitionPriority(component, oldPriority, priorityValue));
    }

    /**
     *
     * @return angle the transition should be displayed at
     */
    public int getAngle() {
        return component.getAngle();
    }

    /**
     *
     * @param angle new angle the transition should be displayed at
     */
    public void setAngle(int angle) {
        int oldAngle = component.getAngle();
        component.setAngle(angle);
        registerUndoableEdit(new TransitionRotation(component, oldAngle, angle));
    }

    /**
     *
     * This is not currently implemented
     *
     * @return the inbound arcs for the transition
     */
    //TODO: GET CURRENT PETRINET
    public Collection<Arc<Place, Transition>> inboundArcs() {
        //        return component.inboundArcs();
        return new LinkedList<>();
    }

    /**
     *
     * @return the transitions rate
     */
    public Rate getRate() {
        return component.getRate();
    }

    /**
     *
     * @param rate new transition rate
     */
    public void setRate(Rate rate) {
        Rate oldRate = component.getRate();
        component.setRate(rate);
        registerUndoableEdit(new SetRateParameter(component, oldRate, rate));
    }
}
