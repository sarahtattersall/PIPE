package pipe.models.component;

import pipe.models.PetriNet;
import pipe.models.strategy.arc.ArcStrategy;
import pipe.models.visitor.PetriNetComponentVisitor;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Arc<S extends Connectable, T extends Connectable> extends AbstractPetriNetComponent {

    @Pnml("source")
    protected S source;

    @Pnml("target")
    protected T target;

    @Pnml("id")
    protected String id;

    protected boolean tagged = false;

    /**
     * Map of Token to corresponding weights
     * Weights can be functional e.g '> 5'
     */
    @Pnml("inscription")
    protected Map<Token, String> tokenWeights = new HashMap<Token, String>();

    private final ArcStrategy strategy;

    /**
     * Intermediate path points
     */
    @Pnml("arcpath")
    private List<ArcPoint> points = new LinkedList<ArcPoint>();

    public Arc(S source, T target,
               Map<Token, String> tokenWeights, ArcStrategy strategy) {
        this.source = source;
        this.target = target;
        this.tokenWeights = tokenWeights;
        this.strategy = strategy;

        this.id = source.getId() + " TO " + target.getId();


        source.addOutbound(this);
        target.addInbound(this);
    }

    public Map<Token, String> getTokenWeights() {
        return tokenWeights;
    }

    public S getSource() {
        return source;
    }

    public void setSource(S source) {
        this.source.removeOutboundArc(this);
        this.source = source;
        source.addOutbound(this);
        notifyObservers();
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target.removeInboundArc(this);
        this.target = target;
        target.addInbound(this);
        notifyObservers();
    }

    /**
     * @return angle in randians between source and target
     */
    private double getAngleBetweenSourceAndTarget() {
        double deltax = source.getX() - target.getX();
        double deltay = source.getY() - target.getY();
        return Math.atan2(deltax, deltay);
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
     * @return The end coordinate of the arc
     */
    public Point2D.Double getEndPoint() {
        double angle = getAngleBetweenSourceAndTarget();
        return target.getArcEdgePoint(Math.PI + angle);
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
    public void accept(final PetriNetComponentVisitor visitor) {
        visitor.visit(this);
    }

    public boolean isTagged() {
        return tagged;
    }

    public void setTagged(boolean tagged) {
        this.tagged = tagged;
        notifyObservers();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        notifyObservers();
    }

    public void setName(String name) {
        setId(name);
    }

    public String getWeightForToken(Token token) {
        if (tokenWeights.containsKey(token)) {
            return tokenWeights.get(token);
        }
        else {
            return "0";
        }
    }

    public void setWeight(Token defaultToken, String weight) {
        tokenWeights.put(defaultToken, weight);
        notifyObservers();
    }

    /**
     * @return true if any of the weights are functional
     */
    public boolean hasFunctionalWeight() {
        for (String weight : tokenWeights.values()) {

            try {
                Integer.parseInt(weight);
            } catch (Exception e) {
                return true;
            }
        }
        return false;
    }

    public boolean canFire() {
        return strategy.canFire(this);
    }

    public ArcType getType() {
        return strategy.getType();

    }


    public void addPoints(final List<ArcPoint> points) {
        this.points = points;
    }

    public List<ArcPoint> getPoints() {
        return points;
    }

    public void addPoint(ArcPoint point) {
        points.add(point);
    }
}
