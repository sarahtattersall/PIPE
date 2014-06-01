/*
 * Created on 28-Feb-2004
 * @author Michael Camacho (and whoever wrote the first bit!)
 * @author Edwin Chung 16 Mar 2007: modified the constructor and several other
 * functions so that DataLayer objects can be created outside the GUI
 */
package pipe.views.viewComponents;

import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.views.AbstractPetriNetViewComponent;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * This class represents each point on the arc path graphically.
 * It's old code so the Bezier maths etc. needs to be addressed
 */
public class ArcPathPoint extends AbstractPetriNetViewComponent<ArcPoint> {

    /**
     * Boolean value determining if the arc is straight or a curve
     */
    public static final boolean STRAIGHT = false;

    private static final int SIZE_OFFSET = 1;

    private static final int SIZE = 3;

    /**
     * Underlying point model
     */
    private final ArcPoint model;

    /**
     * Control used for Bezier curve
     */
    private final Point2D.Double control1 = new Point2D.Double();

    /**
     * Control used for Bezier curve
     */
    private final Point2D.Double control = new Point2D.Double();

    /**
     * Path point belongs to
     */
    private ArcPath arcPath;

    private void setup() {
        _copyPasteable = false; //we can't copy & paste indivial arc points!
    }

    public void setPointLocation(double x, double y) {
        setBounds((int) x - SIZE, (int) y - SIZE, 2 * SIZE + SIZE_OFFSET, 2 * SIZE + SIZE_OFFSET);
    }

    public ArcPathPoint(ArcPoint point, ArcPath arcPath, PetriNetController petriNetController) {
        super("", point, petriNetController);
        setup();
        model = point;
        setPointLocation(model.getPoint());
        this.arcPath = arcPath;
        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName().equals(ArcPoint.UPDATE_LOCATION_CHANGE_MESSAGE)) {
                    Point2D point = (Point2D) propertyChangeEvent.getNewValue();
                    setPointLocation(point.getX(), point.getY());
                }
            }
        });

    }

    public final void setPointLocation(Point2D point) {
        setPointLocation(point.getX(), point.getY());
    }

    public Point2D getPoint() {
        return model.getPoint();
    }

    public boolean isCurved() {
        return model.isCurved();
    }

    public void setVisibilityLock(boolean lock) {
        arcPath.setPointVisibilityLock(lock);
    }

    public double getAngle(Point2D.Double p2) {
        double angle;

        if (model.getPoint().getY() <= p2.y) {
            angle = Math.atan((model.getPoint().getX() - p2.x) / (p2.y - model.getPoint().getY()));
        } else {
            angle = Math.atan((model.getPoint().getX() - p2.x) / (p2.y - model.getPoint().getY())) + Math.PI;
        }

        // Needed to eliminate an exception on Windows
        if (model.getPoint().equals(p2)) {
            angle = 0;
        }
        return angle;
    }

    public Point2D.Double getMidPoint(ArcPathPoint target) {
        return new Point2D.Double((target.model.getPoint().getX() + model.getPoint().getX()) / 2,
                (target.model.getPoint().getY() + model.getPoint().getY()) / 2);
    }

    public Point2D.Double getControl1() {
        return control1;
    }

    public void setControl1(Point2D.Double p) {
        control1.x = p.x;
        control1.y = p.y;
    }

    public Point2D.Double getControl() {
        return control;
    }

    public void setControl(Point2D.Double p) {
        control.x = p.x;
        control.y = p.y;
    }

    public void setControl1(double _x, double _y) {
        control1.x = _x;
        control1.y = _y;
    }

    public void setControl2(double _x, double _y) {
        control.x = _x;
        control.y = _y;
    }

    void hidePoint() {
        super.removeFromContainer();
    }

    @Override
    public void addToPetriNetTab(PetriNetTab tab) {

    }

    @Override
    public void translate(int x, int y) {
        //        this.setPointLocation(point.x + x, point.y + y);
        //        arcPath.updateArc();
    }

    @Override
    public String getName() {
        return this.getArcPath().getArc().getName() + " - Point " + this.getIndex();
    }

    public int getIndex() {
        for (int i = 0; i < arcPath.getNumPoints(); i++) {
            if (arcPath.getPathPoint(i) == this) {
                return i;
            }
        }
        return -1;
    }

    public ArcPath getArcPath() {
        return arcPath;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + model.hashCode();
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
        if (!super.equals(o)) {
            return false;
        }

        ArcPathPoint pathPoint = (ArcPathPoint) o;

        if (!model.equals(pathPoint.model)) {
            return false;
        }

        return true;
    }

    @Override
    public ArcPoint getModel() {
        return model;
    }

    @Override
    public void delete() {// Won't delete if only two points left. General delete.
        if (isDeleteable()) {
            if (getArcPath().getArc().isSelected()) {
                return;
            }
            kill();
            arcPath.updateArc();
        }
    }

    @Override
    public void paintComponent(Graphics g) {

        super.paintComponent(g);
        if (!_ignoreSelection) {
            Graphics2D g2 = (Graphics2D) g;

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_NORMALIZE);

            RectangularShape shape;
            if (model.isCurved()) {
                shape = new Ellipse2D.Double(0, 0, 2 * SIZE, 2 * SIZE);
            } else {
                shape = new Rectangle2D.Double(0, 0, 2 * SIZE, 2 * SIZE);
            }

            if (isSelected()) {
                g2.setPaint(Constants.SELECTION_FILL_COLOUR);
                g2.fill(shape);
                g2.setPaint(Constants.SELECTION_LINE_COLOUR);
                g2.draw(shape);
            } else {
                g2.setPaint(Constants.ELEMENT_FILL_COLOUR);
                g2.fill(shape);
                g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
                g2.draw(shape);
            }
        }
    }

    @Override
    public int getLayerOffset() {
        return Constants.ARC_POINT_LAYER_OFFSET;
    }

    //TODO: WORK OUT HOW TO SELECT THESE?
    @Override
    public boolean isSelected() {
        return false;
    }

    public boolean isDeleteable() {
        int i = getIndex();
        return (i > 0 && i != arcPath.getNumPoints() - 1);
    }

    public void kill() {        // delete without the safety check :)
        super.removeFromContainer(); // called internally by ArcPoint and parent ArcPath
        //        arcPath.deletePoint(this);
        super.delete();
    }
}
