package pipe.controllers.arcCreator;

import pipe.models.component.Connectable;
import pipe.models.component.arc.ArcPoint;
import pipe.models.component.arc.InboundArc;
import pipe.models.component.arc.OutboundArc;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;

import java.util.List;

public interface ArcActionCreator {
    InboundArc createInboundArc(Place source, Transition target,  List<ArcPoint> arcPoints);
    OutboundArc createOutboundArc(Place target, Transition source, List<ArcPoint> arcPoints);
    public <S extends Connectable, T extends Connectable>  boolean canCreate(S source, T target);
}
