package pipe.models.component;

/**
 * For petri net components that have an idea of location
 */
public abstract class PlaceablePetriNetComponent extends AbstractPetriNetComponent {

    /**
     * Message fired when the x attribute is changed
     */
    public static final String X_CHANGE_MESSAGE = "x";

    /**
     * Message fired when the y attribute is changed
     */
    public static final String Y_CHANGE_MESSAGE = "y";

    /**
     * Message fired when the width attribute is changed
     */
    public static final String WIDTH_CHANGE_MESSAGE = "width";

    /**
     * Message fired whtn the height attribute is changed
     */
    public static final String HEIGHT_CHANGE_MESSAGE = "height";

    public abstract int getX();
    public abstract int getY();
    public abstract int getHeight();
    public abstract int getWidth();
}
