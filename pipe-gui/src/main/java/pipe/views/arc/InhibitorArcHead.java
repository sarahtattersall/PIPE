package pipe.views.arc;

import java.awt.*;

/*
 * Draws a round circle to represent inhibitor arc heads
 */
public class InhibitorArcHead implements ArcHead {
    private final static int OVAL_WIDTH = 8;
    private final static int OVAL_HEIGHT = 8;
    private final static int OVAL_X = -OVAL_WIDTH;
    private final static int OVAL_Y = -OVAL_HEIGHT/2;

    /**
     * Will draw the circle where the graphics is located
     * @param g2 graphics
     */
    @Override
    public void draw(Graphics2D g2) {
        Graphics2D graphics2D = (Graphics2D) g2.create();
        graphics2D.setStroke(new BasicStroke(0.8f));

        graphics2D.setColor(Color.WHITE);
        graphics2D.fillOval(OVAL_X, OVAL_Y, OVAL_WIDTH, OVAL_HEIGHT);

        graphics2D.setColor(Color.BLACK);
        graphics2D.drawOval(OVAL_X, OVAL_Y, OVAL_WIDTH, OVAL_HEIGHT);
        graphics2D.dispose();
    }
}
