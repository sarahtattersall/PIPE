package pipe.animation;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import java.util.Collection;
import java.util.Map;

public class HashedState implements State {
    /**
     * The token counts for the current State.
     * Contains Place id -> {Token -> Integer count}
     */
    private final Multimap<String, TokenCount> tokenCounts = HashMultimap.create();

    public HashedState(Multimap<String, TokenCount> tokenCounts) {
        this.tokenCounts.putAll(tokenCounts);
    }

    @Override
    public Collection<TokenCount> getTokens(String id) {
        return tokenCounts.get(id);
    }

    @Override
    public Multimap<String, TokenCount> asMap() {
        return tokenCounts;
    }

    @Override
    public int hashCode() {
        return tokenCounts.hashCode();
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
    public String toString() {

        StringBuilder builder = new StringBuilder();
        builder.append("{");
        int count = 0;
        for (Map.Entry<String, Collection<TokenCount>> entry : tokenCounts.asMap().entrySet()) {
            builder.append("\"").append(entry.getKey()).append("\"").append(": {");
            int insideCount = 0;
            for (TokenCount tokenCount : entry.getValue()) {
                builder.append("\"").append(tokenCount.token).append("\"").append(": ").append(tokenCount.count);
                if (insideCount < entry.getValue().size() - 1) {
                    builder.append(", ");
                }
            }
            builder.append("}");

            if (count < tokenCounts.size() - 1) {
                builder.append(", ");
            }
            count++;
        }
        builder.append("}");
        return builder.toString();
    }

}
