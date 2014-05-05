package pipe.dsl;

import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;

import java.util.Map;

public class ARateParameter implements DSLCreator<RateParameter> {


    private final String id;

    private String expression;

    private ARateParameter(String id) {this.id = id;}

    /**
     * Create an ARateParameter instance with id
     * @param id name of rate parameter
     * @return instantiated ARateParameter
     */
    public static ARateParameter withId(String id) {
        return new ARateParameter(id);
    }

    public ARateParameter andExpression(String expression) {
        this.expression = expression;
        return this;
    }



    @Override
    public RateParameter create(Map<String, Token> tokens, Map<String, Place> places,
                                Map<String, Transition> transitions, Map<String, RateParameter> rateParameters) {
        RateParameter rateParameter = new RateParameter(expression, id, id);
        rateParameters.put(id, rateParameter);
        return rateParameter;
    }
}
