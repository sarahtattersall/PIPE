package pipe.views;

import pipe.gui.Constants;
import pipe.models.component.ArcPoint;
import pipe.models.component.Connectable;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.max;

public class TemporaryArcView<T extends Connectable> extends JComponent {
    private T source;
    private Point2D end;
    private List<ArcPoint> intermediatePoints = new ArrayList<ArcPoint>();

    public TemporaryArcView(T source) {
        super();
        this.source = source;
        Point2D centre = source.getCentre();
        end = new Point2D.Double(centre.getX(), centre.getY());
        //TODO: REPLACE WITH BETTER LOGIC
        setBounds(0,0, 500,500);
    }

    public void setEnd(Point2D end) {
        this.end = end;

        int x = (int) max(source.getCentre().getX(), end.getX());
        int y = (int) max(source.getCentre().getY(), end.getY());
        setBounds(0,0, x, y);
    }

    public void addIntermediatePoint(ArcPoint point) {
        intermediatePoints.add(point);
    }

    public T getSourceConnectable() {
        return source;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        g2.setPaint(Constants.ELEMENT_LINE_COLOUR);
        GeneralPath path = new GeneralPath();
        path.moveTo(source.getCentre().getX(), source.getCentre().getY());

        for(ArcPoint arcPoint : intermediatePoints) {
            path.lineTo(arcPoint.getX(), arcPoint.getY());
        }
        path.lineTo(end.getX(), end.getY());
        g2.draw(path);
    }

    public List<ArcPoint> getIntermediatePoints() {
        return intermediatePoints;
    }
}
