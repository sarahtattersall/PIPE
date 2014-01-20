package pipe.io.adapters.modelAdapter;

import com.google.common.base.Joiner;
import pipe.io.adapters.model.AdaptedConnectable;
import pipe.io.adapters.model.AdaptedPlace;
import pipe.io.adapters.model.OffsetGraphics;
import pipe.io.adapters.model.Point;
import pipe.io.adapters.utils.ConnectableUtils;
import pipe.io.adapters.utils.TokenUtils;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.HashMap;
import java.util.Map;

public class PlaceAdapter extends XmlAdapter<AdaptedPlace, Place> {
    private final Map<String, Place> places;

    private final Map<String, Token> tokens;

    /**
     * Empty contructor needed for marshelling. Since the method to marshell does not actually
     * use these fields it's ok to initialise them as empty/null.
     */
    public PlaceAdapter() {
        places = new HashMap<String, Place>();
        tokens = new HashMap<String, Token>();
    }

    public PlaceAdapter(Map<String, Place> places, Map<String, Token> tokens) {
        this.tokens = tokens;
        this.places = places;
    }

    @Override
    public Place unmarshal(AdaptedPlace adaptedPlace) throws Exception {
        AdaptedConnectable.NameDetails nameDetails = adaptedPlace.getName();
        Place place = new Place(adaptedPlace.getId(), nameDetails.getName());
        place.setCapacity(adaptedPlace.getCapacity());
        ConnectableUtils.setConnectablePosition(place, adaptedPlace);
        ConnectableUtils.setConntactableNameOffset(place, adaptedPlace);
        place.setTokenCounts(stringToWeights(adaptedPlace.getInitialMarking().getTokenCounts()));
        places.put(place.getId(), place);
        return place;
    }

    @Override
    public AdaptedPlace marshal(Place place) throws Exception {
        AdaptedPlace adapted = new AdaptedPlace();
        adapted.setId(place.getId());
        ConnectableUtils.setAdaptedName(place, adapted);
        ConnectableUtils.setPosition(place, adapted);

        adapted.setCapacity(place.getCapacity());
        adapted.getInitialMarking().setTokenCounts(weightToString(place.getTokenCounts()));


        OffsetGraphics offsetGraphics = new OffsetGraphics();
        offsetGraphics.point = new Point();
        offsetGraphics.point.setX(place.getMarkingXOffset());
        offsetGraphics.point.setY(place.getMarkingYOffset());
        adapted.getInitialMarking().setGraphics(offsetGraphics);


        return adapted;
    }

    private String weightToString(Map<Token, Integer> weights) {
        return Joiner.on(",").withKeyValueSeparator(",").join(weights);
    }

    public Map<Token, Integer> stringToWeights(String value) {
        Map<Token, Integer> tokenWeights = new HashMap<Token, Integer>();
        if (value.isEmpty()) {
            return tokenWeights;
        }

        String[] commaSeperatedMarkings = value.split(",");
        if (commaSeperatedMarkings.length == 1) {
            Token token = TokenUtils.getOrCreateDefaultToken(tokens);
            Integer weight = Integer.valueOf(commaSeperatedMarkings[0]);
            tokenWeights.put(token, weight);
        } else {
            for (int i = 0; i < commaSeperatedMarkings.length; i += 2) {
                Integer weight = Integer.valueOf(commaSeperatedMarkings[i + 1].replace("@", ","));
                String tokenName = commaSeperatedMarkings[i];
                Token token = getTokenIfExists(tokenName);
                tokenWeights.put(token, weight);
            }
        }
        return tokenWeights;
    }

    /**
     * @param tokenName token to find in {@link this.tokens}
     * @return token if exists
     * @throws RuntimeException if token does not exist
     */
    private Token getTokenIfExists(String tokenName) {
        if (!tokens.containsKey(tokenName)) {
            throw new RuntimeException("No " + tokenName + " token exists!");
        }
        return tokens.get(tokenName);
    }
}
