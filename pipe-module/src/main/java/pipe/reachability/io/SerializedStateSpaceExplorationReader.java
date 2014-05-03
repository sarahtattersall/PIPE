package pipe.reachability.io;

import pipe.reachability.state.Record;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 *
 * Class that reads state transitions in from the input stream.
 *
 * It assumes they have been written in the order state, successor record
 *
 */
public class SerializedStateSpaceExplorationReader implements StateSpaceExplorationReader {

    /**
     * Formatter that was used to write the original results
     */
    private final WriterFormatter formatter;

    public SerializedStateSpaceExplorationReader(WriterFormatter formatter) {
        this.formatter = formatter;
    }
    /**
     *
     * Calculates the total rates for each state transition
     *
     * @param stream input stream
     * @return Collection of state to successor state with rate
     */
    @Override
    public Collection<Record> getRecords(ObjectInputStream stream) throws IOException {
        Map<StateTransition, Double> totalRates = getTotalRates(stream);
        Collection<Record> result = new HashSet<>();
        for (Map.Entry<StateTransition, Double> entry : totalRates.entrySet()) {
            StateTransition transition = entry.getKey();
            double rate = entry.getValue();
            result.add(new Record(transition.state, transition. successor, rate));
        }
        return result;
    }

    /**
     * Processes the input stream
     * If a duplicate state transition is reached the rate is summed
     *
     *
     * @param inputStream
     * @return
     * @throws IOException
     */
    //TODO: This is awful but I cant find a way to prepend the input stream with the number
    //      of elements
    public Map<StateTransition, Double> getTotalRates(ObjectInputStream inputStream) throws IOException {
        Map<StateTransition, Double> result = new HashMap<>();
        while(true) {
            try {
                Record record = formatter.read(inputStream);
                StateTransition transition = new StateTransition(record.state, record.successor);
                result.put(transition, record.rate);
            } catch (EOFException ignored) {
                return result;
            }
        }
    }


}
