/**
 * Created on 12-Feb-2004
 */
package pipe.views;

import pipe.actions.gui.PipeApplicationModel;
import pipe.constants.GUIConstants;
import pipe.controllers.ArcController;
import pipe.controllers.PetriNetController;
import pipe.handlers.ArcPathPointHandler;
import pipe.utilities.gui.GuiUtils;
import pipe.utilities.math.Cubic;
import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.*;
import uk.ac.imperial.pipe.visitor.component.PetriNetComponentVisitor;

import java.awt.*;
import java.awt.geom.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that displays the path of an arc graphically on screen.
 * It's old code so needs tidying up at some point
 */
public class ArcPath implements Shape, Cloneable {

    private static final Stroke PROXIMITY_STROKE = new BasicStroke(GUIConstants.ARC_PATH_PROXIMITY_WIDTH);

    private static final Stroke STROKE = new BasicStroke(GUIConstants.ARC_PATH_SELECTION_WIDTH);

    /**
     * The midpoint along the arc, used to display the arc weights here if necessary
     */
    public final Point2D.Double midPoint = new Point2D.Double();

    /**
     * Points along the arc path
     */
    private final List<ArcPathPoint> pathPoints = new ArrayList<>();

    /**
     * Parent view who the points belong to
     */
    private final ArcView<? extends Connectable, ? extends Connectable> arcView;

    /**
     * Petri net controller for which the arc belongs to
     */
    private final PetriNetController petriNetController;

    /**
     * Main PIPE application model
     */
    private final PipeApplicationModel applicationModel;

    /**
     * Graphical representation of the path
     */
    private GeneralPath path = new GeneralPath();

    /**
     * When pointlock is on no points will be displayed when the cursor is hovered
     * over them. Nor will they be dragable
     */
    private boolean pointLock = false;

    private Shape shape = STROKE.createStrokedShape(this);

    private Shape proximityShape = PROXIMITY_STROKE.createStrokedShape(this);

    /**
     * Angle at which to meet a transition
     */
    private int transitionAngle;

    /**
     * Constructor
     *
     * @param arcView            arc who these points belong to
     * @param petriNetController Petri net controller for which the underlying arc belongs to
     * @param applicationModel   main PIPE application model
     */
    public ArcPath(ArcView<? extends Connectable, ? extends Connectable> arcView, PetriNetController petriNetController,
                   PipeApplicationModel applicationModel) {
        this.arcView = arcView;
        this.petriNetController = petriNetController;
        this.applicationModel = applicationModel;
        transitionAngle = 0;
    }

