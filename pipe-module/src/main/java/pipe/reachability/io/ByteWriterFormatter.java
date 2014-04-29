package pipe.reachability.io;

import pipe.reachability.state.Record;
import pipe.reachability.state.State;

import java.io.*;

/**
 * Writes the state space exploration transitions into a binary stream
 */
public class ByteWriterFormatter implements WriterFormatter {
    @Override
    public Record read(ObjectInputStream inputStream) throws IOException {
        try  {
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
    public void write(State state, State successor, double successorRate, ObjectOutputStream stream) throws IOException {
            stream.writeObject(state);
            stream.writeObject(successor);
            stream.writeDouble(successorRate);
            stream.flush();
    }


    private static class AppendingObjectOutputStream extends ObjectOutputStream {

        public AppendingObjectOutputStream(OutputStream out) throws IOException {
            super(out);
        }

        @Override
        protected void writeStreamHeader() throws IOException {
            // do not write a header, but reset:
            // this line added after another question
            // showed a problem with the original
            reset();
        }

    }
}
