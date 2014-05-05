package pipe.dsl;

import pipe.models.component.Connectable;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.InboundInhibitorArc;
import pipe.models.component.place.Place;
import pipe.models.component.rate.RateParameter;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;

import java.util.Map;

public class AnInhibitorArc implements DSLCreator<Arc<? extends Connectable, ? extends Connectable>> {
    private String source;

    private String target;

    private AnInhibitorArc() {
    }

    public static AnInhibitorArc withSource(String source) {
        AnInhibitorArc anInhibitorArc = new AnInhibitorArc();
        anInhibitorArc.source = source;
        return anInhibitorArc;
    }

    public AnInhibitorArc andTarget(String target) {
        this.target = target;
        return this;
    }

    @Override
    public Arc<? extends Connectable, ? extends Connectable> create(Map<String, Token> tokens,
                                                                    Map<String, Place> places,
                                                                    Map<String, Transition> transitions, Map<String, RateParameter> rateParameters) {
        return new InboundInhibitorArc(places.get(source), transitions.get(target));
    }
}
