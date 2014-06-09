package pipe.views;
import java.awt.*;

/**
 * Graphical representation of the head of an arc.
 * E.g. circular for inhibitor arcs or triangular for normal arcs
 */
public interface ArcHead {
    /**
     * Draw using graphics g2
     * @param g2 graphics
     */
    void draw(Graphics2D g2);
}