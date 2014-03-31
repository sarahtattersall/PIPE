package pipe.exceptions;

/**
 * Represents an error that can be thrown by the {@link pipe.models.petrinet.PetriNet}
 * when modifying the components it stores
 */
public class PetriNetComponentException  extends Exception {
    public PetriNetComponentException(String message) {
        super(message);
    }
}