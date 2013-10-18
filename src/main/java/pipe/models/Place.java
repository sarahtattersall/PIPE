package pipe.models;

import pipe.gui.Grid;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Place extends Connectable implements PetriNetComponent, Serializable
{
    /**
     * Place position x
     */
    double x = 0;
    /**
     * Place position y
     */
    double y = 0;
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
     * Place markings
     */
    List<Marking> markings =  new LinkedList<Marking>();

    public Place(String id, String name)
    {
        super(id, name);
    }

    /**
     *
     * @return true - Place objects are always selectable
     */
    @Override
    public boolean isSelectable() {
        return true;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
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

    public List<Marking> getMarkings() {
        return markings;
    }

    public void addMarkings(List<Marking> markings) {
        this.markings.addAll(markings);
    }

    public void addMarking(Marking marking) {
        this.markings.add(marking);
    }

    public void removeMarking(Marking marking) {
        this.markings.remove(marking);
    }

    public void removeMarkings(List<Marking> markings) {
        this.markings.remove(markings);
    }
}
