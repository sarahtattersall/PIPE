package pipe.gui.widget;

/**
 * Exception thrown when processing the state space
 */
public class StateSpaceLoaderException extends Exception {
    /**
     *
     * @param message error message
     */
    public StateSpaceLoaderException(String message) {
        super(message);
    }

    /**
     *
     * @param message error message
     * @param cause cause of this exception
     */
    public StateSpaceLoaderException(String message, Throwable cause) {
        super(message, cause);
    }
}
