package pipe.models;

import pipe.gui.ZoomController;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Place extends Connectable implements PetriNetComponent, Serializable
{
    /**
     * Place name x offset relative to its x coordinate
     */
    double nameXOffset = 0;
    /**
     * Place name y offset relative to its y coordinate
     */
    double nameYOffset = 0;
    /**
     * Marking x offset relative to the place x coordinate
     */
    double markingXOffset = 0;

    /**
     * Marking y offset relative to the place y coordinate
     */
    double markingYOffset = 0;
    /**
     * Place capacity
     */
    double capacity = 0;

    /**
     * Place diameter
     */
    private final static int DIAMETER = 30;

    /**
     * Place tokens
     */
    List<Marking> tokens =  new LinkedList<Marking>();

    public Place(String id, String name)
    {
        super(id, name);
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
    public double getCentreX() {
        return getX() + getWidth()/2;
    }

    @Override
    public double getCentreY() {
        return getY() + getHeight()/2;
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

    public double getNameXOffset() {
        return nameXOffset;
    }

    public void setNameXOffset(double nameXOffset) {
        this.nameXOffset = nameXOffset;
    }

    public double getNameYOffset() {
        return nameYOffset;
    }

    public void setNameYOffset(double nameYOffset) {
        this.nameYOffset = nameYOffset;
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

    public double getCapacity() {
        return capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public List<Marking> getTokens() {
        return tokens;
    }

    public void addTokens(List<Marking> markings) {
        this.tokens.addAll(markings);
    }

    public void addToken(Marking marking) {
        this.tokens.add(marking);
    }

    public void removeToken(Marking marking) {
        this.tokens.remove(marking);
    }

    public void removeTokens(List<Marking> markings) {
        this.tokens.remove(markings);
    }
}
