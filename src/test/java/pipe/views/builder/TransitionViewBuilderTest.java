package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import pipe.models.Transition;
import pipe.views.TransitionView;

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
        TransitionView transitionView = builder.build();
        assertEquals(transition.getX(), transitionView._positionX, DOUBLE_DELTA);
        assertEquals(transition.getY(), transitionView._positionY, DOUBLE_DELTA);
        assertEquals(transition.getId(), transitionView.getId());
        assertEquals(transition.getName(), transitionView.getName());
        assertEquals(transition.getNameXOffset(), transitionView._nameOffsetX, DOUBLE_DELTA);
        assertEquals(transition.getNameYOffset(), transitionView._nameOffsetY, DOUBLE_DELTA);
        assertEquals(transition.getAngle(), transitionView.getAngle());
        assertEquals(transition.isTimed(), transitionView.isTimed());
        assertEquals(transition.isInfiniteServer(), transitionView.isInfiniteServer());
    }

}
