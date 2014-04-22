package pipe.exceptions;

/**
 * This exception represents that the {@link pipe.models.component.rate.RateParameter}
 * has an invalid rate for the {@link pipe.models.petrinet.PetriNet}
 */
public class InvalidRateException extends Exception {
    public InvalidRateException(String invalidRate) {
        super("Rate of " + invalidRate + " is invalid");
    }
}
