package pipe.controllers.arcCreator;


import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;
import uk.ac.imperial.pipe.models.petrinet.InboundArc;
import uk.ac.imperial.pipe.models.petrinet.OutboundArc;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.util.List;

/**
 * This class determines if arcs can be created from and to Petri net components and
 * is responsible for creating the inbound/outbound arc models
 */
public interface ArcActionCreator {
    /**
     * Creates an inbound arc
     * @param source of the arc
     * @param target of the arc
     * @param arcPoints on the arc
     * @return inbound arc
     */
    InboundArc createInboundArc(Place source, Transition target, List<ArcPoint> arcPoints);

    /**
     * Creates an outbound arc
     * @param target of the arc
     * @param source of the arc
     * @param arcPoints on the arc
     * @return outbound arc
     */
    OutboundArc createOutboundArc(Place target, Transition source, List<ArcPoint> arcPoints);

    /**
     * Return true if can create an arc from source to target
     * @param source of the arc
     * @param target of the arc
     * @param <S> source
     * @param <T> target
     * @return true if the arc can be created 
     */
    <S extends Connectable, T extends Connectable> boolean canCreate(S source, T target);
}
