package pipe.reachability.io;

import pipe.reachability.state.Record;
import pipe.reachability.state.State;

import java.io.*;

/**
 * Writes the state space exploration transitions into a binary stream
 */
public class ByteWriterFormatter implements WriterFormatter {
    @Override
    public Record read(InputStream stream) throws IOException {
        try (ObjectInputStream inputStream = new ObjectInputStream(stream)) {
            State state = (State) inputStream.readObject();
            State successor = (State) inputStream.readObject();
            double rate = inputStream.readDouble();
            return new Record(state, successor, rate);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new IOException(e);
        }
    }

    @Override
    public void write(State state, State successor, double successorRate, OutputStream writer) throws IOException {
        try (ObjectOutputStream stream = new ObjectOutputStream(writer)) {
            stream.writeObject(state);
            stream.writeObject(successor);
            stream.writeDouble(successorRate);
        }
    }
}
