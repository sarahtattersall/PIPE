package pipe.models.strategy.arc;

import pipe.models.component.Arc;
import pipe.models.component.ArcType;
import pipe.models.component.Place;
import pipe.models.component.Transition;

public class InhibitorStrategy implements  ArcStrategy<Place<Transition>, Transition>{
    @Override
    public boolean canFire(final Arc<Place<Transition>, Transition> arc) {
        return arc.getSource().getNumberOfTokensStored() == 0;
    }

    @Override
    public ArcType getType() {
        return ArcType.INHIBITOR;
    }
}
