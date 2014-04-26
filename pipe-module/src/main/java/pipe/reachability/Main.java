package pipe.reachability;

import org.apache.commons.lang.SerializationUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Map<String, Integer> values = new HashMap<>();
        values.put("P1", 4);
        values.put("P2", 2);
        Serializable state = new HashedState(values);
        byte[] serialized = SerializationUtils.serialize(state);
        HashedState deserialized = (HashedState) SerializationUtils.deserialize(serialized);
        System.out.println(deserialized.getTokens("P1"));
        System.out.println(deserialized.getTokens("P2"));
    }
}
