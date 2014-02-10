package pipe.dsl;

import pipe.models.component.Connectable;
import pipe.models.component.PetriNetComponent;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * Usage:
 * APlace.withId("P0").andCapacity(5).containing(5, "Red).tokens();
 */
public class APlace implements DSLCreator<Place> {
    private String id;
    private int capacity;
    private Map<String, Integer> tokenCounts = new HashMap<>();

    private APlace(String id) { this.id = id; }

    public static APlace withId(String id) {
        return new APlace(id);
    }

    public APlace andCapacity(int capacity) {
        this.capacity = capacity;
        return this;
    }

    public APlace containing(int count, String name) {
        tokenCounts.put(name, count);
        return this;
    }

    /**
     * Added for readability
     * E.g. containing(5, "Default).tokens()
     */
    public APlace tokens() {
        return this;
    }

    /**
     * Added for readability
     * E.g. containing(1, "Default).token()
     */
    public APlace token() {
        return this;
    }

    @Override
    public Place create(Map<String, Token> tokens, Map<String, Connectable> connectables) {
        Place place = new Place(id, id);
        place.setCapacity(capacity);

        for (Map.Entry<String, Integer> entry : tokenCounts.entrySet()) {
            place.setTokenCount(tokens.get(entry.getKey()), entry.getValue());
        }

        connectables.put(id, place);
        return place;
    }


    /**
     * Chains adding tokens
     * E.g.
     * contains(1, "Red").token().and(2, "Blue").tokens();
     * @param count token count
     * @param tokenName token name
     * @return instance of APlace for chainging
     */
    public APlace and(int count, String tokenName) {
        tokenCounts.put(tokenName, count);
        return this;
    }
}
