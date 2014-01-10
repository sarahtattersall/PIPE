package pipe.petrinet.reader.creator;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pipe.models.component.*;
import pipe.models.strategy.arc.ArcStrategy;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Creates an {@link pipe.models.component.Arc} based on an {@link Element}'s information
 */
public class ArcCreator implements ComponentCreator<Arc<?, ?>> {

    private Map<String, Place> places = new HashMap<String, Place>();
    private Map<String, Transition> transitions = new HashMap<String, Transition>();
    private Map<String, Token> tokens = new HashMap<String, Token>();
    private ArcStrategy<Place, Transition> inhibitorStrategy;
    private ArcStrategy<Transition, Place> normalForwardStrategy;
    private ArcStrategy<Place, Transition> normalBackwardStrategy;

    public ArcCreator(ArcStrategy<Place, Transition> inhibitorStrategy,
                      ArcStrategy<Transition, Place> normalForwardStrategy,
                      ArcStrategy<Place, Transition> normalBackwardStrategy) {
        this.inhibitorStrategy = inhibitorStrategy;
        this.normalForwardStrategy = normalForwardStrategy;
        this.normalBackwardStrategy = normalBackwardStrategy;
    }

    /**
     * @param places Map of id to place
     */
    public void setPlaces(Map<String, Place> places) {
        this.places = places;
    }

    /**
     * @param transitions Map of id to transitions
     */
    public void setTransitions(Map<String, Transition> transitions) {
        this.transitions = transitions;
    }

    /**
     * @param tokens map of token id to token
     */
    public void setTokens(Map<String, Token> tokens) {
        this.tokens = tokens;
    }

    /**
     * Sets it source/target to those found in {@link this.connectables}
     *
     * @param element PNML XML Arc element
     * @return Arc model form of the PNML element
     */
    @Override
    public Arc<? extends Connectable, ? extends Connectable> create(Element element) {
        String id = element.getAttribute("id");
        String sourceId = element.getAttribute("source");
        String targetId = element.getAttribute("target");
        String weightInput = element.getAttribute("inscription");
        boolean tagged = CreatorUtils.falseOrValueOf(element.getAttribute("tagged"));

        Map<Token, String> tokenWeights = createTokenWeights(weightInput);

        Arc<? extends Connectable, ? extends Connectable> arc;
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

        List<ArcPoint> points = getArcPaths(element);
        arc.addIntermediatePoints(points);
        return arc;
    }

    private List<ArcPoint> getArcPaths(Element element) {
        List<ArcPoint> points = new LinkedList<ArcPoint>();
        NodeList nodes = element.getElementsByTagName("arcpath");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                Element arcPointInfo = (Element) node;
                if ("arcpath".equals(arcPointInfo.getNodeName())) {
                    double x = Double.valueOf(arcPointInfo.getAttribute("x"));
                    double y = Double.valueOf(arcPointInfo.getAttribute("y"));
//                    arcPointX += Constants.ARC_CONTROL_POINT_CONSTANT + 1;
//                    arcPointY += Constants.ARC_CONTROL_POINT_CONSTANT + 1;
                    boolean isCurved = Boolean.valueOf(arcPointInfo.getAttribute("arcPointType"));
                    Point2D point = new Point2D.Double(x, y);
                    ArcPoint arcPoint = new ArcPoint(point, isCurved);
                    points.add(arcPoint);
                }
            }
        }
        return points;
    }

    /**
     * @param element Arc element to test
     * @return true if element type is inhibitor
     */
    private boolean isInhibitorArc(Element element) {
        NodeList typeNode = element.getElementsByTagName("type");
        if (typeNode.getLength() > 0) {
            String type = ((Element) typeNode.item(0)).getAttribute("type");
            return (type.equals("inhibitor"));
        }
        return false;
    }

    /**
     * Creates the 'markings' a.k.a. weights associated with an Arc.
     *
     * @param weightInput The string of weights as dictacted by PNML e.g. Default, 1, Red, 2
     * @return Map of token weights for the arc in string format
     */
    private Map<Token, String> createTokenWeights(String weightInput) {
        Map<Token, String> tokenWeights = new HashMap<Token, String>();

        String[] commaSeperatedMarkings = weightInput.split(",");
        if (commaSeperatedMarkings.length == 1) {
            Token token = getDefaultToken();
            String weight = commaSeperatedMarkings[0];
            tokenWeights.put(token, weight);
        } else {
            for (int i = 0; i < commaSeperatedMarkings.length; i += 2) {
                String weight = commaSeperatedMarkings[i + 1].replace("@", ",");
                String tokenName = commaSeperatedMarkings[i];
                Token token = getTokenIfExists(tokenName);
                tokenWeights.put(token, weight);
            }
        }
        return tokenWeights;

    }

    /**
     * @return the default token to use if no token is specified in the
     * Arc weight XML.
     */
    private Token getDefaultToken() {
        return getTokenIfExists("Default");
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
