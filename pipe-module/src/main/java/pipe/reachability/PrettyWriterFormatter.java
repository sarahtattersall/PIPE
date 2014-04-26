package pipe.reachability;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Used to crete a String version of a state transition from one state to another
 */
public class PrettyWriterFormatter implements WriterFormatter {
    @Override
    //TODO: Implement
    public Record read(InputStream stream) {
        return null;
    }

    @Override
    public void write(State state, State successor, double successorRate, OutputStream writer) throws IOException {
        String value = String.format("%s to %s with rate %f\n", state.toString(), successor.toString(), successorRate);
        writer.write(value.getBytes());
    }
}
