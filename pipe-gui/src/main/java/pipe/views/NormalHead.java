package pipe.views;

import java.awt.*;

/**
 * Arrow head representation for normal arcs
 */
public class NormalHead implements ArcHead {
    private static final Polygon HEAD = new Polygon(new int[]{0, -10, -7, -10}, new int[]{0, 5, 0, -5}, 4);

    /**
     * Draw arrow head onto canvas
     * @param g2 graphics
     */
    @Override
    public void draw(Graphics2D g2) {
        g2.setStroke(new BasicStroke(0.8f));
        g2.fillPolygon(HEAD);
    }
}
