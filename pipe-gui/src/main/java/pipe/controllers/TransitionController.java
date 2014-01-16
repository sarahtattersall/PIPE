package pipe.controllers;

import pipe.historyActions.*;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;

import java.util.Collection;
import java.util.LinkedList;

public class TransitionController extends AbstractPetriNetComponentController<Transition>
{

    protected TransitionController(Transition component,
                                   HistoryManager historyManager) {
        super(component, historyManager);
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
        return new LinkedList<Arc<Place, Transition>>();
    }

    /**
     *
     * @param infiniteValue
     */
    public void setInfiniteServer(final boolean infiniteValue) {
        component.setInfiniteServer(infiniteValue);
        TransitionInfiniteServer infiniteAction = new TransitionInfiniteServer(component, infiniteValue);
        historyManager.addNewEdit(infiniteAction);
    }

    public void setTimed(final boolean timedValue) {
        component.setTimed(timedValue);
        TransitionTiming timedAction = new TransitionTiming(component, timedValue);
        historyManager.addNewEdit(timedAction);
    }

    public void setPriority(final int priorityValue) {
        int oldPriority = component.getPriority();
        component.setPriority(priorityValue);
        TransitionPriority priorityAction = new TransitionPriority(component, oldPriority, priorityValue);
        historyManager.addNewEdit(priorityAction);
    }

    public void setAngle(final int angle) {
        int oldAngle = component.getAngle();
        component.setAngle(angle);
        TransitionRotation angleAction = new TransitionRotation(component, oldAngle, angle);
        historyManager.addNewEdit(angleAction);
    }
}
