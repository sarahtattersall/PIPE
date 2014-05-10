package pipe.reachability.state;

import com.google.common.base.Charsets;
import com.google.common.hash.*;

import java.util.*;

/**
 * Uses a probabalistic method to compress data by double hashing items.
 * The first hash yields the location for the object and the second is used for
 * object equality comparisons.
 *
 * The idea is that false-positives are very low due to the double hash.
 */
public class ExploredSet {
    private final int size;
    private final List<LinkedList<HashCode>> array;
    private static final Funnel<ExplorerState> funnel = new Funnel<ExplorerState>() {
        @Override
        public void funnel(ExplorerState from, PrimitiveSink into) {
            into.putBoolean(from.isTangible());
            Map<String, Map<String, Integer>> s = from.getState().asMap();
            for (Map.Entry<String, Map<String, Integer>> entry : s.entrySet()) {
                into.putString(entry.getKey(), Charsets.UTF_8);
                for (Map.Entry<String, Integer> entry1 : entry.getValue().entrySet()) {
                    into.putString(entry1.getKey(), Charsets.UTF_8);
                    into.putInt(entry1.getValue());
                }
            }

        }
    };

    public ExploredSet(int size) {
        this.size = size;
        array = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            array.add(new LinkedList<HashCode>());
        }
    }

    /**
     * Uses two hashes to compress the state/
     *
     * The first hash is used to determine the items location in memory
     * The second hash is stored for the object equality
     * @param state
     */
    public void add(ExplorerState state) {
        int location = getLocation(state);
        HashCode value = hashTwo(state);
        LinkedList<HashCode> list = array.get(location);
        list.add(value);
    }

    /**
     *
     * Compresses states and adds them to the explored data structure
     *
     * @param states all states that have been explored
     */
    public void addAll(Collection<ExplorerState> states) {
        for (ExplorerState state : states) {
            add(state);
        }
    }

    /**
     * Adds all elements in the exploredSet into this one
     *
     * Sadly since the original item has been lost, we cannot re-hash it
     * into this set. Therefore we must loop through it and keep items in their
     * same location in memory
     *
     * @param exploredSet
     */
    public void addAll(ExploredSet exploredSet) {
       for (int i = 0; i < exploredSet.array.size(); i++) {
           List<HashCode> theirs = exploredSet.array.get(i);
           List<HashCode> ours = array.get(i % size);
           ours.addAll(theirs);
       }
    }

    /**
     *
     * Works out where the state should be placed/found in array.
     *
     * It does this by working out its hashcode and taking the absolute value.
     *
     * This is then modded by the size of the array to give a guaranteed index
     * into the array.
     *
     * @param state
     * @return the location that this state falls in the array
     */
    public int getLocation(ExplorerState state) {
        return  Math.abs(hashOne(state) % size);
    }

    public boolean contains(ExplorerState state) {
        int location = getLocation(state);
        HashCode value = hashTwo(state);
        List<HashCode> list = array.get(location);
        return list.contains(value);
    }



    private int hashOne(ExplorerState state) {
        HashFunction hf = Hashing.murmur3_32();
        HashCode hc = hashCodeForState(state, hf);
        return hc.asInt();
    }

    private HashCode hashTwo(ExplorerState state) {
        HashFunction hf = Hashing.sha1();
        return hashCodeForState(state, hf);
    }

    private HashCode hashCodeForState(ExplorerState state, HashFunction hf) {
        return hf.newHasher().putObject(state, funnel).hash();
    }

    public void clear() {
        for (List<HashCode> list : array) {
            list.clear();
        }
    }
}
