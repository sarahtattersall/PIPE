package pipe.reachability.state;

import com.google.common.base.Charsets;
import com.google.common.hash.*;
import pipe.animation.TokenCount;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Uses a probabilistic method to compress data by double hashing items.
 * The first hash yields the location for the object and the second is used for
 * object equality comparisons.
 *
 * The idea is that false-positives are very low due to the double hash.
 */
public class ExploredSet {

    /**
     * Size of array
     */
    private final int size;

    /**
     * Due to states potentially having different ordering of the places in their map
     * this affects their hash value.
     *
     * Thus this list defines a definitive ordering for querying places in the state
     */
    private final List<String> placeOrdering;


    /**
     * Array to store LinkedList of HashCode in. This is the underlying 'Set' structure
     */
    private final List<LinkedList<HashCode>> array;

    /**
     * 32 bit hash function
     */
    private final HashFunction murmur3 = Hashing.murmur3_32();

    /**
     * Funnel used to generate HashCode of ExplorerState
     *
     * Due to the behaviour of a HashMap, order is not guarnateed on objects
     * so we cannot loop through the map of the explorer state and add the
     * primitive types, because a differing order will generate a different hash code.
     *
     * It appears though that the map hashcode method returns the same value
     * no matter the order so this has been used here.
     */
    private final Funnel<ExplorerState> funnel = new Funnel<ExplorerState>() {
        @Override
        public void funnel(ExplorerState from, PrimitiveSink into) {
            into.putBoolean(from.isTangible());
            for (String place : placeOrdering) {
                into.putString(place, Charsets.UTF_8);
                for (TokenCount tokenCount : from.getTokens(place)) {
                    into.putString(tokenCount.token, Charsets.UTF_8);
                    into.putInt(tokenCount.count);
                }
            }
        }
    };

    /**
     * Initialises the underlying structure of the set
     *
     * @param size underlying size of the set. It will not change
     */
    public ExploredSet(int size, List<String> placeOrdering) {
        this.size = size;
        array = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            array.add(new LinkedList<HashCode>());
        }
        this.placeOrdering = new LinkedList<>(placeOrdering);
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
        if (exploredSet.array.size() != this.array.size()) {
            throw new RuntimeException("Cannot combine sets with different sized arrays. Due to compression here is no item to reconstruct hashcode from!");
        }
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
        HashCode hc = hashCodeForState(state, murmur3);
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
