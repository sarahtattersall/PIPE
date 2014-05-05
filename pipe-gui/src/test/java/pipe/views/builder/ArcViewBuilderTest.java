package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.controllers.PetriNetController;
import pipe.models.component.Connectable;
import pipe.models.component.arc.InboundArc;
import pipe.models.component.arc.InboundNormalArc;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.views.ArcView;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class ArcViewBuilderTest {
    InboundArc arc;

    NormalArcViewBuilder builder;

    @Mock
    private PetriNetController mockController;

    @Before
    public void setUp() {
        Place source = new Place("source", "source");
        Transition transition = new Transition("id", "name");

        arc = new InboundNormalArc(source, transition, new HashMap<Token, String>());
        arc.setId("id");
        builder = new NormalArcViewBuilder(arc, mockController);
    }

    @Test
    public void setsCorrectModel() {
        ArcView<Connectable, Connectable> view = builder.build();
        assertEquals(arc, view.getModel());
    }

    @Test
    public void setsCorrectAttributes() {
        ArcView<Connectable, Connectable> view = builder.build();
        assertEquals(arc.getId(), view.getId());
        assertEquals(arc.isTagged(), view.isTagged());
    }
}
