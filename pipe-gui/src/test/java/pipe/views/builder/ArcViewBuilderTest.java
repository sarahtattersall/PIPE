package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.models.component.arc.Arc;
import pipe.models.component.arc.ArcType;
import pipe.models.component.place.Place;
import pipe.models.component.token.Token;
import pipe.models.component.transition.Transition;
import pipe.views.ArcView;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ArcViewBuilderTest {
    Arc arc;

    NormalArcViewBuilder builder;

    private PetriNetController mockController;

    @Before
    public void setUp() {
        Place source = new Place("source", "source");
        Transition transition = new Transition("id", "name");

        arc = new Arc<Place, Transition>(source, transition, new HashMap<Token, String>(), ArcType.NORMAL);
        arc.setId("id");
        mockController = mock(PetriNetController.class);
        builder = new NormalArcViewBuilder(arc, mockController);
    }

    @Test
    public void setsCorrectModel() {
        ArcView view = builder.build();
        assertEquals(arc, view.getModel());
    }

    @Test
    public void setsCorrectAttributes() {
        ArcView view = builder.build();
        assertEquals(arc.getId(), view.getId());
        assertEquals(arc.isTagged(), view.isTagged());
    }
}
