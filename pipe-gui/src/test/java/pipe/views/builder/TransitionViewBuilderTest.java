package pipe.views.builder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import pipe.controllers.PetriNetController;
import pipe.gui.PetriNetTab;
import pipe.gui.model.PipeApplicationModel;
import pipe.views.TransitionView;
import uk.ac.imperial.pipe.models.petrinet.DiscreteTransition;
import uk.ac.imperial.pipe.models.petrinet.Transition;

import java.awt.Rectangle;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class TransitionViewBuilderTest {
    private static final double DOUBLE_DELTA = 0.001;
    Transition transition;
    TransitionViewBuilder builder;

    @Mock
    PetriNetTab parent;


    @Mock
    private PetriNetController mockController;


    @Mock
    private PipeApplicationModel model;

    @Before
    public void setUp()
    {
        transition = new DiscreteTransition("id", "name");
        builder = new TransitionViewBuilder(transition, mockController);
    }

    @Test
    public void correctlySetsModel()
    {
        TransitionView transitionView = builder.build(parent, model);
        assertEquals(transition, transitionView.getModel());
    }

    @Test
    public void correctlySetsViewAttributes()
    {
        transition.setX(200);
        TransitionView transitionView = builder.build(parent, model);

        //TODO: SHOULDNT REALLY USE HEIGHT x HEIGHT ITS CONFUSING, its because the transition is actually a square
        // element only partly drawn.
        Rectangle rect = new Rectangle((int)transition.getX(), (int)transition.getY(), transition.getHeight(), transition.getHeight());

        //This - 5 comes from updating the bounds. Its a variable set in PetriNetViewComponent
        rect.grow(5, 5);
        assertEquals(transition.getId(), transitionView.getId());
        assertEquals(transition.getName(), transitionView.getName());
        assertEquals(transition.getAngle(), transitionView.getAngle());
        assertEquals(transition.isTimed(), transitionView.isTimed());
        assertEquals(transition.isInfiniteServer(), transitionView.isInfiniteServer());
    }

}
