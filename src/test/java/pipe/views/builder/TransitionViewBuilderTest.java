package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import pipe.controllers.PetriNetController;
import pipe.models.component.Transition;
import pipe.views.TransitionView;

import java.awt.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class TransitionViewBuilderTest {
    private static final double DOUBLE_DELTA = 0.001;
    Transition transition;
    TransitionViewBuilder builder;

    private PetriNetController mockController;

    @Before
    public void setUp()
    {
        transition = new Transition("id", "name");
        mockController = mock(PetriNetController.class);
        builder = new TransitionViewBuilder(transition, mockController);
    }

    @Test
    public void correctlySetsModel()
    {
        TransitionView transitionView = builder.build();
        assertEquals(transition, transitionView.getModel());
    }

    @Test
    public void correctlySetsViewAttributes()
    {
        transition.setX(200);
        TransitionView transitionView = builder.build();

        //TODO: SHOULDNT REALLY USE HEIGHT x HEIGHT ITS CONFUSING, its because the transition is actually a square
        // element only partly drawn.
        Rectangle rect = new Rectangle((int)transition.getX(), (int)transition.getY(), transition.getHeight(), transition.getHeight());

        //This - 5 comes from updating the bounds. Its a variable set in PetriNetViewComponent
        rect.grow(5, 5);
        assertEquals(rect.getX(), transitionView.getX(), DOUBLE_DELTA);
        assertEquals(rect.getY(), transitionView.getY(), DOUBLE_DELTA);
        assertEquals(transition.getId(), transitionView.getId());
        assertEquals(transition.getName(), transitionView.getName());
        assertEquals(transition.getAngle(), transitionView.getAngle());
        assertEquals(transition.isTimed(), transitionView.isTimed());
        assertEquals(transition.isInfiniteServer(), transitionView.isInfiniteServer());
    }

}
