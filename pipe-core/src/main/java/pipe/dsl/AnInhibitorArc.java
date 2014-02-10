package pipe.dsl;

import pipe.models.component.Connectable;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
import pipe.models.component.token.Token;

import java.util.HashMap;
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
                                                                    Map<String, Connectable> connectables) {
        return new Arc<>(connectables.get(source), connectables.get(target), new HashMap<Token, String>(),
                ArcType.INHIBITOR);
    }
}
