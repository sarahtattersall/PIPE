package pipe.controllers.arcCreator;


import uk.ac.imperial.pipe.models.component.Connectable;
import uk.ac.imperial.pipe.models.component.arc.ArcPoint;
import uk.ac.imperial.pipe.models.component.arc.InboundArc;
import uk.ac.imperial.pipe.models.component.arc.InboundInhibitorArc;
import uk.ac.imperial.pipe.models.component.arc.OutboundArc;
import uk.ac.imperial.pipe.models.component.place.Place;
import uk.ac.imperial.pipe.models.component.transition.Transition;

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
