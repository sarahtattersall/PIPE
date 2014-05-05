package pipe.visitor;

import org.junit.Before;
import org.junit.Test;
import pipe.dsl.*;
import pipe.exceptions.PetriNetComponentNotFoundException;
import pipe.models.component.arc.Arc;
import pipe.models.component.place.Place;
import pipe.models.component.transition.Transition;
import pipe.models.petrinet.PetriNet;

import java.awt.Color;

import static org.junit.Assert.assertTrue;

public class ClonePetriNetTest {
    PetriNet oldPetriNet;
    PetriNet clonedPetriNet;

    @Before
    public void setUp() {
        oldPetriNet = APetriNet.with(AToken.called("Default").withColor(Color.BLACK)).and(
                APlace.withId("P0").and(1, "Default").token()).and(APlace.withId("P1")).and(
                ATransition.withId("T0").whichIsTimed()).and(ATransition.withId("T1").whichIsTimed())
                .and(ANormalArc.withSource("P0").andTarget("T0").with("1", "Default").token())
                .and(ANormalArc.withSource("T0").andTarget("P1").with("1", "Default").token())
                .and(ANormalArc.withSource("P1").andTarget("T1").with("1", "Default").token())
                .andFinally(ANormalArc.withSource("T1").andTarget("P0").with("1", "Default").token());
        clonedPetriNet = ClonePetriNet.clone(oldPetriNet);
    }

    @Test
    public void clonesArcWithNewSourceAndTarget() throws PetriNetComponentNotFoundException {
        Arc<Place, Transition> arc = clonedPetriNet.getComponent("P0 TO T0", Arc.class);
        Place clonedP0 = clonedPetriNet.getComponent("P0", Place.class);
        Transition clonedT0 = clonedPetriNet.getComponent("T0", Transition.class);
        assertTrue(arc.getSource() == clonedP0);
        assertTrue(arc.getTarget() == clonedT0);
    }
}
