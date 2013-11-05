package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import pipe.models.Transition;
import pipe.views.PetriNetView;
import pipe.views.PetriNetViewComponent;
import pipe.views.TransitionView;

import java.awt.*;

import static org.junit.Assert.assertEquals;

public class TransitionViewBuilderTest {
    private static final double DOUBLE_DELTA = 0.001;
    Transition transition;
    TransitionViewBuilder builder;

    @Before
    public void setUp()
    {
        transition = new Transition("id", "name");
        builder = new TransitionViewBuilder(transition);
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
