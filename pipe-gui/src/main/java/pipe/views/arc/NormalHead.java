package pipe.views.arc;

import java.awt.*;

public class NormalHead implements ArcHead{
    private final static Polygon head = new Polygon(new int[]{0, 5, 0, -5}, new int[]{0, -10, -7, -10}, 4);

    @Override
    public void draw(Graphics2D g2) {
        g2.setStroke(new BasicStroke(0.8f));
        g2.fillPolygon(head);
    }
}
