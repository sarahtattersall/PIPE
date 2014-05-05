package pipe.models.component.arc;

import pipe.models.component.AbstractPetriNetComponent;
import pipe.models.component.Connectable;
import pipe.models.component.token.Token;
import pipe.models.petrinet.PetriNet;

import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public abstract class Arc<S extends Connectable, T extends Connectable> extends AbstractPetriNetComponent {

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
    protected Map<Token, String> tokenWeights = new HashMap<>();

    private final ArcType type;

    /**
     * Intermediate path arcPoints
     */
    private final List<ArcPoint> arcPoints = new LinkedList<>();

    public Arc(S source, T target, Map<Token, String> tokenWeights, ArcType type) {
        this.source = source;
        this.target = target;
        this.tokenWeights = tokenWeights;
        this.type = type;

        this.id = source.getId() + " TO " + target.getId();
        tagged = false;


        final ArcPoint sourcePoint = new ArcPoint(getStartPoint(), false, false);
        final ArcPoint endPoint = new ArcPoint(getEndPoint(), false, false);
        arcPoints.add(sourcePoint);
        arcPoints.add(endPoint);

        source.addPropertyChangeListener(new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals(Connectable.X_CHANGE_MESSAGE) || name.equals(Connectable.Y_CHANGE_MESSAGE)) {
                    sourcePoint.setPoint(getStartPoint());
                    endPoint.setPoint(getEndPoint());
                }
            }
        });
        target.addPropertyChangeListener(new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String name = evt.getPropertyName();
                if (name.equals(Connectable.X_CHANGE_MESSAGE) || name.equals(Connectable.Y_CHANGE_MESSAGE)) {
                    sourcePoint.setPoint(getStartPoint());
                    endPoint.setPoint(getEndPoint());
                }
            }
        });


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
        changeSupport.firePropertyChange(TARGET_CHANGE_MESSAGE, old, target);
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
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        String old = this.id;
        this.id = id;
        changeSupport.firePropertyChange(ID_CHANGE_MESSAGE, old, id);
    }

    @Override
    public void setName(String name) {
        setId(name);
    }

    //TODO: Not sure if arcs should have names
    public String getName() {
        return id;
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
        Map<Token, String> old = new HashMap<>(tokenWeights);
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

    public void addIntermediatePoints(Iterable<ArcPoint> points) {
        for (ArcPoint point : points) {
            addIntermediatePoint(point);
        }
    }

    public void addIntermediatePoint(ArcPoint point) {
        int penultimateIndex = arcPoints.size() - 1;
        arcPoints.add(penultimateIndex, point);
        changeSupport.firePropertyChange(NEW_INTERMEDIATE_POINT_CHANGE_MESSAGE, null, point);
    }

    public List<ArcPoint> getArcPoints() {
        return arcPoints;
    }

    public void removeIntermediatePoint(ArcPoint point) {
        arcPoints.remove(point);
        changeSupport.firePropertyChange(DELETE_INTERMEDIATE_POINT_CHANGE_MESSAGE, point, null);
    }

    public ArcPoint getNextPoint(ArcPoint arcPoint) {
        int index = arcPoints.indexOf(arcPoint);
        if (index == arcPoints.size() - 1 || index < 0) {
            throw new RuntimeException("No next point");
        }

        return arcPoints.get(index + 1);
    }


    /**
     * @return The start coordinate of the arc
     */
    public Point2D.Double getStartPoint() {
        double angle;
        if (arcPoints.size() > 1) {
            angle = getAngleBetweenTwoPoints(arcPoints.get(1).getPoint(), source.getCentre());
        } else {
            angle = getAngleBetweenTwoPoints(target.getCentre(), source.getCentre());
        }
        return source.getArcEdgePoint(angle);
    }

    /**
     * @return The end coordinate of the arc
     */
    public Point2D getEndPoint() {
        return target.getArcEdgePoint(getEndAngle());
    }

    public double getEndAngle() {
        if (arcPoints.size() > 1) {
            return getAngleBetweenTwoPoints(arcPoints.get(arcPoints.size() - 2).getPoint(), target.getCentre());
        } else {
            return getAngleBetweenTwoPoints(source.getCentre(), target.getCentre());
        }
    }

    /**
     * @return angle in radians between first and second
     */
    private double getAngleBetweenTwoPoints(Point2D first, Point2D second) {
        double deltax = second.getX() - first.getX();
        double deltay = second.getY() - first.getY();
        return Math.atan2(deltay, deltax);
    }

    @Override
    public int hashCode() {
        int result = source.hashCode();
        result = 31 * result + target.hashCode();
        result = 31 * result + id.hashCode();
        result = 31 * result + (tagged ? 1 : 0);
        result = 31 * result + tokenWeights.hashCode();
        result = 31 * result + arcPoints.hashCode();
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
        if (!arcPoints.equals(arc.arcPoints)) {
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

    /**
     *
     * @param petriNet
     * @param state
     * @return true if given the current state the arc can fire
     */
    //TODO: Dont pass in Petri net, get around this with better design
    public abstract boolean canFire(PetriNet petriNet, Map<String, Map<String, Integer>> state);

}