    /**
     * Note: We cannot do this in O(1) time using a HashMap because ArcPoints are
     * mutable objects, meaning that they could change whilst keys in the HashMap
     *
     * @param point
     * @return true if the path contains the point
     */
    public boolean contains(ArcPoint point) {
        for (ArcPathPoint p : pathPoints) {
            if (p.getModel().equals(point)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Remove the point from the graphical representation of the path
     *
     * @param point
     */
    public void deletePoint(ArcPoint point) {
        ArcPathPoint pointView = null;
        for (ArcPathPoint p : pathPoints) {
            if (p.getModel().equals(point)) {
                pointView = p;
            }
        }
        if (pointView != null) {
            pointView.delete();
            pathPoints.remove(pointView);
        }
    }

    /**
     * @return number of arc points in the path
     */
    public int getNumPoints() {
        return pathPoints.size();
    }

    /**
     * @param index
     * @return the location of the point at this index, or null if it does not exist
     */
    public Point2D getPoint(int index) {
        return pathPoints.get(index).getPoint();
    }

    /**
     * @param index
     * @return the point at this index or null if it does not exist
     */
    public ArcPathPoint getPathPoint(int index) {
        return pathPoints.get(index);
    }

    /**
     * Graphically show the points along the path
     */
    public void showPoints() {
        if (!pointLock) {
            for (ArcPathPoint pathPoint : pathPoints) {
                pathPoint.setVisible(true);
            }
        }
    }

    /**
     * @param lock true if points should always be shown on the canvas
     */
    public void setPointVisibilityLock(boolean lock) {
        pointLock = lock;
    }

    /**
     * Hide all points on the canvas
     */
    public void hidePoints() {
        if (!pointLock) {
            for (ArcPathPoint pathPoint : pathPoints) {
                if (!pathPoint.isSelected()) {
                    pathPoint.setVisible(false);
                }
            }
        }
    }

    /**
     * @return the angle at which the arc leaves its source
     */
    public double getStartAngle() {

        if (getEndIndex() > 0) {
            return pathPoints.get(0).getAngle(pathPoints.get(1).getControl());
        }
        return 0;
    }

    /**
     * @return the index of the last item in the path points
     */
    public int getEndIndex() {
        return pathPoints.size() - 1;
    }

    /**
     * Used because there is no layout manager for the canvas
     *
     * @return the bounds of the arc path
     */
    @Override
    public Rectangle getBounds() {
        return path.getBounds();
    }

    @Override
    public Rectangle2D getBounds2D() {
        return null;
    }


    /**
     * @param x
     * @param y
     * @return false, no point is contained within this arc
     */
    @Override
    public boolean contains(double x, double y) {
        return false;
    }

    /**
     * @param point
     * @return true if the point intersects the shape
     */
    @Override
    public boolean contains(Point2D point) {
        return shape.contains(point);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @return false
     */
    @Override
    public boolean intersects(double arg0, double arg1, double arg2, double arg3) {
        return false;
    }

    /**
     * @param rect
     * @return true if the rectange intersects the shape
     */
    @Override
    public boolean intersects(Rectangle2D rect) {
        return shape.intersects(rect);
    }

    /**
     * @param arg0
     * @param arg1
     * @param arg2
     * @param arg3
     * @return false
     */
    @Override
    public boolean contains(double arg0, double arg1, double arg2, double arg3) {
        return false;
    }

    /**
     * @param rect
     * @return false
     */
    @Override
    public boolean contains(Rectangle2D rect) {
        return false;
    }

    /**
     * @param arg0
     * @return an iterator for the path
     */
    @Override
    public PathIterator getPathIterator(AffineTransform arg0) {
        return path.getPathIterator(arg0);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform arg0, double arg1) {
        return path.getPathIterator(arg0, arg1);
    }


    public boolean proximityContains(Point2D p) {
        return proximityShape.contains(p);
    }

    /**
     * Tells the arc points to remove themselves
     */
    public void delete() {
        while (!pathPoints.isEmpty()) {
            // force delete of ALL points
            pathPoints.get(0).kill();
        }
    }

    /**
     * @param point point to add to path
     * @param index position in the path
     */
    public void insertIntermediatePoint(ArcPoint point, int index) {
        insertPoint(index, createPoint(point));
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
        addPointsToGui(arcView.getParent());
    }

    /**
     * @param point
     * @return a graphical point at the underlying point models location
     */
    private ArcPathPoint createPoint(ArcPoint point) {
        PropertyChangeListener listener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent propertyChangeEvent) {

                createPath();
                arcView.updateBounds();
                arcView.repaint();
            }
        };
        point.addPropertyChangeListener(listener);
        return new ArcPathPoint(point, this, petriNetController, arcView.getParent());

    }

    /**
     * Add all graphical arc points to the Petri net tab
     *
     * @param petriNetTab
     */
    public void addPointsToGui(Container petriNetTab) {
        if (petriNetTab == null) {
            //Parent has not yet been added
            return;
        }


        for (ArcPathPoint point : pathPoints) {
            point.setVisible(false);

            // Check whether the point has already been added to the gui
            // as addPointsToGui() may have been called after the user
            // split an existing point. If this is the case, we don't want
            // to add all the points again along with new action listeners,
            // we just want to add the new point.
            // Nadeem 21/06/2005
            //            if (petriNetTab.getIndexOf(point) < 0) {
            petriNetTab.add(point);

            //TODO SEPERATE HANDLERS INTO THOSE THAT NEED THE CONTROLLER!
            ArcController<? extends Connectable, ? extends Connectable> arcController =
                    petriNetController.getArcController(arcView.getModel());
            ArcPathPointHandler pointHandler =
                    new ArcPathPointHandler(petriNetTab, point, petriNetController, arcController, applicationModel);

            if (point.getMouseListeners().length == 0) {
                point.addMouseListener(pointHandler);
            }

            if (point.getMouseMotionListeners().length == 0) {
                point.addMouseMotionListener(pointHandler);
            }

            if (point.getMouseWheelListeners().length == 0) {
                point.addMouseWheelListener(pointHandler);
            }
        }
    }

    /**
     * Creates the path layout using the path points set
     */
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
        shape = STROKE.createStrokedShape(this);
        proximityShape = PROXIMITY_STROKE.createStrokedShape(this);
    }

    /**
     * Set the control points for the Bezier curves
     */
    private void setControlPoints() {
        //must be in this order
        setCurveControlPoints();
        setStraightControlPoints();
        setEndControlPoints();
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

    /**
     * Creates a curved line
     *
     * @param point point to create curve to
     */
    private void createCurvedPoint(ArcPathPoint point) {
        path.curveTo(point.getControl1().x, point.getControl1().y, point.getControl().x, point.getControl().y,
                point.getPoint().getX(), point.getPoint().getY());
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

    /**
     * Sets the midpoint which is used to relocate the arcs label
     *
     * @param length path length
     */
    private void setMidPoint(double length) {
        ArcPathPoint currentPoint = pathPoints.get(0);
        if (getEndIndex() < 2) {
            midPoint.x = (pathPoints.get(0).getPoint().getX() + pathPoints.get(1).getPoint().getX()) * 0.5;
            midPoint.y = (pathPoints.get(0).getPoint().getY() + pathPoints.get(1).getPoint().getY()) * 0.5;
        } else {
            double acc = 0;
            double percent = 0;
            ArcPathPoint previousPoint = currentPoint;
            for (int point = 1; point < pathPoints.size(); point++) {
                previousPoint = currentPoint;
                currentPoint = pathPoints.get(point);

                double inc = getLength(currentPoint.getPoint(), previousPoint.getPoint());
                double halfLength = length / 2.0;
                if (acc + inc > halfLength) {
                    percent = (halfLength - acc) / inc;
                    break;
                }
                acc += inc;
            }

            midPoint.x = previousPoint.getPoint().getX() + (float) (
                    (currentPoint.getPoint().getX() - previousPoint.getPoint().getX()) * percent);
            midPoint.y = previousPoint.getPoint().getY() + (float) (
                    (currentPoint.getPoint().getY() - previousPoint.getPoint().getY()) * percent);
        }
    }

    /* function sets control points for any curved sections of the path */
    private void setCurveControlPoints() {
        if (pathPoints.isEmpty()) {
            return;
        }

        Cubic[] X;
        Cubic[] Y;
        int c = 1;
        while (c < pathPoints.size()) {
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
                int[] x = new int[lengthOfCurve + 2];
                int[] y = new int[lengthOfCurve + 2];

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
                currentPoint.setControl1(
                        getControlPoint(previousPoint.getPoint(), currentPoint.getPoint(), previousPoint.getPoint(),
                                currentPoint.getPoint())
                );
                currentPoint.setControl(
                        getControlPoint(currentPoint.getPoint(), previousPoint.getPoint(), currentPoint.getPoint(),
                                previousPoint.getPoint())
                );
            } else {
                if (c > 1 && !previousPoint.isCurved()) {
                    myPreviousButOnePoint = pathPoints.get(c - 2);
                    currentPoint.setControl1(getControlPoint(myPreviousButOnePoint.getPoint(), previousPoint.getPoint(),
                            previousPoint.getPoint(), currentPoint.getPoint()));
                }
                if (c < getEndIndex()) {
                    ArcPathPoint nextPoint = pathPoints.get(c + 1);
                    if (!nextPoint.isCurved()) {
                        currentPoint.setControl(
                                getControlPoint(nextPoint.getPoint(), currentPoint.getPoint(), currentPoint.getPoint(),
                                        previousPoint.getPoint())
                        );
                    }
                }
            }
        }
    }

    /**
     * Set the control points for the end of the arc
     */
    private void setEndControlPoints() {
        PetriNetComponentVisitor endPointVisitor = new ArcConnectableVisitor();
        Connectable source = getArc().getModel().getSource();
        try {
            source.accept(endPointVisitor);
        } catch (PetriNetComponentException e) {
            GuiUtils.displayErrorMessage(null, e.getMessage());
        }
    }

    /**
     * We solve the equation
     * [2 1       ] [D[0]]   [3(x[1] - x[0])  ]
     * |1 4 1     | |D[1]|   |3(x[2] - x[0])  |
     * |  1 4 1   | | .  | = |      .         |
     * |    ..... | | .  |   |      .         |
     * |     1 4 1| | .  |   |3(x[n] - x[n-2])|
     * [       1 2] [D[n]]   [3(x[n] - x[n-1])]
     * <p/>
     * by using row operations to convert the matrix to upper triangular
     * and then back substitution.  The D[i] are the derivatives at the knots.
     *
     * @param n
     * @param x
     * @return a natural cubic for the Bezier curve
     */
    private Cubic[] calcNaturalCubic(int n, int[] x) {
        float[] gamma = new float[n + 1];
        float[] delta = new float[n + 1];
        float[] D = new float[n + 1];

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
            p.x = C.getX() + (ABx * modCD / GUIConstants.ARC_CONTROL_POINT_CONSTANT);
            p.y = C.getY() + (ABy * modCD / GUIConstants.ARC_CONTROL_POINT_CONSTANT);
        }
        return p;
    }

    /**
     * @return arc view associated with this point
     */
    public ArcView<? extends Connectable, ? extends Connectable> getArc() {
        return arcView;
    }

    /**
     * Removes all path points
     */
    public void clear() {
        for (ArcPathPoint pathPoint : pathPoints) {
            pathPoint.kill();
        }
        pathPoints.clear();
    }

    /**
     * Visitor interface that visits Places and Transitions
     */
    private interface ConnectableVisitor extends PlaceVisitor, TransitionVisitor {
    }


    private class ArcConnectableVisitor implements ConnectableVisitor {
        @Override
        public void visit(Place place) {
            if (pathPoints.get(getEndIndex()).isCurved()) {
                double angle = Math.toRadians(transitionAngle);
                ArcPathPoint myPoint = pathPoints.get(getEndIndex());
                ArcPathPoint myLastPoint = pathPoints.get(getEndIndex() - 1);
                float distance = (float) getLength(myPoint.getPoint(), myLastPoint.getPoint())
                        / GUIConstants.ARC_CONTROL_POINT_CONSTANT;
                myPoint.setControl2((float) (myPoint.getPoint().getX() + Math.cos(angle) * distance),
                        (float) (myPoint.getPoint().getY() + Math.sin(angle) * distance));

                myPoint = pathPoints.get(1);
                myPoint.setControl(getControlPoint(pathPoints.get(0).getPoint(), myPoint.getControl(),
                        pathPoints.get(0).getPoint(), myPoint.getControl()));
            }
        }

        @Override
        public void visit(Transition transition) {
            if (pathPoints.get(1).isCurved()) {
                double angle = Math.toRadians(transitionAngle);
                ArcPathPoint myPoint = pathPoints.get(1);
                ArcPathPoint myLastPoint = pathPoints.get(0);
                float distance = (float) getLength(myPoint.getPoint(), myLastPoint.getPoint())
                        / GUIConstants.ARC_CONTROL_POINT_CONSTANT;
                myPoint.setControl1((float) (myLastPoint.getPoint().getX() + Math.cos(angle) * distance),
                        (float) (myLastPoint.getPoint().getY() + Math.sin(angle) * distance));

                myPoint = pathPoints.get(getEndIndex());
                myPoint.setControl(getControlPoint(myPoint.getPoint(), myPoint.getControl1(), myPoint.getPoint(),
                        myPoint.getControl1()));
            }
        }
    }
}



