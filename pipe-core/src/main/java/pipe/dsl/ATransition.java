package pipe.dsl;

import pipe.models.component.Connectable;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;

import java.util.Map;

public class ATransition implements DSLCreator<Transition> {

    private String id;

    private ATransition(String id) {this.id = id;}

    public static ATransition withId(String id) {
        return new ATransition(id);
    }

    @Override
    public Transition create(Map<String, Token> tokens, Map<String, Connectable> connectables) {
        Transition transition = new Transition(id, id);
        connectables.put(id, transition);
        return transition;
    }
}
