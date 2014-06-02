package pipe.views;

import pipe.constants.GUIConstants;
import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

/**
 * Building tool for creating an arc, allows the user to go from source to
 * target before adding to petri net
 *
 * @param <T> source class
 */
public class TemporaryArcView<T extends Connectable> extends JComponent {
    private T source;

    private final ArcHead arcHead;

    /**
     * Current end location of the mouse
     */
    private Point2D end;

    /**
     * Intermediate points build up by clicking on the canvas
     * Press shift for curved points
     */
    private List<ArcPoint> intermediatePoints = new ArrayList<>();

    /**
     * Maximum intermediate point for setting rectangle bounds
     */
    private double maxX;

    private double maxY;

    public TemporaryArcView(T source, ArcHead arcHead) {
        super();
        this.source = source;
        this.arcHead = arcHead;
        Point2D centre = source.getCentre();
        end = new Point2D.Double(centre.getX(), centre.getY());
        updateMax(end);
        setBounds(0, 0, (int) centre.getX(), (int) centre.getY());
    }

    /**
     * Updates max points based on point
     */
    private void updateMax(Point2D point) {
        maxX = max(maxX, point.getX());
        maxY = max(maxY, point.getY());
    }

    public void setEnd(Point2D end) {
        this.end = end;
        updateMax(end);
        updateBounds();
    }

    /**
     * Updates bounds to maxX and maxY
     */
    private void updateBounds() {
        int x = (int) maxX;
        int y = (int) maxY;
        setBounds(0, 0, x, y);
    }

    public void addIntermediatePoint(ArcPoint point) {
        intermediatePoints.add(point);
        updateMax(point.getPoint());
        updateBounds();
    }

    public T getSourceConnectable() {
        return source;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setPaint(GUIConstants.ELEMENT_LINE_COLOUR);
        GeneralPath path = new GeneralPath();
        path.moveTo(source.getCentre().getX(), source.getCentre().getY());

        for (ArcPoint arcPoint : intermediatePoints) {
            path.lineTo(arcPoint.getX(), arcPoint.getY());
        }
        path.lineTo(end.getX(), end.getY());
        g2.draw(path);

        g2.translate(end.getX(), end.getY());
        g2.rotate(getRotationAngle());
        arcHead.draw(g2);
    }

    /**
     * @return angle in radians between last point on the arc and the current end
     */
    private double getRotationAngle() {
        Point2D lastPoint = getLastPoint();
        double deltax = end.getX() - lastPoint.getX();
        double deltay = end.getY() - lastPoint.getY();
        return Math.atan2(deltay, deltax);

    }

    /**
     * This method always returns false as to avoid any interaction with the
     * temporary arc view
     *
     * @param x x location of mouse
     * @param y y location of mouse
     * @return false
     */
    @Override
    public boolean contains(int x, int y) {
        return false;
    }

    public Point2D getLastPoint() {
        if (!intermediatePoints.isEmpty()) {
            return intermediatePoints.get(intermediatePoints.size() - 1).getPoint();
        }
        return source.getCentre();
    }

    public List<ArcPoint> getIntermediatePoints() {
        return intermediatePoints;
    }
}
