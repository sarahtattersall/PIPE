package pipe.calculations;

/**
 * @author Nadeem A simple class used to store arcs between states in the
 *         reachability graph. Used as elements in the linked list storing the
 *         arcs.
 */
public class ArcListElement {

	private final int tostate;
	private double rate;
	final int transitionNo;

	public ArcListElement(int to, double r, Integer t) {
		tostate = to;
		rate = r;
		transitionNo = t.intValue();
	}

	public int getTo() {
		return tostate;
	}

	public double getRate() {
		return rate;
	}

	public void setRate(double r) {
		rate = r;
	}

}
