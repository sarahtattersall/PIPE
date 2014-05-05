package pipe.reachability.io;

import pipe.reachability.state.ExplorerState;
import pipe.reachability.state.Record;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Contains the format in which to write States out to a file
 */
public interface WriterFormatter {

    /**
     * Read the a record from file (which was generated using the method write) into a Record
     * @return Record of state transition. I.e. from state to successor with a given rate
     */
    Record read(ObjectInputStream stream) throws IOException;

    /**
     *
     * @param state starting state
     * @param successor state that is transitioned to
     * @param successorRate rate at which state transitions to successor
     * @param writer output stream writer
     * @throws IOException
     */
    void write(ExplorerState state, ExplorerState successor, double successorRate, ObjectOutputStream writer) throws IOException;


}
