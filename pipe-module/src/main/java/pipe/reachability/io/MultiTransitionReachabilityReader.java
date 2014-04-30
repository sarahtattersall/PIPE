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
 * Class that handles duplicate state transitions in the input stream.
 *
 * Due to the nature of {@link pipe.reachability.algorithm.sequential.SequentialStateSpaceExplorer} logging all transitions
 * without prior processing it is possible due to cyclic vanishing states to have a log
 * of the same state transitions with a different rate. This indicates another loop around the cycle with the
 * new rate. In this case the appropriate action is to sum all these rates.
 */
public class MultiTransitionReachabilityReader implements ReachabilityReader {

    /**
     * Formatter that was used to write the original results
     */
    private final WriterFormatter formatter;

    public MultiTransitionReachabilityReader(WriterFormatter formatter) {
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

                if (!result.containsKey(transition)) {
                    result.put(transition, .0);
                }
                result.put(transition, result.get(transition) + record.rate);
            } catch (EOFException ignored) {
                return result;
            }
        }
    }


}
