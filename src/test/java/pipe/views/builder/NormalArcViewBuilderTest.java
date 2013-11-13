package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.models.*;
import pipe.views.ArcView;

import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class NormalArcViewBuilderTest {
    private static final double DOUBLE_DELTA = 0.001;
    NormalArc arc;
    NormalArcViewBuilder builder;
    private PetriNetController mockController;

    @Before
    public void setUp()
    {
        Place source = new Place("source", "source");
        Transition transition = new Transition("id", "name");
        arc = new NormalArc(source, transition, new HashMap<Token, String>());
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
