package pipe.controllers;

import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import pipe.controllers.interfaces.IController;
import pipe.historyActions.*;
import pipe.models.component.Arc;
import pipe.models.component.Place;
import pipe.models.component.Transition;
import pipe.views.TransitionView;
import pipe.views.viewComponents.RateParameter;

import java.util.ArrayList;
import java.util.Collection;

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

    public Collection<Arc<Place, Transition>> inboundArcs() {
        return component.inboundArcs();
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
