package pipe.petrinet.reader.creator;

import org.w3c.dom.Element;
import pipe.models.component.Place;
import pipe.models.component.Token;

import java.util.HashMap;
import java.util.Map;

public class PlaceCreator implements ComponentCreator<Place> {

    private Map<String, Token> tokens = new HashMap<String, Token>();

    public void setTokens(Map<String, Token> tokens) {
        this.tokens = tokens;
    }


    private Map<Token, Integer> getTokenCounts(String input) {
        Map<Token, Integer> tokenCounts = new HashMap<Token, Integer>();

        if (!input.isEmpty()) {
            String[] tokenInput = input.split(",");
            if (tokenInput.length == 1) {
                Token defaultToken = getDefaultToken();
                Integer count = Integer.valueOf(tokenInput[0]);
                tokenCounts.put(defaultToken, count);
            } else {
                for (int i = 0; i < tokenInput.length; i += 2) {
                    String tokenName = tokenInput[i].trim();
                    Token token = getTokenIfExists(tokenName);
                    Integer count = Integer.valueOf(tokenInput[i + 1]);
                    tokenCounts.put(token, count);
                }
            }
        }
        return tokenCounts;
    }

    private Token getDefaultToken() {
        return getTokenIfExists("Default");
    }

    /**
     * @param tokenName
     * @return Token for string tokenName if it exists
     * @throws RuntimeException if it does not exist
     */
    private Token getTokenIfExists(String tokenName) {
        Token token = tokens.get(tokenName);
        if (token == null) {

            throw new RuntimeException("No " + tokenName + " token exists!");
        }
        return token;
    }

    public Place create(Element element) {
        String xInput = element.getAttribute("positionX");
        double x = xInput.isEmpty() ? 0 : Double.valueOf(xInput);

        String yInput = element.getAttribute("positionY");
        double y = yInput.isEmpty() ? 0 : Double.valueOf(yInput);

        String id = element.getAttribute("id");
        String name = element.getAttribute("name");

        double nameXOffset = CreatorUtils.zeroOrValueOf(
                element.getAttribute("nameOffsetX"));
        double nameYOffset = CreatorUtils.zeroOrValueOf(
                element.getAttribute("nameOffsetX"));

        double markingXOffset = CreatorUtils.zeroOrValueOf(element.getAttribute("markingOffsetX"));
        double markingYOffset = CreatorUtils.zeroOrValueOf(element.getAttribute("markingOffsetY"));


        String tokenValues = element.getAttribute("initialMarking");
        Map<Token, Integer> tokenCounts = getTokenCounts(tokenValues);

//        List<Marking> tokens =  getTokens(
//                element.getAttribute("initialMarking"));
//        Token token = getToken();


        double capacity = CreatorUtils.zeroOrValueOf(element.getAttribute("capacity"));

        Place place = new Place(id, name);
        place.setX(x);
        place.setY(y);
//        place.setCentre(x, y);
        place.setNameXOffset(nameXOffset);
        place.setNameYOffset(nameYOffset);
        place.setMarkingXOffset(markingXOffset);
        place.setMarkingYOffset(markingYOffset);
        place.setCapacity(capacity);


        place.setTokenCounts(tokenCounts);


        return place;
    }

}
