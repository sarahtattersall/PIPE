package pipe.controllers.arcCreator;


import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;
import uk.ac.imperial.pipe.models.petrinet.InboundArc;
import uk.ac.imperial.pipe.models.petrinet.OutboundArc;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.util.List;

/**
 * This c
 */
public interface ArcActionCreator {
    /**
     * Creates an inbound arc
     * @param source
     * @param target
     * @param arcPoints
     * @return
     */
    InboundArc createInboundArc(Place source, Transition target, List<ArcPoint> arcPoints);

    /**
     * Creates an outbound arc
     * @param target
     * @param source
     * @param arcPoints
     * @return
     */
    OutboundArc createOutboundArc(Place target, Transition source, List<ArcPoint> arcPoints);

    /**
     * Return true if can create an arc from source to target
     * @param source
     * @param target
     * @param <S>
     * @param <T>
     * @return
     */
    public <S extends Connectable, T extends Connectable> boolean canCreate(S source, T target);
}
