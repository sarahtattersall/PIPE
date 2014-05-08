package pipe.reachability.state;

import com.google.common.base.Charsets;
import com.google.common.hash.*;

import java.util.*;

public class ExploredSet implements Set<CompressedExplorerState> {
    private Set<CompressedExplorerState> states = new HashSet<>();

    private ExplorerStateVisitor stateVisitor = new ExplorerStateVisitor() {
        @Override
        public void visit(HashedExplorerState state) {
            add(state);
        }

        @Override
        public void visit(CompressedExplorerState state) {
            add(state);
        }
    };

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
        if (!(o instanceof CompressedExplorerState)) {
            return false;
        }

        CompressedExplorerState compressed = (CompressedExplorerState) o;
        return states.contains(compressed);
    }

    public boolean containsExplorerState(ExplorerState state) {
        CompressedExplorerState compressed = compress(state);
        return contains(compressed);
    }

    @Override
    public Iterator<CompressedExplorerState> iterator() {
        return states.iterator();
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
    public boolean add(CompressedExplorerState compressedExplorerState) {
        return states.add(compressedExplorerState);
    }

    public boolean add(ExplorerState explorerState) {
        CompressedExplorerState compressedExplorerState = compress(explorerState);
        return add(compressedExplorerState);
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
    public boolean addAll(Collection<? extends CompressedExplorerState> c) {
        for (CompressedExplorerState state : c) {
            state.accept(stateVisitor);
        }
        return true;
    }

    public boolean addAllExplorers(Collection<? extends ExplorerState> c) {
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
}
