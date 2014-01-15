/**
 * Created on 12-Feb-2004
 */
package pipe.views.viewComponents;

import com.sun.org.apache.bcel.internal.generic.RETURN;
import pipe.controllers.ArcController;
import pipe.controllers.PetriNetController;
import pipe.gui.Constants;
import pipe.gui.PetriNetTab;
import pipe.gui.ZoomController;
import pipe.handlers.ArcPathPointHandler;
import pipe.historyActions.HistoryItem;
import pipe.models.component.*;
import pipe.visitor.connectable.ConnectableVisitor;
import pipe.utilities.math.Cubic;
import pipe.views.ArcView;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ArcPath implements Shape, Cloneable {

    private static final Stroke proximityStroke = new BasicStroke(Constants.ARC_PATH_PROXIMITY_WIDTH);
    private static final Stroke stroke = new BasicStroke(Constants.ARC_PATH_SELECTION_WIDTH);
    public final Point2D.Double midPoint = new Point2D.Double();

    /**
     * Use  this to only add enw arcpoints
     */
    private final Set<ArcPoint> points = new HashSet<ArcPoint>();
    private final List<ArcPathPoint> pathPoints = new ArrayList<ArcPathPoint>();


    private final ArcView<? extends Connectable, ? extends Connectable> parent;
    private final PetriNetController petriNetController;
    private GeneralPath path = new GeneralPath();
    private boolean pointLock = false;
    private Shape shape = stroke.createStrokedShape(this);
    private Shape proximityShape = proximityStroke.createStrokedShape(this);;
    private int _transitionAngle;


    public ArcPath(ArcView parent, PetriNetController petriNetController) {
        this.parent = parent;
        this.petriNetController = petriNetController;
        _transitionAngle = 0;
    }

    /**
     * Moves the path to the first point specified on the arc
     */
    private void setStartingPoint(ArcPathPoint point) {
        path.moveTo(point.getPoint().getX(), point.getPoint().getY());
    }

    /**
     * Creates a straight line
     *
     * @param point point to create line to
     */
    private void createStraightPoint(ArcPathPoint point) {
        path.lineTo(point.getPoint().getX(), point.getPoint().getY());
    }

    private void createCurvedPoint(ArcPathPoint point) {
        path.curveTo(point.getControl1().x, point.getControl1().y, point.getControl().x,
                point.getControl().y, point.getPoint().getX(), point.getPoint().getY());
    }

    /**
     * Sets the midpoint which is used to relocate the arcs label
     *
     * @param length path length
     */
    private void setMidPoint(double length) {
        ArcPathPoint currentPoint = pathPoints.get(0);
        if (getEndIndex() < 2) {
            midPoint.x = (pathPoints.get(0).getPoint().getX() +
                    pathPoints.get(1).getPoint().getX()) * 0.5;
            midPoint.y = (pathPoints.get(0).getPoint().getY() +
                    pathPoints.get(1).getPoint().getY()) * 0.5;
        } else {
            double acc = 0;
            double percent = 0;
            ArcPathPoint previousPoint = currentPoint;
            for (int point = 1; point < pathPoints.size(); point++) {
                previousPoint = currentPoint;
                currentPoint = pathPoints.get(point);

                double inc = getLength(currentPoint.getPoint(), previousPoint.getPoint());
                double halfLength = length / 2.0;
                if ((acc + inc > halfLength)) {
                    percent = (halfLength - acc) / inc;
                    break;
                }
                acc += inc;
            }

            midPoint.x = previousPoint.getPoint().getX() +
                    (float) ((currentPoint.getPoint().getX() - previousPoint.getPoint().getX()) * percent);
            midPoint.y = previousPoint.getPoint().getY() +
                    (float) ((currentPoint.getPoint().getY() - previousPoint.getPoint().getY()) * percent);
        }
    }

    public void createPath() {
        setControlPoints();

        path.reset();

        ArcPathPoint currentPoint = pathPoints.get(0);
        setStartingPoint(currentPoint);

        double length = 0;
        for (int point = 1; point <= getEndIndex(); point++) {
            ArcPathPoint previousPoint = currentPoint;
            currentPoint = pathPoints.get(point);

            if (!currentPoint.isCurved()) {
                createStraightPoint(currentPoint);
            } else if (currentPoint.isCurved()) {
                createCurvedPoint(currentPoint);
            }
            length += getLength(currentPoint.getPoint(), previousPoint.getPoint());
        }
        setMidPoint(length);
        shape = stroke.createStrokedShape(this);
        proximityShape = proximityStroke.createStrokedShape(this);
    }

    private void setControlPoints() {
        setCurveControlPoints(); //must be in this order
        setStraightControlPoints();
        setEndControlPoints();
    }

    /* returns a control point for curve CD with incoming vector AB*/
    private Point2D.Double getControlPoint(Point2D A, Point2D B, Point2D C, Point2D D) {
        Point2D.Double p = new Point2D.Double(0, 0);

        double modAB = getLength(A, B);
        double modCD = getLength(C, D);

        double ABx = (B.getX() - A.getX()) / modAB;
        double ABy = (B.getY() - A.getY()) / modAB;

        if (modAB < 7) {
            // hack, stops division by zero, modAB can only be this low if the
            // points are virtually superimposed anyway
            p = (Point2D.Double) C.clone();
        } else {
            p.x = C.getX() + (ABx * modCD / Constants.ARC_CONTROL_POINT_CONSTANT);
            p.y = C.getY() + (ABy * modCD / Constants.ARC_CONTROL_POINT_CONSTANT);
        }
        return p;
    }

    /**
     * @param A
     * @param B
     * @return modulus of vector A -> B
     */
    private double getLength(Point2D A, Point2D B) {
        double ABx = A.getX() - B.getX();
        double ABy = A.getY() - B.getY();

        return Math.sqrt(ABx * ABx + ABy * ABy);
    }

    /* function sets control points for any curved sections of the path */
    private void setCurveControlPoints() {
        if (pathPoints.isEmpty()) {
            return;
        }

        ArcPathPoint firstPoint = pathPoints.get(0);
        firstPoint.setPointType(ArcPathPoint.STRAIGHT);

        Cubic[] X, Y;

        for (int c = 1; c < pathPoints.size(); ) {
            int curveStartIndex;
            int curveEndIndex = 0;
            ArcPathPoint currentPoint = pathPoints.get(c);

            if (currentPoint.isCurved()) {
                curveStartIndex = c - 1;

                for (; c < pathPoints.size() && currentPoint.isCurved(); c++) {
                    currentPoint = pathPoints.get(c);
                    curveEndIndex = c;
                }

            /* calculate a cubic for each section of the curve */
                int lengthOfCurve = curveEndIndex - curveStartIndex;
                int k1;
                int x[] = new int[lengthOfCurve + 2];
                int y[] = new int[lengthOfCurve + 2];

                for (k1 = 0; k1 <= (curveEndIndex - curveStartIndex); k1++) {
                    x[k1] = (int) (pathPoints.get(curveStartIndex + k1)).getPoint().getX();
                    y[k1] = (int) (pathPoints.get(curveStartIndex + k1)).getPoint().getY();
                }
                x[k1] = x[k1 - 1];
                y[k1] = y[k1 - 1];

                X = calcNaturalCubic(k1, x);
                Y = calcNaturalCubic(k1, y);

                for (int k2 = 1; k2 <= lengthOfCurve; k2++) {
                    currentPoint = pathPoints.get(k2 + curveStartIndex);
                    currentPoint.setControl1(X[k2 - 1].getX1(), Y[k2 - 1].getX1());
                    currentPoint.setControl2(X[k2 - 1].getX2(), Y[k2 - 1].getX2());
                }
            } else {
                c++;
            }
        }
    }

    /* fuction sets the control points for any straight sections and for smooth
     * intersection between straight and curved sections */
    private void setStraightControlPoints() {

        ArcPathPoint myPreviousButOnePoint;

        for (int c = 1; c <= getEndIndex(); c++) {
            ArcPathPoint previousPoint = pathPoints.get(c - 1);
            ArcPathPoint currentPoint = pathPoints.get(c);

            if (!currentPoint.isCurved()) {
                currentPoint.setControl1(getControlPoint(previousPoint.getPoint(), currentPoint.getPoint(),
                        previousPoint.getPoint(), currentPoint.getPoint()));
                currentPoint.setControl(getControlPoint(currentPoint.getPoint(), previousPoint.getPoint(),
                        currentPoint.getPoint(), previousPoint.getPoint()));
            } else {
                if (c > 1 && !previousPoint.isCurved()) {
                    myPreviousButOnePoint = pathPoints.get(c - 2);
                    currentPoint.setControl1(
                            getControlPoint(myPreviousButOnePoint.getPoint(), previousPoint.getPoint(),
                                    previousPoint.getPoint(), currentPoint.getPoint()));
                }
                if (c < getEndIndex()) {
                    ArcPathPoint nextPoint = pathPoints.get(c + 1);
                    if (!nextPoint.isCurved()) {
                        currentPoint.setControl(getControlPoint(nextPoint.getPoint(), currentPoint.getPoint(),
                                currentPoint.getPoint(), previousPoint.getPoint()));
                    }
                }
            }
        }
    }

    private void setControlPoint(Place foo) {

    }

    private void setEndControlPoints() {
        ConnectableVisitor endPointVisitor = new ConnectableVisitor() {
            @Override
            public void visit(Place place) {
                if (pathPoints.get(getEndIndex()).isCurved()) {
                    double angle = Math.toRadians(_transitionAngle);
                    ArcPathPoint myPoint = pathPoints.get(getEndIndex());
                    ArcPathPoint myLastPoint = pathPoints.get(getEndIndex() - 1);
                    float distance =
                            (float) getLength(myPoint.getPoint(), myLastPoint.getPoint()) / Constants.ARC_CONTROL_POINT_CONSTANT;
                    myPoint.setControl2((float) (myPoint.getPoint().getX() + Math.cos(angle) * distance),
                            (float) (myPoint.getPoint().getY() + Math.sin(angle) * distance));

                    myPoint = pathPoints.get(1);
                    myPoint.setControl(
                            getControlPoint(pathPoints.get(0).getPoint(), myPoint.getControl(), pathPoints.get(0).getPoint(),
                                    myPoint.getControl()));
                }
            }

            @Override
            public void visit(Transition transition) {
                if (pathPoints.get(1).isCurved()) {
                    double angle = Math.toRadians(_transitionAngle);
                    ArcPathPoint myPoint = pathPoints.get(1);
                    ArcPathPoint myLastPoint = pathPoints.get(0);
                    float distance =
                            (float) getLength(myPoint.getPoint(), myLastPoint.getPoint()) / Constants.ARC_CONTROL_POINT_CONSTANT;
                    myPoint.setControl1((float) (myLastPoint.getPoint().getX() + Math.cos(angle) * distance),
                            (float) (myLastPoint.getPoint().getY() + Math.sin(angle) * distance));

                    myPoint = pathPoints.get(getEndIndex());
                    myPoint.setControl(getControlPoint(myPoint.getPoint(), myPoint.getControl1(), myPoint.getPoint(),
                            myPoint.getControl1()));
                }
            }

            @Override
            public void visit(TemporaryArcTarget arcTarget) {

            }

            @Override
            public void visit(ConditionalPlace conditionalPlace) {

            }
        };

        Connectable source = getArc().getModel().getSource();
        source.accept(endPointVisitor);
    }

    public void addPoint(ArcPoint arcPoint) {
        points.add(arcPoint);
        pathPoints.add(createPoint(arcPoint));
    }

    /**
     * Wont readd arcPoint if its already in the path
     * @param arcPoint
     * @param index
     */
    public void addPointAt(ArcPoint arcPoint, int index) {
        if (!points.contains(arcPoint)) {
            pathPoints.add(index, createPoint(arcPoint));
        }
    }

    private ArcPathPoint createPoint(ArcPoint point) {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {
                createPath();
                parent.updateBounds();
                parent.repaint();
            }
        };
        point.addPropertyChangeListener(listener);
        return new ArcPathPoint(point, this, petriNetController);

    }

    public boolean contains(ArcPoint point) {
        return points.contains(point);
    }

    public void addPoint() {
        pathPoints.add(new ArcPathPoint(this));
    }

    public void deletePoint(ArcPathPoint a) {
        pathPoints.remove(a);
    }

    public void updateArc() {
        parent.updateArcPosition();
    }

    public void setPointLocation(int index, Point2D point) {
        if (index < pathPoints.size() && index >= 0) {
            pathPoints.get(index).setPointLocation((float) point.getX(), (float) point.getY());
        }
    }

    public void setPointType(int index, boolean type) {
        pathPoints.get(index).setPointType(type);
    }

    public void setFinalPointType(boolean type) {
        pathPoints.get(getEndIndex()).setPointType(type);
    }

    public int getEndIndex() {
        return pathPoints.size() - 1;
    }

    public void togglePointType(int index) {
        pathPoints.get(index).togglePointType();
    }

    public boolean getPointType(int index) {
        return pathPoints.get(index).isCurved();
    }

    public int getNumPoints() {
        return pathPoints.size();
    }

    public Point2D getPoint(int index) {
        return pathPoints.get(index).getPoint();
    }

    public ArcPathPoint getPathPoint(int index) {
        return pathPoints.get(index);
    }

    public void showPoints() {
        if (!pointLock) {
            for (ArcPathPoint pathPoint : pathPoints) {
                pathPoint.setVisible(true);
            }
        }
    }

    //
    public void forceHidePoints() {
        for (ArcPathPoint pathPoint : pathPoints) {
            pathPoint.hidePoint();
        }
    }

    /* (non-Javadoc)
     * @see java.awt.Shape#intersects(java.awt.geom.Rectangle2D)
     */
    public void setPointVisibilityLock(boolean lock) {
        pointLock = lock;
    }


    public void hidePoints() {
        if (!pointLock) {
            for (ArcPathPoint pathPoint : pathPoints) {
                ArcPathPoint currentPoint = pathPoint;
                if (!currentPoint.isSelected()) {
                    currentPoint.setVisible(false);
                }
            }
        }
    }

    /* modified to use control points, ensures a curve hits a place tangetially */
    public double getEndAngle() {
        if (getEndIndex() > 0) {
            return pathPoints.get(getEndIndex()).getAngle(pathPoints.get(getEndIndex()).getControl());
        }
        return 0;
    }

    public double getStartAngle() {
        if (getEndIndex() > 0) {
            return pathPoints.get(0).getAngle(pathPoints.get(1).getControl());
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see java.awt.Shape#getBounds()
     */
    @Override
    public Rectangle getBounds() {
        return path.getBounds();
    }

    /* (non-Javadoc)
     * @see java.awt.Shape#getBounds2D()
     */
    @Override
    public Rectangle2D getBounds2D() {
        return null;
    }

    /* (non-Javadoc)
 * @see java.awt.Shape#contains(double, double)
 */
    @Override
    public boolean contains(double arg0, double arg1) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.Shape#contains(java.awt.geom.Point2D)
     */
    @Override
    public boolean contains(Point2D p) {
        return shape.contains(p);
    }

    /* (non-Javadoc)
     * @see java.awt.Shape#intersects(double, double, double, double)
     */
    @Override
    public boolean intersects(double arg0, double arg1, double arg2, double arg3) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.Shape#intersects(java.awt.geom.Rectangle2D)
     */
    @Override
    public boolean intersects(Rectangle2D r) {
        return shape.intersects(r);
    }

    /* (non-Javadoc)
     * @see java.awt.Shape#contains(double, double, double, double)
     */
    @Override
    public boolean contains(double arg0, double arg1, double arg2, double arg3) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.Shape#contains(java.awt.geom.Rectangle2D)
     */
    @Override
    public boolean contains(Rectangle2D arg0) {
        return false;
    }

    /* (non-Javadoc)
     * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform)
     */
    @Override
    public PathIterator getPathIterator(AffineTransform arg0) {
        return path.getPathIterator(arg0);
    }

    /* (non-Javadoc)
     * @see java.awt.Shape#getPathIterator(java.awt.geom.AffineTransform, double)
     */
    @Override
    public PathIterator getPathIterator(AffineTransform arg0, double arg1) {
        return path.getPathIterator(arg0, arg1);
    }

    /* (non-Javadoc)
     * @see java.awt.Shape#contains(java.awt.geom.Point2D)
     */
    public boolean proximityContains(Point2D p) {
        return proximityShape.contains(p);
    }

    /* (non-Javadoc)
     * @see java.awt.Shape#intersects(java.awt.geom.Rectangle2D)
     */
    public boolean proximityIntersects(Rectangle2D r) {
        return proximityShape.intersects(r);
    }

    private Cubic[] calcNaturalCubic(int n, int[] x) {
        float[] gamma = new float[n + 1];
        float[] delta = new float[n + 1];
        float[] D = new float[n + 1];

      /* We solve the equation
         [2 1       ] [D[0]]   [3(x[1] - x[0])  ]
         |1 4 1     | |D[1]|   |3(x[2] - x[0])  |
         |  1 4 1   | | .  | = |      .         |
         |    ..... | | .  |   |      .         |
         |     1 4 1| | .  |   |3(x[n] - x[n-2])|
         [       1 2] [D[n]]   [3(x[n] - x[n-1])]

         by using row operations to convert the matrix to upper triangular
         and then back sustitution.  The D[i] are the derivatives at the knots.
       */

        gamma[0] = 1.0f / 2.0f;
        for (int i = 1; i < n; i++) {
            gamma[i] = 1 / (4 - gamma[i - 1]);
        }
        gamma[n] = 1 / (2 - gamma[n - 1]);

        delta[0] = 3 * (x[1] - x[0]) * gamma[0];
        for (int i = 1; i < n; i++) {
            delta[i] = (3 * (x[i + 1] - x[i - 1]) - delta[i - 1]) * gamma[i];
        }
        delta[n] = (3 * (x[n] - x[n - 1]) - delta[n - 1]) * gamma[n];

        D[n] = delta[n];
        for (int i = n - 1; i >= 0; i--) {
            D[i] = delta[i] - gamma[i] * D[i + 1];
        }

      /* now compute the coefficients of the cubics */
        Cubic[] C = new Cubic[n];
        for (int i = 0; i < n; i++) {
            C[i] = new Cubic(x[i], D[i], 3 * (x[i + 1] - x[i]) - 2 * D[i] - D[i + 1],
                    2 * (x[i] - x[i + 1]) + D[i] + D[i + 1]);
        }
        return C;
    }

    public void delete() {  // Michael: Tells the arc points to remove themselves
        while (!pathPoints.isEmpty()) {
            pathPoints.get(0).kill(); // force delete of ALL points
        }
    }

    public String[][] getArcPathDetails() {
        int length = getEndIndex() + 1;
        String[][] details = new String[length][3];

        int zoom = this.getArc().getZoomPercentage();
        int x, y;
        for (int c = 0; c < length; c++) {
            x = pathPoints.get(c).getX();
            details[c][0] = String.valueOf(ZoomController.getUnzoomedValue(x, zoom));
            y = pathPoints.get(c).getY();
            details[c][1] = String.valueOf(ZoomController.getUnzoomedValue(y, zoom));
            details[c][2] = String.valueOf(pathPoints.get(c).isCurved());
        }
        return details;
    }

    public ArcView<? extends Connectable, ? extends Connectable> getArc() {
        return parent;
    }

    public void set_transitionAngle(int angle) {
        _transitionAngle = angle;
        _transitionAngle %= 360;
    }

    /**
     * insertPoint()
     * Inserts a new point into the Array List of path points
     * at the specified index and shifts all the following points along
     *
     * @param index
     * @param newpoint
     * @author Nadeem
     */
    public void insertPoint(int index, ArcPathPoint newpoint) {
        pathPoints.add(index, newpoint);
        if (parent.getParent() instanceof PetriNetTab) {
            addPointsToGui((PetriNetTab) parent.getParent());
        } else {
            addPointsToGui((JLayeredPane) parent.getParent());
        }
    }

    public void addPointsToGui(JLayeredPane editWindow) {
        ArcPathPoint pathPoint;
        ArcPathPointHandler pointHandler;

        pathPoints.get(0).setDraggable(false);
        pathPoints.get(pathPoints.size() - 1).setDraggable(false);

        for (ArcPathPoint pathPoint1 : pathPoints) {
            pathPoint = pathPoint1;
            pathPoint.setVisible(false);

            // Check whether the point has already been added to the gui
            // as addPointsToGui() may have been called after the user
            // split an existing point. If this is the case, we don't want
            // to add all the points again along with new action listeners,
            // we just want to add the new point.
            // Nadeem 21/06/2005
            if (editWindow.getIndexOf(pathPoint) < 0) {
                editWindow.add(pathPoint);
                //TODO: SEPERATE HANDLERS INTO THOSE THAT NEED THE CONTROLLER
                pointHandler = new ArcPathPointHandler(editWindow, pathPoint, petriNetController, petriNetController.getArcController(parent.getModel()));

                if (pathPoint.getMouseListeners().length == 0) {
                    pathPoint.addMouseListener(pointHandler);
                }

                if (pathPoint.getMouseMotionListeners().length == 0) {
                    pathPoint.addMouseMotionListener(pointHandler);
                }

                if (pathPoint.getMouseWheelListeners().length == 0) {
                    pathPoint.addMouseWheelListener(pointHandler);
                }
                pathPoint.updatePointLocation();
            }
        }
    }

    public void addPointsToGui(PetriNetTab petriNetTab) {
        ArcPathPointHandler pointHandler;

        pathPoints.get(0).setDraggable(false);
        pathPoints.get(pathPoints.size() - 1).setDraggable(false);

        for (ArcPathPoint point : pathPoints) {
            point.setVisible(false);

            // Check whether the point has already been added to the gui
            // as addPointsToGui() may have been called after the user
            // split an existing point. If this is the case, we don't want
            // to add all the points again along with new action listeners,
            // we just want to add the new point.
            // Nadeem 21/06/2005
            if (petriNetTab.getIndexOf(point) < 0) {
                petriNetTab.add(point);

                //TODO SEPERATE HANDLERS INTO THOSE THAT NEED THE CONTROLLER!
                ArcController<? extends  Connectable, ? extends Connectable> arcController = petriNetController.getArcController(parent.getModel());
                pointHandler = new ArcPathPointHandler(petriNetTab, point, petriNetController, arcController);

                if (point.getMouseListeners().length == 0) {
                    point.addMouseListener(pointHandler);
                }

                if (point.getMouseMotionListeners().length == 0) {
                    point.addMouseMotionListener(pointHandler);
                }

                if (point.getMouseWheelListeners().length == 0) {
                    point.addMouseWheelListener(pointHandler);
                }
                point.updatePointLocation();
            }
        }
    }

    /**
     * splitSegment()
     * Goes through neighbouring pairs of ArcPathPoints determining the midpoint
     * between them. Then calculates the distance from midpoint to the point
     * passed as an argument. The pair of ArcPathPoints resulting in the shortest
     * distance then have an extra point added between them at the midpoint
     * effectively splitting that segment into two.
     *
     * @param mouseposition
     * @return
     */
    //TODO: REIMPLEMENT
    public ArcPathPoint splitSegment(Point2D.Double mouseposition) {
//        int wantedpoint = findPoint(mouseposition);
//
//        // wantedpoint is now the index of the first point in the pair of arc
//        // points marking the segment to be split. So we have all we need to
//        // split the arc.
//        ArcPathPoint first = pathPoints.get(wantedpoint);
//        ArcPathPoint second = pathPoints.get(wantedpoint + 1);
//        ArcPathPoint newpoint = new ArcPathPoint(second.getMidPoint(first), first.isCurved(), this);
//        insertPoint(wantedpoint + 1, newpoint);
//        createPath();
//        parent.updateArcPosition();
//
//        return newpoint;
        return null;
    }

    //TODO: REIMPLEMENT
    public HistoryItem insertPointAt(Point2D.Double mouseposition, boolean flag) {
//        int wantedpoint = findPoint(mouseposition);
//
//        // wantedpoint is now the index of the first point in the pair of arc
//        // points marking the segment to be split. So we have all we need to
//        // insert the new point at the given position.
//        ArcPathPoint newPoint = new ArcPathPoint(mouseposition, flag, this);
//        insertPoint(wantedpoint + 1, newPoint);
//        createPath();
//        parent.updateArcPosition();
//
//        return new AddArcPathPoint(this.getArc(), newPoint);
        return null;
    }

    private int findPoint(final Point2D.Double mouseposition) {
        // An array to store all the distances from the midpoints
        double[] distances = new double[pathPoints.size() - 1];

        // Calculate the midpoints and distances to them
        for (int index = 0; index < (pathPoints.size() - 1); index++) {
            ArcPathPoint first = pathPoints.get(index);
            ArcPathPoint second = pathPoints.get(index + 1);
            Point2D.Double midpoint = first.getMidPoint(second);
            distances[index] = midpoint.distance(mouseposition);
        }

        // Now determine the shortest midpoint
        double shortest = distances[0];
        int wantedpoint = 0;
        for (int index = 0; index < pathPoints.size() - 1; index++) {
            if (distances[index] < shortest) {
                shortest = distances[index];
                wantedpoint = index;
            }
        }
        return wantedpoint;
    }

    public void clear() {
        for (ArcPathPoint pathPoint : pathPoints) {
            pathPoint.kill();
        }
        pathPoints.clear();
    }

}



