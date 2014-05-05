package pipe.models.petrinet;

import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.parsers.FunctionalResults;
import pipe.parsers.FunctionalWeightParser;
import pipe.parsers.PetriNetWeightParser;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExprEvaluator {

    private final PetriNet petriNet;

    public ExprEvaluator(PetriNet petriNet) {
        this.petriNet = petriNet;
    }

    public Double parseAndEvalExprForTransition(String expr) throws FunctionalEvaluationException {
        FunctionalWeightParser<Double> transitionWeightParser = new PetriNetWeightParser(petriNet);
        FunctionalResults<Double> result = transitionWeightParser.evaluateExpression(expr);
        if (result.hasErrors()) {
            throw new FunctionalEvaluationException(result.getErrors());
        }
        return result.getResult();

    }

    /**
     * @param expr
     * @param tokenId
     * @return -1 indicates the result value is not an integer
     * @throws Exception
     */
    public int parseAndEvalExpr(String expr, String tokenId) {

        if (!validFloatAndDivision(expr)) {
            return -2;
        }

        String lexpr = new String(expr.replaceAll("\\s", ""));

        Token token = null;
        try {
            token = petriNet.getComponent(tokenId, Token.class);
        } catch (PetriNetComponentNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
        for (Place place : petriNet.getPlaces()) {
            lexpr = findAndReplaceCapacity(lexpr, place);
            String name = getPlaceNameRepresentation(place);

            if (lexpr.toLowerCase().contains(name.toLowerCase())) {
                Map<Token, Integer> tokens = place.getTokenCounts();
                int numOfToken = tokens.get(token);
                lexpr = findAndReplaceTokens(lexpr, name, numOfToken);
            }
        }

        Evaluator evaluator = new Evaluator();
        try {

            String result = evaluator.evaluate(lexpr);
            Double dresult = Double.parseDouble(result);
            return (int) Math.round(dresult);
        } catch (EvaluationException e) {
            e.printStackTrace();
            System.err.println(e.getMessage() + " Expression was " + expr);
            return -1;
        }

    }


    /**
     * @param lexpr
     * @param name       place name
     * @param numOfToken
     * @return expression with place name replaced with its number of tokens
     */
    private String findAndReplaceTokens(String lexpr, String name, int numOfToken) {
        String lexicalExpression = lexpr;
        do {
            lexicalExpression = lexicalExpression.toLowerCase().replace(name.toLowerCase(), Integer.toString(numOfToken));
        } while (lexicalExpression.toLowerCase().contains(name.toLowerCase()));
        return lexicalExpression;
    }

    /**
     * @param place
     * @return name of place in format by #(<name>)
     */
    private String getPlaceNameRepresentation(Place place) {
        String name = place.getName().replaceAll("\\s", "");
        name = ("#(" + name + ")");
        return name;
    }

    /**
     * F
     *
     * @param expr
     * @param place
     * @return String with place name replaced by it's capacity. That is
     * cap(<name>) would be replaced by cap(<capacity>) = cap(10)
     */
    private String findAndReplaceCapacity(String expr, Place place) {
        String returnExpression = expr;
        String capacityWithPlaceName = "cap(" + place.getName().replaceAll("\\s", "") + ")";
        if (returnExpression.toLowerCase().contains(capacityWithPlaceName.toLowerCase())) {
            int capacity = place.getCapacity();
            returnExpression = returnExpression.toLowerCase().replace(capacityWithPlaceName.toLowerCase(), Double.toString(capacity));
        }
        return returnExpression;
    }

    private boolean validFloatAndDivision(String raw) {

        Pattern p = Pattern.compile(".*ceil\\(.*[0-9]*\\.+[0-9]+.*\\).*");
        Pattern p2 = Pattern.compile(".*[0-9]*\\.+[0-9]+.*");
        Matcher m = p.matcher(raw);
        Matcher m1 = p2.matcher(raw);
        Pattern p3 = Pattern.compile(".*floor\\(.*[0-9]*\\.+[0-9]+.*\\).*");
        Matcher m3 = p3.matcher(raw);

        if ((m1.find() && !m.find() && !m3.find())) {
            return false;
        }

        Pattern p1 = Pattern.compile(".*ceil\\(.*/.*\\).*");
        m = p1.matcher(raw);
        Pattern p5 = Pattern.compile(".*floor\\(.*/.*\\).*");
        m1 = p5.matcher(raw);
        if (!m.find() && !m1.find() && raw.contains("/")) {
            return false;
        }

        return true;
    }

}
