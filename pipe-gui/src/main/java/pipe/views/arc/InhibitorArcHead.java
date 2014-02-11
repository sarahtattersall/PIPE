package pipe.views.arc;

import java.awt.*;

/*
 * Draws a round circle to represent inhibitor arc heads
 */
public class InhibitorArcHead implements ArcHead {
    private final static int OVAL_X = -4;
    private final static int OVAL_Y = -8;
    private final static int OVAL_WIDTH = 8;
    private final static int OVAL_HEIGHT = 8;

    /**
     * Will draw the circle where the graphics is located
     * @param g2 graphics
     */
    @Override
    public void draw(Graphics2D g2) {
        g2.setStroke(new BasicStroke(0.8f));
        g2.drawOval(OVAL_X, OVAL_Y, OVAL_WIDTH, OVAL_HEIGHT);
    }
}
