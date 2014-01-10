package pipe.controllers;

import pipe.actions.SplitArcPointAction;
import pipe.historyActions.*;
import pipe.models.component.Arc;
import pipe.models.component.ArcPoint;
import pipe.models.component.Connectable;
import pipe.models.component.Token;

import java.awt.geom.Point2D;
import java.util.Map;

public class ArcController<S extends Connectable<T, S>, T extends Connectable<S, T>> extends AbstractPetriNetComponentController<Arc<S, T>>
{
    private final Arc<S, T> arc;
    private final HistoryManager historyManager;

    ArcController(Arc<S, T> arc, HistoryManager historyManager) {

        super(arc, historyManager);
        this.arc = arc;
        this.historyManager = historyManager;
    }

    /**
     * Sets the weight for the current arc
     * @param token
     * @param expr
     */
    public void setWeight(final Token token, final String expr) {
        historyManager.newEdit();
        updateWeightForArc(token, expr);
    }

    /**
     * Creates a historyItem for updating weight and applies it
     * @param token
     * @param expr
     */
    private void updateWeightForArc(final Token token,
                                    final String expr) {
        String oldWeight = arc.getWeightForToken(token);
        ArcWeight<S,T> weightAction = new ArcWeight<S,T>(arc, token, oldWeight, expr);
        weightAction.redo();
        historyManager.addEdit(weightAction);
    }

    public void setWeights(final Map<Token, String> newWeights) {
        historyManager.newEdit();
        for (Map.Entry<Token, String> entry : newWeights.entrySet()) {
            updateWeightForArc(entry.getKey(), entry.getValue());
        }
    }

    public String getWeightForToken(final Token token) {
        return arc.getWeightForToken(token);
    }

    public boolean hasFunctionalWeight() {
        return arc.hasFunctionalWeight();
    }

    public Connectable<S, T> getTarget() {
        return arc.getTarget();
    }

    public void toggleArcPointType(ArcPoint arcPoint) {
        HistoryItem historyItem = new ArcPathPointType(arcPoint);
        historyItem.redo();
        historyManager.addNewEdit(historyItem);
    }

    public void splitArcPoint(final ArcPoint arcPoint) {
        ArcPoint nextPoint = arc.getNextPoint(arcPoint);

        double x = (arcPoint.getPoint().getX() + nextPoint.getPoint().getX())/2;
        double y = (arcPoint.getPoint().getY() + nextPoint.getPoint().getY())/2;

        Point2D point = new Point2D.Double(x,y);
        ArcPoint newPoint = new ArcPoint(point, arcPoint.isCurved());
        HistoryItem historyItem = new AddArcPathPoint<S,T>(arc, newPoint);
        historyItem.redo();
        historyManager.addNewEdit(historyItem);
    }

    public void addPoint(final Point2D point) {
        ArcPoint newPoint = new ArcPoint(point, false);
        HistoryItem historyItem = new AddArcPathPoint<S,T>(arc, newPoint);
        historyItem.redo();
        historyManager.addNewEdit(historyItem);
    }
}
