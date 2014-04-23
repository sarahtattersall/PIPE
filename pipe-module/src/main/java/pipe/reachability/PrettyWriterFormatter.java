package pipe.reachability;

/**
 * Used to crete a String version of a state transition from one state to another
 */
public class PrettyWriterFormatter implements WriterFormatter {

    /**
     *
     * @param state starting state
     * @param successor state that the starting state transitions to
     * @param rate rate at which starting state transitions to the successor
     * @return String containing STATE to SUCCESSOR with rate RATE where STATE and SUCCESSOR are their string
     *         representations
     */
    @Override
    public String format(State state, State successor, double rate) {
        return String.format("%s to %s with rate %f\n", state.toString(), successor.toString(), rate);
    }

    @Override
    //TODO: Implement
    public Record read(String line) {
        return null;
    }
}
