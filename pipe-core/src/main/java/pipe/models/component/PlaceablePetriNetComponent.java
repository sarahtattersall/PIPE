package pipe.models.component;

/**
 * For petri net components that have an idea of location
 */
public abstract class PlaceablePetriNetComponent extends AbstractPetriNetComponent {
    public abstract int getX();
    public abstract int getY();
    public abstract int getHeight();
    public abstract int getWidth();
}
