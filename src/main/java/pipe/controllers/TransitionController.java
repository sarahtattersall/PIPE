package pipe.controllers;

import pipe.historyActions.*;
import pipe.models.component.Arc;
import pipe.models.component.Place;
import pipe.models.component.Transition;
import pipe.views.viewComponents.RateParameter;

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

    public RateParameter getRateParameter() {
        return component.getRateParameter();
    }

    public String getName() {
        return component.getName();
    }

    public String getRateExpr() {
        return component.getRateExpr();
    }

    public int getPriority() {
        return component.getPriority();
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
    public void setInfiniteServer(
                                  final boolean infiniteValue) {
        TransitionInfiniteServer infiniteAction = new TransitionInfiniteServer(component, infiniteValue);
        infiniteAction.redo();
        historyManager.addNewEdit(infiniteAction);
    }

    public void setTimed(final boolean timedValue) {
        TransitionTiming timedAction = new TransitionTiming(component, timedValue);
        timedAction.redo();
        historyManager.addNewEdit(timedAction);
    }

    public void setPriority(
                            final int priorityValue) {
        int oldPriority = component.getPriority();
        TransitionPriority priorityAction = new TransitionPriority(component, oldPriority, priorityValue);
        priorityAction.redo();
        historyManager.addNewEdit(priorityAction);
    }

    public void setAngle(final int angle) {
        int oldAngle = component.getAngle();
        TransitionRotation angleAction = new TransitionRotation(component, oldAngle, angle);
        angleAction.redo();
        historyManager.addNewEdit(angleAction);
    }
}
