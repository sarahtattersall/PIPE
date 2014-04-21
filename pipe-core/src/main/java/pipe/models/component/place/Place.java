package pipe.models.component.place;

import pipe.models.component.Connectable;
import pipe.models.component.token.Token;
import pipe.visitor.component.PetriNetComponentVisitor;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;

/**
 * This class maps to the Place in PNML
 */
public class Place extends Connectable {
    /**
     * Message fired when the places tokens change in any way
     */
    public final static String TOKEN_CHANGE_MESSAGE = "tokens";

    /**
     * Place diameter
     */
    private final static int DIAMETER = 30;

    /**
     * Marking x offset relative to the place x coordinate
     */
    private double markingXOffset = 0;

    /**
     * Marking y offset relative to the place y coordinate
     */
    private double markingYOffset = 0;

    /**
     * Place capacity
     */
    private int capacity = 0;

    /**
     * Place tokens
     */
    private Map<Token, Integer> tokenCounts = new HashMap<>();

    public Place(String id, String name) {
        super(id, name);
    }

    public Place(Place place) {
        super(place);
        this.capacity = place.capacity;
        this.markingXOffset = place.markingXOffset;
        this.markingYOffset = place.markingYOffset;
    }

    /**
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
        if (visitor instanceof PlaceVisitor) {
            ((PlaceVisitor) visitor).visit(this);
        }
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
        Map<Token, Integer> old = new HashMap<>(this.tokenCounts);
        this.tokenCounts = tokenCounts;
        changeSupport.firePropertyChange(TOKEN_CHANGE_MESSAGE, old, tokenCounts);
    }

    public boolean hasCapacityRestriction() {
        return capacity > 0;
    }

    /**
     * @param tokens map of tokens to their counts
     * @return total number of tokens stored in the map
     */
    private int getNumberOfTokensStored(Map<Token, Integer> tokens) {
        int sum = 0;
        for (Integer value : tokens.values()) {
            sum += value;
        }
        return sum;
    }

    /**
     * Increments the token count of the given token
     *
     * @param token
     */
    public void incrementTokenCount(Token token) {
        Integer count;
        if (tokenCounts.containsKey(token)) {
            count = tokenCounts.get(token);
            count++;
        } else {
            count = 1;
        }
        Map<Token, Integer> old = new HashMap<>(this.tokenCounts);
        setTokenCount(token, count);
        changeSupport.firePropertyChange(TOKEN_CHANGE_MESSAGE, old, tokenCounts);
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
        Map<Token, Integer> old = new HashMap<>(this.tokenCounts);
        tokenCounts.put(token, count);
        changeSupport.firePropertyChange(TOKEN_CHANGE_MESSAGE, old, tokenCounts);
    }

    /**
     * @return the number of tokens currently stored in this place
     */
    public int getNumberOfTokensStored() {
        return getNumberOfTokensStored(tokenCounts);
    }

    public int getTokenCount(Token token) {
        if (tokenCounts.containsKey(token)) {
            return tokenCounts.get(token);
        }
        return 0;
    }

    /**
     * A less efficient way to get the count for a token. Use in cases where token is not avaiable
     *
     * @param tokenName
     * @return token count for the colour token
     */
    //TODO: Make this O(n). Maybe change the hashmap to store name rather than token?
    public int getTokenCount(String tokenName) {
        for (Map.Entry<Token, Integer> entry : tokenCounts.entrySet()) {
            if (entry.getKey().getId().equals(tokenName)) {
                return entry.getValue();
            }
        }
        return 0;
    }

    public void decrementTokenCount(Token token) {
        Map<Token, Integer> old = new HashMap<>(this.tokenCounts);
        Integer count;
        if (tokenCounts.containsKey(token)) {
            count = tokenCounts.get(token);
            count--;
            tokenCounts.put(token, count);
        }
        changeSupport.firePropertyChange(TOKEN_CHANGE_MESSAGE, old, tokenCounts);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Place place = (Place) o;

        if (!super.equals(place)) {
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

        if (!tokenCounts.equals(place.tokenCounts)) {
            return false;
        }

        return true;
    }

    @Override
    public Point2D.Double getCentre() {
        return new Point2D.Double(getX() + getWidth() / 2, getY() + getHeight() / 2);
    }

    @Override
    public int getHeight() {
        return DIAMETER;
    }

    @Override
    public int getWidth() {
        return DIAMETER;
    }

    /**
     * Since Place is a circle, performs basic trigonometry
     * based on the angle that the other object is from
     * <p/>
     * Note (0,0) is top left corner of grid.  -------> x
     * |
     * |
     * |
     * y V
     *
     * @return
     */
    @Override
    public Point2D.Double getArcEdgePoint(double angle) {
        double radius = DIAMETER / 2;
        double centreX = x + radius;
        double opposite = Math.cos(angle);
        double attachX = centreX - radius * opposite;

        double centreY = y + radius;
        double adjacent = Math.sin(angle);
        double attachY = centreY - radius * adjacent;

        return new Point2D.Double(attachX, attachY);
    }

    @Override
    public boolean isEndPoint() {
        return true;
    }


}
