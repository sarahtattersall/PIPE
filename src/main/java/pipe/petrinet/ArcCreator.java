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
        String weight = element.getAttribute("inscription");
        boolean tagged = CreatorUtils.falseOrValueOf(element.getAttribute("tagged"));
        List<Marking> weights = createMarkings(weight);

        Connectable source = connectables.get(sourceId);
        Connectable target = connectables.get(targetId);
        Arc arc;
        if (isInhibitorArc(element)) {
            arc = new InhibitorArc(source, target, weights);
        } else {
            arc = new NormalArc(source, target, weights);
        }
        arc.setId(id);

        //TODO: Add this when changed to Arc not ArcView.
        //source.addOutbound(arc);
        //target.addInbound(arc);
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
     * @param markingInput
     * @return
     */
    private List<Marking> createMarkings(String markingInput) {
        String[] commaSeperatedMarkings = markingInput.split(",");
        if (commaSeperatedMarkings.length == 1)
        {
            Marking marking = processIndividualMarking(commaSeperatedMarkings[0]);
            List<Marking> markings = new LinkedList<Marking>();
            markings.add(marking);
            return markings;
        }
        return processMultipleMarkings(commaSeperatedMarkings);

    }

    /**
     * Turns a string of "TokenId, weight, TokenId, weight" etc. into a
     * list of markings
     * @param markingsInput
     * @return
     */
    private List<Marking> processMultipleMarkings(String[] markingsInput) {
        List<Marking> markings = new LinkedList<Marking>();
        for (int i = 0; i < markingsInput.length; i += 2) {
            String value = markingsInput[i+1].replace("@", ",");
            String tokenId = markingsInput[i];
            Token token = tokens.get(tokenId);
            Marking marking = new Marking(token, value);
            markings.add(marking);
        }
        return markings;
    }

    private Marking processIndividualMarking(String value) {
        Marking marking = new Marking(getDefaultToken(), value);
        return marking;
    }

    /**
     * @return  the default token to use if no token is specified in the
     * Arc weight XML.
     */
    //TODO: WORK OUT WHAT THIS SHOULD BE?
    private Token getDefaultToken()
    {
        return null;
    }

}
