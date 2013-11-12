package pipe.petrinet;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import pipe.models.*;

import java.util.*;

/**
 * Creates an {@link Arc} based on an {@link Element}'s information
 */
public class ArcCreator implements ComponentCreator<Arc> {

    private Map<String, Connectable> connectables = new HashMap<String, Connectable>();
    private Map<String, Token> tokens = new HashMap<String, Token>();

    /**
     *
     * @param connectables Map of id to connectable
     */
    public void setConnectables(Map<String, Connectable> connectables) {
        this.connectables = connectables;
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
     * Creates either a {@link InhibitorArc} or a {@link NormalArc}
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

        Connectable source = connectables.get(sourceId);
        Connectable target = connectables.get(targetId);
        Arc arc;
        if (isInhibitorArc(element)) {
            arc = new InhibitorArc(source, target, tokenWeights);
        } else {
            arc = new NormalArc(source, target, tokenWeights);
        }
        arc.setId(id);

        source.addOutbound(arc);
        target.addInbound(arc);

        arc.setTagged(tagged);

        //TODO: Arc path?

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
