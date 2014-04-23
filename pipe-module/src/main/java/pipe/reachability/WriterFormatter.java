package pipe.reachability;

import java.util.Map;

/**
 * Contains the format in which to write States out to a file
 */
public interface WriterFormatter {

    /**
     *
     * @param state
     * @param successor
     * @param rate
     * @return formatted expression
     */
    String format(State state, State successor, double rate);

    Record read(String line);


    /**
     * Record of Reachability graph states
     * Contains state to successor with rate
     */
    class Record {

        public final Map<String, Integer> state;
        public final Map<String, Integer> successor;
        public final double rate;

        public Record(Map<String, Integer> state, Map<String, Integer> successor, double rate) {
            this.state = state;
            this.successor = successor;
            this.rate = rate;
        }
    }
}
