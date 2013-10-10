package pipe.exceptions;

public class InfiniteServerWithFunctionalArcException extends Exception {
	public InfiniteServerWithFunctionalArcException() {
	      super("The weights of arcs pointing to a infinite server transition should not be functional");
	   }

}
