package pipe.controllers;

import pipe.historyActions.*;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.Connectable;
import pipe.models.component.token.Token;
import pipe.parsers.FunctionalResults;
import pipe.parsers.UnparsableException;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public class ArcController<S extends Connectable, T extends Connectable> extends AbstractPetriNetComponentController<Arc<S, T>>
{
    private final Arc<S, T> arc;

    /**
     * PetriNetController in order to determine if arc expressions are valid
     */
    //TODO: I cant at the moment think of a better way to do this since the arc model
    //      does not know anything about the petri net in which it resides
    private final PetriNetController petriNetController;

    ArcController(Arc<S, T> arc, PetriNetController petriNetController) {

        super(arc);
        this.arc = arc;
        this.petriNetController = petriNetController;
    }

    /**
     * Sets the weight for the current arc
     * @param token
     * @param expr
     */
    public void setWeight(Token token, String expr) throws UnparsableException {
        throwExceptionIfWeightNotValid(expr);
//        historyManager.newEdit();
        updateWeightForArc(token, expr);
    }

    /**
     *
     * @param expr weight expression
     * @throws UnparsableException if the weight could not be parsed or is not an integer
     */
    private void throwExceptionIfWeightNotValid(String expr) throws UnparsableException {
        FunctionalResults<Double> result = petriNetController.parseFunctionalExpression(expr);
        if (result.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            for (String error : result.getErrors()) {
                errorMessage.append(error).append("\n");
            }
            throw new UnparsableException(errorMessage.toString());
        } else if (valueIsNotInteger(result.getResult())) {
            throw new UnparsableException("Value is not an integer, please surround expression with floor or ceil");
        }
    }

    /**
     *
     * @param value
     * @return true if the value was an integer
     */
    private boolean valueIsNotInteger(double value) {
        return value % 1 != 0;
    }

    private void throwExceptionIfWeightsNotValid(Map<Token, String> weights) throws UnparsableException {
        for (Map.Entry<Token, String> entry : weights.entrySet()) {
            throwExceptionIfWeightNotValid(entry.getValue());
        }
    }

    /**
     * Creates a historyItem for updating weight and applies it
     * @param token token to associate the expression with
     * @param expr new weight expression for the arc
     */
    private void updateWeightForArc(Token token,
                                    String expr) {
        String oldWeight = arc.getWeightForToken(token);
        arc.setWeight(token, expr);

        SetArcWeightAction<S,T> weightAction = new SetArcWeightAction<>(arc, token, oldWeight, expr);
//        historyManager.addEdit(weightAction);
    }

    public void setWeights(Map<Token, String> newWeights) throws UnparsableException {
        throwExceptionIfWeightsNotValid(newWeights);

//        historyManager.newEdit();
        for (Map.Entry<Token, String> entry : newWeights.entrySet()) {
            updateWeightForArc(entry.getKey(), entry.getValue());
        }
    }

    public String getWeightForToken(Token token) {
        return arc.getWeightForToken(token);
    }

    public Connectable getTarget() {
        return arc.getTarget();
    }

    public void toggleArcPointType(ArcPoint arcPoint) {
        HistoryItem historyItem = new ArcPathPointType(arcPoint);
        historyItem.redo();
//        historyManager.addNewEdit(historyItem);
    }

    public void splitArcPoint(ArcPoint arcPoint) {
        ArcPoint nextPoint = arc.getNextPoint(arcPoint);

        double x = (arcPoint.getPoint().getX() + nextPoint.getPoint().getX())/2;
        double y = (arcPoint.getPoint().getY() + nextPoint.getPoint().getY())/2;

        Point2D point = new Point2D.Double(x,y);
        ArcPoint newPoint = new ArcPoint(point, arcPoint.isCurved());
        HistoryItem historyItem = new AddArcPathPoint<>(arc, newPoint);
        historyItem.redo();
//        historyManager.addNewEdit(historyItem);
    }

    public void addPoint(Point2D point) {
        ArcPoint newPoint = new ArcPoint(point, false);
        HistoryItem historyItem = new AddArcPathPoint<>(arc, newPoint);
        historyItem.redo();
//        historyManager.addNewEdit(historyItem);
    }

    public void deletePoint(ArcPoint component) {
        HistoryItem historyItem = new DeleteArcPathPoint<>(arc, component);
        historyItem.redo();
//        historyManager.addNewEdit(historyItem);
    }
}
