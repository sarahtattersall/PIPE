package pipe.reachability.state;

import pipe.models.component.token.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * This represents a state by using a HashMap to store place name to token count
 */
public class HashedState implements State {
    /**
     * The token counts for the current State.
     * Contains Place id -> {Token -> Integer count}
     */
    private final Map<String, Map<Token, Integer>> tokenCounts;

    public HashedState(Map<String, Map<Token, Integer>> tokenCounts) {
        this.tokenCounts = new HashMap<>(tokenCounts);
    }

    @Override
    public Map<Token, Integer> getTokens(String id) {
        return tokenCounts.get(id);
    }

    /**
     *
     * Creates a string representation of the state.
     *
     * @return String representation of the state.
     */
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        int count = 0;
        for (Map.Entry<String, Map<Token, Integer>> entry : tokenCounts.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue());

            if (count < tokenCounts.size() - 1) {
                builder.append(", ");
            }
            count++;
        }
        builder.append("}");
        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HashedState)) {
            return false;
        }

        HashedState that = (HashedState) o;

        if (!tokenCounts.equals(that.tokenCounts)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return tokenCounts.hashCode();
    }
}
