package pipe.reachability.formatter;

import pipe.reachability.State.Record;
import pipe.reachability.State.State;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Contains the format in which to write States out to a file
 */
public interface WriterFormatter {

    /**
     * Read the a record from file (which was generated using the method write) into a Record
     * @return Record of state transition. I.e. from state to successor with a given rate
     */
    Record read(InputStream stream) throws IOException;

    /**
     *
     * @param state starting state
     * @param successor state that is transitioned to
     * @param successorRate rate at which state transitions to successor
     * @param writer output stream writer
     * @throws IOException
     */
    void write(State state, State successor, double successorRate, OutputStream writer) throws IOException;


}
