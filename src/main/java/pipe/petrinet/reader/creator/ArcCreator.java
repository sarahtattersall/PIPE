package pipe.petrinet.reader.creator;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pipe.models.component.*;
import pipe.models.strategy.arc.ArcStrategy;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates an {@link pipe.models.component.Arc} based on an {@link Element}'s information
 */
public class ArcCreator implements ComponentCreator<Arc> {

    private Map<String, Place> places = new HashMap<String, Place>();
    private Map<String, Transition> transitions = new HashMap<String, Transition>();
    private Map<String, Token> tokens = new HashMap<String, Token>();
    private ArcStrategy inhibitorStrategy;
    private ArcStrategy normalForwardStrategy;
    private ArcStrategy normalBackwardStrategy;

    public ArcCreator(ArcStrategy inhibitorStrategy, ArcStrategy normalForwardStrategy, ArcStrategy normalBackwardStrategy) {
        this.inhibitorStrategy = inhibitorStrategy;
        this.normalForwardStrategy = normalForwardStrategy;
        this.normalBackwardStrategy = normalBackwardStrategy;
    }

    /**
     *
     * @param places Map of id to place
     */
    public void setPlaces(Map<String, Place> places) {
        this.places = places;
    }
    /**
     *
     * @param transitions Map of id to transitions
     */
    public void setTransitions(Map<String, Transition> transitions) {
        this.transitions = transitions;
    }

    /**
     *
     * @param tokens map of token id to token
     */
    public void setTokens(Map<String, Token> tokens) {
        this.tokens = tokens;
    }


    /**
     *
     * Creates either a {@link pipe.models.component.InhibitorArc} or a {@link pipe.models.component.NormalArc}
     * based on the elements arc type
     *
     * Sets it source/target to those found in {@link this.connectables}
     *
     * @param element PNML XML Arc element
     * @return
     */
    public Arc create(Element element) {
        String id = element.getAttribute("id");
        String sourceId = element.getAttribute("source");
        String targetId = element.getAttribute("target");
        String weightInput = element.getAttribute("inscription");
        boolean tagged = CreatorUtils.falseOrValueOf(element.getAttribute("tagged"));

        Map<Token, String> tokenWeights = createTokenWeights(weightInput);

        Arc arc;
        if (isInhibitorArc(element)) {
            Place source = places.get(sourceId);
            Transition target = transitions.get(targetId);
            arc = new Arc<Place, Transition>(source, target, tokenWeights, inhibitorStrategy);
        } else {
            if (places.containsKey(sourceId)) {
                Place source = places.get(sourceId);
                Transition target = transitions.get(targetId);
                arc = new Arc<Place, Transition>(source, target, tokenWeights, normalBackwardStrategy);
            } else {
                Place target = places.get(targetId);
                Transition source = transitions.get(sourceId);
                arc = new Arc<Transition, Place>(source, target, tokenWeights, normalForwardStrategy);
            }
        }
        arc.setId(id);
        arc.setTagged(tagged);
        return arc;
    }

    /**
     *
     * @param element
     * @return true if element type is inhibitor
     */
    private boolean isInhibitorArc(Element element) {
        NodeList typeNode = element.getElementsByTagName("type");
        if (typeNode.getLength() > 0)
        {
            String type = ((Element) typeNode.item(0)).getAttribute("type");
            return (type.equals("inhibitor"));
        }
        return false;
    }

    /**
     * Creates the 'markings' a.k.a. weights associated with an Arc.
     * @param weightInput
     * @return
     */
    private Map<Token, String> createTokenWeights(String weightInput) {
        Map<Token, String> tokenWeights = new HashMap<Token, String>();

        String[] commaSeperatedMarkings = weightInput.split(",");
        if (commaSeperatedMarkings.length == 1)
        {
            Token token = getDefaultToken();
            String weight = commaSeperatedMarkings[0];
            tokenWeights.put(token, weight);
        } else
        {
            for (int i = 0; i < commaSeperatedMarkings.length; i += 2) {
                String weight = commaSeperatedMarkings[i+1].replace("@", ",");
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

    /**
     * @return  the default token to use if no token is specified in the
     * Arc weight XML.
     */
    private Token getDefaultToken()
    {
        return getTokenIfExists("Default");
    }

}
