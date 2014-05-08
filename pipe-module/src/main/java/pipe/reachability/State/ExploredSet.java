package pipe.reachability.state;

import com.google.common.base.Charsets;
import com.google.common.hash.*;

import java.util.*;

public class ExploredSet implements Set<ExplorerState> {
    Set<CompressedExplorerState> states = new HashSet<>();

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean contains(Object o) {
        if (!(o instanceof ExplorerState)) {
            return false;
        }

        ExplorerState state = (ExplorerState) o;
        CompressedExplorerState compressedExplorerState = compress(state);
        return states.contains(compressedExplorerState);
    }

    @Override
    public Iterator<ExplorerState> iterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object[] toArray() {
        return new Object[0];
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    @Override
    public boolean add(ExplorerState explorerState) {
        CompressedExplorerState compressedExplorerState = compress(explorerState);
        return states.add(compressedExplorerState);
    }

    /**
     * This is not implemented because this class should be used
     * to add states only
     *
     * @param o
     * @return always false
     */
    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object item : c) {
            if (!contains(item)) {
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean addAll(Collection<? extends ExplorerState> c) {
        ExplorerStateVisitor stateVisitor = new ExplorerStateVisitor() {
            @Override
            public void visit(HashedExplorerState state) {
                add(state);
            }
        };
        for (ExplorerState state : c) {
            state.accept(stateVisitor);
        }
        return true;
    }

    public boolean addAll(ExploredSet c) {
         return states.addAll(c.states);
    }

    private CompressedExplorerState compress(ExplorerState explorerState) {
        return new CompressedExplorerState(hashOne(explorerState), hashTwo(explorerState));
    }

    private int hashOne(ExplorerState state) {
        HashFunction hf = Hashing.md5();
        HashCode hc = hashCodeForState(state, hf);
        return hc.asInt();
    }

    private int hashTwo(ExplorerState state) {
        HashFunction hf = Hashing.sha1();
        HashCode hc = hashCodeForState(state, hf);
        return hc.asInt();
    }

    private HashCode hashCodeForState(ExplorerState state, HashFunction hf) {
        return hf.newHasher().putObject(state, new Funnel<ExplorerState>() {
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
        }).hash();
    }

    /**
     * This is not implemented because this class should be used
     * to add states only
     *
     * @param c
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This is not implemented because this class should be used
     * to add states only
     *
     * @param c
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /**
     * This is not implemented because this class should be used
     * to add states only
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    private class CompressedExplorerState {

        private final int hash1;

        private final int hash2;

        private CompressedExplorerState(int hash1, int hash2) {
            this.hash1 = hash1;
            this.hash2 = hash2;
        }

        @Override
        public int hashCode() {
            return hash1;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof CompressedExplorerState)) {
                return false;
            }

            CompressedExplorerState that = (CompressedExplorerState) o;

            return that.hash2 == this.hash2;
        }
    }
}
