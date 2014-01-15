package pipe.petrinet.adapters.modelAdapter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.awt.geom.Point2D;

public class PointAdapter extends XmlAdapter<PointAdapter.AdaptedPoint, Point2D>{

    @Override
    public Point2D unmarshal(AdaptedPoint adaptedPoint) throws Exception {
        return new Point2D.Double(adaptedPoint.x, adaptedPoint.y);
    }

    @Override
    public AdaptedPoint marshal(Point2D point2D) throws Exception {
        AdaptedPoint adaptedPoint = new AdaptedPoint();
        adaptedPoint.x = point2D.getX();
        adaptedPoint.y = point2D.getY();
        return adaptedPoint;
    }

    public static class AdaptedPoint {
        @XmlAttribute
        public double x;
        @XmlAttribute
        public double y;
    }
}
