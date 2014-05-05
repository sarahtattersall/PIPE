package pipe.animation;

import java.util.HashMap;
import java.util.Map;

public class HashedState implements State {
    public HashedState(Map<String, Map<String, Integer>> tokenCounts) {
        this.tokenCounts = new HashMap<>(tokenCounts);
    }

    /**
     * The token counts for the current State.
     * Contains Place id -> {Token -> Integer count}
     */
    private final Map<String, Map<String, Integer>> tokenCounts;


    @Override
    public Map<String, Integer> getTokens(String id) {
        return tokenCounts.get(id);
    }

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("{");
        int count = 0;
        for (Map.Entry<String, Map<String, Integer>> entry : tokenCounts.entrySet()) {
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
