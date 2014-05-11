package pipe.reachability.state;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import pipe.animation.HashedState;
import pipe.animation.TokenCount;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StateUtils {

    private StateUtils() {}


    /**
     * Converts json representation of ExplorerState into the actual clas
     * @param json
     * @return class representing json
     * @throws IOException
     */
    public static ExplorerState toState(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, HashMap<String, Integer>> map =
                mapper.readValue(json, new TypeReference<HashMap<String, HashMap<String, Integer>>>() {
                });
        Multimap<String, TokenCount> stateMap = HashMultimap.create();
        for (Map.Entry<String, HashMap<String, Integer>> entry : map.entrySet()) {
            for (Map.Entry<String, Integer> tokenEntry : entry.getValue().entrySet()) {
                stateMap.put(entry.getKey(), new TokenCount(tokenEntry.getKey(), tokenEntry.getValue()));
            }
        }
        return HashedExplorerState.tangibleState(new HashedState(stateMap));
    }

}
