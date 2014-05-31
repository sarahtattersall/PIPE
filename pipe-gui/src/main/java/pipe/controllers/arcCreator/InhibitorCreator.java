package pipe.controllers.arcCreator;


import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;
import uk.ac.imperial.pipe.models.petrinet.InboundArc;
import uk.ac.imperial.pipe.models.petrinet.InboundInhibitorArc;
import uk.ac.imperial.pipe.models.petrinet.OutboundArc;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.util.List;

public class InhibitorCreator implements ArcActionCreator {

    @Override
    public InboundArc createInboundArc(Place source, Transition target, List<ArcPoint> arcPoints) {
        InboundArc arc =  new InboundInhibitorArc(source, target);
        arc.addIntermediatePoints(arcPoints);
        return arc;
    }

    @Override
    public OutboundArc createOutboundArc(Place target, Transition source, List<ArcPoint> arcPoints) {
        return null;
    }

    @Override
    public <S extends Connectable, T extends Connectable> boolean canCreate(S source, T target) {
        return source.getClass().equals(Place.class) && target.getClass().equals(Transition.class);
    }
}
