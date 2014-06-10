package pipe.controllers;

import pipe.historyActions.rateparameter.ChangeRateParameterRate;
import uk.ac.imperial.pipe.exceptions.InvalidRateException;
import uk.ac.imperial.pipe.models.petrinet.PetriNet;
import uk.ac.imperial.pipe.models.petrinet.RateParameter;
import uk.ac.imperial.pipe.parsers.FunctionalResults;

import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoableEdit;

/**
 * Controller for editing the underlying rate parameter Petri net component
 */
public class RateParameterController extends AbstractPetriNetComponentController<RateParameter> {
    /**
     * The Petri net the rate parameter resides in
     */
    private final PetriNet petriNet;

    /**
     * Constructor
     *
     * @param component underlying rate parameter model
     * @param petriNet  Petri net the rate parameter is housed in
     * @param listener  undoable event listener
     */
    protected RateParameterController(RateParameter component, PetriNet petriNet, UndoableEditListener listener) {
        super(component, listener);
        this.petriNet = petriNet;
    }

    /**
     * Tries to set the functional expression of the rate
     *
     * @param expression new functional expression
     * @throws InvalidRateException if the funcitonal expression is invalid because either it
     *                              contains a syntax error or it references a component that does not exist
     */
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
