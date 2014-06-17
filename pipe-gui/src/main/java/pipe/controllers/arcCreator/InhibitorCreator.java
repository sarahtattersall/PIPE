package pipe.controllers.arcCreator;


import uk.ac.imperial.pipe.exceptions.PetriNetComponentException;
import uk.ac.imperial.pipe.models.petrinet.*;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Determines if an inhibitor arc can be created.
 * <p/>
 * The logic for creating an inhibitor arc is that it can only connect from a place to a transition
 */
public class InhibitorCreator implements ArcActionCreator {

    private static final Logger LOGGER = Logger.getLogger(InhibitorCreator.class.getName());

    /**
     * @param source
     * @param target
     * @param arcPoints any point that should be added along the arc
     * @return inbound arc from the place to the transition
     */
    @Override
    public InboundArc createInboundArc(Place source, Transition target, List<ArcPoint> arcPoints) {
        InboundArc arc = new InboundInhibitorArc(source, target);
        arc.addIntermediatePoints(arcPoints);
        return arc;
    }

    /**
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
     * @param source
     * @param target
     * @param <S>    source connectable of the arc
     * @param <T>    target connectable of the arc
     * @return true if the arc is connecting from place -> transition. False otherwise
     */
    @Override
    public <S extends Connectable, T extends Connectable> boolean canCreate(S source, T target) {
        ConnectableVisior connectableVisior = new ConnectableVisior();
        try {
            source.accept(connectableVisior);
            boolean isSourcePlace = connectableVisior.isPlace;
            target.accept(connectableVisior);
            boolean isTargetTransition = connectableVisior.isTransition;
            return isSourcePlace && isTargetTransition;
        } catch (PetriNetComponentException e) {
            LOGGER.log(Level.INFO, e.toString());
            return false;
        }

    }

    /**
     * Used to determine if a Petri net component object is a place or a transition
     */
    private class ConnectableVisior implements PlaceVisitor, TransitionVisitor {
        /**
         * Is visited item a place
         */
        public boolean isPlace = false;

        /**
         * Is visited item a transition
         */
        public boolean isTransition = false;

        @Override
        public void visit(Place place) throws PetriNetComponentException {
            isPlace = true;
            isTransition = false;
        }

        @Override
        public void visit(Transition transition) {
            isPlace = false;
            isTransition = true;
        }
    }
}
