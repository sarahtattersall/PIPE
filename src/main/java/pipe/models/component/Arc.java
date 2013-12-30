package pipe.models.component;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

public abstract class Arc extends AbstractPetriNetComponent {

    @Pnml("source")
    private Connectable source;

    @Pnml("target")
    private Connectable target;

    @Pnml("id")
    private String id;

    private boolean tagged = false;

    /**
     * Map of Token to corresponding weights
     * Weights can be functional e.g '> 5'
     */
    @Pnml("inscription")
    private Map<Token, String> tokenWeights = new HashMap<Token, String>();

    public Arc(Connectable source, Connectable target,
               Map<Token, String> tokenWeights) {
        this.source = source;
        this.target = target;
        this.tokenWeights = tokenWeights;


        source.addOutbound(this);
        target.addInbound(this);
    }

    public Map<Token, String> getTokenWeights() {
        return tokenWeights;
    }

    public Connectable getSource() {
        return source;
    }

    public void setSource(Connectable source) {
        this.source.removeOutboundArc(this);
        this.source = source;
        source.addOutbound(this);
        notifyObservers();
    }

    public Connectable getTarget() {
        return target;
    }

    public void setTarget(Connectable target) {
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
        double angle = getAngleBetweenSourceAndTarget();
        return source.getArcEdgePoint(angle);
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
            return "";
        }
    }

    public void setWeight(final Token defaultToken, final String weight) {
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
}
