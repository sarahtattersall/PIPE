package pipe.controllers;

import pipe.historyActions.rateparameter.ChangeRateParameterRate;
import uk.ac.imperial.pipe.exceptions.InvalidRateException;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.RateParameter;
import uk.ac.imperial.pipe.parsers.FunctionalResults;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;

public class RateParameterController extends AbstractPetriNetComponentController<RateParameter> {
    private final PetriNet petriNet;

    protected RateParameterController(RateParameter component, PetriNet petriNet, UndoableEditListener listener) {
        super(component, listener);
        this.petriNet = petriNet;
    }

    public void setRate(String expression) throws InvalidRateException {
        String oldRate = component.getExpression();
        if (!oldRate.equals(expression)) {
            FunctionalResults<Double> results = petriNet.parseExpression(expression);
            if (results.hasErrors()) {
                throw new InvalidRateException(results.getErrorString("\n"));
            }
            component.setExpression(expression);
            UndoableEdit rateAction = new ChangeRateParameterRate(component, oldRate, expression);
            registerUndoableEdit(rateAction);
        }

    }
}
