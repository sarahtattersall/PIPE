package matchers.component;

import uk.ac.imperial.pipe.models.petrinet.Connectable;

/**
 * Checks if connectable has given x and y
 */
public class HasXY<T extends Connectable> implements Has<T> {

    double x;
    double y;
    public HasXY(double x, double y) {
        this.x = x;
        this.y =y ;
    }

    @Override
    public boolean matches(T component) {
        return component.getX() == x && component.getY() == y;
    }
}