package pipe.models.component;

import pipe.visitor.connectable.ConnectableVisitor;
import pipe.visitor.PetriNetComponentVisitor;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * This class maps to the Place in PNML
 */
public class Place extends Connectable implements Serializable
{
    /**
     * Marking x offset relative to the place x coordinate
     */
    @Pnml("markingOffsetX")
    double markingXOffset = 0;

    /**
     * Marking y offset relative to the place y coordinate
     */
    @Pnml("markingOffsetY")
    double markingYOffset = 0;

    /**
     * Place capacity
     */
    @Pnml("capacity")
    int capacity = 0;

    /**
     * Place diameter
     */
    private final static int DIAMETER = 30;

    /**
     * Place tokens
     */
    @Pnml("initialMarking")
    Map<Token, Integer> tokenCounts = new HashMap<Token, Integer>();

    public Place(String id, String name)
    {
        super(id, name);
    }

    public Place(Place place) {
        super(place);
        this.capacity = place.capacity;
        this.markingXOffset = place.markingXOffset;
        this.markingYOffset = place.markingYOffset;
    }

    @Override
    public int getHeight() {
        return DIAMETER;
    }

    @Override
    public int getWidth() {
        return DIAMETER;
    }

    @Override
    public Point2D.Double getCentre() {
        return new Point2D.Double(getX() + getWidth()/2, getY() + getHeight()/2);
    }

    /**
     * Since Place is a circle, performs basic trigonometry
     * based on the angle that the other object is from
     *
     * Note (0,0) is top left corner of grid.  -------> x
     *                                        |
     *                                        |
     *                                        |
     *                                      y V
     * @return
     */
    @Override
    public Point2D.Double getArcEdgePoint(double angle) {
        double radius = getWidth() / 2;
        double centreX = x + radius;
        double opposite = Math.sin(angle);
        double attachX = centreX - radius * opposite;

        double centreY = y + radius;
        double adjacent = Math.cos(angle);
        double attachY = centreY - radius * adjacent;

        Point2D.Double coord = new Point2D.Double(attachX, attachY);
        return coord;
    }

    @Override
    public boolean isEndPoint() {
        return true;
    }

    /**
     *
     * @return true - Place objects are always selectable
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
        visitor.visit(this);
    }

    public double getMarkingXOffset() {
        return markingXOffset;
    }

    public void setMarkingXOffset(double markingXOffset) {
        this.markingXOffset = markingXOffset;
    }

    public double getMarkingYOffset() {
        return markingYOffset;
    }

    public void setMarkingYOffset(double markingYOffset) {
        this.markingYOffset = markingYOffset;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public Map<Token, Integer> getTokenCounts() {
        return tokenCounts;
    }

    public void setTokenCounts(Map<Token, Integer> tokenCounts) {
        if (hasCapacityRestriction()) {
            int count = getNumberOfTokensStored(tokenCounts);
            if (count > capacity) {
                throw new RuntimeException("Count of tokens exceeds capacity!");
            }
        }
        Map<Token, Integer> old = new HashMap<Token, Integer>(this.tokenCounts);
        this.tokenCounts = tokenCounts;
        changeSupport.firePropertyChange("tokens", old, tokenCounts);
    }

    /**
     * Increments the token count of the given token
     * @param token
     */
    public void incrementTokenCount(Token token) {
        Integer count;
        if (tokenCounts.containsKey(token)) {
            count = tokenCounts.get(token);
            count++;
        } else
        {
            count = 1;
        }
        Map<Token, Integer> old = new HashMap<Token, Integer>(this.tokenCounts);
        setTokenCount(token, count);
        changeSupport.firePropertyChange("tokens", old, tokenCounts);
    }

    public int getTokenCount(Token token) {
        if (tokenCounts.containsKey(token)) {
            return tokenCounts.get(token);
        }
        return 0;
    }

    public void decrementTokenCount(Token token) {
        Map<Token, Integer> old = new HashMap<Token, Integer>(this.tokenCounts);
        Integer count;
        if (tokenCounts.containsKey(token)) {
            count = tokenCounts.get(token);
            count--;
            tokenCounts.put(token, count);
        }
        changeSupport.firePropertyChange("tokens", old, tokenCounts);
    }

    public void setTokenCount(Token token, int count) {
        if (hasCapacityRestriction()) {
            int currentTokenCount = getNumberOfTokensStored();
            int countMinusToken = currentTokenCount - getTokenCount(token);
            if (countMinusToken + count > capacity) {
                throw new RuntimeException("Cannot set token count that exceeds " +
                        "the capacity of " + count);
            }
        }
        Map<Token, Integer> old = new HashMap<Token, Integer>(this.tokenCounts);
        tokenCounts.put(token, count);
        changeSupport.firePropertyChange("tokens", old, tokenCounts);
    }

    /**
     *
     * @return the number of tokens currently stored in this place
     */
    public int getNumberOfTokensStored() {
        return getNumberOfTokensStored(tokenCounts);
    }

    private int getNumberOfTokensStored(Map<Token, Integer> tokens) {
        int sum = 0;
        for (Integer value : tokens.values()) {
            sum += value;
        }
        return sum;
    }

    public boolean hasCapacityRestriction() {
        return capacity > 0;
    }


    @Override
    public void accept(final ConnectableVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Place place = (Place) o;

        if (!super.equals(place)){
            return false;
        }

        if (Double.compare(place.capacity, capacity) != 0) {
            return false;
        }
        if (Double.compare(place.markingXOffset, markingXOffset) != 0) {
            return false;
        }
        if (Double.compare(place.markingYOffset, markingYOffset) != 0) {
            return false;
        }
        //TODO:
//        if (!tokenCounts.equals(place.tokenCounts)) {
//            return false;
//        }

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(markingXOffset);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(markingYOffset);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(capacity);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + tokenCounts.hashCode();
        return result;
    }
}
