package pipe.controllers;

import pipe.historyActions.transition.*;
import uk.ac.imperial.pipe.models.component.arc.Arc;
import uk.ac.imperial.pipe.models.component.place.Place;
import uk.ac.imperial.pipe.models.component.rate.Rate;
import uk.ac.imperial.pipe.models.component.transition.Transition;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;
import java.util.Collection;
import java.util.LinkedList;

public class TransitionController extends AbstractPetriNetComponentController<Transition>
{

    protected TransitionController(Transition component, UndoableEditListener listener) {
        super(component, listener);
    }

    public boolean isTimed() {
        return component.isTimed();
    }

    public boolean isInfiniteServer() {
        return component.isInfiniteServer();
    }
//
//    public RateParameter getRateParameter() {
//        return component.getRateParameter();
//    }

    public String getName() {
        return component.getName();
    }

    public String getRateExpr() {
        return component.getRateExpr();
    }

    public int getPriority() {
        return component.getPriority();
    }

    public int getAngle() {
        return component.getAngle();
    }

    //TODO: GET CURRENT PETRINET
    public Collection<Arc<Place, Transition>> inboundArcs() {
//        return component.inboundArcs();
        return new LinkedList<>();
    }

    public void setInfiniteServer(boolean infiniteValue) {
        component.setInfiniteServer(infiniteValue);
        UndoableEdit infiniteAction = new TransitionInfiniteServer(component, infiniteValue);
        registerUndoableEdit(infiniteAction);
    }

    public void setTimed(boolean timedValue) {
        component.setTimed(timedValue);
        registerUndoableEdit(new TransitionTiming(component, timedValue));
    }

    public void setPriority(int priorityValue) {
        int oldPriority = component.getPriority();
        component.setPriority(priorityValue);
        registerUndoableEdit(new TransitionPriority(component, oldPriority, priorityValue));
    }

    public void setAngle(int angle) {
        int oldAngle = component.getAngle();
        component.setAngle(angle);
        registerUndoableEdit(new TransitionRotation(component, oldAngle, angle));
    }

    public Rate getRate() {
        return component.getRate();
    }

    public void setRate(Rate rate) {
        Rate oldRate = component.getRate();
        component.setRate(rate);
        registerUndoableEdit(new SetRateParameter(component, oldRate, rate));
    }
}
