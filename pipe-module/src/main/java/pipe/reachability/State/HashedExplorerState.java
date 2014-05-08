package pipe.reachability.state;

import pipe.animation.State;

import java.util.Map;

/**
 * This represents a state by using a HashMap to store place name to token count
 *
 * Uses factory methods to create HashedState based on whether it is a tangible
 * state or vanishing state
 */
public class HashedExplorerState implements ExplorerState {
    private final State state;

    /**
     * Represents if this is a tangible state or a vanishing state
     */
    private final boolean tangible;


    /**
     * Private constructor. Use factory methods
     * @param state
     * @param tangible
     */
    private HashedExplorerState(State state, boolean tangible) {
        this.state = state;
        this.tangible = tangible;
    }

    /**
     * @param state
     * @return new state that represents a tangible state with the following tokens
     */
    public static HashedExplorerState tangibleState(State state) {
        return new HashedExplorerState(state, true);
    }

    /**
     *
     * @param state
     * @return new state that represents a vanishing state with the following tokens
     */
    public static HashedExplorerState vanishingState(State state) {
        return new HashedExplorerState(state, false);
    }

    @Override
    public Map<String, Integer> getTokens(String id) {
        return state.getTokens(id);
    }

    @Override
    public State getState() {
        return state;
    }

    @Override
    public boolean isTangible() {
        return tangible;
    }

    @Override
    public void accept(ExplorerStateVisitor visitor) {
        visitor.visit(this);
    }


    /**
     *
     * Creates a string representation of the state.
     *
     * @return String representation of the state.
     */
    @Override
    public String toString() {
        return state.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HashedExplorerState)) {
            return false;
        }

        HashedExplorerState that = (HashedExplorerState) o;

        if (tangible != that.tangible) {
            return false;
        }
        if (!state.equals(that.state)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = state.hashCode();
        result = 31 * result + (tangible ? 1 : 0);
        return result;
    }
}
