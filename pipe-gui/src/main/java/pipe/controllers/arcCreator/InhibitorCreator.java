package pipe.controllers.arcCreator;


import uk.ac.imperial.pipe.models.petrinet.Connectable;
import uk.ac.imperial.pipe.models.petrinet.ArcPoint;
import uk.ac.imperial.pipe.models.petrinet.InboundArc;
import uk.ac.imperial.pipe.models.petrinet.InboundInhibitorArc;
import uk.ac.imperial.pipe.models.petrinet.OutboundArc;
import uk.ac.imperial.pipe.models.petrinet.Place;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.util.List;

/**
 * Determines if an inhibitor arc can be created.
 *
 * The logic for creating an inhibitor arc is that it can only connect from a place to a transition
 */
public class InhibitorCreator implements ArcActionCreator {

    /**
     *
     * @param source
     * @param target
     * @param arcPoints any point that should be added along the arc
     * @return inbound arc from the place to the transition
     */
    @Override
    public InboundArc createInboundArc(Place source, Transition target, List<ArcPoint> arcPoints) {
        InboundArc arc =  new InboundInhibitorArc(source, target);
        arc.addIntermediatePoints(arcPoints);
        return arc;
    }

    /**
     *
     * @param target
     * @param source
     * @param arcPoints
     * @return null since no arc can exist
     */
    @Override
    public OutboundArc createOutboundArc(Place target, Transition source, List<ArcPoint> arcPoints) {
        return null;
    }

    /**
     *
     * @param source
     * @param target
     * @param <S> source connectable of the arc
     * @param <T> target connectable of the arc
     * @return true if the arc is connecting from place -> transition. False otherwise
     */
    @Override
    public <S extends Connectable, T extends Connectable> boolean canCreate(S source, T target) {
        return source.getClass().equals(Place.class) && target.getClass().equals(Transition.class);
    }
}
