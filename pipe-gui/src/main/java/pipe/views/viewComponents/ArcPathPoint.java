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
import pipe.gui.ZoomController;
import pipe.historyActions.HistoryItem;
import pipe.models.component.ArcPoint;
import pipe.views.AbstractPetriNetViewComponent;
import pipe.views.PetriNetView;
import pipe.views.PetriNetViewComponent;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;


/**
 * This class represents each point on the arc path.
 */
public final class ArcPathPoint extends AbstractPetriNetViewComponent<ArcPoint> {

    public static final boolean STRAIGHT = false;
    public static final boolean CURVED = true;
    private static int SIZE = 3;
    private static final int SIZE_OFFSET = 1;

    private ArcPath arcPath;
    private final ArcPoint model;
//
//    private final Point2D.Double point = new Point2D.Double();
//    private final Point2D.Double realPoint = new Point2D.Double();

    private final Point2D.Double control1 = new Point2D.Double();
    private final Point2D.Double control = new Point2D.Double();

    private void setup() {
        _copyPasteable = false; //we can't copy & paste indivial arc points!
        _zoomPercentage = 100;
        ZoomController controller = petriNetController.getZoomController();
        this.addZoomController(controller);
    }


    public ArcPathPoint(ArcPath a) {
        setup();
        model = null;
        arcPath = a;
        setPointLocation(0, 0);
        ZoomController zoomController = petriNetController.getZoomController();
        addZoomController(zoomController);

    }


    public ArcPathPoint(ArcPoint point, ArcPath arcPath, PetriNetController petriNetController) {
        super("", "", 0, 0, point, petriNetController);
        setup();
        model = point;
        setPointLocation(model.getPoint());
        this.arcPath = arcPath;
        ZoomController zoomController = petriNetController.getZoomController();
        addZoomController(zoomController);

        model.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                if (propertyChangeEvent.getPropertyName().equals("updateLocation")) {
                    Point2D point = (Point2D) propertyChangeEvent.getNewValue();
                    setPointLocation(point.getX(), point.getY());
                }
            }
        });

    }

    @Override
    public ArcPoint getModel() {
        return model;
    }

    public Point2D getPoint() {
        return model.getPoint();
    }


    public void setPointLocation(Point2D point) {
        setPointLocation(point.getX(), point.getY());
    }
    public void setPointLocation(double x, double y) {
//        double realX = ZoomController.getUnzoomedValue(x, _zoomPercentage);
//        double realY = ZoomController.getUnzoomedValue(y, _zoomPercentage);
//        realPoint.setLocation(realX, realY);
//        model.getPoint().setLocation(x, y);
        setBounds((int) x - SIZE, (int) y - SIZE, 2 * SIZE + SIZE_OFFSET, 2 * SIZE + SIZE_OFFSET);
    }


    public boolean isCurved() {
        return model.isCurved();
    }


    public void updatePointLocation() {
//        setPointLocation(point.x, point.y);
    }


    public void setPointType(boolean type) {
//        if (pointType != type) {
//            pointType = type;
//            arcPath.createPath();
//            arcPath.getArc().updateArcPosition();
//        }
    }


    public HistoryItem togglePointType() {
//        pointType = !pointType;
//        arcPath.createPath();
//        arcPath.getArc().updateArcPosition();
//        return new ArcPathPointType(this);
        return null;
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


    public int getIndex() {
        for (int i = 0; i < arcPath.getNumPoints(); i++) {
            if (arcPath.getPathPoint(i) == this) {
                return i;
            }
        }
        return -1;
    }


    /**
     * splitPoint()
     * This method is called when the user selects the popup menu option
     * Split Point on an Arc Point.
     * The method determines the index of the selected point in the listarray of
     * ArcPathPoints that an arcpath has. Then then a new point is created BEFORE
     * this one in the list and offset by a small delta in the x direction.
     *
     * @return
     */
    //TODO: IMPLEMENT
    public HistoryItem splitPoint() {
//        int i = getIndex(); // Get the index of this point
//
//        int DELTA = 10;
//        ArcPathPoint newPoint = new ArcPathPoint(point.x + DELTA, point.y, pointType, arcPath);
//        arcPath.insertPoint(i + 1, newPoint);
//        arcPath.getArc().updateArcPosition();
//        return new AddArcPathPoint(arcPath.getArc(), newPoint);
        return null;
    }


    public Point2D.Double getMidPoint(ArcPathPoint target) {
        return new Point2D.Double((target.model.getPoint().getX() + model.getPoint().getX()) / 2, (target.model.getPoint().getY() + model.getPoint().getY()) / 2);
    }


    public boolean isDeleteable() {
        int i = getIndex();
        return (i > 0 && i != arcPath.getNumPoints() - 1);
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


    public void kill() {        // delete without the safety check :)
        super.removeFromContainer(); // called internally by ArcPoint and parent ArcPath
//        arcPath.deletePoint(this);
        super.delete();
    }


    public Point2D.Double getControl1() {
        return control1;
    }


    public Point2D.Double getControl() {
        return control;
    }


    public void setControl1(double _x, double _y) {
        control1.x = _x;
        control1.y = _y;
    }


    public void setControl2(double _x, double _y) {
        control.x = _x;
        control.y = _y;
    }


    public void setControl1(Point2D.Double p) {
        control1.x = p.x;
        control1.y = p.y;
    }


    public void setControl(Point2D.Double p) {
        control.x = p.x;
        control.y = p.y;
    }


    public ArcPath getArcPath() {
        return arcPath;
    }


    @Override
    public void addedToGui() {
    }


    void hidePoint() {
        super.removeFromContainer();
    }


    @Override
    public AbstractPetriNetViewComponent<?> paste(double despX, double despY, boolean toAnotherView, PetriNetView model) {
        return null;
    }


    @Override
    public PetriNetViewComponent copy() {
        return null;
    }


    @Override
    public int getLayerOffset() {
        return Constants.ARC_POINT_LAYER_OFFSET;
    }

    @Override
    public void addToPetriNetTab(PetriNetTab tab) {

    }


    @Override
    public void translate(int x, int y) {
//        this.setPointLocation(point.x + x, point.y + y);
//        arcPath.updateArc();
    }


    public void undelete(PetriNetView model, PetriNetTab view) {
    }


    @Override
    public String getName() {
        return this.getArcPath().getArc().getName() + " - Point " + this.getIndex();
    }


    @Override
    public void zoomUpdate(int zoom) {
//        this._zoomPercentage = zoom;
//        // change ArcPathPoint's size a little bit when it's zoomed in or zoomed out
//        if (zoom > 213) {
//            SIZE = 5;
//        } else if (zoom > 126) {
//            SIZE = 4;
//        } else {
//            SIZE = 3;
//        }
//        double x = ZoomController.getZoomedValue(realPoint.x, zoom);
//        double y = ZoomController.getZoomedValue(realPoint.y, zoom);
//        point.setLocation(x, y);
//        setBounds((int) x - SIZE, (int) y - SIZE, 2 * SIZE + SIZE_OFFSET, 2 * SIZE + SIZE_OFFSET);
    }



    //TODO: WORK OUT HOW TO SELECT THESE?
    @Override
    public boolean isSelected() {
        return false;
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
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + model.hashCode();
        return result;
    }
}
