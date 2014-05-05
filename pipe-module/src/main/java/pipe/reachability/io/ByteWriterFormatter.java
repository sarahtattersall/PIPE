package pipe.reachability.io;

import pipe.reachability.state.ExplorerState;
import pipe.reachability.state.Record;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Writes the state space exploration transitions into a binary stream
 */
public class ByteWriterFormatter implements WriterFormatter {
    /**
     * Reads in a serialized state, successor and rate
     * @param inputStream
     * @return record of state transition with rate
     * @throws IOException
     */
    @Override
    public Record read(ObjectInputStream inputStream) throws IOException {
        try  {
            ExplorerState state = (ExplorerState) inputStream.readObject();
            ExplorerState successor = (ExplorerState) inputStream.readObject();
            double rate = inputStream.readDouble();
            return new Record(state, successor, rate);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    /**
     *
     * Writes serialized state, successor and rates to the stream
     *
     * @param state starting state
     * @param successor state that is transitioned to
     * @param successorRate rate at which state transitions to successor
     * @param stream
     * @throws IOException
     */
    @Override
    public void write(ExplorerState state, ExplorerState successor, double successorRate, ObjectOutputStream stream) throws IOException {
            stream.writeObject(state);
            stream.writeObject(successor);
            stream.writeDouble(successorRate);
            stream.flush();
    }


}
