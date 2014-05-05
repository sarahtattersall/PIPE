package pipe.dsl;

import pipe.models.component.PetriNetComponent;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;

import java.util.Map;

/**
 * Interface for creating Petri net components via a nice DSL.
 * To create an object a general create method should be used.
 *
 * These Creators largely follow a sort of builder design pattern
 *
 * The {@link pipe.dsl.APetriNet} can then use this create method
 * to put all components into a {@link pipe.models.petrinet.PetriNet}
 */
public interface DSLCreator<T extends PetriNetComponent> {

    /**
     *
     * This method will create the relevant petri net component
     * It needs to take all maps of previously created items so that
     * the implementation of DSLCreator can turn component ids into the actual value.
     *
     * E.g. if a Place contains 5 "Red" tokens, the PlaceCreator needs to be able to
     * look up the Token "Red" in tokens.
     *
     * Also the creators should add the created component to the list to be used
     * by following items. For this reason if components depend on others e.g. a Place
     * needs a Token, these items should be created first.
     *
     * A suggested order would be
     * Tokens
     * RateParameters
     * Places
     * Transitions
     * Arcs
     *
     *
     * @param tokens map of created tokens with id -> Token
     * @param places map of created places with id -> Connectable
     * @param transitions
     *@param rateParameters  @return new {@link PetriNetComponent}
     */
    T create(Map<String, Token> tokens, Map<String, Place> places, Map<String, Transition> transitions, Map<String, RateParameter> rateParameters);
}
