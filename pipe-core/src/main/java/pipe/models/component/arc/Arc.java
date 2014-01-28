package pipe.models.component.arc;

import pipe.models.component.AbstractPetriNetComponent;
import pipe.models.component.Connectable;
import pipe.models.component.token.Token;
import pipe.visitor.foo.PetriNetComponentVisitor;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Arc<S extends Connectable, T extends Connectable> extends AbstractPetriNetComponent {

    /**
     * Message fired when the arc source is changed
     */
    public static final String SOURCE_CHANGE_MESSAGE = "source";

    /**
     * Message fired when the arc target is changed
     */
    public static final String TARGET_CHANGE_MESSAGE = "target";

    /**
     * Message fired when the arc weight is changed
     */
    public static final String WEIGHT_CHANGE_MESSAGE = "weight";

    /**
     * Message fired when an intermediate point is deleted
     */
    public static final String DELETE_INTERMEDIATE_POINT_CHANGE_MESSAGE = "deleteIntermediatePoint";

    /**
     * Message fired when an intermediate point is created
     */
    public static final String NEW_INTERMEDIATE_POINT_CHANGE_MESSAGE = "newIntermediatePoint";

    private S source;

    private T target;

    private String id;

    private boolean tagged;

    /**
     * Map of Token to corresponding weights
     * Weights can be functional e.g '> 5'
     */
    private Map<Token, String> tokenWeights = new HashMap<Token, String>();

    private final ArcType type;

    /**
     * Intermediate path intermediatePoints
     */
    private final List<ArcPoint> intermediatePoints = new LinkedList<ArcPoint>();

    public Arc(S source, T target, Map<Token, String> tokenWeights, ArcType type) {
        this.source = source;
        this.target = target;
        this.tokenWeights = tokenWeights;
        this.type = type;

        this.id = source.getId() + " TO " + target.getId();


        //        source.addOutbound(this);
        //        target.addInbound(this);
        tagged = false;
    }

    public Map<Token, String> getTokenWeights() {
        return tokenWeights;
    }

    public S getSource() {
        return source;
    }

    public void setSource(S source) {
        S old = this.source;
        this.source = source;
        changeSupport.firePropertyChange(SOURCE_CHANGE_MESSAGE, old, source);
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        T old = this.target;
        this.target = target;
        //        target.addInbound(this);
        changeSupport.firePropertyChange(TARGET_CHANGE_MESSAGE, old, target);
    }

    /**
     * @return The start coordinate of the arc
     */
    public Point2D.Double getStartPoint() {
        //        double angle = getAngleBetweenSourceAndTarget();
        //        return source.getArcEdgePoint(angle);
        return source.getCentre();
    }

    /**
     * @return true - Arcs are always selectable
     */
    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public boolean isDraggable() {
        return true;
    }

    @Override
    public void accept(PetriNetComponentVisitor visitor) {
        if (visitor instanceof ArcVisitor) {
            ((ArcVisitor) visitor).visit(this);
        }
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        String old = this.id;
        this.id = id;
        changeSupport.firePropertyChange(ID_CHANGE_MESSAGE, old, old);
    }

    @Override
    public void setName(String name) {
        setId(name);
    }

    public boolean isTagged() {
        return tagged;
    }

    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    public String getWeightForToken(Token token) {
        if (tokenWeights.containsKey(token)) {
            return tokenWeights.get(token);
        } else {
            return "0";
        }
    }

    public void setWeight(Token defaultToken, String weight) {
        Map<Token, String> old = new HashMap<Token, String>(tokenWeights);
        tokenWeights.put(defaultToken, weight);
        changeSupport.firePropertyChange(WEIGHT_CHANGE_MESSAGE, old, tokenWeights);
    }

    /**
     * @return true if any of the weights are functional
     */
    public boolean hasFunctionalWeight() {
        for (String weight : tokenWeights.values()) {

            try {
                Integer.parseInt(weight);
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return false;
    }

    public ArcType getType() {
        return type;

    }

    public void addIntermediatePoints(List<ArcPoint> points) {
        for (ArcPoint point : points) {
            addIntermediatePoint(point);
        }
    }

    public void addIntermediatePoint(ArcPoint point) {

        intermediatePoints.add(point);
        changeSupport.firePropertyChange(NEW_INTERMEDIATE_POINT_CHANGE_MESSAGE, null, point);
    }

    public List<ArcPoint> getIntermediatePoints() {
        return intermediatePoints;
    }

    public void removeIntermediatePoint(ArcPoint point) {
        intermediatePoints.remove(point);
        changeSupport.firePropertyChange(DELETE_INTERMEDIATE_POINT_CHANGE_MESSAGE, point, null);
    }

    public ArcPoint getNextPoint(ArcPoint arcPoint) {
        if (arcPoint.getPoint().equals(source.getCentre())) {
            if (intermediatePoints.isEmpty()) {
                return new ArcPoint(getEndPoint(), false);
            }
            return intermediatePoints.get(0);
        }
        int location = intermediatePoints.indexOf(arcPoint);
        if (location == intermediatePoints.size() - 1 && !intermediatePoints.isEmpty()) {
            return new ArcPoint(getEndPoint(), false);
        }
        if (location == -1 || location + 1 > intermediatePoints.size()) {

            throw new RuntimeException("No next point");
        }
        return intermediatePoints.get(location + 1);
    }

    /**
     * @return The end coordinate of the arc
     */
    public Point2D.Double getEndPoint() {
        double angle = getAngleBetweenSourceAndTarget();
        return target.getArcEdgePoint(Math.PI + angle);
    }

    /**
     * @return angle in randians between source and target
     */
    private double getAngleBetweenSourceAndTarget() {
        double deltax = source.getX() - target.getX();
        double deltay = source.getY() - target.getY();
        return Math.atan2(deltax, deltay);
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + (tagged ? 1 : 0);
        result = 31 * result + tokenWeights.hashCode();
        result = 31 * result + intermediatePoints.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Arc arc = (Arc) o;

        if (tagged != arc.tagged) {
            return false;
        }
        if (!id.equals(arc.id)) {
            return false;
        }
        if (!intermediatePoints.equals(arc.intermediatePoints)) {
            return false;
        }
        if (!source.equals(arc.source)) {
            return false;
        }
        if (!target.equals(arc.target)) {
            return false;
        }
        //TODO:
        //        if (!tokenWeights.equals(arc.tokenWeights)) {
        //            return false;
        //        }

        return true;
    }

}
