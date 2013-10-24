package pipe.petrinet;

import org.w3c.dom.Element;
import pipe.gui.Grid;
import pipe.models.Marking;
import pipe.models.Place;
import pipe.models.Token;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PlaceCreator implements ComponentCreator<Place> {

    private Map<String, Token> tokens = new HashMap<String, Token>();

    public void setTokens(Map<String, Token> tokens) {
        this.tokens = tokens;
    }

    /**
     *
     * @param markingInput a String with token name and marking number delimited by
     *              commas e.g. "Default, 1, Another, 2"
     * @return
     */
    private List<Marking> getTokens(String markingInput)
    {
        List<Marking> markings = new LinkedList<Marking>();
        if (!markingInput.isEmpty())
        {
            /*
             * tokenInputs it of the form <tokenName, number, tokenName, number> etc.
             */
            String[] tokens = markingInput.split(",");

            //TODO: Handle case where token name is not defined ie. marking.length = 1;
            if (tokens.length == 1)
            {
                Token defaultToken = getDefaultToken();
                Marking marking = createMarking(defaultToken.getId(), tokens[0]);
            }
            for(int i = 0; i < tokens.length; i += 2) {
                String tokenName = tokens[i].trim();
                Marking marking = createMarking(tokenName, tokens[i+1]);
                markings.add(marking);
            }

        }
        return markings;
    }

    private Token getDefaultToken() {
        return tokens.get(0);
    }

    private Marking createMarking(String tokenName, String markingValue) {
        int marking = 0;
        try {
            marking = Integer.valueOf(markingValue);
        } catch (NumberFormatException e)
        {
            // Dont care marking just = 0;
        }

        Token token = tokens.get(tokenName);
        return new Marking(token, marking);
    }

    public Place create(Element element)
    {
        String xInput = element.getAttribute("positionX");
        double x = xInput.isEmpty() ? 0 : Double.valueOf(xInput) + 1;
        x = Grid.getModifiedX(x);

        String yInput = element.getAttribute("positionY");
        double y = yInput.isEmpty() ? 0 : Double.valueOf(yInput) + 1;
        y = Grid.getModifiedY(y);

        String id = element.getAttribute("id");
        String name = element.getAttribute("name");

        double nameXOffset = CreatorUtils.zeroOrValueOf(element.getAttribute("nameOffsetX"));
        double nameYOffset = CreatorUtils.zeroOrValueOf(element.getAttribute("nameOffsetX"));

        double markingXOffset = CreatorUtils.zeroOrValueOf(element.getAttribute("markingOffsetX"));
        double markingYOffset = CreatorUtils.zeroOrValueOf(element.getAttribute("markingOffsetY"));

        List<Marking> tokens =  getTokens(
                element.getAttribute("initialMarking"));

        double capacity =  CreatorUtils.zeroOrValueOf(element.getAttribute("capacity"));

        Place place = new Place(id, name);
        //place.setX(x);
        //place.setY(y);
        place.setCentre(x, y);
        place.setNameXOffset(nameXOffset);
        place.setNameYOffset(nameYOffset);
        place.setMarkingXOffset(markingXOffset);
        place.setMarkingYOffset(markingYOffset);
        place.setCapacity(capacity);
        place.addTokens(tokens);

        return place;
    }
}
