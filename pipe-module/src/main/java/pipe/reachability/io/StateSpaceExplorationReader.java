package pipe.reachability.io;

import pipe.reachability.state.Record;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;

/**
 * Reads the results from generating the state space exploration
 */
public interface StateSpaceExplorationReader {
    /**
     * Process the entire stream and build up a collection of Records.
     *
     * @param stream input stream
     * @return Collection of all state transitions with rates
     */
    Collection<Record> getRecords(ObjectInputStream stream) throws IOException;
}
